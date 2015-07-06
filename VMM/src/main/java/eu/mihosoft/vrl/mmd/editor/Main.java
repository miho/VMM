package eu.mihosoft.vrl.mmd.editor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Main extends Application {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    private MainWindowController controller;

    @Override
    public void start(Stage primaryStage) {

        showEditor(primaryStage, "VMM - Editor " + Constants.VMM_VERSION);

    }

    public static void showEditor(String editorTitle) {
        JFXPanel panel = new JFXPanel();
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            Main main = new Main();
            Stage primaryStage = new Stage();
            main.showEditor(primaryStage, editorTitle);
        });

    }

    private void showEditor(Stage primaryStage, String title) {
        ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().
                getResource("MainWindow.fxml"));

        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        Thread.currentThread().setContextClassLoader(ctxLoader);

        controller = fxmlLoader.getController();

        Scene scene = new Scene((Parent) fxmlLoader.getRoot(), 1200, 800);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();

        controller.setWindow(primaryStage);
    }

}
