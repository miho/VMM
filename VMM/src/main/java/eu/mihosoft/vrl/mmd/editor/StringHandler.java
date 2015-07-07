/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd.editor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class StringHandler extends URLStreamHandler {

    private File contentRoot;
    private String content;

    public StringHandler() {
        contentRoot = new File("./");
    }

    public StringHandler(File contentRoot) {
        this.contentRoot = contentRoot;
    }

    public StringHandler(File contentRoot, String content) {
        this.contentRoot = contentRoot;
        this.content = content;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {

        if (u.getPath().equals("content")) {
//            URL contentURL = ;
//            URLConnection connection = contentURL.openConnection();
//            connection.setDoOutput(true);
//
//            try (OutputStreamWriter out = new OutputStreamWriter(
//                    connection.getOutputStream())) {
//                out.write(content);
//            }
//            
//            return contentURL.openConnection();
        }

        URL resourceUrl = new URL(
                String.format("file:///%s/%s",
                        getContentRoot().getCanonicalPath(),
                        u.getPath()
                ));
        if (resourceUrl == null) {
            throw new IOException("Resource not found: " + u);
        }

        return resourceUrl.openConnection();
    }

    /**
     * @return the contentRoot
     */
    public File getContentRoot() {
        return contentRoot;
    }

    /**
     * @param contentRoot the contentRoot to set
     */
    public void setContentRoot(File contentRoot) {
        this.contentRoot = contentRoot;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
}
