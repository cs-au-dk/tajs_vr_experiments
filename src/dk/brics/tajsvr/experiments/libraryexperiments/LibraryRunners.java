package dk.brics.tajsvr.experiments.libraryexperiments;

import com.google.gson.stream.JsonWriter;
import dk.brics.tajs.Main;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.jsnodes.CallNode;
import dk.brics.tajs.flowgraph.jsnodes.ReadPropertyNode;
import dk.brics.tajs.flowgraph.jsnodes.ReadVariableNode;
import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.lattice.ObjProperties;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.PKey;
import dk.brics.tajs.lattice.PKeys;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.UnknownValueResolver;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.monitoring.AnalysisMonitor;
import dk.brics.tajs.monitoring.CompositeMonitor;
import dk.brics.tajs.monitoring.DefaultAnalysisMonitoring;
import dk.brics.tajs.monitoring.PhaseMonitoring;
import dk.brics.tajs.monitoring.ProgressMonitor;
import dk.brics.tajs.monitoring.refinement.RefinerStatistics;
import dk.brics.tajs.monitoring.refinement.RefinerStatisticsAllTests;
import dk.brics.tajs.options.OptionValues;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.refinement.instantiations.forwards_backwards.RefinerOptionValues;
import dk.brics.tajs.refinement.instantiations.forwards_backwards.RefinerOptions;
import dk.brics.tajs.solver.NodeAndContext;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajsvr.TAJSVRExperimentsMain;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newMap;
import static dk.brics.tajs.util.Collections.newSet;

public class LibraryRunners {
    public interface LibraryBenchmarkExperimentRunner {
        void runTests() throws Exception;
    }

    public static class Lodash4ValueRefiner implements LibraryBenchmarkExperimentRunner {

        public static void main(String[] args) throws Exception {
            new Lodash4ValueRefiner().runTests();
        }

        @Override
        public void runTests() throws Exception {
            List<Path> testFiles = Files.list(Paths.get("test-resources/src/lodash/test-suite/4.17.10/")).collect(Collectors.toList());

            OptionValues optionValues = new OptionValues();
            optionValues.enableTest();
            optionValues.getSoundnessTesterOptions().setGenerateOnlyIncludeAutomaticallyForHTMLFiles(true);

            optionValues.enableDeterminacy();
            optionValues.enablePolyfillMDN();
            optionValues.enablePolyfillTypedArrays();
            optionValues.enablePolyfillES6Collections();
            optionValues.enablePolyfillES6Promises();
            optionValues.enableConsoleModel();
            optionValues.enableNoMessages();
            optionValues.setAnalysisTimeLimit(300);

            optionValues.getUnsoundness().setUsePreciseFunctionToString(true);

            runTestsHelper("lodash4", testFiles, optionValues, getRefinerOptionValues(), Optional.of("_"));
        }
    }

    public static class Lodash3ValueRefiner implements LibraryBenchmarkExperimentRunner {

        public static void main(String[] args) throws Exception {
            new Lodash3ValueRefiner().runTests();
        }

        @Override
        public void runTests() throws Exception {
            List<Path> testFiles = Files.list(Paths.get("test-resources/src/lodash/test-suite/3.0.0/")).collect(Collectors.toList());

            OptionValues optionValues = new OptionValues();
            optionValues.enableTest();
            optionValues.getSoundnessTesterOptions().setGenerateOnlyIncludeAutomaticallyForHTMLFiles(true);

            optionValues.enableDeterminacy();
            optionValues.enablePolyfillMDN();
            optionValues.enablePolyfillTypedArrays();
            optionValues.enablePolyfillES6Collections();
            optionValues.enablePolyfillES6Promises();
            optionValues.enableConsoleModel();
            optionValues.enableNoMessages();
            optionValues.setAnalysisTimeLimit(300);

            optionValues.getUnsoundness().setUsePreciseFunctionToString(true);

            runTestsHelper("lodash3", testFiles, optionValues, getRefinerOptionValues(), Optional.of("_"));
        }
    }

