package eg.ui;

import java.awt.event.ActionListener;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/**
 * The main toolbar with methods to add action listeners to the buttons
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
   
   JToolBar toolbar() {
      return toolbar;
   }
   
   //
   //-- add action listeners
   //
   public void openAct(ActionListener al) {
      openBt.addActionListener(al);
   }
   
   public void saveAct(ActionListener al) {
      saveBt.addActionListener(al);
   }
   
   public void undoAct(ActionListener al) {
      undoBt.addActionListener(al);
   }
   
   public void redoAct(ActionListener al) {
      redoBt.addActionListener(al);
   }
   
   public void indentAct(ActionListener al) {
      indentBt.addActionListener(al);
   }
   
   public void outdentAct(ActionListener al) {
      outdentBt.addActionListener(al);
   }

   public void compileAct(ActionListener al) {
      compileBt.addActionListener(al);
   }
   
   public void runAct(ActionListener al) {
      runBt.addActionListener(al);
   }
   
   void disableExtraBts() {
      compileBt.setEnabled(false);
      runBt.setEnabled(false);
   }
   
   void enableExtraBts(boolean isCompile, boolean isRun) {
      compileBt.setEnabled(isCompile);
      runBt.setEnabled(isRun);
   }

   private void initToolbar() { 
      toolbar.setOpaque(false);
      toolbar.setBorder(null);
      toolbar.setFloatable(false);
      
      enableExtraBts(false, false);

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