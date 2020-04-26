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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;

import javax.swing.event.ChangeEvent;

import java.io.File;

import java.util.List;

//--Eadgyth--//
import eg.BusyFunction;
import eg.FileChooser;
import eg.ui.IconFiles;
import eg.utils.ScreenParams;

/**
 * The window with input options to configure a programming project
 */
public class SettingsWindow {

   private static final Dimension DIM_TF = ScreenParams.scaledDimension(220, 14);
   private static final Dimension DIM_TF_LONG = ScreenParams.scaledDimension(300, 14);
   private static final Dimension DIM_TF_SHORT = ScreenParams.scaledDimension(100, 14);
   private static final Dimension DIM_VERT_SPACER = ScreenParams.scaledDimension(0, 14);
   private static final Dimension DIM_RIGHT_SPACER = ScreenParams.scaledDimension(28, 14);

   private static FileChooser chooser = new FileChooser();

   private final JFrame     frame            = new JFrame("Project settings");
   private final JTextField projDirTf        = new JTextField();
   private final JTextField fileTf           = new JTextField();
   private final JTextField customCmdTf      = new JTextField();
   private final JTextField sourcesDirTf     = new JTextField();
   private final JTextField execDirTf        = new JTextField();
   private final JTextField cmdArgsTf        = new JTextField();
   private final JTextField cmdOptionsTf     = new JTextField();
   private final JTextField compileOptionsTf = new JTextField();
   private final JTextField extensionsTf     = new JTextField();
   private final JTextField buildNameTf      = new JTextField();
   private final JButton    okBt             = new JButton("   OK   ");
   private final JButton    cancelBt         = new JButton("Cancel");
   private final JCheckBox  saveConfigBx     = new JCheckBox();

   private final BusyFunction bf;
   private final JPanel sourcePnl;

   private ListInputPanel librariesPnl;
   private JPanel runSettingsPnl;
   private JPanel buildSettingsPnl;
   private boolean useLibs = false;
   private boolean useRunSettings = false;
   private boolean useBuildSettings = false;
   private Component focusedComponent = projDirTf;

   public SettingsWindow() {
      chooser.initSelectFileOrDirectoryChooser();
      sourcePnl = vertBoxPnl();
      addSourceSetting("Name of project directory:", projDirTf, true);
      cancelBt.setFocusable(false);
      okBt.setFocusable(false);
      frame.getRootPane().setDefaultButton(okBt);
      bf = new BusyFunction(frame);
   }

   /**
    * Gets a new <code>InputOptionsBuilder</code>
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
    * Sets the directory for the file chooser used by objects
    * of <code>SettingsWindow</code>.
    *
    * @param dir  the directory
    */
   public void setDirectory(String dir) {
      chooser.setDirectory(dir);
   }

