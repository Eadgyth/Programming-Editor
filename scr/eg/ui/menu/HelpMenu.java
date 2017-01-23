package eg.ui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

class HelpMenu {
   
   private final JMenu     menu     = new JMenu("?");
   private final JMenuItem about    = new JMenuItem("About Eadgyth");
   private final JMenuItem showHelp = new JMenuItem("Show help");
   
   HelpMenu() {
      menu.add(about);
      menu.add(showHelp);
      about.addActionListener(e -> new eg.ui.InfoWin());      
      showHelp.addActionListener(e -> new eg.ui.Help());  
   }
   
   JMenu getMenu() {
      return menu;
   }
}