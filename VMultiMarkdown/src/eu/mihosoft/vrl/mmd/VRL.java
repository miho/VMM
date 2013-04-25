/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VRL {
    
    private static VPropertyFolderManager propertyFolderManager;
    
    static void init() {
        propertyFolderManager = new VPropertyFolderManager();
        propertyFolderManager.evalueteArgs(new String[0]);
    }

    static VPropertyFolderManager getPropertyFolderManager() {
        return propertyFolderManager;
    }
    
}
