package eg.ui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import eg.ui.Help;

/**
 * The menu for help actions
 * <p>Created in {@link MenuBar}
 */
class HelpMenu {
   
   private final Help h = new Help();
   
   private final JMenu     menu        = new JMenu("?");
   private final JMenuItem showHelpItm = new JMenuItem("Open help site");
   private final JMenuItem showDocuItm = new JMenuItem("Open docu site");
   private final JMenuItem aboutItm    = new JMenuItem("About Eadgyth");
   
   public HelpMenu() {
      menu.add(showHelpItm);
      menu.add(showDocuItm);
      menu.add(aboutItm);
      aboutItm.addActionListener(e -> new eg.ui.InfoWin());      
      showHelpItm.addActionListener(e -> h.showHelpSite());
      showDocuItm.addActionListener(e -> h.showDocuSite());
   }
   
   public JMenu getMenu() {
      return menu;
   }
}
