package eu.mihosoft.vrl.mmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class VMMConsole {

    private static CmdOptions options;

    public static void main(String[] args) {
        
        System.out.println("---------------------------------------------------------");
        System.out.println("VMM-Console");
        System.out.println("Copyright " + Constants.YEARS + " by " + Constants.AUTHOR);
        System.out.println("Webbage: " + Constants.WEBPAGE);
        System.out.println("Version: " + Constants.VERSION);
        System.out.println("OS     : " + VSysUtil.getPlatformInfo());
        System.out.println("Java   : " + System.getProperty("java.version"));
        System.out.println("---------------------------------------------------------");

        options = new CmdOptions();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            Logger.getLogger(VMMConsole.class.getName()).log(Level.SEVERE, null, e);
            System.err.println(e.getMessage());
            printUsage(parser);
            return;
        }

        boolean wrongOptions = false;

        if (!options.getInputFolder().isDirectory()) {
            String msg = "-i: specified value is no directory: "
                    + options.getInputFolder();
            Logger.getLogger(VMMConsole.class.getName()).log(Level.SEVERE, msg);
            printUsage(parser);
            wrongOptions = true;
        }

        // wrong options specified, see program output for reasons
        if (wrongOptions) {
            System.exit(1);
        }
        try {
            MultiMarkdown.convert(options.getInputFolder().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.toLowerCase().endsWith(".md")) {
                        System.out.println(">> converting: " + name);
                        return true;
                    } else {
                        return false;
                    }
                }
            }));
        } catch (FileNotFoundException ex) {
            System.err.println(">> Cannot convert files due to I/O error:");
            Logger.getLogger(VMMConsole.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

    }

    private static void printUsage(CmdLineParser parser) {
        parser.printUsage(System.err);
    }
}
