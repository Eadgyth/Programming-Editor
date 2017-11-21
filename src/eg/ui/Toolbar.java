package eg.ui;

import java.awt.FlowLayout;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

//--Eadgyth--
import eg.TabbedFiles;
import eg.CurrentProject;
import eg.Edit;

/**
 * The toolbar.
 * <p> Created in {@link MainWin}
 */
public class Toolbar {

   private final JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
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

   public JPanel toolbar() {
      //return toolbar;
      return pnl;
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

   /**
    * Enables or disables the buttons for actions to compile and run
    * a project. The booleans indicate if the respective button is set
    * enabled or disabled
    *
    * @param isCompile  the boolean
    * @param isRun  the boolean
    */
   public void enableSrcCodeActionBts(boolean isCompile, boolean isRun) {
      compileBt.setEnabled(isCompile);
      runBt.setEnabled(isRun);
   }

   /**
    * Enables or disables the button for changing project actions
    *
    * @param b  true to enable, false to disable the button
    */
   public void enableChangeProjBt(boolean b) {
      changeProjBt.setEnabled(b);
   }

   /**
    * Enables or disables the buttons for cutting and copying actions
    *
    * @param b  true to enable, false to disable both buttons
    */
   public void enableCutCopyBts(boolean b) {
      cutBt.setEnabled(b);
      copyBt.setEnabled(b);
   }

   /**
    * Enables or disables the buttons for undoing and redoing actions. The
    * booleans indicate if the respective button is set enabled or disabled
    *
    * @param isUndo  the boolean
    * @param isRedo  the boolean
    */
   public void enableUndoRedoBts(boolean isUndo, boolean isRedo) {
      undoBt.setEnabled(isUndo);
      redoBt.setEnabled(isRedo);
   }

   //
   //--private methods
   //

   private void initToolbar() {
      toolbar.setOpaque(false);
      toolbar.setBorder(null);
      toolbar.setFloatable(false);
      enableSrcCodeActionBts(false, false);
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
      pnl.add(toolbar);
   }
}
