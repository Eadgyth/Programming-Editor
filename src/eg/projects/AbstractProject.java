package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//--Eadgyth--//
import eg.Prefs;
import eg.utils.Dialogs;

/**
 * The configuration of a project
 */
public abstract class AbstractProject implements Configurable {

   /**
    * The object of <code>InputOptionsBuilder</code> which a project
    * uses to build the content of this <code>SettingsWindow</code>
    *
    * @see SettingsWindow.InputOptionsBuilder
    */
   protected final SettingsWindow.InputOptionsBuilder inputOptions;

   /**
    * The system's file separator
    */
   protected final static String F_SEP = File.separator;

   private final Prefs prefs = new Prefs();
   private final SettingsWindow sw;  // Set in constructor
   //
   // Varaiable that define the configuration
   private final ProjectTypes projType;  //
   private final String sourceExtension; // Set in constructor
   private final boolean useMainFile;    //
   private String projectRoot = "";
   private String mainFileName = "";
   private String namespace = "";
   private String execDirName = "";
   private String sourceDirName = "";
   private String cmdOptions = "";
   private String cmdArgs = "";
   private String compileOption = "";
   private String extensions = "";
   private String buildName = "";
   //
   // Variables to control the configuration
   private File mainFilePath = null;
   private String namespacePath = "";
   private boolean isPathname = false;
   private boolean isNameConflict = false;
   private boolean showNameConflictMsg = true;
   //
   // The Prefs object that stores and reads from a ProjConfig file
   // in a project directory
   private Prefs conf;

   @Override
   public final void setConfiguringAction(ActionListener al) {
      sw.okAct(al);
   }

   @Override
   public final void openSettingsWindow() {
      sw.setVisible(true);
   }

   @Override
   public final boolean configure(String dir) {
      String rootToTest = "";
      String rootName = sw.projDirNameInput();
      String root = rootByName(dir, rootName);
      if (!root.isEmpty()) {
         if (!useMainFile || configBySettingsInput(root)) {
            rootToTest = root;
         }
      }
      boolean success = isConfigured(rootToTest);
      if (success) {
         setCommandParameters();
         sw.setVisible(false);
      }
      return success;
   }

   /**
    * {@inheritDoc}.
    * <p>
    * A 'ProjConfig' file is searched in <code>dir</code> or further
    * upward the directory path. The project then is the directory
    * where the file is found. If it is not found, it is tested if
    * <code>dir</code> is contained in the recent project saved in the
    * "Prefs" file in the program folder.
    */
   @Override
   public final boolean retrieve(String dir) {
      boolean success = false;
      String root = rootByContainedFile(dir, Prefs.PROJ_CONFIG_FILE);
      if (!root.isEmpty()) {
         sw.setSaveProjConfigSelected(true);
         conf = new Prefs(root);
         success = configByPrefs(root, conf);
      }
      else {
         sw.setSaveProjConfigSelected(false);
         root = prefs.getProperty("ProjectRoot");
         if (isInProject(dir, root)) {
             success = configByPrefs(root, prefs);
         }
      }
      if (success) {
         setCommandParameters();
      }
      return success;
   }

   @Override
   public final ProjectTypes projectType() {
      return projType;
   }

   @Override
   public final boolean usesProjectFile() {
      return useMainFile;
   }

   @Override
   public final boolean isInProject(String dir) {
      return isInProject(dir, projectRoot);
   }

   @Override
   public final String projectPath() {
      return projectRoot;
   }

   @Override
   public final String projectName() {
      File f = new File(projectRoot);
      return f.getName();
   }

   @Override
   public final String executableDirName() {
      return execDirName;
   }

   @Override
   public final void storeConfiguration() {
      storeConfigurationImpl();
   }