    public static class UnderscoreValueRefiner implements LibraryBenchmarkExperimentRunner {

        public static void main(String[] args) throws Exception {
            new UnderscoreValueRefiner().runTests();
        }

        @Override
        public void runTests() throws Exception {
            List<Path> testFiles = newList();
            testFiles.addAll(Files.list(Paths.get("test-resources/src/underscore/test-suite/arrays/")).collect(Collectors.toList()));
            testFiles.addAll(Files.list(Paths.get("test-resources/src/underscore/test-suite/chaining/")).collect(Collectors.toList()));
            testFiles.addAll(Files.list(Paths.get("test-resources/src/underscore/test-suite/collections/")).collect(Collectors.toList()));
            testFiles.addAll(Files.list(Paths.get("test-resources/src/underscore/test-suite/functions/")).collect(Collectors.toList()));
            testFiles.addAll(Files.list(Paths.get("test-resources/src/underscore/test-suite/objects/")).collect(Collectors.toList()));
            testFiles.addAll(Files.list(Paths.get("test-resources/src/underscore/test-suite/utility/")).collect(Collectors.toList()));
            OptionValues optionValues = new OptionValues();
            optionValues.enableTest();
            optionValues.getSoundnessTesterOptions().setGenerateOnlyIncludeAutomaticallyForHTMLFiles(true);

            optionValues.enableDeterminacy();
            optionValues.enableNoMessages();
            optionValues.enablePolyfillMDN();
            optionValues.enablePolyfillTypedArrays();
            optionValues.setAnalysisTimeLimit(300);

            runTestsHelper("underscore", testFiles, optionValues, getRefinerOptionValues(), Optional.of("_"));
        }
    }

    public static class PrototypeValueRefiner implements LibraryBenchmarkExperimentRunner {

        public static void main(String[] args) throws Exception {
            new PrototypeValueRefiner().runTests();
        }

        @Override
        public void runTests() throws Exception {
            List<Path> testFiles = Files.list(Paths.get("test-resources/src/prototype/testcases")).collect(Collectors.toList());
            OptionValues optionValues = new OptionValues();
            optionValues.enableTest();
            optionValues.getSoundnessTesterOptions().setGenerateOnlyIncludeAutomaticallyForHTMLFiles(true);

            optionValues.enableDeterminacy();
            optionValues.enablePolyfillMDN();
            optionValues.enablePolyfillTypedArrays();
            optionValues.enablePolyfillES6Collections();
            optionValues.enablePolyfillES6Promises();
            optionValues.enableConsoleModel();
            optionValues.enableNoMessages();
            optionValues.setAnalysisTimeLimit(300);

            optionValues.getUnsoundness().setUsePreciseFunctionToString(true);

            runTestsHelper("prototype", testFiles, optionValues, getRefinerOptionValues(), Optional.empty());
        }
    }

    public static class ScriptaculousValueRefiner implements LibraryBenchmarkExperimentRunner {

        public static void main(String[] args) throws Exception {
            new ScriptaculousValueRefiner().runTests();
        }

        @Override
        public void runTests() throws Exception {
            List<Path> testFiles = Stream.of(Paths.get("test-resources/src/scriptaculous/justload.html")).collect(Collectors.toList());
            OptionValues optionValues = new OptionValues();
            optionValues.enableTest();
            optionValues.getSoundnessTesterOptions().setGenerateOnlyIncludeAutomaticallyForHTMLFiles(true);

            optionValues.enableDeterminacy();
            optionValues.enablePolyfillMDN();
            optionValues.enablePolyfillTypedArrays();
            optionValues.enablePolyfillES6Collections();
            optionValues.enablePolyfillES6Promises();
            optionValues.enableConsoleModel();
            optionValues.enableNoMessages();
            optionValues.setAnalysisTimeLimit(300);
            optionValues.getSoundnessTesterOptions().setDoNotCheckAmbiguousNodeQueries(true);

            optionValues.getUnsoundness().setUsePreciseFunctionToString(true);

            runTestsHelper("scriptaculous", testFiles, optionValues, getRefinerOptionValues(), Optional.empty());
        }
    }

