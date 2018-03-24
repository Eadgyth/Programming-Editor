package eg.projects;

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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Dimension;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.ui.IconFiles;
import eg.Constants;
import eg.utils.ScreenParams;
import eg.utils.UIComponents;

/**
 * The window that shows input options for the configuration of a project
 */
public class SettingsWindow {

   private final static Dimension DIM_TF = ScreenParams.scaledDimension(200, 14);
   private final static Dimension DIM_SPACER = ScreenParams.scaledDimension(0, 14);

   private final JFrame frame = new JFrame("Project settings");

   private final JTextField projDirTf       = new JTextField();
   private final JTextField fileTf          = new JTextField();
   private final JTextField sourcesDirTf    = new JTextField();
   private final JTextField execDirTf       = new JTextField();
   private final JTextField cmdArgsTf       = new JTextField();
   private final JTextField cmdOptionsTf    = new JTextField();
   private final JTextField compileOptionTf = new JTextField();
   private final JTextField extensionsTf    = new JTextField();
   private final JTextField buildNameTf     = new JTextField();
   private final JButton    okBt            = new JButton("   OK   ");
   private final JButton    cancelBt        = new JButton("Cancel");
   private final JCheckBox  saveConfig      = new JCheckBox();

   private String fileLabel = null;
   private boolean useSrcDir = false;
   private boolean useExecDir = false;
   private boolean useCmdOptions = false;
   private boolean useCmdArgs = false;
   private String compileOptionLb = null;
   private String extensionsLabel = null;
   private String buildNameLabel = null;

