package dk.brics.tajsvr.experiments.libraryexperiments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.brics.tajs.monitoring.inspector.util.OccurenceCountingMap;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.brics.tajs.util.Collections.addToMapSet;
import static dk.brics.tajs.util.Collections.newMap;
import static dk.brics.tajs.util.Collections.newSet;

public class RefinerStatisticsAllTestsResultsParser {

    private static Map<String, OccurenceCountingMap<String>> issuedQueries = newMap();
    private static Map<String, OccurenceCountingMap<String>> successfulQueries = newMap();
    private static Map<String, OccurenceCountingMap<String>> unsuccessfulQueries = newMap();
    private static Map<String, Map<String, OccurenceCountingMap<String>>> exceptionalQueries = newMap();

    private static Map<String, OccurenceCountingMap<Long>> timeSuccessfulQueries = newMap();
    private static Map<String, OccurenceCountingMap<Long>> timeUnsuccessfulQueries = newMap();
    private static Map<String, OccurenceCountingMap<Long>> numberOfVisitedNodesSuccessfulQuery = newMap();
    private static Map<String, OccurenceCountingMap<Long>> numberOfVisitedNodesUnsuccessfulQuery = newMap();

    private static Map<String, OccurenceCountingMap<Boolean>> requiresInterproceduralReasoningMap = newMap();
    private static Map<String, OccurenceCountingMap<Boolean>> solvedByPropertyNameInferenceMap = newMap();
    private static Map<String, Set<String>> testsThatUseRefinementAtLocation = newMap();

    private static Map<String, Set<String>> exceptionsToTestFiles = newMap();
    private static final String COLLECTIVE_STATS_TABLE_FORMAT = "%-15s %12s %10s %15s %15s %13s %10s %13s %10s\n";

    public static void parseResults(List<LibraryExperiments> experiments) throws IOException {
        StringBuilder collectiveStatsTable = new StringBuilder();
        collectiveStatsTable.append(String.format(COLLECTIVE_STATS_TABLE_FORMAT, "", "Ref. locs", "Queries", "Successful", "Analysis time", "Query time", "Visited", "Interproc.", "PNI"));
        Map<Integer, String> detailedRefinmentStatisticsTables = new TreeMap();
        for (LibraryExperiments experiment : experiments) {
            String resFileName = "out/stats/refinerStatisticsAllTests" + experiment.getStatsName() + ".json";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject results = gson.fromJson(new FileReader(resFileName), JsonObject.class);
            for (Map.Entry<String, JsonElement> me : results.entrySet()) {
                String testName = me.getKey();
                JsonObject resultsForTest = me.getValue().getAsJsonObject();
                for (Map.Entry<String, JsonElement> me2 : resultsForTest.entrySet()) {
                    String sl = me2.getKey();
                    JsonObject resultsForSl = me2.getValue().getAsJsonObject();

                    restoreOccurrenceCountingMapResults("issuedQueries", issuedQueries, sl, resultsForSl);
                    restoreOccurrenceCountingMapResults("successfulQueries", successfulQueries, sl, resultsForSl);
                    restoreOccurrenceCountingMapResults("unsuccessfulQueries", unsuccessfulQueries, sl, resultsForSl);

                    restoreOccurrenceCountingMapLongResults("timeSuccessfulQueries", timeSuccessfulQueries, sl, resultsForSl);
                    restoreOccurrenceCountingMapLongResults("timeUnsuccessfulQueries", timeUnsuccessfulQueries, sl, resultsForSl);
                    restoreOccurrenceCountingMapLongResults("numberOfVisitedNodesSuccessfulQuery", numberOfVisitedNodesSuccessfulQuery, sl, resultsForSl);
                    restoreOccurrenceCountingMapLongResults("numberOfVisitedNodesUnsuccessfulQuery", numberOfVisitedNodesUnsuccessfulQuery, sl, resultsForSl);

                    restoreOccurrenceCountingMapBooleanResults("requiresInterproceduralReasoningMap", requiresInterproceduralReasoningMap, sl, resultsForSl);
                    restoreOccurrenceCountingMapBooleanResults("solvedByPropertyNameInferenceMap", solvedByPropertyNameInferenceMap, sl, resultsForSl);

                    restoreMapOfOccurCountMapString(testName, "exceptionalQueries", exceptionalQueries, sl, resultsForSl);
                    addToMapSet(testsThatUseRefinementAtLocation, sl, testName);
                }
            }
            if (experiment.getTableNumberInPaper().isPresent()) {
                String tableForExperiment = "Results as presented in table " + experiment.getTableNumberInPaper().get() + ", detailed refinement statistics for " + experiment.getBenchmarkName() + "\n" + getDetailedRefinementStatsTable();
                detailedRefinmentStatisticsTables.put(experiment.getTableNumberInPaper().get(), tableForExperiment);
            }
            collectiveStatsTable.append(getCollectiveStatsLine(experiment));
            reset();
        }
        System.out.println("Results presented like table 3 in the paper:");
        System.out.println(collectiveStatsTable);
        detailedRefinmentStatisticsTables.values().forEach(System.out::println);
    }

