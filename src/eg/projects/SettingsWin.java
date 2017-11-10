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
import eg.utils.ScreenParams;

/**
 * The window for the configuration of a project
 */
public class SettingsWin {

   private final static Dimension DIM_TF = ScreenParams.scaledDimension(200, 16);
   private final static Dimension DIM_SPACER = ScreenParams.scaledDimension(0, 20);

   private final JFrame frame = new JFrame("Project settings");
   private final JTextField fileTf       = new JTextField();
   private final JTextField moduleTf     = new JTextField();
   private final JTextField sourcesDirTf = new JTextField();
   private final JTextField execDirTf    = new JTextField();
   private final JTextField projDirTf    = new JTextField();
   private final JTextField argsTf       = new JTextField();
   private final JTextField buildTf      = new JTextField();
   private final JButton    okBt         = new JButton("   OK   ");
   private final JButton    cancelBt     = new JButton("Cancel");
   private final JCheckBox  saveConfig   = new JCheckBox();

   private String fileLabel = "";
   private String moduleLabel = null;
   private boolean useScr = false;
   private boolean useExec = false;
   private boolean useArgs = false;
   private String buildLabel = null;

   /**
    * Returns a new SettingsWin where only the name for a project file and
    * for a project root can be entered
    * @param fileLabel  the label for the file text field
    * @return  a new SettingsWin
    */
   public static SettingsWin basicWindow(String fileLabel) {
      return new SettingsWin(fileLabel, true);
   }

   /**
    * Returns a new SettingsWin whose content is set up afterwards by choosing
    * from the methods that add input options.
    * <p>
    * The method {@link #setupWindow()} must be invoked (lastly) to initialize
    * the window. Calling only this method yields a SettingsWin that equals to
    * a {@link #basicWindow(String)}.
    * <p>
    * @param fileLabel  the label for the file text field
    * @return  a new SettingsWin
    */
   public static SettingsWin adaptableWindow(String fileLabel) {
      return new SettingsWin(fileLabel, false);
   }

   /**
    * Adds the option to enter a name for a module/subdirectory and
    * sets the label for the corresponding text field
    *
    * @param moduleLabel  the label
    * @return  this
    */
   public SettingsWin addModuleOption(String moduleLabel) {
      this.moduleLabel = moduleLabel;
      return this;
   }

   /**
    * Adds the option to enter a name of a directory where source
    * files are stored
    * @return  this
    */
   public SettingsWin addSourceDirOption() {
      this.useScr = true;
      return this;
   }

   /**
    * Adds the option to enter a name of a directory where executable
    * files are stored
    * @return  this
    */
   public SettingsWin addExecDirOption() {
      useExec = true;
      return this;
   }

   /**
    * Adds the option to enter arguments for a start script.
    * @return  this
    */
   public SettingsWin addArgsOption() {
      this.useArgs = true;
      return this;
   }

   /**
    * Adds the option to enter a build name and sets the label for the
    * corresponding text field
    * @param  buildLabel for the build text field
    * @return  this
    */
   public SettingsWin addBuildOption(String buildLabel) {
      this.buildLabel = buildLabel;
      return this;
   }

   /**
    * Sets up this frame
    */
   public void setupWindow() {
      if (frame.getContentPane().getComponentCount() > 0) {
         throw new IllegalStateException("The frame of this SettingsWin"
               + " is already initialized");
      }
      initWindow();
   }
   
   /**
    * Asks the text field for setting the project file to get focus
    */
   public void focusInFileTextField() {
      fileTf.requestFocusInWindow();
   }