    public static class jQueryValueRefiner implements LibraryBenchmarkExperimentRunner {

        public static void main(String[] args) throws Exception {
            new jQueryValueRefiner().runTests();
        }

        @Override
        public void runTests() throws Exception {
            List<Path> testFiles = Files.list(Paths.get("test-resources/src/jquery/test-suite/1.10")).collect(Collectors.toList());

            OptionValues optionValues = new OptionValues();
            optionValues.enableTest();
            optionValues.getSoundnessTesterOptions().setGenerateOnlyIncludeAutomaticallyForHTMLFiles(true);

            optionValues.enableDeterminacy();
            optionValues.enablePolyfillMDN();
            optionValues.enablePolyfillTypedArrays();
            optionValues.enablePolyfillES6Collections();
            optionValues.enableConsoleModel();
            optionValues.enableNoMessages();
            optionValues.enableIncludeDom();
            optionValues.enableUnevalizer();
            optionValues.setAnalysisTimeLimit(60);

            optionValues.getUnsoundness().setIgnoreSomePrototypesDuringDynamicPropertyReads(true);
            optionValues.getUnsoundness().setIgnoreImpreciseEvals(true);
            optionValues.getUnsoundness().setIgnoreUnlikelyUndefinedAsFirstArgumentToAddition(true);
            optionValues.getUnsoundness().setAssumeInOperatorReturnsTrueWhenSoundResultIsMaybeTrueAndPropNameIsNumber(true);
            optionValues.getUnsoundness().setIgnoreUnlikelyPropertyReads(true);
            optionValues.getUnsoundness().setUseFixedRandom(true);
            optionValues.getUnsoundness().setShowUnsoundnessUsage(true);
            optionValues.getSoundnessTesterOptions().setGenerateOnlyIncludeAutomaticallyForHTMLFiles(true);


            runTestsHelper("jQuery", testFiles, optionValues, getRefinerOptionValues(), Optional.of("$"));
        }
    }

    public static class JSAIValueRefiner implements LibraryBenchmarkExperimentRunner {

        public static void main(String[] args) throws Exception {
            new JSAIValueRefiner().runTests();
        }

        @Override
        public void runTests() throws Exception {
            List<Path> testFiles = newList();
            testFiles.addAll(Files.list(Paths.get("test-resources/src/jsai/jsai2014")).collect(Collectors.toList()));
            testFiles.addAll(Files.list(Paths.get("test-resources/src/jsai/jsai2015")).collect(Collectors.toList()));

            OptionValues optionValues = new OptionValues();
            optionValues.enableTest();

            optionValues.enableDeterminacy();
            optionValues.enablePolyfillMDN();
            optionValues.enablePolyfillTypedArrays();
            optionValues.enablePolyfillES6Collections();
            optionValues.enablePolyfillES6Promises();
            optionValues.enableConsoleModel();
            optionValues.enableNoMessages();
            optionValues.setAnalysisTimeLimit(300);
            optionValues.enableIncludeDom();

            optionValues.getUnsoundness().setUsePreciseFunctionToString(true);

            runTestsHelper("JSAI", testFiles, optionValues, getRefinerOptionValues(), Optional.empty());
        }
    }

    private static RefinerOptionValues getRefinerOptionValues() {
        RefinerOptionValues refinerOptions = new RefinerOptionValues();
        refinerOptions.setWritePropertyRefineEnabled(true);
        refinerOptions.setDoNotRefinePreciseValues(true);
        refinerOptions.setSpecializeImpreciseClosureVariablesWithOnlyOneWrite(true);
        refinerOptions.setDoNotRefinePropertyNameForUndefined(true);
        refinerOptions.setUseSoundDefaultForFailedQueriesLocations(true);
        return refinerOptions;
    }

