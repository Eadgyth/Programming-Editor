package eg.ui.projectsetting;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.event.ChangeEvent;

import java.io.File;

import java.util.List;

//--Eadgyth--/
import eg.BusyFunction;
import eg.FileChooser;
import eg.ui.IconFiles;
import eg.ui.UIComponents;
import eg.utils.ScreenParams;

/**
 * The window with input options to configure a programming project.
 * <p>
 * Input read from text fields is returned with leading and trailing
 * spaces removed.
 */
public class SettingsWindow {

   private static final Dimension DIM_TF = ScreenParams.scaledDimension(220, 14);
   private static final Dimension DIM_TF_LONG = ScreenParams.scaledDimension(300, 14);
   private static final Dimension DIM_TF_SHORT = ScreenParams.scaledDimension(100, 14);
   private static final Dimension DIM_VERT_SPACER = ScreenParams.scaledDimension(0, 14);
   private static final Dimension DIM_RIGHT_SPACER = ScreenParams.scaledDimension(28, 14);

   private static final FileChooser chooser = new FileChooser();

   private final JFrame     frame              = new JFrame();
   private final JTextField projDirTf          = new JTextField();
   private final JTextField fileTf             = new JTextField();
   private final JTextField moduleTf           = new JTextField();
   private final JTextField sourcesDirTf       = new JTextField();
   private final JTextField execDirTf          = new JTextField();
   private final JTextField cmdArgsTf          = new JTextField();
   private final JTextField cmdOptionsTf       = new JTextField();
   private final JTextField compileOptionsTf   = new JTextField();
   private final JTextField extensionsTf       = new JTextField();
   private final JTextField buildNameTf        = new JTextField();
   private final JTextField customRunCmdTf     = new JTextField();
   private final JTextField customCompileCmdTf = new JTextField();
   private final JTextField customBuildCmdTf   = new JTextField();
   private final JButton    okBt               = new JButton("   OK   ");
   private final JButton    cancelBt           = new JButton("Cancel");
   private final JCheckBox  saveConfigBx       = new JCheckBox();

   private JTabbedPane tb;

   private final BusyFunction bf;
   private final JPanel sourcePnl;

   private ListInputPanel librariesPnl;
   private ListInputPanel libModulesPnl;
   private JPanel runSettingsPnl;
   private JPanel buildSettingsPnl;
   private JPanel customCmdSettingsPnl;
   private boolean useLibs = false;
   private boolean useMods = false;
   private boolean useRunSettings = false;
   private boolean useBuildSettings = false;
   private boolean useCustomCmds = false;

   /**
    * Creates a <code>SettingsWindow</code>
    *
    * @param projectType  the name of the project type that is
    * shown in the window's title
    */
   public SettingsWindow(String projectType) {
      chooser.initSelectFileOrDirectoryChooser();
      sourcePnl = vertBoxPnl();
      addSourceSetting("Name of project directory:", projDirTf, DIM_TF, true);
      frame.getRootPane().setDefaultButton(okBt);
      frame.setTitle("Project settings (" + projectType + ")");
      bf = new BusyFunction(frame);
   }

   /**
    * Returns a new <code>InputOptionsBuilder</code>
    *
    * @return  the {@link SettingsWindow.InputOptionsBuilder}
    */
   public InputOptionsBuilder inputOptionsBuilder() {
      return new SettingsWindow.InputOptionsBuilder(this);
   }

   /**
    * Registers the listener on this ok button
    *
    * @param r  the Runnable invoked by the action
    */
   public void okAct(Runnable r) {
      okBt.addActionListener(e -> bf.execute(r));
   }

   /**
    * Registers the listener on this cancel button
    *
    * @param al  the ActionListener
    */
   public void setCancelAct(ActionListener al) {
      cancelBt.addActionListener(al);
   }

   /**
    * Registers the listener on this close button
    *
    * @param wa  the <code>WindowAdapter</code>
    */
   public void setDefaultCloseAct(WindowAdapter wa) {
      frame.addWindowListener(wa);
   }

   /**
    * Sets the directory that is or is contained in the putative
    * project directory or is already the known project directory
    *
    * @param dir  the directory
    */
   public void setDirectory(String dir) {
      if (useLibs) {
         librariesPnl.setDirectory(dir);
         librariesPnl.trySetProjectDir(projDirTf.getText().trim());
      }
      if (useMods) {
         libModulesPnl.setDirectory(dir);
         libModulesPnl.trySetProjectDir(projDirTf.getText().trim());
      }
      chooser.setDirectory(dir);
   }