   /**
    * Adds an <code>ActionListener</code> to this ok button
    *
    * @param al  the <code>ActionListener</code>;
    */
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }

   /**
    * Makes this frame visible or invisible, depending on the
    * specified boolean value 
    *
    * @param b  the boolean
    */
   public void setVisible(boolean b) {
      fileTf.requestFocus();
      frame.setVisible(b);
   }

   /**
    * Returns the input in the text field for the name of a project
    * file
    * @return  the the name of a project file
    */
   public String projectFileNameInput() {
      return fileTf.getText();
   }

   /**
    * Returns the input in the text field for the name of a module/package
    * @return  the name of a module/package
    */
   public String moduleNameInput() {
      return moduleTf.getText();
   }

   /**
    * Returns the input in the text field for the name of a directory for
    * source files
    * @return  the name of a directory for source files
    */
   public String sourcesDirNameInput() {
      return sourcesDirTf.getText();
   }

   /**
    * Returns the input in the text field for the name of a directory for
    * executables
    * @return  the name of a directory for executables
    */
   public String execDirNameInput() {
      return execDirTf.getText();
   }

   /**
    * Returns the input in the text field for the name of a project root
    * directory
    * @return  the name of a project root directory
    */
   public String projDirNameInput() {
      return projDirTf.getText();
   }

   /**
    * Returns the input in the text field for arguments
    * @return  the arguments
    */
   public String argsInput() {
      return argsTf.getText();
   }

   /**
    * Returns the input in the text field for a name of a build
    * @return  a name of a build
    */
   public String buildNameInput() {
      return buildTf.getText();
   }

   /**
    * Shows in the corresponding text field the name of the main project
    * file
    * @param fileName  the name of the main project
    */
   public void displayFile(String fileName) {
      fileTf.setText(fileName);
   }

   /**
    * Shows in the corresponding text field the name of a
    * module/package/namespace
    * @param moduleName  the name of a module/package/namespace
    */
   public void displayModule(String moduleName) {
      moduleTf.setText(moduleName);
   }

   /**
    * Shows in the corresponding text field the name of the directory that
    * contains source files
    * @param dirName  the name of the directory for source files
    */
   public void displaySourcesDir(String dirName) {
      sourcesDirTf.setText(dirName);
   }

   /**
    * Shows in the corresponding text field the name of the directory
    * where executables are saved
    * @param in  the name of the directory for executable files
    */
   public void displayExecDir(String in) {
      execDirTf.setText(in);
   }

   /**
    * Shows in the corresponding text field the name of the project's
    * root directory
    * @param in  the name of the the project's root directory
    */
   public void displayProjDirName(String in) {
      projDirTf.setText(in);
   }

   /**
    * Shows in the corresponding text field the name of a build
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

   //
   //--private--/
   //
   
   private SettingsWin(String fileLabel, boolean initWindow) {
      this.fileLabel = fileLabel + " (without Ext.)";
      if (initWindow) {
         initWindow();
      }
   }

   private JPanel structurePanel() {
      int gridSize = 3;
      GridLayout grid = new GridLayout(gridSize, 0);
      JPanel projPnl = new JPanel(grid);

      // file panel
      JLabel fileLb = new JLabel(fileLabel + ":");
      projPnl.add(holdLbAndTf(fileLb, fileTf));

      // module/subdir panel
      if (moduleLabel != null) {
         gridSize++;
         grid.setRows(gridSize);
         JLabel moduleLb = new JLabel(moduleLabel + ":");
         projPnl.add(holdLbAndTf(moduleLb, moduleTf));
      }
      //
      // sources panel
      if (useScr) {
         gridSize++;
         grid.setRows(gridSize);
         JLabel sourcesDirLb = new JLabel("Name of sources directory:");
         projPnl.add(holdLbAndTf(sourcesDirLb, sourcesDirTf));
      }
      //
      // executabled panel
      if (useExec) {
         gridSize++;
         grid.setRows(gridSize);
         JLabel execDirLb = new JLabel("Name of executables directory:");
         projPnl.add(holdLbAndTf(execDirLb, execDirTf));
      }

      JLabel projDirLb = new JLabel("Name of project root (input not rqd.):");
      projPnl.add(holdLbAndTf(projDirLb, projDirTf));
      projPnl.add(checkBxPnl(saveConfig, "Store settings in 'eadconfig' file"));

      projPnl.setBorder(titledBorder("Structure"));
      return projPnl;
   }

   private JPanel argsPanel() {
      JPanel argsPnl = new JPanel( new GridLayout(1, 0));
      JLabel argsLb = new JLabel("Arguments:");
      argsPnl.add(holdLbAndTf(argsLb, argsTf));
      argsPnl.setBorder(titledBorder("Startscript"));
      return argsPnl;
   }

   private JPanel buildPanel() {
      JPanel buildPnl = new JPanel(new GridLayout(1, 0));
      JLabel buildLb = new JLabel("Name for " + buildLabel +":");
      buildPnl.add(holdLbAndTf(buildLb, buildTf));
      buildPnl.setBorder(titledBorder("Build (" + buildLabel + ")"));
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
      lb.setFont(eg.Constants.SANSSERIF_BOLD_9);
      tf.setFont(eg.Constants.SANSSERIF_PLAIN_9);
      tf.setPreferredSize(DIM_TF);
      holdPnl.add(lb);
      holdPnl.add(tf);
      return holdPnl;
   }

   private TitledBorder titledBorder(String title) {
      TitledBorder tBorder = BorderFactory.createTitledBorder
            (new LineBorder(Color.BLACK, 1), title);
      tBorder.setTitleFont(eg.Constants.VERDANA_PLAIN_8);
      return tBorder;
   }

   private JPanel checkBxPnl(JCheckBox checkBox, String title) {
      JPanel holdPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JLabel label = new JLabel(title);
      label.setFont(Constants.SANSSERIF_BOLD_9);
      holdPnl.add(label);
      holdPnl.add(checkBox);
      return holdPnl;
   }

   private JPanel combineAll() {
      JPanel combineAll = new JPanel();
      combineAll.setLayout(new BoxLayout(combineAll, BoxLayout.Y_AXIS));
      combineAll.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      combineAll.add(structurePanel());
      if (useArgs) {
         combineAll.add(Box.createRigidArea(DIM_SPACER));
         combineAll.add(argsPanel());
      }
      if (buildLabel != null) {
         combineAll.add(Box.createRigidArea(DIM_SPACER));
         combineAll.add(buildPanel());
      }
      combineAll.add(Box.createRigidArea(DIM_SPACER));
      combineAll.add(buttonsPanel());
      return combineAll;
   }

   private void initWindow() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.getContentPane().add(combineAll());
      frame.pack();
   }
}
