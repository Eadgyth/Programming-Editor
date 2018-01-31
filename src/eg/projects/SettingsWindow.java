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

   private final static Dimension DIM_TF = ScreenParams.scaledDimension(200, 16);
   private final static Dimension DIM_SPACER = ScreenParams.scaledDimension(0, 20);

   private final JFrame frame = new JFrame("Project settings");
   private final JTextField projDirTf    = new JTextField();
   private final JTextField fileTf       = new JTextField();
   private final JTextField sourcesDirTf = new JTextField();
   private final JTextField execDirTf    = new JTextField();
   private final JTextField argsTf       = new JTextField();
   private final JTextField searchExtTf    = new JTextField();
   private final JTextField buildTf      = new JTextField();
   private final JButton    okBt         = new JButton("   OK   ");
   private final JButton    cancelBt     = new JButton("Cancel");
   private final JCheckBox  saveConfig   = new JCheckBox();

   private String fileLabel = null;
   private boolean useScr = false;
   private boolean useExec = false;
   private boolean useArgs = false;
   private String searchExtLabel = null;
   private String buildLabel = null;

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
    * Returns the input in the text field for arguments
    *
    * @return  the input
    */
   public String argsInput() {
      return argsTf.getText().trim();
   }

   /**
    * Returns the input in the text field for file extensions that
    * may be used for a file search.
    * <p>
    * Extensions may be entered as comma, semicolon or space separated
    * but the returnd string is formatted as comma separated in either
    * case.
    *
    * @return  the input
    */
    public String searchExtensionsInput() {
       return searchExtTf.getText().trim().replaceAll("[\\s,;]+", ",");
    }

   /**
    * Returns the input in the text field for a name of a build
    *
    * @return  the input
    */
   public String buildNameInput() {
      return buildTf.getText().trim();
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
   public void displaySearchExtensions(String in) {
      searchExtTf.setText(in);
   }

   /**
    * Shows in the corresponding text field the name for a build
    *
    * @param in  the name
    */
   public void displayBuildName(String in) {
      buildTf.setText(in);
   }

   /**
    * Returns if the option to save project parameters to an
    * \"eadproject\" file is selected in the correponding checkbox
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
   //--private--//
   //

   private void buildWindow() {
      if (frame.getContentPane().getComponentCount() > 0) {
         throw new IllegalStateException(
               "The settings window has been initialized already");
      }
      initWindow();
   }

   private JPanel structurePanel() {
      int gridSize = 1;
      GridLayout grid = new GridLayout(gridSize, 0);
      JPanel structPnl = new JPanel(grid);
      //
      // project dir
      JLabel projDirLb = new JLabel("Name of project root:");
      structPnl.add(holdLbAndTf(projDirLb, projDirTf));
      //
      // project file option
      if (fileLabel != null) {
         gridSize++;
         grid.setRows(gridSize);
         JLabel fileLb = new JLabel(fileLabel + ":");
         structPnl.add(holdLbAndTf(fileLb, fileTf));
      }
      //
      // sources dir option
      if (useScr) {
         gridSize++;
         grid.setRows(gridSize);
         JLabel sourcesDirLb = new JLabel("Name of sources directory:");
         structPnl.add(holdLbAndTf(sourcesDirLb, sourcesDirTf));
      }
      //
      // executable dir option
      if (useExec) {
         gridSize++;
         grid.setRows(gridSize);
         JLabel execDirLb = new JLabel("Name of executables directory:");
         structPnl.add(holdLbAndTf(execDirLb, execDirTf));
      }

      structPnl.setBorder(UIComponents.titledBorder("Directory/file structure"));
      return structPnl;
   }

   private JPanel argsPanel() {
      JPanel argsPnl = new JPanel( new GridLayout(1, 0));
      JLabel argsLb = new JLabel("Arguments:");
      argsPnl.add(holdLbAndTf(argsLb, argsTf));
      argsPnl.setBorder(UIComponents.titledBorder("Run"));
      return argsPnl;
   }

   private JPanel buildPanel() {
      int gridSize = 1;
      GridLayout grid = new GridLayout(gridSize, 0);
      JPanel buildPnl = new JPanel(grid);
      //
      // include files option
      if (searchExtLabel != null) {
         JLabel searchExtLb = new JLabel(searchExtLabel + " :");
         buildPnl.add(holdLbAndTf(searchExtLb, searchExtTf));
      }
      //
      // set build name option
      if (buildLabel != null) {
         gridSize++;
         grid.setRows(gridSize);
         JLabel buildLb = new JLabel("Name for " + buildLabel +":");
         buildPnl.add(holdLbAndTf(buildLb, buildTf));
      }
      buildPnl.setBorder(UIComponents.titledBorder("Compilation and build"));
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
      if (buildLabel != null || searchExtLabel != null) {
         combineAll.add(Box.createRigidArea(DIM_SPACER));
         combineAll.add(buildPanel());
      }
      combineAll.add(checkBxPnl(saveConfig, "Save settings to \"eadproject\" file"
            + " in the project folder"));
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

   /**
    * The building of the content of <code>SettingsWindow</code> with
    * selectable input options.
    * <p>
    * Is created in {@link SettingsWindow}
    */
   public static class InputOptionsBuilder {

      private SettingsWindow sw;

      private InputOptionsBuilder(SettingsWindow sw) {
         this.sw = sw;
      }

      /**
       * Adds the option to enter a name for the main project file and
       * sets the label for the corresponding text field
       *
       * @param fileLabel  the label
       * @return  this
       */
      public InputOptionsBuilder addFileOption(String fileLabel) {
         sw.fileLabel = fileLabel + " (without extension)";
         return this;
      }

      /**
       * Adds the option to enter a name of a directory where source
       * files are stored
       *
       * @return  this
       */
      public InputOptionsBuilder addSourceDirOption() {
         sw.useScr = true;
         return  this;
      }

      /**
       * Adds the option to enter a name of a directory where executable
       * files are stored
       *
       * @return  this
       */
      public InputOptionsBuilder addExecDirOption() {
         sw.useExec = true;
         return this;
      }

      /**
       * Adds the option to enter arguments for a start script
       *
       * @return  this
       */
      public InputOptionsBuilder addArgsOption() {
         sw.useArgs = true;
         return this;
      }

      /**
       * Adds the option to enter extensions of files that may be used
       * for a file search and set the label for the corresponding text
       * field
       *
       * @param label  the label
       * @return  this
       */
       public InputOptionsBuilder addSearchExtensionsOption(String label) {
          sw.searchExtLabel = label;
          return this;
       }

      /**
       * Adds the option to enter a build name and sets the label for the
       * corresponding text field
       *
       * @param  label  the label
       * @return  this
       */
      public InputOptionsBuilder addBuildOption(String label) {
         sw.buildLabel = label;
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