   public InputOptionsBuilder getInputOptionsBuilder() {
      SettingsWindow.InputOptionsBuilder optBuilder
            = new SettingsWindow.InputOptionsBuilder(this);

      return optBuilder;
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
    * Makes this frame visible or invisible
    *
    * @param b  the boolean value that is true to make the
    * frame visible, false to make it invisible
    */
   public void setVisible(boolean b) {
      projDirTf.requestFocusInWindow();
      frame.setVisible(b);
   }

   /**
    * Returns the input in the text field for the name of a project
    * file
    *
    * @return  the input
    */
   public String fileNameInput() {
      return fileTf.getText().trim();
   }

   /**
    * Returns the input in the text field for the name of a directory for
    * source files
    *
    * @return  the input
    */
   public String sourcesDirNameInput() {
      return sourcesDirTf.getText().trim();
   }

   /**
    * Returns the input in the text field for the name of a directory for
    * executables
    *
    * @return  the input
    */
   public String execDirNameInput() {
      return execDirTf.getText().trim();
   }

   /**
    * Returns the input in the text field for the name of a project root
    * directory
    *
    * @return  the input
    */
   public String projDirNameInput() {
      return projDirTf.getText().trim();
   }

   /**
    * Returns the input in the text field for command options
    *
    * @return  the input
    */
   public String cmdOptionsInput() {
      return cmdOptionsTf.getText().trim();
   }

   /**
    * Returns the input in the text field for command arguments
    *
    * @return  the input
    */
   public String cmdArgsInput() {
      return cmdArgsTf.getText().trim();
   }

   /**
    * Returns the input in the text field for a compile option
    *
    * @return  the input
    */
   public String compileOptionInput() {
      return compileOptionTf.getText().trim();
   }

   /**
    * Returns the input in the text field for file extensions that may
    * be used for a file search.
    * <p>
    * Extensions may be entered as comma, semicolon or space separated
    * but the returnd string is formatted as comma separated in either
    * case.
    *
    * @return  the input
    */
    public String extensionsInput() {
       return extensionsTf.getText().trim().replaceAll("[\\s,;]+", ",");
    }

   /**
    * Returns the input in the text field for a name of a build
    *
    * @return  the input
    */
   public String buildNameInput() {
      return buildNameTf.getText().trim();
   }

   /**
    * Shows in the corresponding text field the name of the main project
    * file
    *
    * @param fileName  the name
    */
   public void displayFile(String fileName) {
      fileTf.setText(fileName);
   }

   /**
    * Shows in the corresponding text field the name of the directory that
    * contains source files
    *
    * @param dirName  the name
    */
   public void displaySourcesDir(String dirName) {
      sourcesDirTf.setText(dirName);
   }

   /**
    * Shows in the corresponding text field the name of the directory
    * where executables are saved
    *
    * @param in  the name
    */
   public void displayExecDir(String in) {
      execDirTf.setText(in);
   }

   /**
    * Shows in the corresponding text field the name of the project's
    * root directory
    *
    * @param in  the name
    */
   public void displayProjDirName(String in) {
      projDirTf.setText(in);
   }

   /**
    * Shows in the corresponding text field the string that contains
    * extensions that may be used for a file search
    *
    * @param in  the file extensions
    */
   public void displayExtensions(String in) {
      extensionsTf.setText(in);
   }

   /**
    * Shows in the corresponding text field the name for a build
    *
    * @param in  the name
    */
   public void displayBuildName(String in) {
      buildNameTf.setText(in);
   }

   /**
    * Returns if the option to save project parameters to an
    * "eadproject" file is selected in the correponding checkbox
    *
    * @return  the boolean value that is true if selected
    */
   public boolean isSaveToEadproject() {
      return saveConfig.isSelected();
   }

   /**
    * Sets the boolean that specifies if the checkbox for selecting
    * if project parameters are saved in an "eadproject" file is
    * set selected
    *
    * @param isSelected  the boolean value. True to select
    */
   public void setSaveEadprojectSelected(boolean isSelected) {
      saveConfig.setSelected(isSelected);
   }

   //
   //--private--/
   //

   private void buildWindow() {
      if (frame.getContentPane().getComponentCount() > 0) {
         throw new IllegalStateException(
               "The settings window has been initialized already");
      }
      initWindow();
   }

   private JPanel structurePnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      //
      // project dir
      JLabel projDirLb = new JLabel("Name of project root:");
      pnl.add(holdLbAndTf(projDirLb, projDirTf));
      //
      // project file option
      if (fileLabel != null) {
         JLabel fileLb = new JLabel(fileLabel + ":");
         pnl.add(holdLbAndTf(fileLb, fileTf));
      }
      //
      // sources dir option
      if (useSrcDir) {
         JLabel sourcesDirLb = new JLabel("Name of sources directory:");
         pnl.add(holdLbAndTf(sourcesDirLb, sourcesDirTf));
      }
      //
      // executable dir option
      if (useExecDir) {
         JLabel execDirLb = new JLabel("Name of executables directory:");
         pnl.add(holdLbAndTf(execDirLb, execDirTf));
      }

      JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      holder.add(pnl);
      return holder;
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
      // compile options
      //
      if (compileOptionLb != null) {
         JLabel compOptLb = new JLabel(compileOptionLb + ":");
         pnl.add(holdLbAndTf(compOptLb, compileOptionTf));
      }
      //
      // include files option
      if (extensionsLabel != null) {
         JLabel extLb = new JLabel(extensionsLabel + ":");
         pnl.add(holdLbAndTf(extLb, extensionsTf));
      }
      //
      // set build name option
      if (buildNameLabel != null) {
         JLabel buildLb = new JLabel("Name for " + buildNameLabel +":");
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
      cancelBt.addActionListener(e -> {
         frame.setVisible(false);
      });
      return pnl;
   }

   private JPanel holdLbAndTf(JLabel lb, JTextField tf) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      lb.setFont(eg.Constants.SANSSERIF_BOLD_8);
      tf.setFont(eg.Constants.SANSSERIF_PLAIN_8);
      tf.setPreferredSize(DIM_TF);
      pnl.add(lb);
      pnl.add(tf);
      return pnl;
   }

