package eg.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

class HelpMenu {
   
   private final JMenu     menu        = new JMenu("?");
   private final JMenuItem aboutItm    = new JMenuItem("About Eadgyth");
   private final JMenuItem showHelpItm = new JMenuItem("Show help");
   
   HelpMenu() {
      menu.add(aboutItm);
      menu.add(showHelpItm);
      aboutItm.addActionListener(e -> new eg.ui.InfoWin());      
      showHelpItm.addActionListener(e -> new eg.ui.Help());  
   }
   
   JMenu getMenu() {
      return menu;
   }
}
