package eg.ui;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

//--Eadgyth--
import eg.TabbedFiles;
import eg.CurrentProject;
import eg.Edit;

/**
 * The toolbar.
 * <p> Created in {@link MainWin}
 */
class Toolbar {

   private final JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);

   private final JButton openBt       = new JButton(IconFiles.OPEN_ICON);
   private final JButton saveBt       = new JButton(IconFiles.SAVE_ICON);
   private final JButton undoBt       = new JButton(IconFiles.UNDO_ICON);
   private final JButton redoBt       = new JButton(IconFiles.REDO_ICON);
   private final JButton cutBt        = new JButton(IconFiles.CUT_ICON);
   private final JButton copyBt       = new JButton(IconFiles.COPY_ICON);
   private final JButton pasteBt      = new JButton(IconFiles.PASTE_ICON);
   private final JButton indentBt     = new JButton(IconFiles.INDENT_ICON);
   private final JButton outdentBt    = new JButton(IconFiles.OUTDENT_ICON);
   private final JButton changeProjBt = new JButton(IconFiles.CHANGE_PROJ_ICON);
   private final JButton compileBt    = new JButton(IconFiles.COMPILE_ICON);
   private final JButton runBt        = new JButton(IconFiles.RUN_ICON);

   public Toolbar() {
      initToolbar();
   }

   public JToolBar toolbar() {
      return toolbar;
   }

   public void setFileActions(TabbedFiles tf) {
      openBt.addActionListener(e -> tf.openFileByChooser());
      saveBt.addActionListener(e -> tf.save(true));
   }

   public void setProjectActions(CurrentProject currProj) {
      changeProjBt.addActionListener(e -> currProj.changeProject());
      runBt.addActionListener(e -> currProj.runProj());
      compileBt.addActionListener(e -> currProj.saveAllAndCompile());
   }

   public void setEditTextActions(Edit edit) {
      undoBt.addActionListener(e -> edit.undo());
      redoBt.addActionListener(e -> edit.redo());
      cutBt.addActionListener(e -> edit.cut());
      copyBt.addActionListener(e -> edit.setClipboard());
      pasteBt.addActionListener(e -> edit.pasteText());
      indentBt.addActionListener(e -> edit.indent());
      outdentBt.addActionListener(e -> edit.outdent());
   }

   public void enableCompileRunBts(boolean isCompile, boolean isRun) {
      compileBt.setEnabled(isCompile);
      runBt.setEnabled(isRun);
   }
   
   /**
    * Enables/disables the button for the action to change project
    *
    * @param isEnabled  if the botton for changing project
    * is enabled
    */
   void enableChangeProjBt(boolean isEnabled) {
      changeProjBt.setEnabled(isEnabled);
   }
   
   /**
    * Enables/disables the buttons for cut and copy actions
    *
    * @param isEnabled  if the buttons for cut and copy are enabled
    */
   void enableCutCopyBts(boolean isEnabled) {
      cutBt.setEnabled(isEnabled);
      copyBt.setEnabled(isEnabled);
   }

   /**
    * Enables/disables the buttons for undo and redo actions
    *
    * @param enableUndo  if the button for undo is enabled
    * @param enableRedo  if the button for redo is enabled
    */
   public void enableUndoRedoBts(boolean enableUndo, boolean enableRedo) {
      undoBt.setEnabled(enableUndo);
      redoBt.setEnabled(enableRedo);
   }

   //
   //--private methods
   //

   private void initToolbar() {
      toolbar.setOpaque(false);
      toolbar.setBorder(null);
      toolbar.setFloatable(false);
      enableCompileRunBts(false, false);
      changeProjBt.setEnabled(false);
      JButton[] bts = new JButton[] {
         openBt, saveBt,
         undoBt, redoBt, cutBt, copyBt, pasteBt,
         indentBt, outdentBt,
         compileBt, runBt, changeProjBt
      };
      String[] toolTips = new String[] {
         "Open file", "Save selected file",
         "Undo", "Redo", "Cut selection", "Copy selection", "Paste",
         "Increase indentation by the set indent length",
         "Reduce indentation by the set indent length",
         "Save all opened source files of active project and compile",
         "Run project", "Change project"
      };
      for (int i = 0; i < bts.length; i++) {
         toolbar.add(bts[i]);
         if (i == 1 || i == 8) {
            toolbar.addSeparator();
         }
         bts[i].setBorder(new EmptyBorder(5, 5, 5, 5));
         bts[i].setToolTipText(toolTips[i]);
         bts[i].setFocusable(false);
         bts[i].setFocusPainted(false);
      }
   }
}
