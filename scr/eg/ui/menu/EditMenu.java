package eg.ui.menu;

import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
      "Plain text", "Java", "HTML"
   };

   private final Preferences prefs = new Preferences();

    /* Edit */
   private final JMenu     menu         = new JMenu("Edit");
   private final JMenuItem undo         = new JMenuItem("Undo", IconFiles.undoIcon);
   private final JMenuItem redo         = new JMenuItem("Redo", IconFiles.redoIcon);
   private final JMenuItem selectAll    = new JMenuItem("Select all");
   private final JMenuItem copy         = new JMenuItem("Copy");
   private final JMenuItem paste        = new JMenuItem("Paste");
   private final JMenuItem indent       = new JMenuItem("Indent selection more ",
                                          IconFiles.indentIcon);
   private final JMenuItem outdent      = new JMenuItem("Indent selection less",
                                          IconFiles.outdentIcon);
   private final JMenuItem changeIndent = new JMenuItem("Indent/outdent length");
   private final JMenuItem clearSpaces  = new JMenuItem("Clear spaces");
   private final JMenu     language     = new JMenu("Language in new tabs");
   private final JCheckBoxMenuItem[] selectLanguage
                                        = new JCheckBoxMenuItem[LANGUAGES.length];

   EditMenu() {
      assembleMenu();
      shortCuts();
   }

   JMenu getMenu() {
      return menu;
   }

   public void registerAct(Edit edit) {
      undo.addActionListener(e -> edit.undo());
      redo.addActionListener(e -> edit.redo());
      selectAll.addActionListener(e -> edit.selectAll());
      copy.addActionListener(e -> edit.setClipboard());  
      paste.addActionListener(e -> edit.pasteText());   
      indent.addActionListener(e -> edit.indentSelection());
      outdent.addActionListener(e -> edit.outdentSelection());
      clearSpaces.addActionListener(e -> edit.clearSpaces());
      changeIndent.addActionListener(e -> edit.setNewIndentUnit());
      for (int i = 0; i < selectLanguage.length; i++) {
         selectLanguage[i].addActionListener(e ->
               edit.changeLanguage(getNewLanguage(e)));
      }
   }

   private Languages getNewLanguage(ActionEvent e) {
      Languages lang = null;
      for (int i = 0; i < selectLanguage.length; i++) {
         if (e.getSource() == selectLanguage[i]) {
            lang = Languages.values()[i];
         }
         else selectLanguage[i].setState(false);
      }
      return lang;
   }

   private void assembleMenu() {
      prefs.readPrefs();

      menu.add(undo);
      menu.add(redo);
      menu.addSeparator();
      menu.add(selectAll);
      menu.add(copy);
      menu.add(paste );
      menu.addSeparator();
      menu.add(indent);
      menu.add(outdent);
      menu.add(changeIndent);
      menu.add(clearSpaces);
      menu.addSeparator();
      for (int i = 0; i < selectLanguage.length; i++) {
         selectLanguage[i] = new JCheckBoxMenuItem(LANGUAGES[i]);
         if (prefs.getProperty("language").equals(
               eg.Languages.values()[i].toString())) {
            selectLanguage[i].setState(true);
         }
      }
      menu.add(language);
      for (int i = 0; i < selectLanguage.length; i++) {
         language.add(selectLanguage[i]);
      }
   }

   private void shortCuts() {
      copy.setAccelerator(KeyStroke.getKeyStroke('C',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      paste.setAccelerator(KeyStroke.getKeyStroke('V',
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      indent.setAccelerator(KeyStroke.getKeyStroke("control R"));
      outdent.setAccelerator(KeyStroke.getKeyStroke("control L"));
   }
}