    private static void reset() {
        issuedQueries = newMap();
        successfulQueries = newMap();
        unsuccessfulQueries = newMap();
        exceptionalQueries = newMap();

        timeSuccessfulQueries = newMap();
        timeUnsuccessfulQueries = newMap();
        numberOfVisitedNodesSuccessfulQuery = newMap();
        numberOfVisitedNodesUnsuccessfulQuery = newMap();

        requiresInterproceduralReasoningMap = newMap();
        solvedByPropertyNameInferenceMap = newMap();
        testsThatUseRefinementAtLocation = newMap();

        exceptionsToTestFiles = newMap();
    }

    public static void main(String[] args) throws IOException {
        parseResults(Stream.of(LibraryExperiments.SCRIPTACULOUS).collect(Collectors.toList()));
    }

    private static String getCollectiveStatsLine(LibraryExperiments experiment) {
        float totalNumberIssuedQueries = issuedQueries.values().stream().flatMap(occ -> occ.getMapView().values().stream()).reduce(0, (a, b) -> a+b);
        float totalNumberSuccessfulQueries = successfulQueries.values().stream().flatMap(occ -> occ.getMapView().values().stream()).reduce(0, (a, b) -> a+b);
        float totalTimeSuccessfulQueries = timeSuccessfulQueries.values().stream().flatMap(occ -> occ.getMapView().entrySet().stream()).map(e -> e.getKey() * e.getValue()).reduce(0l, (a, b) -> a+b);
        float totalTimeUnsuccessfulQueries = timeUnsuccessfulQueries.values().stream().flatMap(occ -> occ.getMapView().entrySet().stream()).map(e -> e.getKey() * e.getValue()).reduce(0l, (a, b) -> a+b);
        float totalNumberVisitedNodesSuccessfulQueries = numberOfVisitedNodesSuccessfulQuery.values().stream().flatMap(occ -> occ.getMapView().entrySet().stream()).map(e -> e.getKey() * e.getValue()).reduce(0l, (a, b) -> a+b);
        float totalNumberPropertyNameInference = solvedByPropertyNameInferenceMap.values().stream().map(occ -> occ.getMapView().getOrDefault(true, 0)).reduce(0, (a, b) -> a+b);
        float totalNumberInterprocedural = requiresInterproceduralReasoningMap.values().stream().map(occ -> occ.getMapView().getOrDefault(true, 0)).reduce(0, (a, b) -> a+b);
        float percentageSuccess = (totalNumberSuccessfulQueries*100) / totalNumberIssuedQueries;
        float averageTime = totalTimeSuccessfulQueries / totalNumberSuccessfulQueries;


        float averageLocs = totalNumberVisitedNodesSuccessfulQueries / totalNumberSuccessfulQueries;
        float percentagePNI = (totalNumberPropertyNameInference*100) / totalNumberSuccessfulQueries;
        float percentageInterproc = (totalNumberInterprocedural*100) / totalNumberSuccessfulQueries;

        return String.format(COLLECTIVE_STATS_TABLE_FORMAT,
                experiment.getBenchmarkName(),
                issuedQueries.keySet().size(),
                round(totalNumberIssuedQueries/experiment.getNumberOfTests(), 2),
                round(percentageSuccess, 2) + "%",
                round((float)((100 * experiment.getTotalRefinementTime()) / experiment.getTotalAnalysisTime()), 2) + "%",
                round(averageTime, 2) + "ms",
                round(averageLocs, 2),
                round(percentageInterproc, 2) + "%",
                round(percentagePNI, 2) + "%"
                );

    }

