package eg.ui.menu;

import java.io.File;
import java.io.IOException;

import java.awt.event.KeyEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

//--Eadgyth--//
import eg.javatools.SearchFiles;
import eg.plugin.PluginStarter;

/**
 * The menu for starting a plugin.
 * <p> Created in {@link MenuBar}
 */
public class PluginMenu {

   private final JMenu menu          = new JMenu("Plugins");
   private final JMenu allPlugsMenu  = new JMenu("Add to function panel");
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
      menu.setMnemonic(KeyEvent.VK_U);
   }

   JMenu getMenu() {
      return menu;
   }

   /**
    * Starts a plugin and opens the function panel
    *
    * @param plugStart  the reference to {@link PluginStarter}
    * @param vMenu  the reference to {@link ViewMenu}
    */
   public void startPlugin(PluginStarter plugStart, ViewMenu vMenu) {
      selectPlugAct((ActionEvent e) -> {
         try {
            plugStart.startPlugin(getPluginIndex(e));
            vMenu.doFunctionItmAct(true);
         }
         catch (IOException ioe) {
            eg.utils.FileUtils.logStack(ioe);
         }
      });
   }

   private void selectPlugAct(ActionListener al) {
      if (selectPlugItm != null) {
          for (JMenuItem itm : selectPlugItm) {
              itm.addActionListener(al);
          }
      }
   }

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