    public static void runTestsHelper(String outputFile, List<Path> tests, OptionValues optionValues, RefinerOptionValues refuterOptions, Optional<String> libraryObjectVariableName) throws Exception {
//        warmupVM(tests.get(0), optionValues, refuterOptions);
        String startTime = new Date().toString();
        Path statDir = Paths.get("out/stats");
        Path f = statDir.resolve(outputFile + ".jsonp");
        Path statsFile = statDir.resolve("statsRefiner.html");
        //noinspection ResultOfMethodCallIgnored
        statDir.toFile().mkdirs();
        Files.copy(Paths.get("test-resources/statsRefiner.html"), statsFile, StandardCopyOption.REPLACE_EXISTING);
        //noinspection ResultOfMethodCallIgnored
        try (FileWriter fw = new FileWriter(f.toFile())) {
            Main.initLogging();
            JsonWriter w = new JsonWriter(fw);
            fw.write("data = ");
            w.beginArray();
            int numberOfTests = tests.size();
            int currentTest = 0;
            for (Path test : tests) {
                RefinerStatisticsAllTests.init();
                System.out.format("[%d/%d] %s\n", ++currentTest, numberOfTests, test.toString());
                System.out.println("Test started at: " + new Date());

                OptionValues options = optionValues;
                Throwable throwable = null;
                long time = 0;
                Analysis a = null;
                ProgressMonitor progressMonitor = new ProgressMonitor(false);
                LibraryRunners.PrecisionMonitor precisionMonitor = new LibraryRunners.PrecisionMonitor(libraryObjectVariableName);
                AnalysisMonitor analysisMonitor = new AnalysisMonitor();
                try {
                    Options.set(options);
                    RefinerOptions.set(refuterOptions);
                    if (RefinerStatisticsAllTests.isInitialized())
                        RefinerStatisticsAllTests.get().initAnalysis(startTime, outputFile, test.toString());
                    a = TAJSVRExperimentsMain.init(new String[]{test.toString()}, CompositeMonitor.make(analysisMonitor, progressMonitor, precisionMonitor));
                    if (a == null)
                        throw new AnalysisException("Error during initialization");
                    time = System.currentTimeMillis();
                    TAJSVRExperimentsMain.run(a);
                } catch (Throwable e) {
                    throwable = e;
                }

                long elapsed = System.currentTimeMillis() - time;
                System.out.println("Analysis " + (throwable == null ?
                        ("succeeded and passed all " + analysisMonitor.getNumberOfSoundnessChecks() + " soundness tests") :
                        ("failed: " + throwable.getMessage())));
                System.out.println("Total time: " + String.format("%.2fs", ((double)elapsed)/1000));

                if (throwable == null)
                    System.out.println("Analysis time: " + String.format("%.2fs", ((double)RefinerStatistics.get().getDataflowPhaseTime())/1000));

                w.beginObject();
                String name = test.getFileName().toString();
                w.name("name").value(name.replace("test-resources/src", "").replace("benchmarks/tajs/src", ""));
                w.name("options").value(optionValues.toString());
                try {
                    w.name("error").value(throwable != null ? throwable.getMessage().length() > 200 ? throwable.getMessage().substring(0, 200) + "..." : throwable.getMessage() : "");
                } catch (Throwable e) {
                    w.name("error").value(throwable.toString());
                }
                if (time != 0) {
                    w.name("time").value(((double) elapsed) / 1000);
                }

                w.name("Analysis_time").value(RefinerStatistics.get().getDataflowPhaseTime());
                w.name("Assumption_checking_time").value(RefinerStatistics.get().getAssumptionCheckingTime());
                w.name("Refinement_time").value(RefinerStatistics.get().getRefinementTime());
                w.name("number_queries_value_refiner").value(RefinerStatistics.get().getNumberOfRefinementQueriesToValueRefiner());
                w.name("number_queries_cache").value(RefinerStatistics.get().getNumberOfRefinementQueriesAnsweredByCache());
                w.name("number_different_queries").value(RefinerStatistics.get().getNumberOfDifferentQueries());
                w.name("average_refinement_time").value(RefinerStatistics.get().getAverageRefinementTime());
                w.name("number_exceptions_from_refiner").value(RefinerStatistics.get().getNumberOfExceptionsFromRefiner());
                w.name("Refinement_nodes").value(RefinerStatistics.get().getNodesWithRefinement());

                if (a != null) {
                    w.name("node_transfers").value(progressMonitor.getPreScanMonitor().getNodeTransfers());
                    w.name("visited_usercode_node").value(progressMonitor.getPreScanMonitor().getVisitedNonHostNodes().size());
                    w.name("transfers_per_visited_node").value(!progressMonitor.getPreScanMonitor().getVisitedNonHostNodes().isEmpty() ? ((double) progressMonitor.getPreScanMonitor().getNodeTransfers()) / progressMonitor.getPreScanMonitor().getVisitedNonHostNodes().size() : -1);
                    w.name("visited_div_total_nodes").value(a.getSolver().getFlowGraph().getNumberOfUserCodeNodes() != 0 ? ((double) progressMonitor.getPreScanMonitor().getVisitedNonHostNodes().size()) / a.getSolver().getFlowGraph().getNumberOfUserCodeNodes() : -1);
                    w.name("total_usercode_nodes").value(a.getSolver().getFlowGraph().getNumberOfUserCodeNodes());
                    w.name("abstract_states").value(a.getSolver().getAnalysisLatticeElement().getNumberOfStates());
                    w.name("states_per_block").value(((double) a.getSolver().getAnalysisLatticeElement().getNumberOfStates()) / a.getSolver().getFlowGraph().getNumberOfBlocks());
                    w.name("average_state_size").value(((double) progressMonitor.getPreScanMonitor().getStateSize()) / a.getSolver().getAnalysisLatticeElement().getNumberOfStates());
                    w.name("average_node_transfer_time").value(progressMonitor.getPreScanMonitor().getNodeTransfers() != 0 ? ((double) elapsed / progressMonitor.getPreScanMonitor().getNodeTransfers()) : -1);
                    w.name("callgraph_edges").value(a.getSolver().getAnalysisLatticeElement().getCallGraph().getSizeIgnoringContexts());
                    w.name("soundness_tests").value(analysisMonitor.getNumberOfSoundnessChecks());

                    if (throwable == null) {
                        // determinacy paper - precision 1:
                        w.name("percentage_unique_types_readen").value(precisionMonitor.getScanMonitor().getPercentageVariableReadAndPropertyReadWithUniqueTypeContextSensitively());
                        w.name("avg_number_types_pr_read").value(precisionMonitor.getScanMonitor().getAverageNumberTypesAtReadVariableAndPropertyContextSensitively());

                        // determinacy paper - precision 2:
                        w.name("library_object_precision").value(precisionMonitor.getScanMonitor().getPercentagePreciseValuesForInterestingVariableAtExit());

                        // determinacy paper - precision 3:
                        w.name("percentage_unique_callees").value(precisionMonitor.getScanMonitor().getPercentageUniqueCalleesContextSensitively());
                    } else {
                        w.name("percentage_unique_types_readen").value(-1);
                        w.name("avg_number_types_pr_read").value(-1);
                        w.name("library_object_precision").value(-1);
                        w.name("percentage_unique_callees").value(-1);
                    }
                }

                w.endObject();
                TAJSVRExperimentsMain.reset();
                System.gc();
                if (RefinerStatisticsAllTests.isInitialized()) {
                    RefinerStatisticsAllTests.get().endAnalysis();
                    RefinerStatisticsAllTests.reset();
                }
                System.gc();
            }
            w.endArray();
        }
        System.out.println("Output written to " + f + ", open stats.html?" + outputFile + " in a browser to view the results");
    }

//    private static void warmupVM(Path test, OptionValues optionValues, RefinerOptionValues refuterOptions) {
//        long startTime = System.currentTimeMillis();
//
//        while (System.currentTimeMillis() - startTime < 1 * 60 * 1000) {
//            OptionValues options = optionValues;
////            if (test.toString().endsWith(".html")) {
////                optionValues.getSoundnessTesterOptions().setRootDirFromMainDirectory(Paths.get("../.."));
////                options.disableAsyncEvents();
////                options.disableCommonAsyncPolyfill();
////            } else {
////                options.getSoundnessTesterOptions().setRootDirFromMainDirectory(null);
////                options.enableIncludeDom();
////            }
//
//            try {
//                Options.set(options);
//                RefinerOptions.set(refuterOptions);
//                Analysis a = TAJSVRExperimentsMain.init(new String[]{test.toString()}, CompositeMonitoring.buildFromList(Monitoring.make()));
//                if (a == null)
//                    throw new AnalysisException("Error during initialization");
//                TAJSVRExperimentsMain.run(a);
//            } catch (Throwable e) {
//                System.out.println("Error: " + e.getMessage());
//                e.printStackTrace();
//            }
//            TAJSVRExperimentsMain.reset();
//            System.gc();
//        }
//
//        System.out.println("DONE warming up VM");
//    }

