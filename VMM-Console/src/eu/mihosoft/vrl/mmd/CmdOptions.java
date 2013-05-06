/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd;

import java.io.File;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CmdOptions {
    @Option(name = "-i", usage = "input folder", required = true)
    private File inputFolder;

    /**
     * @return the inputFolder
     */
    public File getInputFolder() {
        return inputFolder;
    }
}