   private JPanel checkBxPnl(JCheckBox checkBox, String title) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JLabel label = new JLabel(title);
      label.setFont(Constants.SANSSERIF_BOLD_8);
      pnl.add(label);
      pnl.add(checkBox);
      return pnl;
   }

   private JPanel combinedPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      JTabbedPane tb = null;
      if (useCmdOptions || useCmdArgs) {
         tb = new JTabbedPane();
         tb.setFont(Constants.SANSSERIF_PLAIN_8);
         tb.add("Run", commandPnl());
      }
      if (buildNameLabel != null || extensionsLabel != null) {
         tb.addTab("Compilation and build", compileAndBuildPnl());
      }
      if (tb != null) {
         tb.insertTab("Project structure", null, structurePnl(), null, 0);
         tb.setSelectedIndex(0);
         pnl.add(tb);
      }
      else {
         pnl.add(structurePnl());
      }
      pnl.add(checkBxPnl(saveConfig,
            "Save \"eadproject\" file in the project folder"));
      pnl.add(Box.createRigidArea(DIM_SPACER));
      pnl.add(buttonsPanel());
      return pnl;
   }

   private void initWindow() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
      frame.getContentPane().add(combinedPnl());
      frame.pack();
   }

   /**
    * The building of the content of <code>SettingsWindow</code> with
    * selectable input options.
    * <p>
    * Is created in {@link SettingsWindow}
    */
   public static class InputOptionsBuilder {

      private final SettingsWindow sw;

      private InputOptionsBuilder(SettingsWindow sw) {
         this.sw = sw;
      }

      /**
       * Adds the option to enter a name for the main project file and
       * sets the label for the corresponding text field
       *
       * @param fileLabel  the label which the term "(without extension)" is
       * added to
       * @return  this
       */
      public InputOptionsBuilder addFileInput(String fileLabel) {
         sw.fileLabel = fileLabel + " (without extension)";
         return this;
      }

      /**
       * Adds the option to enter a name of a directory where source
       * files are stored
       *
       * @return  this
       */
      public InputOptionsBuilder addSourceDirInput() {
         sw.useSrcDir = true;
         return  this;
      }

      /**
       * Adds the option to enter a name of a directory where executable
       * files are stored
       *
       * @return  this
       */
      public InputOptionsBuilder addExecDirInput() {
         sw.useExecDir = true;
         return this;
      }

      /**
       * Adds the option to enter command options
       *
       * @return  this
       */
      public InputOptionsBuilder addCmdOptionsInput() {
         sw.useCmdOptions = true;
         return this;
      }

      /**
       * Adds the option to enter command arguments
       *
       * @return  this
       */
      public InputOptionsBuilder addCmdArgsInput() {
         sw.useCmdArgs = true;
         return this;
      }


      /**
       * Adds the option to enter a compile option and sets the label
       * for the corresponding text field
       *
       * @param label  the label
       * @return  this
       */
      public InputOptionsBuilder addCompileOptionInput(String label) {
         sw.compileOptionLb = label;
         return this;
      }

      /**
       * Adds the option to enter extensions of files that may be used
       * for a file search and sets the label for the corresponding text
       * field
       *
       * @param label  the label
       * @return  this
       */
       public InputOptionsBuilder addExtensionsInput(String label) {
          sw.extensionsLabel = label;
          return this;
       }

      /**
       * Adds the option to enter a build name and sets the label for the
       * corresponding text field.
       *
       * @param  label  the label that specifies the kind of build
       * @return  this
       */
      public InputOptionsBuilder addBuildNameInput(String label) {
         sw.buildNameLabel = label;
         return this;
      }

      /**
       * Builds the window content of <code>SettingsWindow</code>.
       * <p>
       * If none of the methods to add input options has been invoked
       * the window shows only the field to enter the name of a root
       * directory of a project.
       */
      public void buildWindow() {
         sw.buildWindow();
      }
   }
}
