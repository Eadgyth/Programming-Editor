package eg.ui;

import javax.swing.JToolBar;
import javax.swing.JButton;

//--Eadgyth--/
import eg.TabbedDocuments;
import eg.Projects;
import eg.Edit;
import eg.utils.ScreenParams;

/**
 * Defines the main toolbar of the editor
 */
public class ToolBar {

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

   private final JToolBar tb;

   public ToolBar() {
      compileBt.setEnabled(false);
      runBt.setEnabled(false);
      changeProjBt.setEnabled(false);
      JButton[] bts = new JButton[] {
         openBt, saveBt,
         undoBt, redoBt, cutBt, copyBt, pasteBt, indentBt, outdentBt,
         compileBt, runBt, changeProjBt
      };
      String[] toolTips = new String[] {
         "Open file", "Save file",
         "Undo", "Redo", "Cut selection", "Copy selection", "Paste",
         "Increase indentation (Tab)", "Decrease indentation (Shift+Tab)",
         "Save and compile project", "", "Change Project"
      };
      tb = UIComponents.toolbar(bts, toolTips);
      int h = ScreenParams.scaledSize(2);
      tb.setBorder(UIComponents.lightBkgdMatteBorder(h, 0, h, 0));
   }

   /**
    * Returns this toolbar
    *
    * @return  the JToolBar
    */
    public JToolBar toolBar() {
      return tb;
    }

   /**
    * Sets listeners for file actions
    *
    * @param td  the reference to {@link TabbedDocuments}
    */
   public void setFileActions(TabbedDocuments td) {
      openBt.addActionListener(e -> td.open());
      saveBt.addActionListener(e -> td.save());
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
    * Sets listeners for project actions
    *
    * @param p  the reference to {@link Projects}
    */
   public void setProjectActions(Projects p) {
      changeProjBt.addActionListener(e -> p.change());
      runBt.addActionListener(e -> p.run());
      compileBt.addActionListener(e -> p.compile());
   }

   /**
    * Enables or diables the buttons for cutting and copying actions.
    *
    * @param b  true to enable, false to disable
    */
   public void enableCutCopyBts(boolean b) {
      cutBt.setEnabled(b);
      copyBt.setEnabled(b);
   }

   /**
    * Enables or disables the button for saving actions
    *
    * @param b  true to enable, false to disable
    */
    public void enableSaveBt(boolean b) {
       saveBt.setEnabled(b);
    }

   /**
    * Enables or disables the buttons for und/redo actions. The
    * specified boolean each are true to enable, false to disable
    *
    * @param isUndo  the boolean for undo actions
    * @param isRedo  the boolean for redo actions
    */
   public void enableUndoRedoBts(boolean isUndo, boolean isRedo) {
      undoBt.setEnabled(isUndo);
      redoBt.setEnabled(isRedo);
   }

   /**
    * Enables the button for actions to compile a project
    *
    * @param b  true to enable, false to disable
    */
   public void enableCompileBt(boolean b) {
      compileBt.setEnabled(b);
   }

   /**
    * Enables the button for actions to run a project
    *
    * @param b  true to enable, false to disable
    * @param tooltip  the tooltip text for the button
    */
   public void enableRunBt(boolean b, String tooltip) {
      runBt.setEnabled(b);
      runBt.setToolTipText(tooltip);
   }

   /**
    * Enables or disables the button for actions to change project
    *
    * @param b  true to enable, false to disable
    */
   public void enableChangeProjBt(boolean b) {
      changeProjBt.setEnabled(b);
   }
}
