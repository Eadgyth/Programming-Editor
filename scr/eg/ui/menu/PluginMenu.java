package eg.ui.menu;

import java.io.File;
import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

//--Eadgyth--//
import eg.ui.ViewSettings;
import eg.javatools.SearchFiles;
import eg.plugin.PluginStarter;

public class PluginMenu {
   
   private final JMenu menu          = new JMenu("Plugins");
   private final JMenu allPlugsMenu  = new JMenu("Add in function panel");
   private JMenuItem[] selectPlugItm = null;
   
   PluginMenu() {    
      menu.add(allPlugsMenu);
      File[] plugJars = null;
      plugJars = new SearchFiles().filteredFilesToArr("./Plugins", ".jar");
      if (plugJars != null) {
         selectPlugItm = new JMenuItem[plugJars.length];
         for (int i = 0; i < plugJars.length; i++) {
            selectPlugItm[i] = new JMenuItem(plugJars[i].getName());
            allPlugsMenu.add(selectPlugItm[i]);
         }
      }
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   public void startPlugin(PluginStarter plugStart, ViewSettings viewSet) {
      selectPlugAct((ActionEvent e) -> {
         try {
            plugStart.startPlugin(getPluginIndex(e));
            viewSet.setShowFunctionState(true);
         }
         catch (IOException ioe) {
            System.out.println(ioe.getMessage());  
         }
      });
   }
   
   private void selectPlugAct(ActionListener al) {
      if (selectPlugItm != null) {
         for (int i = 0; i < selectPlugItm.length; i++) {
            selectPlugItm[i].addActionListener(al);
         }
      }
   }
   
   /**
    * @return  the index of the plugin selected in the menu
    */
   private int getPluginIndex(ActionEvent e) {
      int pluginIndex = 0;
      for (int i = 0; i < selectPlugItm.length; i++) {
         if (e.getSource() == selectPlugItm[i]) {
            pluginIndex = i;
         }
      }
      return pluginIndex;
   }
}