    private static String getDetailedRefinementStatsTable() {
        String appendixTablesFormat = "%-10s%10s%12s%14s%10s%13s%10s%13s\n";
        StringBuilder res = new StringBuilder();
        res.append(String.format(appendixTablesFormat, "Ref. loc", "Queries", "Success", "Time (ms)", "Locs.", "Interproc.", "PNI", "Test cases"));
        for (String sl : issuedQueries.keySet()) {
            //column 1
            String shortSL = sl.substring(sl.indexOf(":") + 1); // What about the refinement location outside the library load code?

            //column2 - % successful queries:
            Collection<Integer> issuedQueriesCollection = issuedQueries.getOrDefault(sl, new OccurenceCountingMap()).getMapView().values();
            float numberIssuedQueries = issuedQueriesCollection.stream().reduce(0, (a, b) -> a + b);
            Collection<Integer> successfulQueriesCollection = successfulQueries.getOrDefault(sl, new OccurenceCountingMap()).getMapView().values();
            float numberSuccessfulQueries = successfulQueriesCollection.stream().reduce(0, (a, b) -> a + b);
            float percentageSuccessfulQueries = round((numberSuccessfulQueries * 100) / numberIssuedQueries, 1);

            //column3 - Average nodes visited
            OccurenceCountingMap<Long> visitedNodesSuccessfulQueries = numberOfVisitedNodesSuccessfulQuery.getOrDefault(sl, new OccurenceCountingMap());
            String averageNodesVisited = formatMinMaxAverageStatsPaper(visitedNodesSuccessfulQueries, false);

            //column4 - percentage queries answered by property name inference
            float numberAnsweredByPropertyNameInference = solvedByPropertyNameInferenceMap.getOrDefault(sl, new OccurenceCountingMap()).getResult(true);
            float numberNotAnsweredByPropertyNameInference = solvedByPropertyNameInferenceMap.getOrDefault(sl, new OccurenceCountingMap()).getResult(false);
            float totalNumberPropertyNameInference = numberAnsweredByPropertyNameInference + numberNotAnsweredByPropertyNameInference;
            String percentageRequirePropertyNameInference =  totalNumberPropertyNameInference > 0 ?
                    "" + round(((100 * numberAnsweredByPropertyNameInference) / totalNumberPropertyNameInference), 1) :
                    "0";

            //column5 - Average refinement time
            OccurenceCountingMap<Long> refinementTimeMap = timeSuccessfulQueries.getOrDefault(sl, new OccurenceCountingMap());
            String avgRefinementTime = formatMinMaxAverageStatsPaper(refinementTimeMap, false);

            //column6 - percentage queries interprocedural reasoning
            float numberRequireInterproceduralReasoning = requiresInterproceduralReasoningMap.getOrDefault(sl, new OccurenceCountingMap()).getResult(true);
            float numberDoesNotRequireInterproceduralReasoning = requiresInterproceduralReasoningMap.getOrDefault(sl, new OccurenceCountingMap()).getResult(false);
            float totalNumberRequireInterproceduralReasoning = numberRequireInterproceduralReasoning + numberDoesNotRequireInterproceduralReasoning;
            String percentageRequireInterproceduralReasoning =  totalNumberRequireInterproceduralReasoning > 0 ?
                    "" + round(((100 * numberRequireInterproceduralReasoning) / totalNumberRequireInterproceduralReasoning), 1) :
                    "0";

            // column7 - Number of tests that use refinement at location
            int numberTests = testsThatUseRefinementAtLocation.getOrDefault(sl, newSet()).size();

            res.append(String.format(appendixTablesFormat,
                    shortSL,
                    (int)numberIssuedQueries,
                    percentageSuccessfulQueries + "%",
                    avgRefinementTime,
                    averageNodesVisited,
                    percentageRequireInterproceduralReasoning + "%",
                    percentageRequirePropertyNameInference + "%",
                    numberTests));
        }
        return res.toString();
    }