   /**
    * Makes this frame visible or invisible
    *
    * @param b  true for visible, false for invisible
    */
   public void setVisible(boolean b) {
      if (b) {
         focusedComponent.requestFocusInWindow();
      }
      else {
         focusedComponent = frame.getFocusOwner();
      }
      EventQueue.invokeLater(() -> frame.setVisible(b));
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
    * option to set libraries is added
    *
    * @param l  the list
    */
   public void assignLibrariesInput(List<String> l) {
      if (useLibs) {
         librariesPnl.assignListInput(l);
      }
   }

   /**
    * Returns the input for a custom command
    *
    * @return  the input
    */
   public String customCmdInput() {
      return customCmdTf.getText().trim();
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
    * Returns the input for extensions of files that can be used for a
    * compilation/build in addition to source files
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
    * Shows in the corresponding text field the name of the sources
    * directory
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
    * Displays in the corresponding text field a custom commannd
    *
    * @param s  the custom command
    */
   public void displayCustomCmd(String s) {
      customCmdTf.setText(s);
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
    * Shows in the corresponding text field the compile option
    *
    * @param s  the compile option
    */
   public void displayCompileOptions(String s) {
      compileOptionsTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the string that contains
    * extensions of files that can be used for a compilation/build in
    * addition to source files
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
    * Returns if the option to save project parameters to an
    * "ProjConfig" file is selected in the correponding checkbox
    *
    * @return  true if selected
    */
   public boolean isSaveToProjConfig() {
      return saveConfigBx.isSelected();
   }

   /**
    * Selects or unselects the checkbox for setting if project
    * parameters are saved in an "ProjConfig" file
    *
    * @param isSelected  true to select
    */
   public void setSaveProjConfigSelected(boolean isSelected) {
      saveConfigBx.setSelected(isSelected);
   }

    /**
    * The building of the content of <code>SettingsWindow</code> with
    * selectable input options
    */
   public static class InputOptionsBuilder {

      private final SettingsWindow sw;

      private boolean useCustomCmd = false;
      private boolean useMainFile = false;

      private InputOptionsBuilder(SettingsWindow sw) {
         this.sw = sw;
      }

      /**
       * Adds the option to enter a name for a main project file in the
       * 'Source' panel
       *
       * @param label  the label for the file input
       * @return  this
       */
      public InputOptionsBuilder addFileInput(String label) {
         String s = label + ":";
         sw.addSourceSetting(s, sw.fileTf, true);
         useMainFile = true;
         return this;
      }

      /**
       * Adds the option to enter a name for a source folder in the
       * 'Source' panel
       *
       * @return  this
       */
      public InputOptionsBuilder addSourceDirInput() {
         String s = "Source directory (relative to project):";
         sw.addSourceSetting(s, sw.sourcesDirTf, true);
         return  this;
      }

      /**
       * Adds the option to enter a name of a destination folder
       * for compiled files in the 'Source' panel. The input field
       * has a vertical distance to the previous one.
       *
       * @param label  the label for the input option
       * @return  this
       */
      public InputOptionsBuilder addExecDirInput(String label) {
         String s = label + ":";
         sw.addSpacer(sw.sourcePnl);
         sw.addSourceSetting(s, sw.execDirTf, false);
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
       * Adds the option to enter a custom commnd in the 'Run' panel
       *
       * @return  this
       */
      public InputOptionsBuilder addCustomCommandInput() {
         String s = "System command:";
         sw.addRunSetting(s, sw.customCmdTf, false);
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
       * Adds the option to enter extensions of files that can be used
       * for compiling/build in addition to source files
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
       * If none of the methods to add input options is invoked before
       * only the field to enter the name of the project directory
       * is shown.
       *
       * @throws IllegalStateException  if the window has been built
       * by the same project or if a project adds the options file
       * input and custom command
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
   }

   //
   //--private--/
   //

   private void addSourceSetting(String label, JTextField tf, boolean useBrowser) {
      sourcePnl.add(singleTextfieldPnl(label, tf, useBrowser));
   }

   private void addRunSetting(String label, JTextField tf, boolean useBrowser) {
      if (runSettingsPnl == null) {
         runSettingsPnl = vertBoxPnl();
         useRunSettings = true;
      }
      runSettingsPnl.add(singleTextfieldPnl(label, tf, useBrowser));
   }

   private void addBuildSetting(String label, JTextField tf, boolean useBrowser) {
      if (buildSettingsPnl == null) {
         buildSettingsPnl = vertBoxPnl();
         useBuildSettings = true;
      }
      buildSettingsPnl.add(singleTextfieldPnl(label, tf, useBrowser));
   }

   private void addLibrariesSetting(String label) {
      librariesPnl = new ListInputPanel(label);
      useLibs = true;
   }

   private void addSpacer(JPanel toAdd) {
      JPanel spacer = new JPanel();
      spacer.setPreferredSize(DIM_VERT_SPACER);
      toAdd.add(spacer);
   }

   private JPanel singleTextfieldPnl(String label, JTextField tf,
         boolean useBrowser) {

      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JLabel lb = new JLabel(label);
      lb.setFont(ScreenParams.scaledFontToBold(lb.getFont(), 8));
      tf.setFont(ScreenParams.scaledFontToPlain(tf.getFont(), 8));
      Dimension d;
      if (tf == customCmdTf) {
         d = DIM_TF_LONG;
      }
      else if (tf == execDirTf) {
         d = DIM_TF_SHORT;
      }
      else {
         d = DIM_TF;
      }
      tf.setPreferredSize(d);
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
         JPanel spacer = new JPanel();
         spacer.setPreferredSize(DIM_RIGHT_SPACER);
         pnl.add(spacer);
      }
      return pnl;
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
      frame.setLocation(550, 100);
      frame.setVisible(false);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.getContentPane().add(contentPnl());
      frame.pack();
   }

   private JPanel contentPnl() {
      JPanel pnl = vertBoxPnl();
      pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      if (useLibs || useRunSettings || useBuildSettings) {
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
      JTabbedPane tb = new JTabbedPane();
      tb.setFont(ScreenParams.scaledFontToPlain(tb.getFont(), 8));
      tb.add("Sources", textfieldsHolderPnl(sourcePnl));
      if (useLibs) {
         tb.add("Libraries", listHolderPnl(librariesPnl.content()));
         tb.addChangeListener((ChangeEvent e) -> {
            JTabbedPane sourceTb = (JTabbedPane) e.getSource();
            int i = sourceTb.getSelectedIndex();
            if (i == 1 && !sourceTb.hasFocus()) {
               librariesPnl.setLastFocus();
            }
         });
         tb.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
               librariesPnl.disableButtons();
            }
         });
      }
      if (useRunSettings) {
         tb.add("Run", textfieldsHolderPnl(runSettingsPnl));
      }
      if (useBuildSettings) {
         tb.add("Compile/build", textfieldsHolderPnl(buildSettingsPnl));
      }
      tb.setSelectedIndex(0);
      return tb;
   }

   private JPanel textfieldsHolderPnl(JPanel content) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      pnl.add(content);
      return pnl;
   }

   private JPanel listHolderPnl(JPanel content) {
      JPanel pnl = vertBoxPnl();
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
