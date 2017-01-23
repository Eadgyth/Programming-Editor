package eg.ui;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

//--Eadgyth--
import eg.TabbedFiles;
import eg.CurrentProject;
import eg.Edit;

/**
 * The main toolbar
 */
public class Toolbar {

   private final JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);

   private final JButton openBt    = new JButton(IconFiles.openIcon);
   private final JButton saveBt    = new JButton(IconFiles.saveIcon);
   private final JButton undoBt    = new JButton(IconFiles.undoIcon);
   private final JButton redoBt    = new JButton(IconFiles.redoIcon);
   private final JButton cutBt     = new JButton(IconFiles.cutIcon);
   private final JButton copyBt    = new JButton(IconFiles.copyIcon);
   private final JButton pasteBt   = new JButton(IconFiles.pasteIcon);
   private final JButton indentBt  = new JButton(IconFiles.indentIcon);
   private final JButton outdentBt = new JButton(IconFiles.outdentIcon); 
   private final JButton compileBt = new JButton(IconFiles.compileIcon);
   private final JButton runBt     = new JButton(IconFiles.runIcon);
   
   public Toolbar() {
      initToolbar();
   }
   
   /**
    * @return  this {@code JToolBar}
    */
   public JToolBar toolbar() {
      return toolbar;
   }
   
   /**
    * Adds ActionsListeners to the buttons for file actions
    * @param tf  the reference to the {@link TabbedFiles} object
    * that handles the file actions
    */
   public void registerFileActions(TabbedFiles tf) {
      openBt.addActionListener(e -> tf.openFileByChooser());
      saveBt.addActionListener(e -> tf.saveOrSaveAs());      
   }
   
   /**
    * Adds ActionsListeners to the buttons for project actions
    * @param currProj  the reference to the {@link CurrentProject}
    * object that handles the project actions
    */
   public void registerProjectActions(CurrentProject currProj) {
      runBt.addActionListener(e -> currProj.runProj());
      compileBt.addActionListener(e -> currProj.compile());
   }
   
   /**
    * Adds ActionsListeners to the buttons for edit actions
    * @param edit  the reference to the {@link Edit}
    * object that handles the edit actions
    */
   public void registerEdit(Edit edit) {
      undoBt.addActionListener(e -> edit.undo());
      redoBt.addActionListener(e -> edit.redo());
      cutBt.addActionListener(e -> edit.cut());
      copyBt.addActionListener(e -> edit.setClipboard());
      pasteBt.addActionListener(e -> edit.pasteText());
      indentBt.addActionListener(e -> edit.indentSelection());   
      outdentBt.addActionListener(e -> edit.outdentSelection());
   }
   
   /**
    * Enabled/disabled buttons for project actions
    * @param isCompile  if compiling a project is enabled
    * @param isRun  if running a project is enabled
    */
   public void enableProjBts(boolean isCompile, boolean isRun) {
      compileBt.setEnabled(isCompile);
      runBt.setEnabled(isRun);
   }

   //
   //--private methods
   //
   
   private void initToolbar() { 
      toolbar.setOpaque(false);
      toolbar.setBorder(null);
      toolbar.setFloatable(false);
      enableProjBts(false, false);
      JButton[] bts = new JButton[] {
         openBt, saveBt,
         undoBt, redoBt, cutBt, copyBt, pasteBt,
         indentBt, outdentBt,
         compileBt, runBt
      };
                          
      String[] toolTips = new String[] {
         "Open file", "Save selected file",
         "Undo", "Redo", "Cut selection", "Copy selection",
         "Paste",
         "Indent selection more", "Indent selection less",
         "Save open files and compile project", "Run project"
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
