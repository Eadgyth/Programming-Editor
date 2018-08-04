package eg.ui;

import java.awt.FlowLayout;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

//--Eadgyth--
import eg.TabbedDocuments;
import eg.Projects;
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

   /**
    * Sets listeners for file actions
    *
    * @param td  the reference to {@link TabbedDocuments}
    */
   public void setFileActions(TabbedDocuments td) {
      openBt.addActionListener(e -> td.openFileByChooser());
      saveBt.addActionListener(e -> td.save(true));
   }

   /**
    * Sets listeners for actions to change project and to run and
    * compile a project
    *
    * @param p  the reference to {@link Projects}
    */
   public void setProjectActions(Projects p) {
      changeProjBt.addActionListener(e -> p.changeProject());
      runBt.addActionListener(e -> p.runProject());
      compileBt.addActionListener(e -> p.saveAllAndCompile());
   }

   /**
    * Sets listeners for actions to edit text
    *
    * @param edit  the reference to {@link Edit}
    */
   public void setEditActions(Edit edit) {
      undoBt.addActionListener(e -> edit.undo());
      redoBt.addActionListener(e -> edit.redo());
      cutBt.addActionListener(e -> edit.cut());
      copyBt.addActionListener(e -> edit.setClipboard());
      pasteBt.addActionListener(e -> edit.pasteText());
      indentBt.addActionListener(e -> edit.indent());
      outdentBt.addActionListener(e -> edit.outdent());
   }

   /**
    * Sets the booleans that specify if the buttons for actions to
    * compile and run a project are enabled or disabled
    *
    * @param isCompile  the boolean value for compile actions
    * @param isRun  the boolean value for run actions
    */
   public void enableProjectActionsBts(boolean isCompile, boolean isRun) {
      compileBt.setEnabled(isCompile);
      runBt.setEnabled(isRun);
   }

   /**
    * Sets the boolean that specifies if the button for actions to
    * change project is enabled or disabled
    *
    * @param b  the boolean value
    */
   public void enableChangeProjBt(boolean b) {
      changeProjBt.setEnabled(b);
   }

   /**
    * Sets the boolean that specifies if the buttons for cutting and
    * copying actions are enabled or disabled
    *
    * @param b  the boolean value
    */
   public void enableCutCopyBts(boolean b) {
      cutBt.setEnabled(b);
      copyBt.setEnabled(b);
   }
   
   /**
    * Sets the boolean that specifies if the button for saving actions
    * is enabled or disabled
    *
    * @param b  the boolean value
    */
    public void enableSaveBt(boolean b) {
       saveBt.setEnabled(b);
    }

   /**
    * Sets the booleans that specify if the buttons for undoing and
    * redoing actions are enabled or disabled
    *
    * @param isUndo  the boolean value for undoing actions
    * @param isRedo  the boolean value for redoing actions
    */
   public void enableUndoRedoBts(boolean isUndo, boolean isRedo) {
      undoBt.setEnabled(isUndo);
      redoBt.setEnabled(isRedo);
   }

   //
   //--private--/
   //

   private void initToolbar() {
      toolbar.setOpaque(false);
      toolbar.setBorder(null);
      toolbar.setFloatable(false);
      enableProjectActionsBts(false, false);
      changeProjBt.setEnabled(false);
      JButton[] bts = new JButton[] {
         openBt, saveBt,
         undoBt, redoBt, cutBt, copyBt, pasteBt,
         indentBt, outdentBt,
         compileBt, runBt, changeProjBt
      };
      String[] toolTips = new String[] {
         "Open file", "Save file",
         "Undo", "Redo", "Cut selection", "Copy selection", "Paste",
         "Increase indentation by the set indent length",
         "Reduce indentation by the set indent length",
         "Save all opened source files of active project and compile",
         "Run project", "Change project"
      };
      for (int i = 0; i < bts.length; i++) {
         toolbar.add(bts[i]);
         bts[i].setBorder(new EmptyBorder(10, 8, 10, 8));
         bts[i].setToolTipText(toolTips[i]);
         bts[i].setFocusable(false);
         bts[i].setFocusPainted(false);
      }
      pnl.add(toolbar);
   }
}
