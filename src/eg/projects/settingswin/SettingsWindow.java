package eg.projects.settingswin;

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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;

import javax.swing.event.ChangeEvent;

import java.util.List;

//--Eadgyth--//
import eg.BusyFunction;
import eg.FileChooser;
import eg.ui.IconFiles;
import eg.utils.ScreenParams;

/**
 * The window that shows input options to configure commands to run, compile
 * or build a program
 */
public class SettingsWindow {

   private final static Dimension DIM_TF = ScreenParams.scaledDimension(200, 14);
   private final static Dimension DIM_SPACER = ScreenParams.scaledDimension(0, 14);

   private final JFrame frame = new JFrame("Project settings");
   private final BusyFunction bf;

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
   private final JCheckBox  saveConfig       = new JCheckBox();

   private static FileChooser CHOOSER = null;

   private ListInputPanel librariesPnl;
   private String fileLabel = null;
   private boolean useCustomCmd = false;
   private boolean useSrcDir = false;
   private boolean useExecDir = false;
   private boolean useLibs = false;
   private boolean useCmdOptions = false;
   private boolean useCmdArgs = false;
   private boolean useRunSettings = false;
   private String compileOptionsLb = null;
   private String extensionsLb = null;
   private String buildNameLb = null;
   private boolean useBuildSettings = false;
   private boolean needTabs = false;

   private Component focused = projDirTf;

   public SettingsWindow() {
      if (CHOOSER == null) {
         CHOOSER = new FileChooser();
         CHOOSER.initSelectFileOrDirectoryChooser();
      }
      cancelBt.setFocusable(false);
      okBt.setFocusable(false);
      bf = new BusyFunction(frame);
   }

   /**
    * Gets a new <code>InputOptionsBuilder</code>
    *
    * @return  the {@link SettingsWindow.InputOptionsBuilder}
    */
   public InputOptionsBuilder inputOptionsBuilder() {
      SettingsWindow.InputOptionsBuilder optBuilder
            = new SettingsWindow.InputOptionsBuilder(this);

      return optBuilder;
   }

