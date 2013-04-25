/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd.editor;

import eu.mihosoft.vrl.mmd.Format;
import eu.mihosoft.vrl.mmd.MultiMarkdown;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooserBuilder;
import javafx.stage.Window;

/**
 * FXML Controller class
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MainWindowController implements Initializable {

    @FXML
    TextArea editor;
    @FXML
    WebView outputView;
    private Window window;
    private File currentDocument;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //
    }

    @FXML
    public void onKeyTyped(KeyEvent evt) {
//        String output = editor.getText();
//
//        output = MultiMarkdown.convert(output);
//
//        System.out.println(output);
//
//        
//
//        URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
//
//            @Override
//            public URLStreamHandler createURLStreamHandler(String protocol) {
//                
//            }
//        });
//        
//        
//        outputView.getEngine().s
    }

    private void updateMarkdown() {
        if (currentDocument != null) {
            try {

                String outputFilename = currentDocument.getName();

                if (outputFilename.endsWith(".md")) {
                    outputFilename = "." + outputFilename.substring(0, outputFilename.length() - 3) + ".html";
                }

                File outputDocument = new File(currentDocument.getAbsoluteFile().getParent(), outputFilename);
                MultiMarkdown.convert(currentDocument, outputDocument, Format.html);

                try {
                    outputView.getEngine().load(outputDocument.toURI().toURL().toExternalForm());
                } catch (MalformedURLException ex) {
                    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void exportHTML(File f) {
        if (currentDocument != null) {
            try {

                String outputFilename = currentDocument.getName();

                if (outputFilename.endsWith(".md")) {
                    outputFilename = "." + outputFilename.substring(0, outputFilename.length() - 3) + ".html";
                }

                File outputDocument = new File(currentDocument.getAbsoluteFile().getParent(), outputFilename);
                MultiMarkdown.convert(currentDocument, outputDocument, Format.html);

                try {
                    outputView.getEngine().load(outputDocument.toURI().toURL().toExternalForm());
                } catch (MalformedURLException ex) {
                    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    public void onLoadAction(ActionEvent e) {
        try {
            FileChooser.ExtensionFilter mdFilter =
                    new FileChooser.ExtensionFilter("Text Files (*.md, *.txt)", "*.md", "*.txt");

            FileChooser.ExtensionFilter allFilesfilter =
                    new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

            currentDocument =
                    FileChooserBuilder.create().title("Open Markdown File").
                    extensionFilters(mdFilter, allFilesfilter).build().
                    showOpenDialog(window).getAbsoluteFile();

            List<String> lines =
                    Files.readAllLines(Paths.get(currentDocument.getAbsolutePath()),
                    Charset.forName("UTF-8"));

            String document = "";

            for (String l : lines) {
                document += l + "\n";
            }

            editor.setText(document);

            updateMarkdown();

        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void onSaveAction(ActionEvent e) {

        if (currentDocument == null) {
            FileChooser.ExtensionFilter mdFilter =
                    new FileChooser.ExtensionFilter("Text Files (*.md, *.txt)", "*.md", "*.txt");

            FileChooser.ExtensionFilter allFilesfilter =
                    new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

            currentDocument =
                    FileChooserBuilder.create().title("Open Markdown File").
                    extensionFilters(mdFilter, allFilesfilter).build().
                    showSaveDialog(window).getAbsoluteFile();
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(currentDocument);
            fileWriter.write(editor.getText());
            fileWriter.close();
            updateMarkdown();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    public void onExportHtmlAction(ActionEvent e) {
    }

    @FXML
    public void onCloseAction(ActionEvent e) {
    }
}