    public static class PrecisionMonitor extends PhaseMonitoring<DefaultAnalysisMonitoring, LibraryRunners.PrecisionMonitor.ScanPrecisionMonitor> { // TODO: would be nice to be able to extract information from other monitors...

        public PrecisionMonitor(Optional<String> variableName) {
            super(new DefaultAnalysisMonitoring(), new LibraryRunners.PrecisionMonitor.ScanPrecisionMonitor(variableName));
        }

        public static class ScanPrecisionMonitor extends DefaultAnalysisMonitoring {

            private Solver.SolverInterface c;

            /**
             * Number of call/construct nodes visited context sensitively.
             */
            private int call_nodes_context_sensitively = 0;

            /**
             * Number of reachable read variable nodes context sensitively.
             */
            private int number_read_variables_with_single_type = 0;

            /**
             * Number of reachable read property nodes context sensitively.
             */
            private int number_read_property_with_single_type = 0;

            /**
             * Call/construct nodes that may involve calls to multiple user-defined functions in the same call context (ignoring callee contexts).
             */
            private Set<AbstractNode> call_polymorphic = newSet();

            /**
             * ReadVariableNode/ReadPropertyNode and contexts that yield values of different type.
             */
            private Map<NodeAndContext, Integer> polymorphic_reads_context_sensitive = newMap();

