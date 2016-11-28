package eg.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

//--Eadgyth--//
import eg.Preferences;
import eg.FontSetting;

import eg.ui.ViewSettings;

public class FormatMenu {
   
   private final Preferences prefs = new Preferences();
   
   private final JMenu     menu   = new JMenu("Format");
   private final JMenuItem font   = new JMenuItem("Font ...");
   private final JCheckBoxMenuItem wordWrap
                                  = new JCheckBoxMenuItem("Wordwrap");
                             
   FormatMenu() {
      assembleMenu();
   }
   
   public void registerAct(FontSetting fontSet, ViewSettings viewSet) {
      font.addActionListener(e -> fontSet.makeFontSetWinVisible(true));
      wordWrap.addActionListener(e -> viewSet.enableWordWrap());
   }
   
   /**
    * If the wordwrap menu item is selected
    */
   public boolean isWordWrapSelected() {
      return wordWrap.getState();
   }
   
   public void selectWordWrap(boolean select) {
      wordWrap.setState(select);
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   private void assembleMenu() {
      prefs.readPrefs();

      menu.add(font);
      menu.add(wordWrap);
      if ("enabled".equals(prefs.prop.getProperty("wordWrap"))) {
         wordWrap.setState(true);
      }
   }
}