   /**
    * @param projType  the project type which has a valaue in
    * {@link ProjectTypes}
    * @param useMainFile  true to indicate that the project uses a main
    * file which is executed when the project is run
    * @param sourceExtension  the extension of source files (or of the
    * main file if extensions differ) without the period
    */
   protected AbstractProject(ProjectTypes projType, boolean useMainFile,
         String sourceExtension) {

      this.projType = projType;
      this.sourceExtension = "." + sourceExtension;
      this.useMainFile = useMainFile;
      sw = new SettingsWindow();
      inputOptions = sw.inputOptionsBuilder();
      sw.setCancelAct(e -> undoSettings());
      sw.setDefaultCloseAct(DefaultClosing);
   }

   /**
    * Sets command parameters
    */
   protected abstract void setCommandParameters();

   /**
    * Returns if the main project file can be located based on the last
    * successful configuration or, if not, after re-configuring the
    * project. A dialog which asks to open the settings window is shown
    * if the file cannot be located.
    *
    * @return  true if the main can be located
    */
   protected boolean locateMainFile() {
      if (!useMainFile) {
         throw new IllegalStateException("A main file is not used");
      }
      boolean exists = mainFilePath.exists();
      if (!exists) {
         exists = configBySettingsInput(projectRoot);
         if (exists) {
            setCommandParameters();
            storeConfiguration();
         }
         else {
            int res = Dialogs.warnConfirmYesNo(
                  MAIN_FILE_PATH_ERR
                  +"\n\nOpen the project settings?");

            if (res == 0) {
               sw.setVisible(true);
            }
         }
      }
      return exists;
   }

   /**
    * Returns the name of main project file without extension
    *
    * @return  the name
    */
   protected String mainFileName() {
      if (!useMainFile) {
         throw new IllegalStateException("A main file is not used");
      }
      return mainFileName;
   }

   /**
    * Returns the namespace of the main file. This is a relative
    * directory or directory path based at the sources directory or,
    * if a sources directory is not given, at the project's root
    * directory.
    *
    * @return  the namespace; the empty string if no namespace is given
    */
   protected String namespace() {
      return namespace;
   }

   /**
    * Returns the last name of the directoy where source files are saved
    *
    * @return  the name; the empty string if no source directory is given
    */
   protected String sourceDirName() {
      return sourceDirName;
   }

   /**
    * Returns the extension of source files (or of the main project
    * file if extensions differ) with the beginning period
    *
    * @return  the extension; null if no source extension is given
    */
   protected String sourceExtension() {
      return sourceExtension;
   }

   /**
    * Returns command options
    *
    * @return  the options; the empty string if no options are given
    */
   protected String cmdOptions() {
      return cmdOptions;
   }

   /**
    * Returns command arguments
    *
    * @return  the arguments. The empty string if no arguments are given
    */
   protected String cmdArgs() {
      return cmdArgs;
   }

   /**
    * Returns the compile option
    *
    * @return  the option. The empty string if no option is given
    */
   protected String compileOption() {
      return compileOption;
   }

   /**
    * Returns the array of file extensions
    *
    * @return  the array or null of no extensions are given
    */
   protected String[] fileExtensions() {
      if (extensions.isEmpty()) {
          return null;
       }
       else {
          return extensions.split(",");
       }
   }

   /**
    * Returns the name for a build
    *
    * @return  the name
    */
   protected String buildName() {
      return buildName;
   }

   //
   //--private--/
   //

   private String rootByName(String dir, String name) {
      File root = new File(dir);
      String existingName;
      while (root != null) {
         existingName = root.getName();
         if (name.equals(existingName) && root.exists()) {
            return root.getPath();
         }
         root = root.getParentFile();
      }
      return "";
   }

   private String rootByContainedFile(String dir, String file) {
      File root = new File(dir);
      String relToRoot = F_SEP + file;
      String existingPath;
      while (root != null) {
         existingPath = root.getPath() + relToRoot;
         if (new File(existingPath).exists()) {
            return root.getPath();
         }
         root = root.getParentFile();
      }
      return "";
   }

