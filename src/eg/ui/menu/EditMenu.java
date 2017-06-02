package eg.ui.menu;

import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.Edit;
import eg.TabbedFiles;
import eg.Languages;
import eg.Preferences;

import eg.ui.IconFiles;

public class EditMenu {

   private final JMenu     menu            = new JMenu("Edit");
   private final JMenuItem undoItm         = new JMenuItem("Undo", IconFiles.UNDO_ICON);
   private final JMenuItem redoItm         = new JMenuItem("Redo", IconFiles.REDO_ICON);
   private final JMenuItem selectAllItm    = new JMenuItem("Select all");
   private final JMenuItem cutItm          = new JMenuItem("Cut", IconFiles.CUT_ICON);
   private final JMenuItem copyItm         = new JMenuItem("Copy", IconFiles.COPY_ICON);
   private final JMenuItem pasteItm        = new JMenuItem("Paste", IconFiles.PASTE_ICON);
   private final JMenuItem indentItm       = new JMenuItem("Indent selection more ",
                                             IconFiles.INDENT_ICON);
   private final JMenuItem outdentItm      = new JMenuItem("Indent selection less",
                                             IconFiles.OUTDENT_ICON);
   private final JMenuItem changeIndentItm = new JMenuItem("Indent/outdent length");
   private final JMenuItem clearSpacesItm  = new JMenuItem("Clear trailing spaces");
   private final JMenu     languageMenu    = new JMenu("Language");
   private final JCheckBoxMenuItem[] selectLangChBxItm
                                           = new JCheckBoxMenuItem[Languages.values().length];
                                            
   private final Preferences prefs = new Preferences();

   EditMenu() {
      assembleMenu();
      shortCuts();
   }

   JMenu getMenu() {
      return menu;
   }

   /**
    * Sets the selection state of the menu items for the language
    *
    * @param lang  the language that has one of the constant values in
    * {@link Languages}
    * @param enableSelection  true to enable non-selected items to be selectable
    */
   public void setLanguagesItms(Languages lang, boolean enableSelection) {
      for (int i = 0; i < selectLangChBxItm.length; i++) {
         if (lang == Languages.values()[i]) {
            selectLangChBxItm[i].setEnabled(false);
            selectLangChBxItm[i].setSelected(true);
         }
         else {
            selectLangChBxItm[i].setEnabled(enableSelection);
            selectLangChBxItm[i].setSelected(false);
         }
      }
   }

   public void registerAct(Edit edit, TabbedFiles tf) {
      undoItm.addActionListener(e -> edit.undo());
      redoItm.addActionListener(e -> edit.redo());
      selectAllItm.addActionListener(e -> edit.selectAll());
      cutItm.addActionListener(e -> edit.cut());
      copyItm.addActionListener(e -> edit.setClipboard());
      pasteItm.addActionListener(e -> edit.pasteText());
      indentItm.addActionListener(e -> edit.indentSelection());
      outdentItm.addActionListener(e -> edit.outdentSelection());
      clearSpacesItm.addActionListener(e -> edit.clearTrailingSpaces());
      changeIndentItm.addActionListener(e -> edit.setNewIndentUnit());
      for (JCheckBoxMenuItem item : selectLangChBxItm) {
           item.addActionListener(e -> getNewLanguage(e, edit, tf));
       }
   }

   //
   //--private methods--//
   //

   private void getNewLanguage(ActionEvent e, Edit edit, TabbedFiles tf) {
      Languages lang = null;
      for (int i = 0; i < selectLangChBxItm.length; i++) {
         if (e.getSource() == selectLangChBxItm[i]) {
            lang = Languages.values()[i];
            tf.changeLanguage(lang);
            selectLangChBxItm[i].setEnabled(false);
         }
         else {
            selectLangChBxItm[i].setSelected(false);
            selectLangChBxItm[i].setEnabled(true);
         }
      }
   }

   private void assembleMenu() {
      menu.add(undoItm);
      menu.add(redoItm);
      menu.addSeparator();
      menu.add(cutItm);
      menu.add(copyItm);
      menu.add(pasteItm );
      menu.add(selectAllItm);
      menu.addSeparator();
      menu.add(indentItm);
      menu.add(outdentItm);
      menu.add(changeIndentItm);
      menu.add(clearSpacesItm);
      menu.addSeparator();
      prefs.readPrefs();
      for (int i = 0; i < selectLangChBxItm.length; i++) {
         selectLangChBxItm[i] = new JCheckBoxMenuItem(Languages.values()[i].display());
         if (prefs.getProperty("language").equals(
               eg.Languages.values()[i].toString())) {
            selectLangChBxItm[i].setSelected(true);
            selectLangChBxItm[i].setEnabled(false);
         }
      }
      menu.add(languageMenu);
      for (JCheckBoxMenuItem itm : selectLangChBxItm) {
         languageMenu.add(itm);
      }
      menu.setMnemonic(KeyEvent.VK_E);
   }

   private void shortCuts() {
      undoItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
            ActionEvent.CTRL_MASK));
      redoItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
            ActionEvent.CTRL_MASK));
      cutItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
            ActionEvent.CTRL_MASK));
      copyItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
            ActionEvent.CTRL_MASK));
      pasteItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
            ActionEvent.CTRL_MASK));
      selectAllItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
            ActionEvent.CTRL_MASK));
      indentItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
            ActionEvent.CTRL_MASK));
      outdentItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
            ActionEvent.CTRL_MASK));
   }
}
