/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd.editor;

import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
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
    @FXML
    private MenuItem loadItem;
    @FXML
    private MenuItem saveItem;
    @FXML
    private MenuItem saveAsItem;
    @FXML
    private MenuItem exportItem;
    @FXML
    private MenuItem closeItem;
    @FXML
    private MenuItem insertImageItem;

    private double prevScrollLoc = 0;
    private boolean scrollEventHadAnEffect;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMenu();

        editor.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });

        editor.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    String filePath = null;
                    for (File file : db.getFiles()) {
                        filePath = file.getAbsolutePath();

                        if (filePath.toLowerCase().endsWith(".jpg")
                                || filePath.toLowerCase().endsWith(".png")) {
                            insertImage(new File(filePath));
                        } else if (filePath.toLowerCase().endsWith(".md")
                                || filePath.toLowerCase().endsWith(".txt")) {
                            loadTextFile(file);
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        outputView.getEngine().getLoadWorker().stateProperty().
                addListener((ov, oldV, newV) -> {
                    if (newV == State.SUCCEEDED) {
                        setVScrollBarLocation(outputView, prevScrollLoc);
                    }
                });

//        outputView.setOnScrollStarted((evt) -> {
//            prevScrollLoc = 0;
//        });
        ScrollBar sb = getScrollBar(outputView);

//        if (sb != null) {
//            sb.valueProperty().addListener((ov, oldV, newV) -> {
//                scrollEventHadAnEffect = true;
//            });
//        }
        outputView.setOnScroll((evt) -> {

            if (sb.getValue() > sb.getMin() + 50
                    && sb.getValue() < sb.getMax() - 50) {
                prevScrollLoc += evt.getDeltaY();
            }
        });

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

    private ScrollBar getScrollBar(WebView webView) {

        Set<Node> scrolls = webView.lookupAll(".scroll-bar");
        for (Node scrollNode : scrolls) {

            if (ScrollBar.class.isInstance(scrollNode)) {
                ScrollBar scroll = (ScrollBar) scrollNode;
                return scroll;
            }
        }
        return null;
    }

    private double getScrollBarLocation(WebView webView) {

        Set<Node> scrolls = webView.lookupAll(".scroll-bar");
        for (Node scrollNode : scrolls) {

            if (ScrollBar.class.isInstance(scrollNode)) {
                ScrollBar scroll = (ScrollBar) scrollNode;
                return scroll.getValue();
            }
        }

        return -1;
    }

    private double getScrollBarLocationMin(WebView webView) {

        Set<Node> scrolls = webView.lookupAll(".scroll-bar");
        for (Node scrollNode : scrolls) {

            if (ScrollBar.class.isInstance(scrollNode)) {
                ScrollBar scroll = (ScrollBar) scrollNode;
                return scroll.getMin();
            }
        }

        return -1;
    }

    private double getScrollBarLocationMax(WebView webView) {

        Set<Node> scrolls = webView.lookupAll(".scroll-bar");
        for (Node scrollNode : scrolls) {

            if (ScrollBar.class.isInstance(scrollNode)) {
                ScrollBar scroll = (ScrollBar) scrollNode;
                return scroll.getMax();
            }
        }

        return -1;
    }

    private void setVScrollBarLocation(WebView webView, double loc) {

//        System.out.println("-- set: " + loc);
//
//        Set<Node> scrolls = webView.lookupAll(".scroll-bar");
//        for (Node scrollNode : scrolls) {
//
//            if (ScrollBar.class.isInstance(scrollNode)) {
//                System.out.println("---- new: " + scrollNode);
//                ScrollBar scroll = (ScrollBar) scrollNode;
//                if (scroll.getOrientation() == Orientation.VERTICAL) {
//                    System.out.println("set-val: " + loc + ", get-val: " + scroll.getValue() + ", max: " + scroll.getMax());
//
//                    scroll.setValue(loc);
//                }
//            }
//        }
        sendScrollEvent(webView.getScene(), webView, 0, loc);
    }

    private void exportAsHTML() {
        if (currentDocument != null) {
            try {

                FileChooser.ExtensionFilter htmlFilter
                        = new FileChooser.ExtensionFilter("HTML Files (*.html)", "*.html");

                FileChooser.ExtensionFilter allFilesfilter
                        = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

                File outputDocument
                        = FileChooserBuilder.create().title("Save HTML File").
                        extensionFilters(htmlFilter, allFilesfilter).build().
                        showSaveDialog(window).getAbsoluteFile();

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

    private void exportAsODF() {
        if (currentDocument != null) {
            try {

                FileChooser.ExtensionFilter htmlFilter
                        = new FileChooser.ExtensionFilter("HTML Files (*.odf)", "*.odf");

                FileChooser.ExtensionFilter allFilesfilter
                        = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

                File outputDocument
                        = FileChooserBuilder.create().title("Save ODF File").
                        extensionFilters(htmlFilter, allFilesfilter).build().
                        showSaveDialog(window).getAbsoluteFile();

                MultiMarkdown.convert(currentDocument, outputDocument, Format.odf);

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
        loadTextFile(null);
    }

    @FXML
    public void onSaveAction(ActionEvent e) {
        saveDocument(false);
    }

    private void saveDocument(boolean askForLocationIfAlreadyOpened) {

        if (askForLocationIfAlreadyOpened || currentDocument == null) {
            FileChooser.ExtensionFilter mdFilter
                    = new FileChooser.ExtensionFilter("Text Files (*.md, *.txt)", "*.md", "*.txt");

            FileChooser.ExtensionFilter allFilesfilter
                    = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

            currentDocument
                    = FileChooserBuilder.create().title("Save Markdown File").
                    extensionFilters(mdFilter, allFilesfilter).build().
                    showSaveDialog(window).getAbsoluteFile();
        }

        try (FileWriter fileWriter = new FileWriter(currentDocument)) {
            fileWriter.write(editor.getText());
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        updateMarkdown();
    }

    private void insertImage(File img) {

        if (img == null) {
            FileChooser.ExtensionFilter imgFilter
                    = new FileChooser.ExtensionFilter(
                            "Image Files (*.png, *.jpg)", "*.png", "*.jpg");

            FileChooser.ExtensionFilter allFilesfilter
                    = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

            img
                    = FileChooserBuilder.create().title("Choose Image File").
                    extensionFilters(imgFilter, allFilesfilter).build().
                    showOpenDialog(window).getAbsoluteFile();
        }

        String imgText
                = "![IMG_NAME][]\n"
                + "\n"
                + "[IMG_NAME]: " + img.getAbsolutePath() + " width=450px";

        insertStringAtCurrentPosition(imgText);
    }

    private void insertStringAtCurrentPosition(String s) {
        editor.insertText(editor.getCaretPosition(), s);
    }

    @FXML
    public void onInsertImageAction(ActionEvent e) {
        insertImage(null);
    }

    @FXML
    public void onSaveAsAction(ActionEvent e) {
        saveDocument(true);
    }

    @FXML
    public void onExportHtmlAction(ActionEvent e) {
        exportAsHTML();
    }

    @FXML
    public void onCloseAction(ActionEvent e) {
    }

    /**
     * @param window the window to set
     */
    public void setWindow(Window window) {
        this.window = window;
    }

    private void initMenu() {
        loadItem.setAccelerator(new KeyCodeCombination(KeyCode.O, TextControlUtil.getSystemControlModifier()));
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, TextControlUtil.getSystemControlModifier()));
        saveAsItem.setAccelerator(new KeyCodeCombination(
                KeyCode.S, KeyCodeCombination.SHIFT_DOWN, TextControlUtil.getSystemControlModifier()));
        exportItem.setAccelerator(new KeyCodeCombination(KeyCode.E, TextControlUtil.getSystemControlModifier()));
        closeItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, TextControlUtil.getSystemControlModifier()));
        insertImageItem.setAccelerator(new KeyCodeCombination(KeyCode.I,
                KeyCodeCombination.SHIFT_DOWN, TextControlUtil.getSystemControlModifier()));
    }

    private void loadTextFile(File f) {

        try {
            if (f == null) {
                FileChooser.ExtensionFilter mdFilter
                        = new FileChooser.ExtensionFilter("Text Files (*.md, *.txt)", "*.md", "*.txt");

                FileChooser.ExtensionFilter allFilesfilter
                        = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

                currentDocument
                        = FileChooserBuilder.create().title("Open Markdown File").
                        extensionFilters(mdFilter, allFilesfilter).build().
                        showOpenDialog(window).getAbsoluteFile();
            } else {
                currentDocument = f;
            }

            List<String> lines
                    = Files.readAllLines(Paths.get(currentDocument.getAbsolutePath()),
                            Charset.defaultCharset());

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
    public void onTestAction(ActionEvent e) {
        setVScrollBarLocation(outputView, 2000);
    }

    /**
     * Send ScrollEvent in the center of the control
     *
     * @param scene scene
     * @param node node
     * @param scrollX Number of pixels to scroll by x coordinate
     * @param scrollY Number of pixels to scroll by y coordinate
     */
    protected static void sendScrollEvent(final Scene scene, Node node, double scrollX, double scrollY) {
        double x = node.getLayoutBounds().getWidth() / 4;
        double y = node.getLayoutBounds().getHeight() / 4;
        sendScrollEvent(scene, node, scrollX, scrollY, ScrollEvent.HorizontalTextScrollUnits.NONE, scrollX, ScrollEvent.VerticalTextScrollUnits.NONE, scrollY, x, y, node.getLayoutBounds().getMinX() + x, node.getLayoutBounds().getMinY() + y);
    }

    protected static void sendScrollEvent(final Scene scene, final Node node,
            double _scrollX, double _scrollY,
            ScrollEvent.HorizontalTextScrollUnits _scrollTextXUnits, double _scrollTextX,
            ScrollEvent.VerticalTextScrollUnits _scrollTextYUnits, double _scrollTextY,
            double _x, double _y,
            double _screenX, double _screenY) {
    //For 2.1.0 :
        //final ScrollEvent scrollEvent = ScrollEvent.impl_scrollEvent(_scrollX, _scrollY, _scrollTextXUnits, _scrollTextX, _scrollTextYUnits, _scrollTextY, _x, _y, _screenX, _screenY, false, false, false, false);
        //For 2.2.0 :
        //Interpretation: EventType<ScrollEvent> eventType, double _scrollX, double _scrollY, double _totalScrollX, double _totalScrollY, HorizontalTextScrollUnits _scrollTextXUnits, double _scrollTextX, VerticalTextScrollUnits _scrollTextYUnits, double _scrollTextY, int _touchPoints, double _x, double _y, double _screenX, double _screenY, boolean _shiftDown, boolean _controlDown, boolean _altDown, boolean _metaDown, boolean _direct, boolean _inertia)
        //For 8.0 before b64 and RT-9383
        //final ScrollEvent scrollEvent = new ScrollEvent.impl_scrollEvent(ScrollEvent.SCROLL, _scrollX, _scrollY, _scrollX, _scrollY, _scrollTextXUnits, _scrollTextX, _scrollTextYUnits, _scrollTextY, 0, _x, _y, _screenX, _screenY, false, false, false, false, false, false);

        //new ScrollEvent(EventType<ScrollEvent> eventType, 
        //double x, double y, double screenX, double screenY, 
        //boolean shiftDown, boolean controlDown, boolean altDown, boolean metaDown, 
        //boolean direct, boolean inertia, double deltaX, double deltaY, double gestureDeltaX, double gestureDeltaY, 
        //ScrollEvent.HorizontalTextScrollUnits textDeltaXUnits, double textDeltaX, 
        //ScrollEvent.VerticalTextScrollUnits textDeltaYUnits, double textDeltaY, int touchCount)
        final ScrollEvent scrollEvent = new ScrollEvent(ScrollEvent.SCROLL,
                _x, _y, _screenX, _screenY,
                false, false, false, false,
                false, false, _scrollX, _scrollY, 0, 0,
                _scrollTextXUnits, _scrollTextX,
                _scrollTextYUnits, _scrollTextY, 0, null /* PickResult?*/);

        Point2D pointOnScene = node.localToScene(node.getLayoutBounds().getWidth() / 4, node.getLayoutBounds().getHeight() / 4);

        node.fireEvent(scrollEvent);

//        final PickResultChooser result = new PickResultChooser();
//        scene.getRoot().impl_pickNode(new PickRay(pointOnScene.getX(), pointOnScene.getY(), 0, 0, 100), result);
//        Node nodeToSendEvent = result.getIntersectedNode();
//        nodeToSendEvent.fireEvent(scrollEvent);
    }
}
