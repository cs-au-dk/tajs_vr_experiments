package dk.brics.tajsvr.experiments.libraryexperiments;

import java.util.Optional;

public enum LibraryExperiments {
    UNDERSCORE("Underscore", 182, "underscore", new LibraryRunners.UnderscoreValueRefiner(), Optional.of(4)),
    LODASH3("Lodash3", 176, "lodash3", new LibraryRunners.Lodash3ValueRefiner(), Optional.of(5)),
    LODASH4("Lodash4", 306, "lodash4", new LibraryRunners.Lodash4ValueRefiner(), Optional.of(6)),
    PROTOTYPE("Prototype", 6, "prototype", new LibraryRunners.PrototypeValueRefiner(), Optional.of(7)),
    SCRIPTACULOUS("Scriptaculous", 1, "scriptaculous", new LibraryRunners.ScriptaculousValueRefiner(), Optional.of(8)),
    JQUERY("JQuery", 71, "jQuery", new LibraryRunners.jQueryValueRefiner(), Optional.of(9)),
    JSAI("JSAI tests", 29, "JSAI", new LibraryRunners.JSAIValueRefiner(), Optional.empty());

    private final String benchmarkName;
    private final int numberOfTests;
    private final String statsName;
    private final LibraryRunners.LibraryBenchmarkExperimentRunner testRunner;
    private final Optional<Integer> tableNumberInPaper;
    private double totalAnalysisTime;
    private double totalRefinementTime;

    LibraryExperiments(String benchmarkName, int numberOfTests, String statsName, LibraryRunners.LibraryBenchmarkExperimentRunner testRunner, Optional<Integer> tableNumberInPaper) {
        this.benchmarkName = benchmarkName;
        this.numberOfTests = numberOfTests;
        this.statsName = statsName;
        this.testRunner = testRunner;
        this.tableNumberInPaper = tableNumberInPaper;
    }

    public String getBenchmarkName() {
        return benchmarkName;
    }

    public int getNumberOfTests() {
        return numberOfTests;
    }

    public String getStatsName() {
        return statsName;
    }

    public void runTests() throws Exception {
        testRunner.runTests();
    }

    public Optional<Integer> getTableNumberInPaper() {
        return tableNumberInPaper;
    }

    public double getTotalAnalysisTime() {
        return totalAnalysisTime;
    }

    public void setTotalAnalysisTime(double totalAnalysisTime) {
        this.totalAnalysisTime = totalAnalysisTime;
    }

    public double getTotalRefinementTime() {
        return totalRefinementTime;
    }

    public void setTotalRefinementTime(double totalRefinementTime) {
        this.totalRefinementTime = totalRefinementTime;
    }
}
