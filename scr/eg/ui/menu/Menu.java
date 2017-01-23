package eg.ui.menu;

import javax.swing.JMenuBar;
import javax.swing.Box;
import javax.swing.UIManager;

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

   public JMenuBar menubar() {
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
      if ("Windows".equals(UIManager.getLookAndFeel().getName())) {
         strutSize = 5;
      }
      menuMain.add(fileMenu.getMenu());
      menuMain.add(Box.createHorizontalStrut(strutSize));
      menuMain.add(editMenu.getMenu());
      menuMain.add(Box.createHorizontalStrut(strutSize));
      menuMain.add(formatMenu.getMenu());
      menuMain.add(Box.createHorizontalStrut(strutSize));
      menuMain.add(viewMenu.getMenu());    
      menuMain.add(Box.createHorizontalStrut(strutSize));
      menuMain.add(plugMenu.getMenu());
      menuMain.add(Box.createHorizontalStrut(strutSize));
      menuMain.add(projectMenu.getMenu());    
      menuMain.add(Box.createHorizontalStrut(strutSize));
      menuMain.add(helpMenu.getMenu());
   }
}
