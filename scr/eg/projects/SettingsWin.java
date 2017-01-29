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

   private final JTextField fileTf       = new JTextField();
   private final JTextField moduleTf     = new JTextField("");
   private final JTextField sourcesDirTf = new JTextField();      
   private final JTextField execDirTf    = new JTextField();
   private final JTextField argsTf       = new JTextField("");
   private final JTextField buildTf      = new JTextField();
   private final JButton    okBt         = new JButton("   OK   ");
   private final JButton    cancelBt     = new JButton("Cancel");
   private final JCheckBox  saveConfig   = new JCheckBox();
   
   private String fileKind = null;
   private String moduleKind = null;
   private boolean useScr = false;
   private boolean useExec = false;
   private boolean useArgs = false;
   private String buildKind = null;
   
   /**
    * Sets the text that describes what kind of project file and module
    * is asked for
    * @param fileKind  the text that describes the kind of project file
    * (e.g. name of HTML file). Is not null.
    * @param moduleKind  the desciption kind of module (e.g.
    * package/subdirectory). Null to not show the related label and text
    * field
    * @return  this
    */
   public SettingsWin setProjectFile(String fileKind, String moduleKind) {
      this.fileKind = fileKind;
      this.moduleKind = moduleKind;
      return this;
   }
   
   /**
    * Turns on asking for the name of a directory for source files.
    * The related text field and label is not shown if this method is
    * not called
    * @return  this
    */
   public SettingsWin useSourceDir() {
      this.useScr = true;
      return this;
   }
   
   /**
    * Turns on asking for the name of a directory for executable files.
    * The related text field and label is not shown if this method is
    * not called
    * @return  this
    */
   public SettingsWin useExecDir() {
      useExec = true;
      return this;
   }
   
   /**
    * Turns on asking for arguments in a start script. The related text
    * field and label is not shown if this method is not called.
    * @return  this
    */
   public SettingsWin useArgs() {
      this.useArgs = true;
      return this;
   }
   
   /**
    * Sets the text that describes what kind of build is asked for. The
    * related text field is not shown if this method not called.
    * @param  buildKind  he text that describes what kind of build is
    * asked for (e.g. name of jar file).
    * @return  this
    */
   public SettingsWin setBuildKind(String buildKind) {
      this.buildKind = buildKind;
      return this;
   }
   
   /**
    * Sets up this frame
    * @throws IllegalArgumentException if no description for the kind of
    * project file was set using {@link #setProjectFile(String, String)}
    */
   public void setupWindow() {
      if (fileKind == null) {
         throw new IllegalStateException("The settings window cannot be build");
      }
      setWindow();
   }
      

   /**
    * Makes this frame visible
    * @param isVisible  true to make this frame visible
    */
   public void makeVisible(boolean isVisible) {
      fileTf.requestFocus();
      frame.setVisible(isVisible);
   }      

   /**
    * Adds an {@code ActionListener} to this ok button
    * @param al  the {@code ActionListener};
    */
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }
   
   /**
    * @return  the input in the text field for the name of the project
    * file
    */
   String projectFileIn() {
      return fileTf.getText();
   }

   /**
    * @return  the input in the text field for the module/package
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
    * Shows in the related text field the name of the project file
    * @param fileName  te name of the main file of a project
    */
   public void displayFile(String fileName) {
      fileTf.setText(fileName);
   }

   /**
    * Shows in the related text field the name of a module/package
    * @param moduleName  the name of a module/package/namespace
    */
   public void displayModule(String moduleName) {
      moduleTf.setText(moduleName);
   }

   /**
    * Shows in the related text field the name of the directory that
    * contains source files
    * @param dirName  the name of the directory for source files
    */
   public void displaySourcesDir(String dirName) {
      sourcesDirTf.setText(dirName);
   }

   /**
    * Shows in the related text field the name of the directory
    * where executables are saved
    * @param in  the name of the directory for executable files
    */
   public void displayExecDir(String in) {
      execDirTf.setText(in);
   }
   
   /**
    * Shows in the related text field the name of a build
    * @param in  the name of a build
    */
   public void displayBuildName(String in) {
      buildTf.setText(in);
   }
   
   /**
    * @return  if the checkbox to save the text field inputs to a
    * local config file is selected
    */
   public boolean isSaveConfig() {
      return saveConfig.isSelected();
   }
   
   /**
    * @param isSelected  true select the checkbox for saving text
    * field inputs to a local config file
    */
   public void setSaveConfigSelected(boolean isSelected) {
      saveConfig.setSelected(isSelected);
   }

   private JPanel projectPanel() {
      int gridSize = 2;
      GridLayout grid = new GridLayout(gridSize, 0);
      JPanel projPnl = new JPanel(grid);

      // file panel
      JLabel fileLb = new JLabel(fileKind + ":");
      projPnl.add(holdLbAndTf(fileLb, fileTf));

      // module/subdir panel
      if (moduleKind != null) {
         gridSize++;
         grid.setRows(gridSize);
         JLabel moduleLb = new JLabel(moduleKind + ":");
         projPnl.add(holdLbAndTf(moduleLb, moduleTf));
      }
      //
      // sources panel
      if (useScr) {
         gridSize++;
         grid.setRows(gridSize);        
         JLabel sourcesDirLb = new JLabel("Source directory:");
         projPnl.add(holdLbAndTf(sourcesDirLb, sourcesDirTf));
      }
      //
      // executabled panel
      if (useExec) {
         gridSize++;
         grid.setRows(gridSize);
         JLabel execDirLb = new JLabel("Executable directory:");
         projPnl.add(holdLbAndTf(execDirLb, execDirTf));
      }

      projPnl.add(checkBxPnl(saveConfig, "Save settings in 'eadconfig' file"));

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
   
   private JPanel combineAll() {
      JPanel combineAll = new JPanel();
      combineAll.setLayout(new BoxLayout(combineAll, BoxLayout.Y_AXIS));
      combineAll.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      combineAll.add(projectPanel());
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

   private void setWindow() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(combineAll());
      frame.pack();
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON.getImage());
   }
}
