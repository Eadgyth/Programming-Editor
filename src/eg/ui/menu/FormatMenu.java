package eg.ui.menu;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

/**
 * The menu for format actions.
 * <p>Created in {@link MenuBar}
 */
public class FormatMenu {
   
   private final JMenu     menu     = new JMenu("Format");
   private final JMenuItem fontItm  = new JMenuItem("Font ...");
   private final JCheckBoxMenuItem wordWrapItm
                                    = new JCheckBoxMenuItem("Wordwrap");
                             
   public FormatMenu() {
      assembleMenu();
   }
   
   public JMenu getMenu() {
      return menu;
   }
   
   public void setFontAction(ActionListener al) {
      fontItm.addActionListener(al);
   }
   
   public void setChangeWordWrapAct(ActionListener al) {
      wordWrapItm.addActionListener(al);
   }
   
   public boolean isWordWrapItmSelected() {
      return wordWrapItm.isSelected();
   }
  
   public void selectWordWrapItm(boolean select) {
      wordWrapItm.setState(select);
   }
   
   private void assembleMenu() {
      menu.add(fontItm);
      menu.add(wordWrapItm);
      menu.setMnemonic(KeyEvent.VK_O);
   }
}
