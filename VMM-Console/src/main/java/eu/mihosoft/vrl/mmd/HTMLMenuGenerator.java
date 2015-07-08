/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd;


import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class HTMLMenuGenerator {

    private String title;
    private String introText;
    private String metaString = "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "    <meta charset=\"utf-8\"/>\n"
            + "    <link type=\"text/css\" rel=\"stylesheet\" href=\"${CSS}\"/>\n"
            + "</head>\n";
    private String cssFile = "resources/css/vrl-documentation.css";
    private Collection<Pair<String, String>> entries =
            new ArrayList<Pair<String, String>>();

    public HTMLMenuGenerator() {
        //
    }

    public HTMLMenuGenerator(String title, String introText) {
        this.title = title;
        this.introText = introText;
    }

    public void addMenuEntry(String text, String link) {
        entries.add(new Pair<String, String>(text, link));
    }

    public String render() {
        String result = metaString.replace("${CSS}", cssFile);

        result += "<body>\n";

        result += "<h1>" + title + "</h1>\n";

        result += "<p>" + introText + "</p>\n";

        result += "<ul>\n";

        for (Pair<String, String> menuEntry : entries) {
            result += "  <li><a href=\"" + menuEntry.getSecond() + "\">"
                    + menuEntry.getFirst()
                    + "</a></li>\n";
        }

        result += "</ul>\n";

        result += "</body>\n";
        
        result += "</html>\n";

        return result;
    }

    /**
     * @return the cssFile
     */
    public String getCssFile() {
        return cssFile;
    }

    /**
     * @param cssFile the cssFile to set
     */
    public void setCssFile(String cssFile) {
        this.cssFile = cssFile;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the introText
     */
    public String getIntroText() {
        return introText;
    }

    /**
     * @param introText the introText to set
     */
    public void setIntroText(String introText) {
        this.introText = introText;
    }
}

class Pair<T, V> {
    private T first;
    private V second;

    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * @return the first
     */
    public T getFirst() {
        return first;
    }

    /**
     * @return the second
     */
    public V getSecond() {
        return second;
    }

}

