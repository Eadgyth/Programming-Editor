package eg.ui.menu;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

/**
 * The menu for formatting actions.
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

   /**
    * Gets this menu
    *
    * @return  the menu
    */
   public JMenu getMenu() {
      return menu;
   }

   /**
    * Sets the listener for actions to set the font
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setFontAction(ActionListener al) {
      fontItm.addActionListener(al);
   }

   /**
    * Sets the listener for actions to enable or diable wordwrap
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setChangeWordWrapAct(ActionListener al) {
      wordWrapItm.addActionListener(al);
   }

   /**
    * Returns the boolean that indicates if the item for enabling and
    * disabling wordwrap is selected
    *
    * @return the boolean value
    */
   public boolean isWordWrapItmSelected() {
      return wordWrapItm.isSelected();
   }

   /**
    * Sets the selection state of the item for enabling and disabling
    * wordwrap
    *
    * @param b  the boolean that is true to select and false to unselect
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
