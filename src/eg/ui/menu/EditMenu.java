package eg.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.Edit;
import eg.TabbedDocuments;
import eg.Languages;
import eg.Preferences;
import eg.ui.IconFiles;
import eg.edittools.EditTools;

/**
 * The menu for edit actions.
 * <p>Created in {@link MenuBar}
 */
public class EditMenu {

   private final JMenu     menu             = new JMenu("Edit");
   private final JMenuItem undoItm          = new JMenuItem("Undo", IconFiles.UNDO_ICON);
   private final JMenuItem redoItm          = new JMenuItem("Redo", IconFiles.REDO_ICON);
   private final JMenuItem cutItm           = new JMenuItem("Cut", IconFiles.CUT_ICON);
   private final JMenuItem copyItm          = new JMenuItem("Copy", IconFiles.COPY_ICON);
   private final JMenuItem pasteItm         = new JMenuItem("Paste", IconFiles.PASTE_ICON);
   private final JMenuItem selectAllItm     = new JMenuItem("Select all");
   private final JMenuItem[] editToolsItm   = new JMenuItem[EditTools.values().length];
   private final JMenuItem indentItm        = new JMenuItem(
                                            "Increase indentation by the set indent length",
                                            IconFiles.INDENT_ICON);
   private final JMenuItem outdentItm       = new JMenuItem(
                                            "Reduce indentation by the set indent length",
                                            IconFiles.OUTDENT_ICON);
   private final JMenuItem changeIndentItm = new JMenuItem("Change indentation length");
   private final JMenuItem clearSpacesItm  = new JMenuItem("Clear trailing spaces");
   private final JMenu     languageMenu    = new JMenu("Language");
   private final JCheckBoxMenuItem[] selectLangChBxItm
                                           = new JCheckBoxMenuItem[Languages.values().length];

   private final Preferences prefs = new Preferences();

   public EditMenu() {
      assembleMenu();
      shortCuts();
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
    * Sets listeners for actions to edit text
    *
    * @param edit  the reference to {@link Edit}
    */
   public void setEditTextActions(Edit edit) {
      undoItm.addActionListener(e -> edit.undo());
      redoItm.addActionListener(e -> edit.redo());
      cutItm.addActionListener(e -> edit.cut());
      copyItm.addActionListener(e -> edit.setClipboard());
      pasteItm.addActionListener(e -> edit.pasteText());
      selectAllItm.addActionListener(e -> edit.selectAll());
      indentItm.addActionListener(e -> edit.indent());
      outdentItm.addActionListener(e -> edit.outdent());
      clearSpacesItm.addActionListener(e -> edit.clearTrailingSpaces());
      changeIndentItm.addActionListener(e -> edit.setNewIndentUnit());
   }

   /**
    * Sets the listener to an element in the array of items that are destined
    * for actions to open an <code>AddableEditTool</code>
    *
    * @param al  the <code>ActionListener</code>
    * @param i  the index of the array element
    */
   public void setEditToolsActions(ActionListener al, int i) {
      editToolsItm[i].addActionListener(al);
   }

   /**
    * Sets the listeners for actions to select the language
    *
    * @param td  the reference to {@link TabbedDocuments}
    */
   public void setChangeLanguageActions(TabbedDocuments td) {
      for (JCheckBoxMenuItem item : selectLangChBxItm) {
         item.addActionListener(e -> setLanguage(e, td));
      }
   }

  /**
    * Sets the booleans that specify if the items for undoing and
    * redoing actions are enabled or disabled
    *
    * @param isUndo  the boolean value for undoing actions
    * @param isRedo  the boolean value for redoing actions
    */
   public void enableUndoRedoItms(boolean isUndo, boolean isRedo) {
      undoItm.setEnabled(isUndo);
      redoItm.setEnabled(isRedo);
   }
   
   /**
    * Sets the boolean that specifies if the items for cutting and
    * copying actions are enabled or disabled
    *
    * @param b  the boolean value
    */
   public void enableCutCopyItms(boolean b) {
      cutItm.setEnabled(b);
      copyItm.setEnabled(b);
   }

   /**
    * Selects and disables the item for the specified language and enables
    * the items for the other languages if the specified <code>enable</code>
    * is true
    *
    * @param lang  the language that has one of the constant values in
    * {@link Languages}
    * @param enable  true to enable the non-selected items
    */
   public void selectLanguageItm(Languages lang, boolean enable) {
      for (int i = 0; i < selectLangChBxItm.length; i++) {
         if (lang == Languages.values()[i]) {
            selectLangChBxItm[i].setEnabled(false);
            selectLangChBxItm[i].setSelected(true);
         }
         else {
            selectLangChBxItm[i].setEnabled(enable);
            selectLangChBxItm[i].setSelected(false);
         }
      }
   }

   //
   //--private--
   //

   private void setLanguage(ActionEvent e, TabbedDocuments tf) {
      for (int i = 0; i < selectLangChBxItm.length; i++) {
         if (e.getSource() == selectLangChBxItm[i]) {
            Languages lang = Languages.values()[i];
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
      for (int i = 0; i < editToolsItm.length; i++) {
         editToolsItm[i] = new JMenuItem(EditTools.values()[i].display());
         menu.add(editToolsItm[i]);
      }
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
