package eg.ui;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

class FormatMenu {
   
   private final JMenu     menu     = new JMenu("Format");
   private final JMenuItem fontItm  = new JMenuItem("Font ...");
   private final JCheckBoxMenuItem wordWrapItm
                                    = new JCheckBoxMenuItem("Wordwrap");
                             
   FormatMenu() {
      assembleMenu();
   }
   
   void fontAct(ActionListener al) {
      fontItm.addActionListener(al);
   }
   
   void changeWordWrapAct(ActionListener al) {
      wordWrapItm.addActionListener(al);
   }
   
   boolean isWordWrapItmSelected() {
      return wordWrapItm.isSelected();
   }
  
   void selectWordWrapItm(boolean select) {
      wordWrapItm.setState(select);
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   private void assembleMenu() {
      menu.add(fontItm);
      menu.add(wordWrapItm);
      menu.setMnemonic(KeyEvent.VK_O);
   }
}
