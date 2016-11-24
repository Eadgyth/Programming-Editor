package eg.ui;

import java.awt.event.ActionListener;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

//--Eadgyth--
import eg.TabActions;
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
   private final JButton indentBt  = new JButton(IconFiles.indentIcon);
   private final JButton outdentBt = new JButton(IconFiles.outdentIcon); 
   private final JButton compileBt = new JButton(IconFiles.compileIcon);
   private final JButton runBt     = new JButton(IconFiles.runIcon);
   
   public Toolbar() {
      initToolbar();
   }
   
   public JToolBar toolbar() {
      return toolbar;
   }
   
   public void registerFileActions(TabActions ta) {
      openBt.addActionListener(e -> ta.openFileByChooser());
      saveBt.addActionListener(e -> ta.saveOrSaveAs());      
      compileBt.addActionListener(e -> ta.saveAndCompile());
   }
   
   public void registerProjectActions(CurrentProject currProj) {
      runBt.addActionListener(e -> currProj.runProj());
   }
   
   public void registerEdit(Edit edit) {
      undoBt.addActionListener(e -> edit.undo());
      redoBt.addActionListener(e -> edit.redo());
      indentBt.addActionListener(e -> edit.indentSelection());   
      outdentBt.addActionListener(e -> edit.outdentSelection());
   }
   
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
         openBt, saveBt, undoBt, redoBt, indentBt,
         outdentBt, compileBt, runBt
      };
                          
      String[] toolTips = new String[] {
         "Open file", "Save selected file",
         "Undo", "Redo", "Indent selection more",
         "Indent selection less",
         "Save all open files and compile project", "Run project"
      };
      
      for (int i = 0; i < bts.length; i++) {
         toolbar.add(bts[i]);
         if (i == 5) {
            toolbar.addSeparator();
         }
         bts[i].setBorder(new EmptyBorder(5, 5, 5, 5));
         bts[i].setToolTipText(toolTips[i]);
         bts[i].setFocusable(false);
         bts[i].setBorderPainted(false);
         bts[i].setFocusPainted(false);
      }
   }
}