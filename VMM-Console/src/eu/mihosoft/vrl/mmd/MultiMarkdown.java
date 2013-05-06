package eu.mihosoft.vrl.mmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiMarkdown {

    private static File tmpFile;
    private static Process multiMarkdownProcess;
    private static final ReentrantLock lock = new ReentrantLock();
    private static boolean initialized;
    private static final Map<Format, String> formatToStringMap = new HashMap<>();

    static {
        formatToStringMap.clear();
        formatToStringMap.put(Format.html, "html");
        formatToStringMap.put(Format.latex, "latex");
        formatToStringMap.put(Format.memoir, "memoir");
        formatToStringMap.put(Format.beamer, "beamer");
        formatToStringMap.put(Format.odf, "odf");
        formatToStringMap.put(Format.opml, "opml");
    }

    public static void main(String[] args) {

        String output = convert("# Test\nThis is a **Test**.");

        System.out.println(output);
    }

    /**
     * Initializes property folder and executable.
     */
    private static void initialize() {

        // already initialized: we don't do anything
        if (initialized) {
            return;
        }

        // initialize property folders etc
        VRL.init();

        // initialize executable
        String tmpPath = getExecutablePath();

        // initialization was not successful
        if (tmpPath == null || tmpPath.isEmpty()) {
            throw new IllegalStateException("Executable path not initialized!");
        }

        initialized = true;
    }

    /**
     * Converts the specified markdown formatted string to HTML.
     *
     * @param input string to convert
     * @return the converted string or an empty string if the conversion failed
     */
    public static String convert(String input) {

        return convert(input, Format.html);
    }

    /**
     * Converts the specified markdown formatted string to the specified output
     * format.
     *
     * @param input string to convert
     * @param format format
     * @return the converted string or an empty string if the conversion failed
     */
    public static String convert(String input, Format format) {

        initialize();

        try {
            File tmpIn = IOUtil.stringToTmpFile(input);
            File output = new File(IOUtil.createTempDir(), "output.txt");

            convert(tmpIn, output, format);

            List<String> lines = Files.readAllLines(
                    Paths.get(output.toURI()), Charset.forName("UTF-8"));

            StringBuilder builder = new StringBuilder();

            for (String l : lines) {
                builder.append(l).append('\n');
            }

            return builder.toString();

        } catch (IOException ex) {
            Logger.getLogger(MultiMarkdown.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return "";
    }

    /**
     * Converts an input file to the specified format. The result will be
     * written to the given output location.
     *
     * @param input input file to convert
     * @param output output file (will be overwritten if it exists)
     * @param format format, e.g., html, latex, etc.
     *
     * @throws FileNotFoundException if the specified input file does not exist
     * or is no regular file
     * @throws FileNotFoundException if the specified output file does exist and
     * is no regular file
     */
    public static void convert(File input, File output, Format format)
            throws FileNotFoundException {

        initialize();

        if (!input.isFile()) {
            throw new FileNotFoundException(
                    "The specified input file does not exist or is no regular file!");
        }

        if (output.isDirectory()) {
            throw new FileNotFoundException(
                    "The specified output file exist and is no regular file!");
        }

        multimarkdown("-t", formatToArgument(format),
                "-o", output.getAbsolutePath(),
                input.getAbsolutePath());
    }

    /**
     * Converts the specified markdown files to HTML. The result will be written
     * to the same folder where the input files are located.
     * 
     * @param input input files to convert 
     * @throws FileNotFoundException 
     */
    public static void convert(File[] input) throws FileNotFoundException {
        for (File i : input) {

            String oName = i.getName();

            if (oName.toLowerCase().endsWith(".md")) {
                oName = oName.substring(oName.length() - 3) + ".html";
            }

            File o = new File(i.getAbsoluteFile().getParentFile(), oName);

            convert(i, o, Format.html);
        } // end for each input file
    }

    /**
     * Converts the specified format to the appropriate argument string.
     *
     * @param format format to convert
     * @return the specified format to the appropriate argument string or
     * <code>null</code> if no such argument string exists
     */
    private static String formatToArgument(Format format) {
        return formatToStringMap.get(format);
    }

    /**
     * Calls multimarkdown with the specified arguments.
     *
     * @param arguments arguments
     */
    public static void multimarkdown(String... arguments) {

        lock.lock();
        initialize();

        try {

            if (arguments == null || arguments.length == 0) {
                arguments = new String[]{"--help"};
            }

            String[] cmd = new String[arguments.length + 1];

            cmd[0] = getExecutablePath();

            for (int i = 1; i < cmd.length; i++) {
                cmd[i] = arguments[i - 1];
            }
            try {
                multiMarkdownProcess = Runtime.getRuntime().exec(cmd);
                multiMarkdownProcess.waitFor();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(MultiMarkdown.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Destroys the currently running multimarkdown process.
     */
    public static void destroy() {

        lock.lock();
        try {
            if (multiMarkdownProcess != null) {
                multiMarkdownProcess.destroy();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the path to the markdown executable. If the executable has not
     * been initialized this will be done as well.
     *
     * @return the path to the markdown executable
     */
    private static String getExecutablePath() {

        if (!VSysUtil.isOsSupported()) {
            throw new UnsupportedOperationException(
                    "The current OS is not supported: "
                    + System.getProperty("os.name"));
        }

        if (tmpFile == null || !tmpFile.isFile()) {
            try {
                File dir = IOUtil.createTempDir();

                tmpFile = new File(dir, "multimarkdown");

                String resourceName =
                        "/eu/mihosoft/vrl/mmd/resources/"
                        + VSysUtil.getSystemBinaryPath()
                        + "multimarkdown";

                if (VSysUtil.isWindows()) {
                    resourceName += ".exe";
                }

                IOUtil.saveStreamToFile(
                        MultiMarkdown.class.getResourceAsStream(resourceName),
                        tmpFile);

                if (!VSysUtil.isWindows()) {
                    try {
                        Process p = Runtime.getRuntime().exec(new String[]{
                            "chmod", "u+x",
                            tmpFile.getAbsolutePath()
                        });

                        InputStream stderr = p.getErrorStream();

                        BufferedReader reader =
                                new BufferedReader(
                                new InputStreamReader(stderr));

                        String line;

                        while ((line = reader.readLine()) != null) {
                            System.out.println("Error: " + line);
                        }

                        p.waitFor();
                    } catch (IOException | InterruptedException ex) {
                        Logger.getLogger(MultiMarkdown.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(MultiMarkdown.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        return tmpFile.getAbsolutePath();
    }
}
