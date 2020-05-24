package eg.ui.menu;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--/
import eg.Edit;
import eg.ui.IconFiles;
import eg.edittools.EditTools;
import eg.utils.SystemParams;

/**
 * The menu for edit actions
 */
public class EditMenu {

   private final JMenu menu = new JMenu("Edit");
   private final JMenuItem undoItm = new JMenuItem();
   private final JMenuItem redoItm = new JMenuItem();
   private final JMenuItem cutItm = new JMenuItem("Cut", IconFiles.CUT_ICON);
   private final JMenuItem copyItm = new JMenuItem("Copy", IconFiles.COPY_ICON);
   private final JMenuItem pasteItm = new JMenuItem();
   private final JMenu selectMenu = new JMenu("Select");
   private final JMenuItem selectAllItm = new JMenuItem("All");
   private final JMenuItem selectLineItm = new JMenuItem("Line");
   private final JMenuItem selectLineTextItm
         = new JMenuItem("Line from beginning of text");
   private final JMenuItem selectLineFromCursorItm
         = new JMenuItem("Line from cursor position");
   private final JCheckBoxMenuItem[] editToolsItm
         = new JCheckBoxMenuItem[EditTools.values().length];
   private final JMenuItem indentItm = new JMenuItem();
   private final JMenuItem outdentItm = new JMenuItem();
   private final JMenuItem setIndentItm = new JMenuItem("Indentation settings ...");
   private final JMenu clearSpacesMenu = new JMenu("Remove trailing spaces");
   private final JMenuItem clearSpacesAllItm
         = new JMenuItem("Remove in entire text");
   private final JMenuItem clearSpacesItm
         = new JMenuItem("Remove in current line or selection");

   /**
    * @param languageMenu  the reference to <code>LangageMenu</code>
    */
   public EditMenu(LanguageMenu languageMenu) {
      assembleMenu(languageMenu);
      shortCuts();
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
    * Sets listeners for editing actions
    *
    * @param edit  the reference to Edit
    */
   public void setEditActions(Edit edit) {
      undoItm.setAction(edit.undoAction());
      undoItm.setIcon(IconFiles.UNDO_ICON);
      redoItm.setAction(edit.redoAction());
      redoItm.setIcon(IconFiles.REDO_ICON);
      cutItm.addActionListener(e -> edit.cut());
      copyItm.addActionListener(e -> edit.setClipboard());
      pasteItm.setAction(edit.pasteAction());
      pasteItm.setIcon(IconFiles.PASTE_ICON);
      selectAllItm.addActionListener(e -> edit.selectAll());
      selectLineItm.addActionListener(e -> edit.selectLine());
      selectLineTextItm.addActionListener(e -> edit.selectLineText());
      selectLineFromCursorItm.addActionListener(e -> edit.selectLineFromCursor());
      indentItm.setAction(edit.indentAction());
      indentItm.setIcon(IconFiles.INDENT_ICON);
      outdentItm.setAction(edit.outdentAction());
      outdentItm.setIcon(IconFiles.OUTDENT_ICON);
      setIndentItm.addActionListener(e -> edit.openIndentSettingWin());
      clearSpacesAllItm.addActionListener(e -> edit.clearAllTrailingSpaces());
      clearSpacesItm.addActionListener(e -> edit.clearTrailingSpaces());
   }

   /**
    * Sets the listener to an item at the specified index
    * in the array of check boxes for actions to open an
    * <code>AddableEditTool</code>
    *
    * @param al  the ActionListener
    * @param i  the index of the array element
    */
   public void setEditToolsActionsAt(ActionListener al, int i) {
      editToolsItm[i].addActionListener(al);
   }

   /**
    * Unselects the item at the specified index in the array of
    * check boxes for actions to open an <code>AddableEditTool</code>
    *
    * @param i  the index
    */
   public void unselectEditToolItmAt(int i) {
      editToolsItm[i].setSelected(false);
   }

   /**
    * Unselects the items in the array of check boxes for actions
    * to open an <code>AddableEditTool</code> except for the item
    * at the specified index
    *
    * @param i  the index
    */
   public void unselectEditToolItmExcept(int i) {
      for (int y = 0; y < editToolsItm.length; y++) {
         if (y != i) {
            editToolsItm[y].setSelected(false);
         }
      }
   }

   /**
    * Returns if the item at the specified index in the arrray
    * of check boxes for actions to open an <code>AddableEditTool</code>
    * is selected
    *
    * @param i  the index of the array element
    * @return  true if selected, false otherwise
    */
   public boolean isEditToolItmSelected(int i) {
      return editToolsItm[i].isSelected();
   }

   /**
    * Enables or disables the undo/redo actions. The specified
    * booleans each are true to enable, false to disable
    *
    * @param isUndo  the boolean for undo actions
    * @param isRedo  the boolean for redo actions
    */
   public void enableUndoRedoItms(boolean isUndo, boolean isRedo) {
      undoItm.getAction().setEnabled(isUndo);
      redoItm.getAction().setEnabled(isRedo);
   }

   /**
    * Enables or disables the items for actions to cut and copy
    * text
    *
    * @param b  true to enable, false to disable
    */
   public void enableCutCopyItms(boolean b) {
      cutItm.setEnabled(b);
      copyItm.setEnabled(b);
   }

   //
   //--private--/
   //

   private void assembleMenu(LanguageMenu lm) {
      menu.add(undoItm);
      menu.add(redoItm);
      menu.addSeparator();
      menu.add(cutItm);
      menu.add(copyItm);
      menu.add(pasteItm );
      menu.addSeparator();
      for (int i = 0; i < editToolsItm.length; i++) {
         editToolsItm[i] = new JCheckBoxMenuItem(EditTools.values()[i].display());
         menu.add(editToolsItm[i]);
      }
      menu.addSeparator();
      menu.add(selectMenu);
      selectMenu.add(selectAllItm);
      selectMenu.add(selectLineItm);
      selectMenu.add(selectLineTextItm);
      selectMenu.add(selectLineFromCursorItm);
      JMenu indentMenu = new JMenu("Indentation");
      menu.add(indentMenu);
      indentMenu.add(indentItm);
      indentMenu.add(outdentItm);
      indentMenu.addSeparator();
      indentMenu.add(setIndentItm);
      menu.add(clearSpacesMenu);
      clearSpacesMenu.add(clearSpacesAllItm);
      clearSpacesMenu.add(clearSpacesItm);
      menu.addSeparator();
      menu.add(lm.menu());
      menu.setMnemonic(KeyEvent.VK_E);
   }

   private void shortCuts() {
      cutItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
            SystemParams.MODIFIER_MASK));
      copyItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
            SystemParams.MODIFIER_MASK));
      selectAllItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
            SystemParams.MODIFIER_MASK));
      selectLineItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
            SystemParams.MODIFIER_MASK));
      selectLineTextItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
            SystemParams.MODIFIER_MASK));
      selectLineFromCursorItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
            SystemParams.MODIFIER_MASK));
      clearSpacesItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
            SystemParams.MODIFIER_MASK));
   }
}