   /**
    * Sets the current directory for the file chooser used by
    * <code>SettingsWindow</code>.
    *
    * @param dir  the directory
    */
   public void setDirectory(String dir) {
      EventQueue.invokeLater(() -> CHOOSER.setDirectory(dir));
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
    * Registers the listener on the close button of this window
    *
    * @param wa  the <code>WindowAdapter</code>
    */
   public void setDefaultCloseAct(WindowAdapter wa) {
      frame.addWindowListener(wa);
   }

   /**
    * Makes this frame visible or invisible
    *
    * @param b  true for visible, false for invisible
    */
   public void setVisible(boolean b) {
      if (b) {
         focused.requestFocusInWindow();
      }
      else {
         focused = frame.getFocusOwner();
      }
      frame.setVisible(b);
   }

   /**
    * Returns if this frame is visible
    *
    * @return  true if visible, false otherwise
    */
   public boolean isVisible() {
      return frame.isVisible();
   }

   /**
    * Returns the input for the name of a project root directory
    *
    * @return  the input
    */
   public String projDirNameInput() {
      return projDirTf.getText().trim();
   }

   /**
    * Returns the input for the name of a project file
    *
    * @return  the input
    */
   public String fileNameInput() {
      return fileTf.getText().trim();
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
    * Returns the input for the name of a source directory
    *
    * @return  the input
    */
   public String sourcesDirNameInput() {
      return sourcesDirTf.getText().trim();
   }

   /**
    * Returns the input for the name of an executables directory
    *
    * @return  the input
    */
   public String execDirNameInput() {
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
    * Returns the input for file extensions.
    * <p>
    * Extensions may be entered as comma, semicolon or space separated
    * but the returnd string is formatted as comma separated
    *
    * @return  the input
    */
    public String extensionsInput() {
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
   public void displayProjDirName(String s) {
      projDirTf.setText(s);
   }

   /**
    * Shows in the corresponding text field the name of the project file
    *
    * @param s  the name
    */
   public void displayFile(String s) {
      fileTf.setText(s);
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
         librariesPnl.displayText(l);
      }
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
    * file extensions
    *
    * @param s  the file extensions
    */
   public void displayExtensions(String s) {
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
      return saveConfig.isSelected();
   }

   /**
    * Selects or unselects the checkbox for setting if project parameters
    * are saved in an "ProjConfig" file
    *
    * @param isSelected  true to select
    */
   public void setSaveProjConfigSelected(boolean isSelected) {
      saveConfig.setSelected(isSelected);
   }

    /**
    * The building of the content of <code>SettingsWindow</code> with
    * selectable input options.
    * <p>
    * Is created in the enclosing {@link SettingsWindow}
    */
   public static class InputOptionsBuilder {

      private final SettingsWindow sw;

      private InputOptionsBuilder(SettingsWindow sw) {
         this.sw = sw;
      }

      public InputOptionsBuilder addCustomCommandInput() {
         sw.useCustomCmd = true;
         return this;
      }

      /**
       * Adds the option to enter a name for a main project file and sets
       * the label for the corresponding text field
       *
       * @param label  the label for the file input
       * @return  this
       */
      public InputOptionsBuilder addFileInput(String label) {
         sw.fileLabel = label;
         return this;
      }

      /**
       * Adds the option to enter a name for a sources directory
       *
       * @return  this
       */
      public InputOptionsBuilder addSourceDirInput() {
         sw.useSrcDir = true;
         return  this;
      }

      /**
       * Adds the option to enter a name of an executables directory
       *
       * @return  this
       */
      public InputOptionsBuilder addExecDirInput() {
         sw.useExecDir = true;
         return this;
      }

      /**
       * Adds the option to enter libraries
       *
       * @param label  the label for the list input
       * @return  this
       */
      public InputOptionsBuilder addLibrariesInput(String label) {
         sw.librariesPnl = new ListInputPanel(label);
         sw.useLibs = true;
         return this;
      }

      /**
       * Adds the option to enter command options
       *
       * @return  this
       */
      public InputOptionsBuilder addCmdOptionsInput() {
         sw.useCmdOptions = true;
         sw.useRunSettings = true;
         return this;
      }

      /**
       * Adds the option to enter command arguments
       *
       * @return  this
       */
      public InputOptionsBuilder addCmdArgsInput() {
         sw.useCmdArgs = true;
         sw.useRunSettings = true;
         return this;
      }

      /**
       * Adds the option to enter a compile option and sets the label for
       * the corresponding text field
       *
       * @param label  the label
       * @return  this
       */
      public InputOptionsBuilder addCompileOptionInput(String label) {
         sw.compileOptionsLb = label;
         sw.useBuildSettings = true;
         return this;
      }

      /**
       * Adds the option to enter file extensions and sets the label for
       * the corresponding text field
       *
       * @param label  the label
       * @return  this
       */
       public InputOptionsBuilder addExtensionsInput(String label) {
          sw.extensionsLb = label;
          sw.useBuildSettings = true;
          return this;
       }

      /**
       * Adds the option to enter a build name and sets the label for the
       * corresponding text field
       *
       * @param  label  the label
       * @return  this
       */
      public InputOptionsBuilder addBuildNameInput(String label) {
         sw.buildNameLb = label;
         sw.useBuildSettings = true;
         return this;
      }

      /**
       * Builds the content of <code>SettingsWindow</code>.
       * <p>
       * If none of the methods to add input options has been invoked
       * the window shows only the field to enter the name of a root
       * directory of a project.
       *
       * @throws IllegalStateException  if the content has been already
       * built
       */
      public void buildWindow() {
         if (sw.frame.getContentPane().getComponentCount() > 0) {
            throw new IllegalStateException(
                  "The window has been initialized already");
         }
         if (sw.fileLabel != null && sw.useCustomCmd == true) {
            throw new IllegalArgumentException(
                  "Input for both main file name and custom command is not permitted");
         }
         sw.needTabs = sw.useLibs || sw.useRunSettings || sw.useBuildSettings;
         sw.initWindow();
      }
   }

   //
   //--private--/
   //

   private void initWindow() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setVisible(false);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.getContentPane().add(combinedPnl());
      frame.pack();
   }

   private JPanel combinedPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      JTabbedPane tb = null;
      if (needTabs) {
         tb = new JTabbedPane();
         tb.setFont(ScreenParams.scaledFontToPlain(tb.getFont(), 8));
      }
      if (useLibs) {
         tb.add("Libraries", libPnl());
         tb.addChangeListener((ChangeEvent e) -> {
            JTabbedPane sourceTb = (JTabbedPane) e.getSource();
            int i = sourceTb.getSelectedIndex();
            if (i == 1) {
               if (!sourceTb.hasFocus()) {
                  librariesPnl.setLastFocus();
               }
            }
         });
         tb.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
               JTabbedPane sourceTb = (JTabbedPane) e.getSource();
               librariesPnl.disableButtons();
            }
         });
      }
      if (useRunSettings) {
         tb.add("Run", commandPnl());
      }
      if (useBuildSettings) {
         tb.addTab("Compilation and build", compileAndBuildPnl());
      }
      if (tb != null) {
         tb.insertTab("Structure", null, structurePnl(), null, 0);
         tb.setSelectedIndex(0);
         pnl.add(tb);
      }
      else {
         pnl.add(structurePnl());
      }
      pnl.add(checkBxPnl(saveConfig,
            "Save \'ProjConfig\' file in the project to retrieve settings"));