    private static float round(float value, int precision) {
        float scale = (float) Math.pow(10, precision);
        float res = Math.round(value * scale) / scale;
        return res;
    }

    public static String formatMinMaxAverageStatsPaper(OccurenceCountingMap<Long> map, boolean includeMinMax) {
        long smallest = Long.MAX_VALUE;
        long largest = -1;
        int numberOfElements = 0;
        long totalSum = 0;

        for (OccurenceCountingMap.CountingResult<Long> time : map.getResults()) {
            if (time.getElement() < smallest)
                smallest = time.getElement();
            if (time.getElement() > largest)
                largest = time.getElement();
            numberOfElements += time.getOccurences();
            totalSum += time.getElement() * time.getOccurences();
        }
        if (numberOfElements == 0) {
            return "-";
        } else {
            long avg = totalSum / numberOfElements;
            String res = "" + avg;
            if (includeMinMax && smallest != largest) {
                res += "(" + smallest + "-" + largest + ")";
            }
            return res;
        }
    }

    private static void restoreMapOfOccurCountMapString(String testName, String keyName, Map<String, Map<String, OccurenceCountingMap<String>>> map, String sl, JsonObject resultsForSl) {
        JsonObject exceptionalQueriesResults = resultsForSl.getAsJsonObject(keyName);
        if (!map.containsKey(sl)) {
            map.put(sl, newMap());
        }
        Map<String, OccurenceCountingMap<String>> exceptionStringToQuery = map.get(sl);
        for (Map.Entry<String, JsonElement> me : exceptionalQueriesResults.entrySet()) {
            String exceptionString = me.getKey();
            addToMapSet(exceptionsToTestFiles, exceptionString, testName);
            if (!exceptionStringToQuery.containsKey(sl)) {
                exceptionStringToQuery.put(exceptionString, new OccurenceCountingMap());
            }
            JsonObject queryResultForExceptionString = exceptionalQueriesResults.getAsJsonObject(exceptionString);
            for (Map.Entry<String, JsonElement> me2 : queryResultForExceptionString.entrySet()) {
                String queryString = me2.getKey();
                int exceptionCountForQuery = queryResultForExceptionString.get(queryString).getAsInt();
                exceptionStringToQuery.get(exceptionString).count(queryString, exceptionCountForQuery);
            }
        }
    }

    private static void restoreOccurrenceCountingMapResults(String keyName, Map<String, OccurenceCountingMap<String>> map, String sl, JsonObject resultsForSl) {
        JsonObject issuedQueriesResults = resultsForSl.getAsJsonObject(keyName);
        if (!map.containsKey(sl)) {
            map.put(sl, new OccurenceCountingMap());
        }
        for (Map.Entry<String, JsonElement> me : issuedQueriesResults.entrySet()) {
            map.get(sl).count(me.getKey(), me.getValue().getAsInt());
        }
    }

    private static void restoreOccurrenceCountingMapLongResults(String keyName, Map<String, OccurenceCountingMap<Long>> map, String sl, JsonObject resultsForSl) {
        JsonObject issuedQueriesResults = resultsForSl.getAsJsonObject(keyName);
        for (Map.Entry<String, JsonElement> me : issuedQueriesResults.entrySet()) {
            if (!map.containsKey(sl)) {
                map.put(sl, new OccurenceCountingMap());
            }
            map.get(sl).count(Long.parseLong(me.getKey()), me.getValue().getAsInt());
        }
    }

    private static void restoreOccurrenceCountingMapBooleanResults(String keyName, Map<String, OccurenceCountingMap<Boolean>> map, String sl, JsonObject resultsForSl) {
        JsonObject issuedQueriesResults = resultsForSl.getAsJsonObject(keyName);
        for (Map.Entry<String, JsonElement> me : issuedQueriesResults.entrySet()) {
            if (!map.containsKey(sl)) {
                map.put(sl, new OccurenceCountingMap());
            }
            map.get(sl).count(Boolean.parseBoolean(me.getKey()), me.getValue().getAsInt());
        }
    }
}