            private Optional<String> variableName;

            public ScanPrecisionMonitor(Optional<String> variableName) {
                this.variableName = variableName;
            }

            /**
             * Returns the average number of types read at readvariable and readproperty nodes context sensitively
             */
            public float getAverageNumberTypesAtReadVariableAndPropertyContextSensitively() {
                float numberPreciseReads = number_read_property_with_single_type + number_read_variables_with_single_type;
                float totalNumberTypes = numberPreciseReads
                        + polymorphic_reads_context_sensitive.values().stream().reduce(0, (a, b) -> a + b);

                float totalNumberReads = numberPreciseReads + polymorphic_reads_context_sensitive.size();
                return totalNumberTypes/totalNumberReads;
            }

            /**
             * Returns the average number of types read at readvariable and readproperty nodes context sensitively
             */
            public float getPercentageVariableReadAndPropertyReadWithUniqueTypeContextSensitively() {
                float numberPreciseReads = number_read_property_with_single_type + number_read_variables_with_single_type;
                float totalNumberReads = numberPreciseReads + polymorphic_reads_context_sensitive.size();

                return (numberPreciseReads/totalNumberReads) * 100;
            }

            /**
             * Returns the percentage of context sensitive callnode visits, which has a single callee
             */
            public float getPercentageUniqueCalleesContextSensitively() {
                float numberCallNodes = call_nodes_context_sensitively;
                float numberPreciseCallNodes = numberCallNodes - call_polymorphic.size();
                return (numberPreciseCallNodes / numberCallNodes) * 100;
            }

