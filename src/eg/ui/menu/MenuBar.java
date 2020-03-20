package eg.ui.menu;

import javax.swing.JMenuBar;
import javax.swing.Box;
import javax.swing.UIManager;

import javax.swing.border.EmptyBorder;

/**
 * The menu bar that has the menus.<br>
 * Created in {@link eg.ui.MainWin}
 */
public class MenuBar {

   private final JMenuBar menuBar = new JMenuBar();
   private final FileMenu fileMenu = new FileMenu();
   private final LanguageMenu languageMenu = new LanguageMenu();
   private final EditMenu editMenu = new EditMenu(languageMenu);
   private final FormatMenu formatMenu = new FormatMenu();
   private final ViewMenu viewMenu = new ViewMenu();
   private final ProjectMenu projectMenu = new ProjectMenu();
   private final HelpMenu helpMenu = new HelpMenu();

   public MenuBar() {
      menuBar.setBorder(new EmptyBorder(0, 0, 0, 0));
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
    * @return  this {@link FileMenu}
    */
   public FileMenu fileMenu() {
      return fileMenu;
   }

   /**
    * Gets this edit menu
    *
    * @return  this {@link EditMenu}
    */
   public EditMenu editMenu() {
      return editMenu;
   }
   
   /**
    * Gets this language menu
    *
    * @return  this language menu
    */
   public LanguageMenu languageMenu() {
      return languageMenu;
   }

   /**
    * Gets this format menu
    *
    * @return  this {@link FormatMenu}
    */
   public FormatMenu formatMenu() {
      return formatMenu;
   }

   /**
    * Gets this view menu
    *
    * @return  this {@link ViewMenu}
    */
   public ViewMenu viewMenu() {
      return viewMenu;
   }

   /**
    * Gets this project menu
    *
    * @return  this {@link ProjectMenu}
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
      menuBar.add(editMenu.menu());
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
