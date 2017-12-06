package eg.ui.menu;

import javax.swing.JMenuBar;
import javax.swing.Box;
import javax.swing.UIManager;

/**
 * The menu bar that has the menus.<br>
 * Created in {@link eg.ui.MainWin}
 */
public class MenuBar {

   private final JMenuBar menuBar = new JMenuBar();
   private final FileMenu fileMenu = new FileMenu();
   private final EditMenu editMenu = new EditMenu();
   private final FormatMenu formatMenu = new FormatMenu();
   private final ViewMenu viewMenu = new ViewMenu();
   private final ProjectMenu projectMenu = new ProjectMenu();
   private final HelpMenu helpMenu = new HelpMenu();

   public MenuBar() {
      menuBar.setOpaque(false);
      menuBar.setBorder(null);
      assembleMenu();
   }

   /**
    * Gets this menu bar
    *
    * @return  this menu bar
    */
   public JMenuBar menuBar() {
      return menuBar;
   }

   /**
    * Gets this file menu
    *
    * @return  this file menu
    */
   public FileMenu fileMenu() {
      return fileMenu;
   }

   /**
    * Gets this edit menu
    *
    * @return  this edit menu
    */
   public EditMenu editMenu() {
      return editMenu;
   }

   /**
    * Gets this format menu
    *
    * @return  this format menu
    */
   public FormatMenu formatMenu() {
      return formatMenu;
   }

   /**
    * Gets this view menu
    *
    * @return  this view menu
    */
   public ViewMenu viewMenu() {
      return viewMenu;
   }

   /**
    * Gets this project menu
    *
    * @return  this project menu
    */
   public ProjectMenu projectMenu() {
      return projectMenu;
   }

   //
   //--private
   //

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
      menuBar.add(projectMenu.getMenu());
      menuBar.add(Box.createHorizontalStrut(strutSize));
      menuBar.add(helpMenu.getMenu());
   }
}
