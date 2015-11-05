/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.mmd.editor;

import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginDependency;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;
import eu.mihosoft.vrl.visual.ActionDelegator;
import eu.mihosoft.vrl.visual.VAction;
import java.awt.event.ActionEvent;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VMMPluginConfigurator extends VPluginConfigurator { 

    public VMMPluginConfigurator() {
        //specify the plugin name and version
        setIdentifier(new PluginIdentifier("VMM-Plugin", Constants.VMM_VERSION));

        // optionally allow other plugins to use the api of this plugin
        // you can specify packages that shall be
        // exported by using the exportPackage() method:
        //
        // exportPackage("com.your.package");
        exportPackage("eu.mihosoft.vrl.mmd");
        
        
        // describe the plugin
        setDescription("MultiMarkdown Editor.");

        // copyright info
        setCopyrightInfo("VMM-Plugin",
                "(c) Michael Hoffer",
                "www.mihosoft.eu", "LGPL",
                "For author and license info see VRL-Plugin");

        // specify dependencies
        addDependency(new PluginDependency("VRL", "0.4.3.0.1", "0.4.x"));

        setAutomaticallySelected(true);
        setRelevantForPersistence(false);

    }

    @Override
    public void register(PluginAPI api) {

        // register plugin with canvas
        if (api instanceof VPluginAPI) {
            VPluginAPI vapi = (VPluginAPI) api;

            // Register visual components:
            //
            // Here you can add additional components,
            // type representations, styles etc.
            //
            // ** NOTE **
            //
            // To ensure compatibility with future versions of VRL,
            // you should only use the vapi or api object for registration.
            // If you directly use the canvas or its properties, please make
            // sure that you specify the VRL versions you are compatible with
            // in the constructor of this plugin configurator because the
            // internal api is likely to change.
            //
            // examples:
            //
            // vapi.addComponent(MyComponent.class);
            // vapi.addTypeRepresentation(MyType.class);
            
            vapi.addAction(new VAction("VMM-Editor") {
                
                @Override
                public void actionPerformed(ActionEvent ae, Object o) {
                    Main.showEditor("VMultiMarkdown-Editor " + Constants.VMM_VERSION);
                }
            },ActionDelegator.TOOL_MENU);
        }
    }


    @Override
    public void unregister(PluginAPI api) {
        //
    }

    @Override
    public void init(InitPluginAPI iApi) {
        // nothing to init
    }
}
