package eg.ui.menu;

import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

//--Eadgyth--/
import eg.Languages;
import eg.LanguageChanger;

/**
 * The menu for actions to change the language
 */
public class LanguageMenu {

   private final JMenu menu = new JMenu("Language");
   private final ButtonGroup group = new ButtonGroup();
   private final JRadioButtonMenuItem[] itm
         = new JRadioButtonMenuItem[Languages.values().length];

   public LanguageMenu() {
      init();
   }

   /**
    * Gets this menu
    *
    * @return  the menu
    */
   public JMenu menu() {
      return menu;
   }

   /**
    * Sets a <code>LanguageChanger</code> to the elements in the
    * array of items for actions to select the language
    *
    * @param lc  the {@link LanguageChanger}
    */
   public void setChangeLanguageActions(LanguageChanger lc) {
      for (JRadioButtonMenuItem item : itm) {
         item.addActionListener(e -> setLanguage(e, lc));
      }
   }

   /**
    * Selects the item for the specified language
    *
    * @param lang  the language
    */
   public void selectLanguageItm(Languages lang) {
      for (int i = 0; i < itm.length; i++) {
         if (lang == Languages.values()[i]) {
            itm[i].setSelected(true);
         }
      }
   }

   //
   //--private--/
   //

   private void setLanguage(ActionEvent e, LanguageChanger lc) {
      for (int i = 0; i < itm.length; i++) {
         if (e.getSource() == itm[i]) {
            lc.change(Languages.values()[i]);
         }
      }
   }

   private void init() {
      for (int i = 0; i < itm.length; i++) {
         itm[i] = new JRadioButtonMenuItem(Languages.values()[i].display());
         group.add(itm[i]);
         menu.add(itm[i]);
      }
   }
}