   /**
    * Makes this frame visible or invisible
    *
    * @param b  true for visible, false for invisible
    */
   public void setVisible(boolean b) {
      EventQueue.invokeLater(() -> {
         frame.setVisible(b);
         if (tb != null && b) {
            tb.requestFocusInWindow();
         }
      });
   }

   /**
    * Returns the input for the name of a project directory
    *
    * @return  the input
    */
   public String projDirInput() {
      return projDirTf.getText().trim();
   }

   /**
    * Returns the input for the name of a main project file
    *
    * @return  the input
    */
   public String filenameInput() {
      return fileTf.getText().trim();
   }

   /**
    * Returns the input for a module name
    *
    * @return  the input
    */
   public String moduleInput() {
      return moduleTf.getText().trim();
   }

   /**
    * Returns the input for a source directory
    *
    * @return  the input
    */
   public String sourcesDirInput() {
      return sourcesDirTf.getText().trim();
   }

   /**
    * Returns the input for directory for executables
    *
    * @return  the input
    */
   public String execDirInput() {
      return execDirTf.getText().trim();
   }

   /**
    * Assigns the input for libraries to the specified list if the
    * option to set libraries is added by a project
    *
    * @param l  the list
    */
   public void assignLibrariesInput(List<String> l) {
      if (useLibs) {
         librariesPnl.assignListInput(l);
      }
   }

   /**
    * Assigns the input for library modules to the specified list if
    * this option is added by a project
    *
    * @param l  the list
    */
   public void assignLibModulesInput(List<String> l) {
      if (useMods) {
         libModulesPnl.assignListInput(l);
      }
   }

   /**
    * Returns the input for a custom run command
    *
    * @return  the input
    */
   public String customRunCmdInput() {
      return customRunCmdTf.getText().trim();
   }

   /**
    * Returns the input for a custom compile command
    *
    * @return  the input
    */
   public String customCompileCmdInput() {
      return customCompileCmdTf.getText().trim();
   }

   /**
    * Returns the input for a custom build command
    *
    * @return  the input
    */
   public String customBuildCmdInput() {
      return customBuildCmdTf.getText().trim();
   }

   /**
    * Returns the input for command options
    *
    * @return  the input
    */
   public String cmdOptionsInput() {
      return cmdOptionsTf.getText().trim();
   }

   /**
    * Returns the input for command arguments
    *
    * @return  the input
    */
   public String cmdArgsInput() {
      return cmdArgsTf.getText().trim();
   }

   /**
    * Returns the input for a compile option
    *
    * @return  the input
    */
   public String compileOptionsInput() {
      return compileOptionsTf.getText().trim();
   }

   /**
    * Returns the input for file extensions
    * <p>
    * Extensions may be entered as comma, semicolon or space separated
    * but the returned string is formatted as comma separated
    *
    * @return  the input
    */
    public String fileExtensionsInput() {
       return extensionsTf.getText().trim().replaceAll("[\\s,;]+", ",");
    }

   /**
    * Returns the input for the name of a build
    *
    * @return  the input
    */
   public String buildNameInput() {
      return buildNameTf.getText().trim();
   }