      pnl.add(Box.createRigidArea(DIM_SPACER));
      pnl.add(buttonsPanel());
      return pnl;
   }

   private JPanel structurePnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      //
      // project dir
      JLabel projDirLb = new JLabel("Name of project directory:");
      BrowserButton projDirBr = new BrowserButton(CHOOSER, projDirTf);
      pnl.add(holdLbAndTf(projDirBr, projDirLb, projDirTf));
      //
      // custom run cmd
      if (useCustomCmd) {
         JLabel cmdLb = new JLabel("System command to run:");
         pnl.add(Box.createRigidArea(DIM_SPACER));
         pnl.add(holdLbAndTf(cmdLb, customCmdTf));
         pnl.add(Box.createRigidArea(DIM_SPACER));
      }
      //
      // project file option
      if (fileLabel != null) {
         JLabel fileLb = new JLabel(fileLabel + ":");
         BrowserButton fileBr = new BrowserButton(CHOOSER, fileTf);
         pnl.add(holdLbAndTf(fileBr, fileLb, fileTf));
      }
      //
      // sources dir option
      if (useSrcDir) {
         JLabel sourcesDirLb = new JLabel("Name of sources directory:");
         BrowserButton sourceDirBr = new BrowserButton(CHOOSER, sourcesDirTf);
         pnl.add(holdLbAndTf(sourceDirBr, sourcesDirLb, sourcesDirTf));
      }
      //
      // executables dir option
      if (useExecDir) {
         JLabel execDirLb = new JLabel("Name of executables directory:");
         BrowserButton execDirBr = new BrowserButton(CHOOSER, execDirTf);
         pnl.add(holdLbAndTf(execDirBr, execDirLb, execDirTf));
      }
      JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      holder.add(pnl);
      return holder;
   }

   private JPanel libPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      pnl.add(librariesPnl.content());
      return pnl;
   }

   private JPanel commandPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      //
      // start options
      if (useCmdOptions) {
         JLabel cmdOptLb = new JLabel("Command options:");
         pnl.add(holdLbAndTf(cmdOptLb, cmdOptionsTf));
      }
      //
      // arguments
      if (useCmdArgs) {
         JLabel cmdArgsLb = new JLabel("Command arguments:");
         pnl.add(holdLbAndTf(cmdArgsLb, cmdArgsTf));
      }
      JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      holder.add(pnl);
      return holder;
   }

   private JPanel compileAndBuildPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      //
      // compile options
      if (compileOptionsLb != null) {
         JLabel compOptLb = new JLabel(compileOptionsLb + ":");
         pnl.add(holdLbAndTf(compOptLb, compileOptionsTf));
      }
      //
      // include files option
      if (extensionsLb != null) {
         JLabel extLb = new JLabel(extensionsLb + ":");
         pnl.add(holdLbAndTf(extLb, extensionsTf));
      }
      //
      // set build name option
      if (buildNameLb != null) {
         JLabel buildLb = new JLabel(buildNameLb +":");
         pnl.add(holdLbAndTf(buildLb, buildNameTf));
      }
      JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      holder.add(pnl);
      return holder;
   }

   private JPanel buttonsPanel() {
      JPanel pnl = new JPanel(new FlowLayout());
      pnl.add(okBt);
      pnl.add(cancelBt);
      frame.getRootPane().setDefaultButton(okBt);
      return pnl;
   }

   private JPanel holdLbAndTf(JLabel lb, JTextField tf) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      lb.setFont(ScreenParams.scaledFontToBold(lb.getFont(), 8));
      tf.setFont(ScreenParams.scaledFontToPlain(tf.getFont(), 8));
      tf.setPreferredSize(DIM_TF);
      pnl.add(lb);
      pnl.add(tf);
      return pnl;
   }

    private JPanel holdLbAndTf(BrowserButton brBt, JLabel lb, JTextField tf) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      lb.setFont(ScreenParams.scaledFontToBold(lb.getFont(), 8));
      tf.setFont(ScreenParams.scaledFontToPlain(tf.getFont(), 8));
      tf.setPreferredSize(DIM_TF);
      pnl.add(lb);
      brBt.addButton(pnl);
      pnl.add(tf);
      return pnl;
   }

   private JPanel checkBxPnl(JCheckBox checkBox, String title) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JLabel lb = new JLabel(title);
      lb.setFont(ScreenParams.scaledFontToBold(lb.getFont(), 8));
      pnl.add(lb);
      pnl.add(checkBox);
      checkBox.setFocusable(false);
      return pnl;
   }
}
