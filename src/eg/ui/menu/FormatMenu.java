package eg.ui.menu;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

/**
 * The menu for formatting actions
 */
public class FormatMenu {

   private final JMenu menu = new JMenu("Format");
   private final JMenuItem fontItm = new JMenuItem("Font ...");
   private final JCheckBoxMenuItem wordWrapItm
         = new JCheckBoxMenuItem("Wordwrap");

   public FormatMenu() {
      assembleMenu();
   }

   /**
    * Gets this menu
    *
    * @return  the menu
    */
   public JMenu getMenu() {
      return menu;
   }

   /**
    * Sets the listener for actions to open the font
    * settings
    *
    * @param al  the ActionListener
    */
   public void setFontAction(ActionListener al) {
      fontItm.addActionListener(al);
   }

   /**
    * Sets the listener for actions to enable or diable wordwrap
    *
    * @param al  the ActionListener
    */
   public void setChangeWordWrapAct(ActionListener al) {
      wordWrapItm.addActionListener(al);
   }

   /**
    * Returns if the item for setting wordwrap is selected
    *
    * @return  true if enabled, false otherwise
    */
   public boolean isWordWrapItmSelected() {
      return wordWrapItm.isSelected();
   }

   /**
    * Sets the selection state of the item for enabling/disabling
    * wordwrap
    *
    * @param b  true to select, false to unselect
    */
   public void selectWordWrapItm(boolean b) {
      wordWrapItm.setState(b);
   }

   //
   //--private--/
   //

   private void assembleMenu() {
      menu.add(fontItm);
      menu.add(wordWrapItm);
      menu.setMnemonic(KeyEvent.VK_O);
   }
}
