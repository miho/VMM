package eu.mihosoft.vrl.mmd;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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

        if (options.isMergeIndexFiles()) {
            mergeIndexFiles(parser);
        } else {
            convertFiles(parser);
        }


    }

    private static void mergeIndexFiles(CmdLineParser parser) {
        if (options.getMergedIndexLocation() == null
                || !options.getMergedIndexLocation().isDirectory()) {
            System.err.println(">> Cannot merge input files: merge output is no directory!");
        }

        List<String> bodies = new ArrayList<>();

        for (File f : options.getMergedIndexInputFiles()) {
            
            String folderName = f.getName();

            System.out.println(" f: " + f);

            f = new File(f, "index.html");

            if (!f.isFile()) {
                System.err.println(" --> skipping \"" + f + "\": file does not exist!");
                continue;
            }

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(f);
                Node body = document.getElementsByTagName("body").item(0);

                if (body != null) {
                    try {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        TransformerFactory.newInstance().newTransformer().transform(
                                new javax.xml.transform.dom.DOMSource(body),
                                new javax.xml.transform.stream.StreamResult(outStream));
                        bodies.add(outStream.toString("UTF-8").replace("href=\"","href=\""+folderName+"/"));
                    } catch (TransformerException ex) {
                        Logger.getLogger(VMMConsole.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    System.err.println(" --> skipping \"" + f + "\": body element does not exist!");
                }

            } catch (ParserConfigurationException | SAXException | IOException ex) {
                System.err.println(" --> skipping \"" + f + "\": IO error!");
                Logger.getLogger(VMMConsole.class.getName()).log(Level.SEVERE, null, ex);
            }

        } // end for each input file

        String metaString = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "    <meta charset=\"utf-8\"/>\n"
                + "    <link type=\"text/css\" rel=\"stylesheet\" href=\"${CSS}\"/>\n"
                + "</head>\n";

        String cssFile = options.getCssFile();

        String mergedDocument = metaString.replace("${CSS}", cssFile);

        mergedDocument += "<body>\n";

        for (String b : bodies) {
            mergedDocument += "    " + b + "\n<hr>\n";
        }

        mergedDocument += "</body>\n";
        try {
            writeStringToFile(new File(options.getMergedIndexLocation(), "index.html"), mergedDocument);
        } catch (IOException ex) {
            System.err.println(" >> Cannot merge index files: ");
            Logger.getLogger(VMMConsole.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void convertFiles(CmdLineParser parser) {
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

        generateIndex();

        if (options.getMergedIndexLocation() == null
                || !options.getMergedIndexLocation().isDirectory()) {
            System.err.println(">> Cannot merge files due to invalid -merge-out option: directory expected");
            printUsage(parser);
            System.exit(1);
        }
    }

    private static void generateIndex() {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getAbsolutePath().toLowerCase().endsWith(".html");
            }
        };

        File indexFileName = new File(options.getInputFolder(), "index.html");

        HTMLMenuGenerator menuGenerator = new HTMLMenuGenerator(options.getTitle(),
                options.getIntro());

        menuGenerator.setCssFile(options.getCssFile());

        System.out.println(">> creating " + indexFileName);

        List<File> files = Arrays.asList(options.getInputFolder().listFiles(filter));

        List<Pair<Double, File>> sortedFiles = new ArrayList<>();

        for (File f : files) {
            double index = -1; // unsorted

            // index is added from us and won't be part of the menu/list
            if (f.getName().equals("index.html")) {
                continue;
            }

            boolean found = false;

            try {
                for (String l : Files.readAllLines(Paths.get(f.getAbsolutePath()), Charset.forName("UTF-8"))) {

                    int strPos = l.indexOf("<!--VMM-INDEX=");

                    found = strPos >= 0;

                    if (found) {
                        l = l.substring(strPos + "<!--VMM-INDEX=".length());
                        strPos = l.indexOf("-->");

                        if (strPos < 0) {
                            System.err.println("ERROR: '-->' missing");
                            System.err.println(" --> please add a line '<!--VMM-HELP-INDEX=n-->' for n := Integer[0,MAX_INT], to the file.");
                            System.err.println(" --> file: " + f.getName());
                            continue;
                        }

                        l = l.substring(0, strPos);

                        if (l.isEmpty()) {
                            System.err.println("ERROR: number after '=' is missing");
                            System.err.println(" --> please add a line '<!--VMM-HELP-INDEX=n-->' for n := Integer[0,MAX_INT], to the file.");
                            System.err.println(" --> file: " + f.getName());
                            continue;
                        }

                        try {
                            index = Double.parseDouble(l);
                        } catch (NumberFormatException ex) {
                            System.err.println("ERROR: number after '=' is malformed: " + l);
                            System.err.println(" --> please add a line '<!--VMM-HELP-INDEX=n-->' for n := Integer[0,MAX_INT], to the file.");
                            System.err.println(" --> file: " + f.getName());
                            continue;
                        }

                        break;
                    }
                }

                sortedFiles.add(new Pair<>(index, f));

                if (!found) {
                    System.err.println("ERROR: Index not found:");
                    System.out.println(" --> please add a line '<!--VMM-HELP-INDEX=n-->' for n := Integer[0,MAX_INT], to the file.");
                    System.err.println(" --> file: " + f.getName());
                    continue;
                }
            } catch (IOException ex) {
                Logger.getLogger(VMMConsole.class.getName()).log(Level.SEVERE, null, ex);
            }

//            System.out.println("INDEX: " + index + ", file: " + f.getName());


        }

        // sort entries as defined by VMM-Index
        Collections.sort(sortedFiles, new Comparator<Pair<Double, File>>() {
            @Override
            public int compare(Pair<Double, File> o1, Pair<Double, File> o2) {
                return o1.getFirst().compareTo(o2.getFirst());
            }
        });

        for (Pair<Double, File> sF : sortedFiles) {

            File f = sF.getSecond();

            // index is added from us and won't be part of the menu/list
            if (f.getName().equals("index.html")) {
                continue;
            }

            String name = f.getName();

            name = name.toLowerCase().replace("vrl-studio", "vrlstudio");

            String[] tokens = name.split("-");

            name = "";

            for (String t : tokens) {

                t = t.replace("vrlstudio", "VRL-Studio");
                t = t.replace("vrl", "VRL");

                name += t.substring(0, 1).toUpperCase() + t.substring(1) + " ";
            }

            name = name.replace(".html", "").trim();

            if (name.startsWith("What Is") || name.startsWith("How To")) {
                name = name + "?";

                name = name.replace("??", "?");
            }

            menuGenerator.addMenuEntry(name, f.getName());

            System.out.println(" --> entry-name: " + name);
        }

        String indexFile = menuGenerator.render();

        try {
            System.out.println(">> writing " + indexFileName);
            writeStringToFile(indexFileName, indexFile);
        } catch (IOException ex) {
            Logger.getLogger(VMMConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void writeStringToFile(File indexFileName, String indexFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFileName))) {
            writer.write(indexFile);
            writer.flush();
        }
    }

    private static void printUsage(CmdLineParser parser) {
        parser.printUsage(System.err);
    }
}
