package eg.ui.menu;

import javax.swing.JMenuBar;
import javax.swing.Box;
import javax.swing.UIManager;

/**
 * The menu bar that has the menus
 */
public class MenuBar {

   private final JMenuBar menuBar = new JMenuBar();
   private final FileMenu fileMenu = new FileMenu();
   private final EditMenu editMenu = new EditMenu();
   private final FormatMenu formatMenu = new FormatMenu();
   private final ViewMenu viewMenu = new ViewMenu();
   private final PluginMenu plugMenu = new PluginMenu();
   private final ProjectMenu projectMenu = new ProjectMenu();
   private final HelpMenu helpMenu = new HelpMenu();

   public MenuBar() {
      menuBar.setOpaque(false);
      menuBar.setBorder(null);
      assembleMenu();
   }

   public JMenuBar menuBar() {
      return menuBar;
   }

   public FileMenu fileMenu() {
      return fileMenu;
   }

   public EditMenu editMenu() {
      return editMenu;
   }

   public FormatMenu formatMenu() {
      return formatMenu;
   }

   public ViewMenu viewMenu() {
      return viewMenu;
   }

   public PluginMenu pluginMenu() {
      return plugMenu;
   }

   public ProjectMenu projectMenu() {
      return projectMenu;
   }

   private void assembleMenu() {
      int strutSize = 0;
      if ("Windows".equals(UIManager.getLookAndFeel().getName())) {
         strutSize = 5;
      }
      menuBar.add(fileMenu.getMenu());
      menuBar.add(Box.createHorizontalStrut(strutSize));
      menuBar.add(editMenu.getMenu());
      menuBar.add(Box.createHorizontalStrut(strutSize));
      menuBar.add(formatMenu.getMenu());
      menuBar.add(Box.createHorizontalStrut(strutSize));
      menuBar.add(viewMenu.getMenu());
      menuBar.add(Box.createHorizontalStrut(strutSize));
      menuBar.add(plugMenu.getMenu());
      menuBar.add(Box.createHorizontalStrut(strutSize));
      menuBar.add(projectMenu.getMenu());
      menuBar.add(Box.createHorizontalStrut(strutSize));
      menuBar.add(helpMenu.getMenu());
   }
}