            /**
             * get percentage of dead code from execution, that is considered dead by the analysis
             */
            public float getPercentageDeadCodeDetectedAsDeadCode() {
                //TODO
                return -1;
            }

            public float getPercentagePreciseValuesForInterestingVariableAtExit() {
                if (!variableName.isPresent())
                    return -1;
                String varname = variableName.get();
                BasicBlock ordinaryExit = c.getFlowGraph().getMain().getOrdinaryExit();
                Map<Context, State> exitStates = c.getAnalysisLatticeElement().getStates(ordinaryExit);
                State joinedExitStates = null;
                for (State s : exitStates.values()) {
                    if (joinedExitStates == null) {
                        joinedExitStates = s.clone();
                    } else {
                        joinedExitStates.propagate(s, false, false);
                    }
                }
                Value v = UnknownValueResolver.getRealValue(joinedExitStates.readVariableDirect(varname), joinedExitStates);

                AtomicInteger numberPreciseProperties = new AtomicInteger();
                AtomicInteger numberImpreciseProperties = new AtomicInteger();
                c.withState(joinedExitStates, () -> {
                    ObjProperties properties = c.getState().getProperties(v.getObjectLabels(), ObjProperties.PropertyQuery.makeQuery().includeSymbols());
                    for (PKey pkey : properties.getMaybe()) {
                        Value propertyVal = properties.getProperties().get(pkey.toValue());
                        if (propertyVal.isMaybeObject() && propertyVal.getObjectLabels().stream().anyMatch(obj -> obj.getKind() == ObjectLabel.Kind.FUNCTION)) {
                            if (propertyVal.getObjectLabels().size() == 1 && propertyVal.typeSize() == 1) {
                                numberPreciseProperties.getAndIncrement();
                            } else {
                                numberImpreciseProperties.getAndIncrement();
                            }
                        }
                    }
                });

                float totalNumberProperties = numberPreciseProperties.get() + numberImpreciseProperties.get();
                float percentagePreciseProperties = (numberPreciseProperties.get() / totalNumberProperties) * 100;

                return percentagePreciseProperties;
            }

            @Override
            public void setSolverInterface(Solver.SolverInterface c) {
                this.c = c;
            }

            @Override
            public void visitNodeTransferPre(AbstractNode n, State s) {
                if (n instanceof CallNode) {
                    call_nodes_context_sensitively++;
                }
            }

            @Override
            public void visitReadProperty(ReadPropertyNode n, Set<ObjectLabel> objlabels, PKeys propertyname, boolean maybe, State state, Value v, ObjectLabel global_obj) {
                int numberTypes = UnknownValueResolver.getRealValue(v, state).typeSize();
                if (numberTypes <= 1) {
                    number_read_property_with_single_type++;
                } else {
                    polymorphic_reads_context_sensitive.put(new NodeAndContext(n, state.getContext()), numberTypes);
                }
            }

            @Override
            public void visitReadVariable(ReadVariableNode n, Value v, State state) {
                int numberTypes = UnknownValueResolver.getRealValue(v, state).typeSize();
                if (numberTypes <= 1) {
                    number_read_variables_with_single_type++;
                } else {
                    polymorphic_reads_context_sensitive.put(new NodeAndContext(n, state.getContext()), numberTypes);
                }
            }

            @Override
            public void visitCall(AbstractNode n, Value funval) {
                if (funval.getObjectLabels().size() > 1) {
                    call_polymorphic.add(n);
                }
            }
        }
    }
}
