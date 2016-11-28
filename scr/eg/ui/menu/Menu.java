package eg.ui.menu;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;
import javax.swing.Box;

/**
 * The main menu
 */
public class Menu {

   private final JMenuBar menuMain = new JMenuBar();

   private final FileMenu fileMenu = new FileMenu();
   private final EditMenu editMenu = new EditMenu();
   private final FormatMenu formatMenu = new FormatMenu();
   private final ViewMenu viewMenu = new ViewMenu();
   private final PluginMenu plugMenu = new PluginMenu();
   private final ProjectMenu projectMenu = new ProjectMenu();
   private final HelpMenu helpMenu = new HelpMenu();

   public Menu() { 
      menuMain.setOpaque(false);
      menuMain.setBorder(null);
      assembleMenu();
   }

   public JMenuBar getMenuBar() {
      return menuMain;
   }
   
   public FileMenu getFileMenu() {
      return fileMenu;
   }
   
   public EditMenu getEditMenu() {
      return editMenu;
   }
   
   public FormatMenu getFormatMenu() {
      return formatMenu;
   }
   
   public ViewMenu getViewMenu() {
      return viewMenu;
   }
   
   public PluginMenu getPluginMenu() {
      return plugMenu;
   }
   
   public ProjectMenu getProjectMenu() {
      return projectMenu;
   }

   private void assembleMenu() {
      int strutSize = 0;
      if ("Windows".equals(eg.Constants.CURR_LAF_STR)) {
         strutSize = 5;
      }
      Component strut = Box.createHorizontalStrut(strutSize);
      menuMain.add(fileMenu.getMenu());
      menuMain.add(strut);
      menuMain.add(editMenu.getMenu());
      menuMain.add(strut);
      menuMain.add(formatMenu.getMenu());
      menuMain.add(strut);
      menuMain.add(viewMenu.getMenu());    
      menuMain.add(strut);
      menuMain.add(plugMenu.getMenu());
      menuMain.add(strut);
      menuMain.add(projectMenu.getMenu());    
      menuMain.add(strut);
      menuMain.add(helpMenu.getMenu());
   }
}