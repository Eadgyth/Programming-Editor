package eg.ui.menu;

import java.awt.Toolkit;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.Edit;
import eg.Languages;
import eg.Preferences;

import eg.ui.IconFiles;

public class EditMenu {

   private final static String[] LANGUAGES = {
      "Plain text", "Java", "HTML", "Perl"
   };

   private final Preferences prefs = new Preferences();

    /* Edit */
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
   private final JMenuItem clearSpacesItm  = new JMenuItem("Clear spaces");
   private final JMenu     language        = new JMenu("Language");
   private final JCheckBoxMenuItem[] selectLangChBxItm
                                            = new JCheckBoxMenuItem[LANGUAGES.length];

   EditMenu() {
      assembleMenu();
      shortCuts();
   }

   JMenu getMenu() {
      return menu;
   }

   public void registerAct(Edit edit) {
      undoItm.addActionListener(e -> edit.undo());
      redoItm.addActionListener(e -> edit.redo());
      selectAllItm.addActionListener(e -> edit.selectAll());
      cutItm.addActionListener(e -> edit.cut());
      copyItm.addActionListener(e -> edit.setClipboard());  
      pasteItm.addActionListener(e -> edit.pasteText());   
      indentItm.addActionListener(e -> edit.indentSelection());
      outdentItm.addActionListener(e -> edit.outdentSelection());
      clearSpacesItm.addActionListener(e -> edit.clearSpaces());
      changeIndentItm.addActionListener(e -> edit.setNewIndentUnit());
      for (JCheckBoxMenuItem item : selectLangChBxItm) {
           item.addActionListener(e -> edit.changeLanguage(getNewLanguage(e)));
       }
   }

   private Languages getNewLanguage(ActionEvent e) {
      Languages lang = null;
      for (int i = 0; i < selectLangChBxItm.length; i++) {
         if (e.getSource() == selectLangChBxItm[i]) {
            lang = Languages.values()[i];
         }
         else selectLangChBxItm[i].setState(false);
      }
      return lang;
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
         selectLangChBxItm[i] = new JCheckBoxMenuItem(LANGUAGES[i]);
         if (prefs.getProperty("language").equals(
               eg.Languages.values()[i].toString())) {
            selectLangChBxItm[i].setState(true);
         }
      }
      menu.add(language);
      for (JCheckBoxMenuItem itm : selectLangChBxItm) {
         language.add(itm);
      }
   }

   private void shortCuts() {
      cutItm.setAccelerator(KeyStroke.getKeyStroke('X',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      copyItm.setAccelerator(KeyStroke.getKeyStroke('C',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      pasteItm.setAccelerator(KeyStroke.getKeyStroke('V',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      selectAllItm.setAccelerator(KeyStroke.getKeyStroke("control A"));
      indentItm.setAccelerator(KeyStroke.getKeyStroke("control R"));
      outdentItm.setAccelerator(KeyStroke.getKeyStroke("control L"));
   }
}