   private boolean configBySettingsInput(String root) {
      boolean success = false;
      namespacePath = "";
      isNameConflict = false;
      getSettingsInput(); // assigns value to isPathname
      if (!isPathname) {
         String sourceRoot = root;
         if (!sourceDirName.isEmpty()) {
            sourceRoot = sourceRoot + "/" + sourceDirName;
         }
         setNamespace(sourceRoot, mainFileName + sourceExtension);
         if (!namespacePath.isEmpty() && !isNameConflict) {
            if (namespacePath.length() > sourceRoot.length()) {
               namespace = namespacePath.substring(sourceRoot.length() + 1);
            }
            else {
               namespace = ""; // no subdir in source or project root
            }
            mainFilePath = new File(root + "/" + pathRelToRoot());
            success = true;
         }
      }
      else {
         File toTest = new File(root + "/" + pathRelToRoot());
         if (toTest.exists()) {
            mainFilePath = toTest;
            success = true;
         }
      }
      return success;
   }

   private void getSettingsInput() {
      String mainFileInput = sw.fileNameInput();
      splitMainFileInput(mainFileInput);
      sourceDirName = sw.sourcesDirNameInput();
      execDirName = sw.execDirNameInput();
      cmdOptions = sw.cmdOptionsInput();
      cmdArgs = sw.cmdArgsInput();
      compileOption = sw.compileOptionInput();
      extensions = sw.extensionsInput();
      buildName = sw.buildNameInput();
   }

   private void setNamespace(String sourceRoot, String name) {
      File f = new File(sourceRoot);
      File[] list = f.listFiles();
      if (list != null) {
         for (File fInList : list) {
            if (fInList.isFile()) {
               if (fInList.getName().equals(name)) {
                  if (!isNameConflict && namespacePath.length() > 0
                        && showNameConflictMsg) {

                     isNameConflict = true;
                  }
                  else {
                     namespacePath = fInList.getParent();
                  }
               }
            }
            else {
               setNamespace(fInList.getPath(), name);
            }
         }
      }
   }

   private boolean configByPrefs(String root, Prefs pr) {
      String projTypeToTest = pr.getProperty("ProjectType");
      if (!projTypeToTest.equals(projType.toString())) {
         return false;
      }
      File toTest;
      if (!useMainFile) {
         toTest = new File(root);
      }
      else {
         String mainFileInput = pr.getProperty("MainProjectFile");
         splitMainFileInput(mainFileInput);
         if (isPathname) {
            sw.displayFile(namespace + F_SEP + mainFileName);
         }
         else {
            namespace = pr.getProperty("Namespace");
         }
         sourceDirName = pr.getProperty("SourceDir");
         execDirName = pr.getProperty("ExecDir");
         extensions = pr.getProperty("IncludedFiles");
         buildName = pr.getProperty("BuildName");
         toTest = new File(root + F_SEP + pathRelToRoot());
      }
      boolean success = false;
      if (toTest.exists()) {
         success = (useMainFile && toTest.isFile())
               || (!useMainFile && toTest.isDirectory());

         if (success) {
            projectRoot = root;
            displaySettings();
            if (pr == conf) {
               store(prefs);
            }
            if (useMainFile) {
               mainFilePath = toTest;
            }
         }
      }
      return success;
   }

   private void splitMainFileInput(String mainFileInput) {
      String formatted = mainFileInput.replace("\\", "/");
      int lastSepPos = formatted.lastIndexOf("/", mainFileInput.length());
      isPathname = lastSepPos != -1;
      if (isPathname) {
         namespace = formatted.substring(0, lastSepPos).replaceAll("/",
               java.util.regex.Matcher.quoteReplacement(F_SEP));

         mainFileName = formatted.substring(lastSepPos + 1);
      }
      else {
         mainFileName = mainFileInput;
      }
   }

   private String pathRelToRoot() {
      StringBuilder sb = new StringBuilder();
      if (!sourceDirName.isEmpty()) {
         sb.append(sourceDirName).append("/");
      }
      if (!namespace.isEmpty()) {
         sb.append(namespace).append("/");
      }
      if (!mainFileName.isEmpty()) {
         sb.append(mainFileName).append(sourceExtension);
      }
      return sb.toString();
   }

   private boolean isInProject(String dir, String projRoot) {
      File child = new File(dir);
      File root = new File(projRoot);
      while(child != null) {
         if (child.equals(root)) {
            return true;
         }
         child = child.getParentFile();
      }
      return false;
   }

