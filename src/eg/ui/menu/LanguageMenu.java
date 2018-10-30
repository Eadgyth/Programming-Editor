package eg.ui.menu;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;

//--Eadgyth--/
import eg.Languages;
import eg.LanguageChanger;

/**
 * The menu for actions to change the language
 */
public class LanguageMenu {

   private final JMenu menu = new JMenu("Language");
   private final JCheckBoxMenuItem[] itm
         = new JCheckBoxMenuItem[Languages.values().length];

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
    * Sets a <code>LanguageSetter</code> to the elements in the array of
    * items for actions to select the language
    *
    * @param lc  the {@link LanguageChanger}
    */
   public void setChangeLanguageActions(LanguageChanger lc) {
      for (JCheckBoxMenuItem item : itm) {
         item.addActionListener(e -> setLanguage(e, lc));
      }
   }

    /**
    * Selects and disables the item for the specified language
    *
    * @param lang  the language
    * @param b   true to enable, false to disable the items for the
    * other languages
    */
   public void selectLanguageItm(Languages lang, boolean b) {
      for (int i = 0; i < itm.length; i++) {
         if (lang == Languages.values()[i]) {
            itm[i].setEnabled(false);
            itm[i].setSelected(true);
         }
         else {
            itm[i].setEnabled(b);
            itm[i].setSelected(false);
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
            itm[i].setEnabled(false);
         }
         else {
            itm[i].setSelected(false);
            itm[i].setEnabled(true);
         }
      }
   }

   private void init() {
      for (int i = 0; i < itm.length; i++) {
         itm[i] = new JCheckBoxMenuItem(Languages.values()[i].display());
         menu.add(itm[i]);
      }
   }
}
