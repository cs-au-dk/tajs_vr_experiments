package dk.brics.tajsvr.experiments.libraryexperiments;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static dk.brics.tajs.util.Collections.newList;

public class LibraryExperimentsSummarizer {

    private static String table2Format = "%-15s %12s | %8s | %8s";

    public static void summarizeTestSuites(List<LibraryExperiments> experiments) throws IOException {
        StringBuilder table2Builder = new StringBuilder();
        table2Builder.append(String.format("%-28s | %-19s", "", centerString(19, "TAJS-VR"))).append(System.lineSeparator());
        table2Builder.append(String.format(table2Format, "Benchmark", "", "Success", "Time (s)")).append(System.lineSeparator());
        for (LibraryExperiments experiment : experiments) {
            List<BenchmarkResult> benchmarkResults = getBenchmarkResults(experiment);
            table2Builder.append(summarizeAnalysisResults(experiment, benchmarkResults)).append(System.lineSeparator());
            double totalAnalysisTime = benchmarkResults.stream().mapToDouble(b -> b.analysisTime).sum();
            double totalRefinementTime = benchmarkResults.stream().mapToDouble(b -> b.refinementTime).sum();
            experiment.setTotalAnalysisTime(totalAnalysisTime);
            experiment.setTotalRefinementTime(totalRefinementTime);
        }
        System.out.println("Results presented like table 2 in the paper:");
        System.out.println(table2Builder);
    }

    private static String summarizeAnalysisResults(LibraryExperiments experiment, List<BenchmarkResult> benchmarkResults) throws IOException {
        String averageTime = benchmarkResults.isEmpty() ?
                "-" :
                String.format("%.1f", benchmarkResults.stream().mapToDouble(b -> b.analysisTime/1000).average().getAsDouble());
        int percentageSuccess = (100 * benchmarkResults.size()) / experiment.getNumberOfTests();
        return String.format(table2Format, experiment.getBenchmarkName(), "(" + experiment.getNumberOfTests() + " tests)", percentageSuccess + "%", averageTime);
    }

    private static List<BenchmarkResult> getBenchmarkResults(LibraryExperiments experiment) throws IOException {
        Path resFile = Paths.get("out/stats/" + experiment.getStatsName() + ".jsonp");

        List<String> lines = Files.readAllLines(resFile);
        if (lines.size() != 1) {
            throw new RuntimeException("Expected results file to contain one line");
        }
        String fileContent = lines.get(0);
        String jsonContent = fileContent.substring(fileContent.indexOf("["));
        JsonElement parse = new JsonParser().parse(jsonContent);

        JsonArray jsonArray = parse.getAsJsonArray();
        List<BenchmarkResult> benchmarkResults = newList();

        jsonArray.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (!(jsonObject.get("error").getAsString().isEmpty())) {
                return;
            }
            benchmarkResults.add(createBenchmarkResultFromJsonObject(jsonObject));
        });

        return benchmarkResults;
    }

    private static BenchmarkResult createBenchmarkResultFromJsonObject(JsonObject jsonObject) {
        return new BenchmarkResult(
                jsonObject.get("name").getAsString(),
                jsonObject.get("Analysis_time").getAsInt(),
                jsonObject.get("Refinement_time").getAsInt(),
                jsonObject.get("Assumption_checking_time").getAsInt(),
                jsonObject.get("number_queries_value_refiner").getAsInt(),
                jsonObject.get("number_queries_cache").getAsInt(),

                jsonObject.get("percentage_unique_types_readen").getAsDouble(),
                jsonObject.get("avg_number_types_pr_read").getAsDouble(),
                jsonObject.get("library_object_precision").getAsDouble(),
                jsonObject.get("percentage_unique_callees").getAsDouble(),
                jsonObject.get("soundness_tests").getAsInt()
        );
    }

    private static class BenchmarkResult {
        private final double analysisTime;
        private final double refinementTime;
        private final double assumptionCheckingTime;
        private final int valueRefinerQueries;
        private final int cacheQueries;
        private final String testName;

        private final double percentageUniqueTypesReaden;
        private final double avgNumberTypesPrRead;
        private final double libraryObjectPrecision;
        private final double percentageUniqueCallees;
        private final int soundnessTests;

        private BenchmarkResult(String testName, double analysisTime, double refinementTime, double assumptionCheckingTime, int valueRefinerQueries, int cacheQueries, double percentageUniqueTypesReaden, double avgNumberTypesPrRead, double libraryObjectPrecision, double percentageUniqueCallees, int soundnessTests) {
            this.analysisTime = analysisTime;
            this.refinementTime = refinementTime;
            this.assumptionCheckingTime = assumptionCheckingTime;
            this.valueRefinerQueries = valueRefinerQueries;
            this.cacheQueries = cacheQueries;
            this.testName = testName;
            this.percentageUniqueTypesReaden = percentageUniqueTypesReaden;
            this.avgNumberTypesPrRead = avgNumberTypesPrRead;
            this.libraryObjectPrecision = libraryObjectPrecision;
            this.percentageUniqueCallees = percentageUniqueCallees;
            this.soundnessTests = soundnessTests;
        }
    }

    private static String centerString(int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }
}