   private void displaySettings() {
      sw.displayProjDirName(projectName());
      if (isPathname) {
         sw.displayFile(namespace + F_SEP + mainFileName);
      }
      else {
         sw.displayFile(mainFileName);
      }
      sw.displaySourcesDir(sourceDirName);
      sw.displayExecDir(execDirName);
      sw.displayExtensions(extensions);
      sw.displayBuildName(buildName);
      sw.displayCmdArgs(cmdArgs);
      sw.displayCmdOptions(cmdOptions);
      sw.displayCompileOption(compileOption);
   }

   private boolean isConfigured(String rootToTest) {
      boolean success = !rootToTest.isEmpty();
      if (!success) {
         if (isNameConflict) {
            showNameConflictMsg();
         }
         else {
            if (useMainFile) {
               Dialogs.warnMessageOnTop(MAIN_FILE_PATH_ERR);
            }
            else {
               showProjRootInputWarning();
            }
         }
      }
      else {
         if (projectRoot.isEmpty() || !projectRoot.equals(rootToTest)) {
            projectRoot = rootToTest;

         }
      }
      return success;
   }

   private void storeConfigurationImpl() {
      if (projectRoot.isEmpty()) {
         throw new IllegalStateException("The project is not configured");
      }
      store(prefs);
      if (sw.isSaveToProjConfig()) {
         if (conf == null) {
            conf = new Prefs(projectRoot);
         }
         store(conf);
      }
      else {
         deleteProjConfigFile();
      }
   }

   private void store(Prefs pr) {
      if (pr == prefs) {
         pr.setProperty("ProjectRoot", projectRoot);
      }
      pr.setProperty("SourceDir", sourceDirName);
      pr.setProperty("ExecDir", execDirName);
      pr.setProperty("IncludedFiles", extensions);
      pr.setProperty("BuildName", buildName);
      pr.setProperty("ProjectType", projType.toString());
      if (isPathname) {
         pr.setProperty("MainProjectFile", namespace + F_SEP + mainFileName);
         pr.setProperty("Namespace", "");
      }
      else {
         pr.setProperty("MainProjectFile", mainFileName);
         pr.setProperty("Namespace", namespace);
      }
      pr.store();
   }

   private void deleteProjConfigFile() {
      File configFile = new File(projectRoot + "/" + Prefs.PROJ_CONFIG_FILE);
      if (configFile.exists()) {
         int res = Dialogs.warnConfirmYesNo(DELETE_CONF_OPT);
         if (res == 0) {
            boolean success = configFile.delete();
            if (!success) {
               Dialogs.warnMessage("Deleting the ProgConfig file failed");
            }
         }
         else {
            sw.setSaveProjConfigSelected(true);
            store(conf);
         }
      }
   }

   private void undoSettings() {
      displaySettings();
      sw.setVisible(false);
   }

   private final WindowAdapter DefaultClosing = new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent we) {
         undoSettings();
      }
   };

   private void showNameConflictMsg() {
      Dialogs.warnMessageOnTop(
         "<html>"
         + mainFileName
         + sourceExtension()
         + " seems to exist more than once in the project."
         + "<br><br>"
         + "If this name should be used it may be necessary to specify"
         + " its pathname relative to the source root.<br>"
         + "The source root is the sources directory if available or"
         + " the project directory."
         + "</html>");

         showNameConflictMsg = false;
   }

   private void showProjRootInputWarning() {
      Dialogs.warnMessageOnTop(
         "The name \'"
         + sw.projDirNameInput()
         + "\' for the project directory cannot be matched with an"
         + " existing directory.");
   }

   private final static String MAIN_FILE_PATH_ERR
         = "The entries in the project settings window cannot be matched"
         + " with an existing path to the main project file.";

   private final static String DELETE_CONF_OPT
         = "<html>"
         + "Saving the \'ProjConfig\' file is no more selected.<br>"
         + "Remove the file?"
         + "</html>";
}
