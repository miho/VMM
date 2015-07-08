/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd.editor;

import eu.mihosoft.vrl.mmd.IOUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLStreamHandler;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class RewriteDocumentLocationHandler extends URLStreamHandler {

    private File documentFile;
    private String content;
    private File contentTmpLocation;
    private File tmpContentFile;

    public RewriteDocumentLocationHandler() {
        documentFile = new File("./");
    }

    public RewriteDocumentLocationHandler(File contentRoot) {
        this.documentFile = contentRoot;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {

        File fileFromURL = new File(u.getFile());

        if (fileFromURL.equals(getDocumentFile())) {
//            System.out.println("REWRITE");
//            System.out.println(" ->from: " + getDocumentFile());
//            System.out.println(" ->  to: " + getTmpContentFile());
            fileFromURL = getTmpContentFile();
        }
        
        final File finalFName = fileFromURL;

        return new URLConnection(u) {

            @Override
            public void connect() throws IOException {

            }

            @Override
            public InputStream getInputStream() throws FileNotFoundException {
                return new BufferedInputStream(new FileInputStream(finalFName));
            }
        };

    }

    /**
     * @return the contentRoot
     */
    public File getDocumentFile() {
        return documentFile;
    }

    /**
     * @param documentFile the contentRoot to set
     */
    public void setDocumentFile(File documentFile) {
        this.documentFile = documentFile;
    }

    void setTmpContentFile(File tmpContentFile) {
        this.tmpContentFile = tmpContentFile;
    }

    /**
     * @return the tmpContentFile
     */
    public File getTmpContentFile() {
        return tmpContentFile;
    }

}