   /**
    * Shows in the corresponding text field the name of the project
    * root directory
    *
    * @param s  the name
    */
   public void displayProjDir(String s) {
      projDirTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the name of the project file
    *
    * @param s  the name
    */
   public void displayFilename(String s) {
      fileTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the name of module
    *
    * @param s  the name
    */
   public void displayModule(String s) {
      moduleTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the sources directory
    *
    * @param s  the name
    */
   public void displaySourcesDir(String s) {
      sourcesDirTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the name of the executables
    * directory
    *
    * @param s  the name
    */
   public void displayExecDir(String s) {
      execDirTf.setText(s);
   }

   /**
    * Shows in the corresponding text fields the libraries if this
    * option is added by a project
    *
    * @param l  the list of libraries
    */
   public void displayLibraries(List<String> l) {
      if (useLibs) {
         librariesPnl.displayList(l);
      }
   }

   /**
    * Shows in the corresponding text fields the library modules
    * if this option is added by a project
    *
    * @param l  the list of libraries
    */
   public void displayLibModules(List<String> l) {
      if (useMods) {
         libModulesPnl.displayList(l);
      }
   }

   /**
    * Shows in the corresponding text field a custom run command
    *
    * @param s  the custom command
    */
   public void displayCustomRunCmd(String s) {
      customRunCmdTf.setText(s);
   }

   /**
    * Shows in the corresponding text field a custom compile command
    *
    * @param s  the custom command
    */
   public void displayCustomCompileCmd(String s) {
      customCompileCmdTf.setText(s);
   }

   /**
    * Shows in the corresponding text field a custom build command
    *
    * @param s  the custom command
    */
   public void displayCustomBuildCmd(String s) {
      customBuildCmdTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the command arguments
    *
    * @param s  the name
    */
   public void displayCmdArgs(String s) {
      cmdArgsTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the command options
    *
    * @param s  the command options
    */
   public void displayCmdOptions(String s) {
      cmdOptionsTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the compile options
    *
    * @param s  the compile options
    */
   public void displayCompileOptions(String s) {
      compileOptionsTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the string that contains
    * file extensions
    *
    * @param s  the file extensions
    */
   public void displayFileExtensions(String s) {
      extensionsTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the name for a build
    *
    * @param s  the name
    */
   public void displayBuildName(String s) {
      buildNameTf.setText(s);
   }

   /**
    * Returns if the option to save project parameters to a
    * "ProjConfig" file is selected in the correponding checkbox
    *
    * @return  true if selected
    */
   public boolean isSaveToProjConfig() {
      return saveConfigBx.isSelected();
   }

   /**
    * Selects or unselects the checkbox for setting if project
    * parameters are saved in a "ProjConfig" file
    *
    * @param isSelected  true to select
    */
   public void setSaveProjConfigSelected(boolean isSelected) {
      saveConfigBx.setSelected(isSelected);
   }

   /**
    * The building of the content of <code>SettingsWindow</code> with
    * selectable input options.
    * <p>
    * The field in which the name of a project directory is entered is
    * fixed and also is the topmost field in the 'Source' panel (or tab).
    */
   public static class InputOptionsBuilder {

      private final SettingsWindow sw;

      private boolean useCustomCmd = false;
      private boolean useMainFile = false;

      /**
       * Adds the option to enter a name for a main project file in the
       * 'Source' panel
       *
       * @param label  the label for the file input  option
       * @param browse true to add a 'browser' button
       * @return  this
       */
      public InputOptionsBuilder addFileInput(String label, boolean browse) {
         String s = label + ":";
         sw.addSourceSetting(s, sw.fileTf, DIM_TF, browse);
         useMainFile = true;
         return this;
      }

      /**
       * Adds the option to enter a source directory in the 'Source'
       * panel
       *
       * @param label  the label for input option
       * @return  this
       */
      public InputOptionsBuilder addSourceDirInput(String label) {
         String s = label + ":";
         sw.addSourceSetting(s, sw.sourcesDirTf, DIM_TF, true);
         return  this;
      }

      /**
       * Adds the option to enter a module name in the 'Source' panel.
       *
       * @param label  the label for the input option
       * @return  this
       */
      public InputOptionsBuilder addModuleNameInput(String label) {
         String s = label + ":";
         sw.addSpacer(sw.sourcePnl, SettingsWindow.DIM_VERT_SPACER);
         sw.addSourceSetting(s, sw.moduleTf, DIM_TF_SHORT, false);
         return this;
      }

      /**
       * Adds the option to enter a destination directory for compiled
       * files in the 'Source' panel
       *
       * @param label  the label for the input option
       * @return  this
       */
      public InputOptionsBuilder addExecDirInput(String label) {
         String s = label + ":";
         sw.addSpacer(sw.sourcePnl, SettingsWindow.DIM_VERT_SPACER);
         sw.addSourceSetting(s, sw.execDirTf, DIM_TF_SHORT, false);
         return this;
      }

      /**
       * Adds the option to enter libraries in the 'Libraries' panel
       *
       * @param label  the label for the input option
       * @return  this
       */
      public InputOptionsBuilder addLibrariesInput(String label) {
         sw.addLibrariesSetting(label);
         return this;
      }

      /**
       * Adds the option to enter library modules in the 'Library
       * modules' panel
       *
       * @param label  the label for the input option
       * @return  this
       */
      public InputOptionsBuilder addLibModulesInput(String label) {
         sw.addLibModulesSetting(label);
         return this;
      }

      /**
       * Adds the option to enter custom commnds labeled with 'Compile',
       * 'Run' and 'Build' in the 'Commands' panel. This option cannot
       * be combined with the option to enter a main project file.
       *
       * @return  this
       */
      public InputOptionsBuilder addCustomCommandInput() {
         sw.addCustomCmdSetting();
         useCustomCmd = true;
         return this;
      }

      /**
       * Adds the option to enter command options in the 'Run' panel
       *
       * @return  this
       */
      public InputOptionsBuilder addCmdOptionsInput() {
         String s = "Command options:";
         sw.addRunSetting(s, sw.cmdOptionsTf, false);
         return this;
      }

      /**
       * Adds the option to enter command arguments in the 'Run' panel
       *
       * @return  this
       */
      public InputOptionsBuilder addCmdArgsInput() {
         String s = "Command arguments:";
         sw.addRunSetting(s, sw.cmdArgsTf, false);
         return this;
      }

      /**
       * Adds the option to enter compiler options in the
       * 'Compile/build' panel
       *
       * @return  this
       */
      public InputOptionsBuilder addCompileOptionsInput() {
         String s = "Compiler options:";
         sw.addBuildSetting(s, sw.compileOptionsTf, false);
         return this;
      }

      /**
       * Adds the option to enter file extensions
       *
       * @param label  the label for the input option
       * @return  this
       */
       public InputOptionsBuilder addFileExtensionsInput(String label) {
          String s = label + ":";
          sw.addBuildSetting(s, sw.extensionsTf, false);
          return this;
       }

      /**
       * Adds the option to enter a build name in the 'Compile/build'
       * panel
       *
       * @param label  the label for the input option
       * @return  this
       */
      public InputOptionsBuilder addBuildNameInput(String label) {
         String s = label + ":";
         sw.addBuildSetting(s, sw.buildNameTf, false);
         return this;
      }

      /**
       * Builds the content of <code>SettingsWindow</code>.
       *
       * @throws IllegalStateException  if the window has been built
       * by the same project or if the options 'file input' and
       * 'custom commands' are added.
       */
      public void buildWindow() {
         if (sw.frame.getContentPane().getComponentCount() > 0) {
            throw new IllegalStateException(
                  "The project settings window has been"
                  + "built already by the same project.");
         }
         if (useMainFile && useCustomCmd) {
            throw new IllegalStateException(
                  "Input options for both file and custom "
                  + "command are not permitted");
         }
         sw.initWindow();
      }

      //
      //--private--/
      //

      private InputOptionsBuilder(SettingsWindow sw) {
         this.sw = sw;
      }
   }

   //
   //--private--/
   //

   private void addSourceSetting(String label, JTextField tf, Dimension tfDim,
         boolean useBrowser) {

      sourcePnl.add(singleTextfieldPnl(label, tf, tfDim, useBrowser));
   }

   private void addCustomCmdSetting() {
      useCustomCmds = true;
      customCmdSettingsPnl = vertBoxPnl();
      customCmdSettingsPnl.add(UIComponents.labelPanel(
            "Enter a system command in the fields where needed:"));

      customCmdSettingsPnl.add(
            singleTextfieldPnl("Compile:", customCompileCmdTf, DIM_TF_LONG, false));
      customCmdSettingsPnl.add(
            singleTextfieldPnl("Run:", customRunCmdTf, DIM_TF_LONG, false));
      customCmdSettingsPnl.add(
            singleTextfieldPnl("Build:", customBuildCmdTf, DIM_TF_LONG, false));
   }

   private void addRunSetting(String label, JTextField tf, boolean useBrowser) {
      if (runSettingsPnl == null) {
         runSettingsPnl = vertBoxPnl();
         useRunSettings = true;
      }
      runSettingsPnl.add(singleTextfieldPnl(label, tf,  DIM_TF, useBrowser));
   }

   private void addBuildSetting(String label, JTextField tf, boolean useBrowser) {
      if (buildSettingsPnl == null) {
         buildSettingsPnl = vertBoxPnl();
         useBuildSettings = true;
      }
      buildSettingsPnl.add(singleTextfieldPnl(label, tf, DIM_TF, useBrowser));
   }

   private void addLibrariesSetting(String label) {
      librariesPnl = new ListInputPanel(label, chooser);
      useLibs = true;
   }

   private void addLibModulesSetting(String label) {
      libModulesPnl = new ListInputPanel(label, chooser);
      useMods = true;
   }

   private JPanel singleTextfieldPnl(String label, JTextField tf, Dimension tfDim,
         boolean useBrowser) {

      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JLabel lb = new JLabel(label);
      lb.setFont(ScreenParams.scaledFontToBold(lb.getFont(), 8));
      tf.setFont(ScreenParams.scaledFontToPlain(tf.getFont(), 8));
      tf.setPreferredSize(tfDim);
      pnl.add(lb);
      pnl.add(tf);
      if (useBrowser) {
         JButton bt = new JButton("...");
         bt.addActionListener(e -> setText(tf));
         bt.setFocusable(false);
         bt.setPreferredSize(DIM_RIGHT_SPACER);
         pnl.add(bt);
      }
      else {
         addSpacer(pnl, DIM_RIGHT_SPACER);
      }
      return pnl;
   }

   private void addSpacer(JPanel toAdd, Dimension d) {
      JPanel spacer = new JPanel();
      spacer.setPreferredSize(d);
      toAdd.add(spacer);
   }

   private void setText(JTextField tf) {
      File file = chooser.selectedFileOrDirectory();
      if (file != null) {
         String text = file.getName();
         tf.setText(text);
         tf.requestFocusInWindow();
      }
   }

   private void initWindow() {
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setVisible(false);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.getContentPane().add(contentPnl());
      frame.pack();
      frame.setLocationRelativeTo(null);
   }

   private JPanel contentPnl() {
      JPanel pnl = vertBoxPnl();
      pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      if (useLibs || useRunSettings || useBuildSettings || useCustomCmds) {
         pnl.add(tabPane());
      }
      else {
         pnl.add(textfieldsHolderPnl(sourcePnl));
      }
      pnl.add(checkBxPnl());
      pnl.add(Box.createRigidArea(DIM_VERT_SPACER));
      pnl.add(buttonsPnl());
      return pnl;
   }

   private JTabbedPane tabPane() {
      tb = new JTabbedPane();
      tb.setFont(ScreenParams.scaledFontToPlain(tb.getFont(), 8));
      tb.add("Sources", textfieldsHolderPnl(sourcePnl));
      addListInputTabs(tb);
      if (useRunSettings) {
         tb.add("Run", textfieldsHolderPnl(runSettingsPnl));
      }
      if (useBuildSettings) {
         tb.add("Compile/build", textfieldsHolderPnl(buildSettingsPnl));
      }
      if (useCustomCmds) {
         tb.add("Commands", textfieldsHolderPnl(customCmdSettingsPnl));
      }
      tb.setSelectedIndex(0);
      return tb;
   }

   private void addListInputTabs(JTabbedPane tb) {
      int i = 0;
      if (useLibs) {
         i = 1;
         tb.add("Libraries", librariesPnl.content());
      }
      if (useMods) {
         i = 2;
         tb.add("Library modules", libModulesPnl.content());
      }
      if (i > 0) {
         int iFin = i;
         tb.addChangeListener((ChangeEvent e) -> {
            JTabbedPane sourceTb = (JTabbedPane) e.getSource();
            int iSel = sourceTb.getSelectedIndex();
            if (iSel == 1) {
               librariesPnl.updateWhenSetVisible();
               librariesPnl.trySetProjectDir(projDirTf.getText().trim());
            }
            else if (iFin == 2 && iSel == 2) {
               libModulesPnl.updateWhenSetVisible();
               libModulesPnl.trySetProjectDir(projDirTf.getText().trim());
            }
         });
      }
   }

   private JPanel textfieldsHolderPnl(JPanel content) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      pnl.add(content);
      return pnl;
   }

   private JPanel vertBoxPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      return pnl;
   }

   private JPanel checkBxPnl() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      String s = "Save \'ProjConfig\' file in the project to retrieve settings";
      JLabel lb = new JLabel(s);
      lb.setFont(ScreenParams.scaledFontToBold(lb.getFont(), 8));
      pnl.add(lb);
      pnl.add(saveConfigBx);
      saveConfigBx.setFocusable(false);
      return pnl;
   }

   private JPanel buttonsPnl() {
      JPanel pnl = new JPanel(new FlowLayout());
      pnl.add(okBt);
      pnl.add(cancelBt);
      return pnl;
   }
}
