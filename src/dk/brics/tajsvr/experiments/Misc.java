package dk.brics.tajsvr.experiments;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajsvr.TAJSVRExperimentsMain;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Misc {

    private static final Logger log = Logger.getLogger(Misc.class);

    public static void run(String[] args, IAnalysisMonitoring monitoring) throws AnalysisException {
        try {
            Analysis a = TAJSVRExperimentsMain.init(args, monitoring);
            if (a == null)
                throw new AnalysisException("Error during initialization");
            TAJSVRExperimentsMain.run(a);
            TAJSVRExperimentsMain.reset();
        } catch (AnalysisException e) {
            log.info(e.getMessage());
            throw e;
        }
    }

    public static void runSource(String... src) {
        runSource(src, null);
    }

    public static void runSource(String[] source, IAnalysisMonitoring monitoring) {
        try {
            runSourceWithFile(File.createTempFile("temp-source-file", ".js"), source, monitoring);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runSourceWithFile(File file, String[] src, IAnalysisMonitoring monitoring) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            sb.append(src[i] + "\n");
        }
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String[] args = new String[]{file.getPath()};
        run(args, monitoring);
        file.deleteOnExit();
    }
}
