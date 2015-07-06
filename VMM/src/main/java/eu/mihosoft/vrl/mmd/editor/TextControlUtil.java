/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd.editor;

import com.sun.javafx.scene.control.behavior.TextInputControlBehavior;
import com.sun.javafx.scene.control.skin.TextInputControlSkin;
import eu.mihosoft.vrl.mmd.VSysUtil;
import java.nio.file.WatchEvent.Modifier;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class TextControlUtil {

    private static void undo(TextInputControl control) {
        ((TextInputControlBehavior) ((TextInputControlSkin) control.getSkin()).getBehavior()).callAction("Undo");
    }

    private static void redo(TextInputControl control) {
        ((TextInputControlBehavior) ((TextInputControlSkin) control.getSkin()).getBehavior()).callAction("Redo");
    }

    private static void copy(TextInputControl control) {
        control.copy();
    }

    private static void paste(TextInputControl control) {
        control.paste();
    }

    private static void cut(TextInputControl control) {
        control.cut();
    }
    
    public static KeyCombination.Modifier getSystemControlModifier() {
        KeyCombination.Modifier controlOrMeta = KeyCombination.CONTROL_DOWN;
        
        if (VSysUtil.isMacOSX()) {
            controlOrMeta = KeyCombination.META_DOWN;
        }
        
        return controlOrMeta;
    }

    public static void addShortcutsToContextMenu(ContextMenu contextMenu) {
        
        KeyCombination.Modifier controlOrMeta = KeyCombination.CONTROL_DOWN;
        
        if (VSysUtil.isMacOSX()) {
            controlOrMeta = KeyCombination.META_DOWN;
        }
        
        MenuItem undoItem = contextMenu.getItems().get(0);
        MenuItem redoItem = contextMenu.getItems().get(1);
        MenuItem cutItem = contextMenu.getItems().get(2);
        MenuItem copyItem = contextMenu.getItems().get(3);
        MenuItem pasteItem = contextMenu.getItems().get(4);
        MenuItem deleteItem = contextMenu.getItems().get(5);
        MenuItem selectAllItem = contextMenu.getItems().get(6);
        
        undoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, controlOrMeta));
        redoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, controlOrMeta, KeyCombination.SHIFT_DOWN));
        
        cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, controlOrMeta));
        copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, controlOrMeta));
        pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, controlOrMeta));
        deleteItem.setAccelerator(new KeyCodeCombination(KeyCode.D, controlOrMeta));
        selectAllItem.setAccelerator(new KeyCodeCombination(KeyCode.A, controlOrMeta));
        
    }
}
