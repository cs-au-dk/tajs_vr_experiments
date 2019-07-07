package dk.brics.tajsvr.experiments.microexperiments;

import dk.brics.tajs.Main;
import dk.brics.tajs.monitoring.AnalysisMonitor;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.refinement.instantiations.forwards_backwards.RefinerOptionValues;
import dk.brics.tajs.refinement.instantiations.forwards_backwards.RefinerOptions;
import dk.brics.tajs.util.AnalysisResultException;
import dk.brics.tajsvr.experiments.Misc;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MicroTestsExperiment {

    public static void main(String[] args) {
        new MicroTestsExperiment().runMicroBenchmarkExperiment();
    }
    public void runMicroBenchmarkExperiment() {
        System.out.println("Running micro benchmarks");
        List<String> experiments = Stream.of("CF", "CG", "AF", "AG", "M1", "M2", "M3").collect(Collectors.toList());
        StringBuilder b = new StringBuilder();
        String format = "%-10s | %-5s | %-7s";
        b.append(String.format(format,
                "Benchmark",
                centerString(5, "TAJS"),
                centerString(7, "TAJS-VR"))).append(System.lineSeparator());
        experiments.forEach(experiment -> {
            b.append(String.format(format,
                    experiment,
                    formatResult(5, runExperiment(experiment, false)),
                    formatResult(7, runExperiment(experiment, true))
            )).append(System.lineSeparator());
        });
        System.out.println("\nResults presented like table 1 in the paper:");
        System.out.println(b);
    }

    private static String centerString (int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }

    private String formatResult(int width, boolean result) {
        String resultCharacter = result ?
                "\u2713" :
                "\u2717";
        return centerString(width, resultCharacter);
    }

    private boolean runExperiment(String testName, boolean useRefiner) {
        String testFile = "test-resources/src/micro-different-loop-kinds/" + testName + ".js";
        initOptions();
        boolean success;
        try {
            if (useRefiner)
                testWithRefiner(testFile);
            else
                testWithoutRefiner(testFile);
            success = true;
        } catch (AnalysisResultException e) {
            success = false;
        }
        String analysis = useRefiner ? "TAJS-VR" : "TAJS";
        System.out.println("  " + analysis + (success ? " succeeded" : " failed") + " test " + testName);
        return success;
    }

    public void initOptions() {
        Main.reset();
        Options.get().enableTest();
        Options.get().enableNoStringSets();
        Options.get().enableNoMessages();
        Main.initLogging();
    }

    private void testWithRefiner(String testFile){
        Options.get().enableNoForInSpecialization();

        RefinerOptionValues refOptions = new RefinerOptionValues();
        refOptions.setWritePropertyRefineEnabled(true);
        refOptions.setSpecializeImpreciseClosureVariablesWithOnlyOneWrite(true);

        test(testFile, refOptions);
    }

    private void testWithoutRefiner(String testFile) {
        Options.get().enableDeterminacy();
        test(testFile, new RefinerOptionValues());
    }

    private void test(String testFile, RefinerOptionValues refOptions){
        String path = Paths.get(testFile).toString();
        RefinerOptions.set(refOptions);
        IAnalysisMonitoring monitoring = new AnalysisMonitor();
        Misc.run(new String[]{path}, monitoring);
    }
}
