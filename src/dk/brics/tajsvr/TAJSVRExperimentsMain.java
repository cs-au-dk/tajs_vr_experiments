package dk.brics.tajsvr;

import dk.brics.tajs.Main;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.Transfer;
import dk.brics.tajs.monitoring.AnalysisMonitor;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.monitoring.refinement.RefinerStatistics;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.refinement.instantiations.forwards_backwards.RefinerOptionValues;
import dk.brics.tajs.refinement.instantiations.forwards_backwards.RefinerOptions;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Collectors;
import dk.brics.tajsvr.experiments.libraryexperiments.LibraryExperiments;
import dk.brics.tajsvr.experiments.libraryexperiments.LibraryExperimentsSummarizer;
import dk.brics.tajsvr.experiments.microexperiments.MicroTestsExperiment;
import dk.brics.tajsvr.experiments.libraryexperiments.RefinerStatisticsAllTestsResultsParser;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TAJSVRExperimentsMain {

    /**
     * Runs the analysis on the given source files.
     * Run without arguments to see the usage.
     * Terminates with System.exit.
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            String expKind = args[0];
            switch (expKind) {
                case "micro-benchmarks":
                    if (args.length != 1) {
                        throw new IllegalArgumentException("The experiment micro-benchmarks does not take any arguments");
                    }
                    new MicroTestsExperiment().runMicroBenchmarkExperiment();
                    return;
                case "library-benchmarks":
                    if (args.length != 2) {
                        throw new IllegalArgumentException("The experiment library-benchmarks requires one argument specifying the test-suite");
                    }
                    String testSuitesString = args[1];
                    String[] testSuites = testSuitesString.equals("ALL") ?
                            new String[]{"prototype", "scriptaculous", "underscore", "lodash3", "lodash4", "jsai", "jquery"}:
                            testSuitesString.split(",");
                    List<LibraryExperiments> experiments = getExperimentsFromStrings(testSuites);
                    for (LibraryExperiments experiment : experiments) {

                        long startTime = System.currentTimeMillis();
                        System.out.println("Starting experiment: " + experiment.getBenchmarkName());
                        experiment.runTests();
                        LibraryExperimentsSummarizer.summarizeTestSuites(Stream.of(experiment).collect(java.util.stream.Collectors.toList()));
                        RefinerStatisticsAllTestsResultsParser.parseResults(Stream.of(experiment).collect(java.util.stream.Collectors.toList()));
                        System.out.println("Finished experiment: " + experiment.getBenchmarkName());
                        long secondsForExperiment = (System.currentTimeMillis() - startTime) / 1000;
                        System.out.println("It took " + (secondsForExperiment / 60) + " minutes, or more precisely " + secondsForExperiment + " seconds");
                    }
                    System.out.println("\n\n");
                    System.out.println("===================================");
                    System.out.println("Summarizing all the results:");
                    System.out.println("===================================");
                    LibraryExperimentsSummarizer.summarizeTestSuites(experiments);
                    RefinerStatisticsAllTestsResultsParser.parseResults(experiments);
                    return;
                case "single-test":
                    try {
                        initLogging();
                        RefinerOptionValues refOptions = new RefinerOptionValues();
                        refOptions.setWritePropertyRefineEnabled(true);
                        refOptions.setDoNotRefinePreciseValues(true);
                        refOptions.setSpecializeImpreciseClosureVariablesWithOnlyOneWrite(true);
                        refOptions.setDoNotRefinePropertyNameForUndefined(true);
                        refOptions.setUseSoundDefaultForFailedQueriesLocations(true);
                        RefinerOptions.set(refOptions);

                        String[] analysisArgs = Arrays.copyOfRange(args, 1, args.length);

                        Analysis a = init(analysisArgs, null);
                        if (a == null)
                            System.exit(-1);
                        run(a);
                        System.exit(0);
                    } catch (AnalysisException e) {
                        e.printStackTrace();
                        System.exit(-2);
                    }
                default:
                    throw new IllegalArgumentException("Illegal argument");
            }
        }
    }

    private static List<LibraryExperiments> getExperimentsFromStrings(String[] testSuites) {
        List<LibraryExperiments> experiments = new ArrayList();
        for (String testSuite : testSuites) {
            switch (testSuite.toLowerCase()) {
                case "jquery": experiments.add(LibraryExperiments.JQUERY); break;
                case "jsai": experiments.add(LibraryExperiments.JSAI); break;
                case "prototype": experiments.add(LibraryExperiments.PROTOTYPE); break;
                case "scriptaculous": experiments.add(LibraryExperiments.SCRIPTACULOUS); break;
                case "underscore": experiments.add(LibraryExperiments.UNDERSCORE); break;
                case "lodash3": experiments.add(LibraryExperiments.LODASH3); break;
                case "lodash4": experiments.add(LibraryExperiments.LODASH4); break;
                default:
                    throw new IllegalArgumentException("Invalid test-suite: " + testSuite + System.lineSeparator());
            }
        }
        return experiments;
    }

    public static void initLogging() {
        Main.initLogging();
    }

    public static void reset() {
        Main.reset();
        RefinerStatistics.reset();
    }

    public static Analysis init(String[] args) throws AnalysisException {
        return init(args, null);
    }

    public static Analysis init(String[] args, IAnalysisMonitoring monitoring) throws AnalysisException {
        ValueRefiner refiner = new ValueRefiner();

        Options.get().getArguments().addAll(Stream.of(args).map(file -> Paths.get(file)).collect(Collectors.toList()));
        Analysis analysis = Main.init(Options.get(), monitoring == null ? new AnalysisMonitor() : monitoring, null, new Transfer(), null, refiner);
        return analysis;
    }

    public static void run(Analysis analysis) throws AnalysisException {
        Main.run(analysis);
    }
}
