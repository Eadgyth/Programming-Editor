package eg.ui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

//--Eadgyth--/
import eg.ui.UIComponents;

/**
 * The menu bar that also has the menus
 */
public class MenuBar {

   private final FileMenu fileMenu = new FileMenu();
   private final LanguageMenu languageMenu = new LanguageMenu();
   private final EditMenu editMenu = new EditMenu(languageMenu);
   private final FormatMenu formatMenu = new FormatMenu();
   private final ViewMenu viewMenu = new ViewMenu();
   private final ProjectMenu projectMenu = new ProjectMenu();
   private final HelpMenu helpMenu = new HelpMenu();
   private final JMenuBar mb;

   public MenuBar() {
      JMenu[] menus = new JMenu[] {
         fileMenu().getMenu(),
         editMenu().getMenu(),
         formatMenu().getMenu(),
         viewMenu().getMenu(),
         projectMenu().getMenu(),
         helpMenu().getMenu()
      };
      mb = UIComponents.menuBar(menus);
      mb.setBorderPainted(false);
   }

   /**
    * Gets this menu bar
    *
    * @return  this menu bar
    */
   public JMenuBar menuBar() {
      return mb;
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

   /**
    * Gets this helpMenu
    *
    * @return this {@link HelpMenu}
    */
   public HelpMenu helpMenu() {
      return helpMenu;
   }
}
