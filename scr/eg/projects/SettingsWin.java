package eg.projects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.ui.IconFiles;
import eg.Constants;

/**
 * The window for the configuration of a project
 */
public class SettingsWin {

   private final static Dimension DIM_TF = new Dimension(300, 22);
   private final static Dimension DIM_SPACER = new Dimension(0, 20);

   private final JFrame frame = new JFrame("Eadgyth - Project settings");

   private final JTextField   fileTf       = new JTextField();
   private final JTextField   moduleTf     = new JTextField("");
   private final JTextField   sourcesDirTf = new JTextField();      
   private final JTextField   execDirTf    = new JTextField();
   private final JTextField   argsTf       = new JTextField("");
   private final JTextField   buildTf      = new JTextField();
   private final JButton      okBt         = new JButton("   OK   ");
   private final JButton      cancelBt     = new JButton("Cancel");
   private final JCheckBox    saveConfig   = new JCheckBox();

   /**
    * Creates a SettingsWin and defines which inputs are asked for.
    * <p>
    * @param fileKind  a description for the kind of file which a project is
    * configured for. Is not null.
    * @param moduleKind  a description for the kind of module (e.g. package/
    * directory relative to the project root). Null to skip asking for a module
    * @param useScrExec  true to ask for the directories that contain source
    * files and executables, respectively
    * @param useArgs  true to ask for additional arguments of a start script
    * @param buildKind  the name for the kind of build. Null to skip asking
    * for a build
    */
   public SettingsWin(String fileKind, String moduleKind, boolean useScrExec,
         boolean useArgs, String buildKind) {
      setWindow(fileKind, moduleKind, useScrExec, useArgs, buildKind);
   }

   /**
    * Makes this frame visible
    */
   public void makeVisible(boolean isVisible) {
      fileTf.requestFocus();
      frame.setVisible(isVisible);
   }      

