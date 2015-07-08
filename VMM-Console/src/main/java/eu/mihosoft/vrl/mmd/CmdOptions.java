/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd;

import java.io.File;
import java.util.List;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CmdOptions {

    @Option(name = "-i", usage = "input folder", required = false)
    private File inputFolder;
    @Option(name = "-title", usage = "Index Title", required = false)
    private String title = "Page Title";
    @Option(name = "-intro", usage = "Index Intro Text", required = false)
    private String intro = "Index";
    @Option(name = "-css", usage = "CSS File Location", required = false)
    private String cssFile = "resources/css/vrl-documentation.css";
    @Option(name = "-merge", usage = "Merge Index Files", required = false)
    private boolean mergeIndexFiles = false;
    @Option(name = "-merge-out", usage = "Location For Merged Index File", required = false)
    private File mergedIndexLocation;
     @Option(name = "-merge-input", usage = "Index Files that shall be merged", required = false)
    private List<File> mergedIndexInputFiles;

    /**
     * @return the inputFolder
     */
    public File getInputFolder() {
        return inputFolder;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the intro
     */
    public String getIntro() {
        return intro;
    }

    /**
     * @return the cssFile
     */
    public String getCssFile() {
        return cssFile;
    }

    /**
     * @return the mergeIndexFiles
     */
    public boolean isMergeIndexFiles() {
        return mergeIndexFiles;
    }

    /**
     * @param mergeIndexFiles the mergeIndexFiles to set
     */
    public void setMergeIndexFiles(boolean mergeIndexFiles) {
        this.mergeIndexFiles = mergeIndexFiles;
    }

    /**
     * @return the mergedIndexLocation
     */
    public File getMergedIndexLocation() {
        return mergedIndexLocation;
    }

    /**
     * @param mergedIndexLocation the mergedIndexLocation to set
     */
    public void setMergedIndexLocation(File mergedIndexLocation) {
        this.mergedIndexLocation = mergedIndexLocation;
    }

    /**
     * @return the mergedIndexInputFiles
     */
    public List<File> getMergedIndexInputFiles() {
        return mergedIndexInputFiles;
    }

    /**
     * @param mergedIndexInputFiles the mergedIndexInputFiles to set
     */
    public void setMergedIndexInputFiles(List<File> mergedIndexInputFiles) {
        this.mergedIndexInputFiles = mergedIndexInputFiles;
    }

}
