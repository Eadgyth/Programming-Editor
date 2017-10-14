package eg.ui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * The menu for help actions
 * <p>Created in {@link MenuBar}
 */
class HelpMenu {
   
   private final JMenu     menu        = new JMenu("?");
   private final JMenuItem aboutItm    = new JMenuItem("About Eadgyth");
   private final JMenuItem showHelpItm = new JMenuItem("Show help");
   
   public HelpMenu() {
      menu.add(aboutItm);
      menu.add(showHelpItm);
      aboutItm.addActionListener(e -> new eg.ui.InfoWin());      
      showHelpItm.addActionListener(e -> new eg.ui.Help());  
   }
   
   public JMenu getMenu() {
      return menu;
   }
}