   /**
    * Adds an action event handler to this ok button
    */
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }
   
   /**
    * @return  the input in the text field for the name of the
    * file which a project is configured for
    */
   String projectFileIn() {
      return fileTf.getText();
   }

   /**
    * @return  the input in the text field for the module / package
    */
   String moduleIn() {
      return moduleTf.getText();
   }

   /**
    * @return  the input in the text field for the directory of source
    * files
    */
   public String sourcesDirIn() {
      return sourcesDirTf.getText();
   }

   /**
    * @return  the input in the text field for the directory of
    * executables
    */
   public String execDirIn() {
      return execDirTf.getText();
   }

   /**
    * @return  the input in the text field for arguments
    */
   public String argsIn() {
      return argsTf.getText();
   }

   /**
    * @return  the input in the text field for a name of a build
    */
   public String buildNameIn() {
      return buildTf.getText();
   }

   /**
    * Shows in the text field the name of the file which a project
    * is set for
    */
   public void displayFile(String in) {
      fileTf.setText(in);
   }

   /**
    * Shows in the text field the directory of the
    * module / package
    */
   public void displayModule(String in) {
      moduleTf.setText(in);
   }

   /**
    * Shows in the text field the name of the directory that
    * contains source files / packages
    */
   public void displaySourcesDir(String in) {
      sourcesDirTf.setText(in);
   }

   /**
    * Shows in the text field the name of the directory
    * that contains executable files / packages
    * @param in  the name of the directory that contains
    * executable files / packages
    */
   public void displayExecDir(String in) {
      execDirTf.setText(in);
   }
   
   /**
    * @return if the checkbox to save the text field inputs
    * it saved to local prefs file
    */
   public boolean isSaveConfig() {
      return saveConfig.isSelected();
   }
   
   /**
    * @param isSelected  true to mark the checkbox for saving text
    * field inputs to a local prefs file selected
    */
   public void setSaveConfigSelected(boolean isSelected) {
      saveConfig.setSelected(isSelected);
   }

   private JPanel projectPanel(String fileKind, String moduleKind, boolean useScrExec) {
      GridLayout grid = new GridLayout(2, 0);
      JPanel projPnl = new JPanel(grid);

      // file panel
      JLabel fileLb = new JLabel(fileKind + ":");
      projPnl.add(holdLbAndTf(fileLb, fileTf));

      // module/subdir panel
      if (moduleKind != null) {
         grid.setRows(3);
         JLabel moduleLb = new JLabel(moduleKind + ":");
         projPnl.add(holdLbAndTf(moduleLb, moduleTf));
      }

      // scources/executables panel
      if (useScrExec) {
         grid.setRows(5);        
         JLabel sourcesDirLb = new JLabel("Sources directory:");
         projPnl.add(holdLbAndTf(sourcesDirLb, sourcesDirTf));
         JLabel execDirLb = new JLabel("Executables directory:");
         projPnl.add(holdLbAndTf(execDirLb, execDirTf));
      }

      projPnl.add(checkBxPnl(saveConfig, "Save settings in a 'config' file"));

      projPnl.setBorder(titledBorder("Project"));  
      return projPnl;
   }

   private JPanel argsPanel() {
      JPanel argsPnl = new JPanel( new GridLayout(1, 0));
      JLabel argsLb = new JLabel("Arguments:");
      argsPnl.add(holdLbAndTf(argsLb, argsTf));
      argsPnl.setBorder(titledBorder("Startscript"));  
      return argsPnl;
   }  

   private JPanel buildPanel(String buildKind) {
      JPanel buildPnl = new JPanel(new GridLayout(1, 0));
      JLabel buildLb = new JLabel("Name for " + buildKind +":");
      buildPnl.add(holdLbAndTf(buildLb, buildTf));
      buildPnl.setBorder(titledBorder("Build (" + buildKind + ")"));      
      return buildPnl;
   }

   private JPanel buttonsPanel() {
      JPanel buttons = new JPanel(new FlowLayout());
      buttons.add(okBt);
      buttons.add(cancelBt);
      frame.getRootPane().setDefaultButton(okBt);
      cancelBt.addActionListener(e -> {
         frame.setVisible(false);
      });
      return buttons;
   }

   private JPanel holdLbAndTf(JLabel lb, JTextField tf) {
      JPanel holdPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      lb.setFont(eg.Constants.SANSSERIF_BOLD_12);
      tf.setFont(eg.Constants.SANSSERIF_PLAIN_12);
      tf.setPreferredSize(DIM_TF);
      holdPnl.add(lb);
      holdPnl.add(tf);
      return holdPnl;
   }

   private TitledBorder titledBorder(String title) {
      TitledBorder tBorder = BorderFactory.createTitledBorder
            (new LineBorder(Color.BLACK, 1), title);
      tBorder.setTitleFont(eg.Constants.VERDANA_PLAIN_11);
      return tBorder;
   }
   
   private JPanel checkBxPnl(JCheckBox checkBox, String title) {
      JLabel label = new JLabel(title);
      label.setFont(Constants.SANSSERIF_BOLD_12);
      
      JPanel checkBxPnl = new JPanel(); 
      checkBxPnl.setLayout(new BoxLayout(checkBxPnl, BoxLayout.LINE_AXIS));
      checkBox.setHorizontalTextPosition(JCheckBox.LEFT);
      checkBxPnl.add(Box.createHorizontalGlue());   
      checkBxPnl.add(label);
      checkBxPnl.add(checkBox);
      checkBxPnl.add(Box.createRigidArea(new Dimension(30, 0)));
      return checkBxPnl;
   }
   
   private JPanel combineAll(String fileKind, String moduleKind, boolean useScrExec,
         boolean useArgs, String buildKind) {
      JPanel combineAll = new JPanel();
      combineAll.setLayout(new BoxLayout(combineAll, BoxLayout.Y_AXIS));
      combineAll.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      combineAll.add(projectPanel(fileKind, moduleKind, useScrExec));
      if (useArgs) {
         combineAll.add(Box.createRigidArea(DIM_SPACER));
         combineAll.add(argsPanel());
      }
      if (buildKind != null) {
         combineAll.add(Box.createRigidArea(DIM_SPACER));
         combineAll.add(buildPanel(buildKind));
      }
      combineAll.add(Box.createRigidArea(DIM_SPACER));
      combineAll.add(buttonsPanel());
      return combineAll;
   }

   private void setWindow(String fileKind, String moduleKind, boolean useScrExec,
         boolean useArgs, String buildKind) {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(combineAll(fileKind, moduleKind, useScrExec, 
            useArgs, buildKind));
      frame.pack();
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.eadgythIcon.getImage());
   }
}