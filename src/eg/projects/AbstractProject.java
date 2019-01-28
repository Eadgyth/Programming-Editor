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
   private String relMainFilePath = "";
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
      boolean exists = mainFilePath.exists();
      if (!exists) {
         exists = configBySettingsInput(projectRoot);
         if (exists) {
            setCommandParameters();
            storeConfiguration();
         }
         else {
            int res = Dialogs.warnConfirmYesNo(
                  mainFileInputWarning()
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
      return mainFileName;
   }

   /**
    * Returns the path to the main file relative to the project root
    *
    * @return  the filepath
    */
   protected String relMainFilePath() {
      return relMainFilePath;
   }

   /**
    * Returns the namespace of the main file. This is a relative
    * directory or directory path based at the sources directory if
    * given or at the project root directory.
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
    * @return  the arguments; the empty string if no arguments are given
    */
   protected String cmdArgs() {
      return cmdArgs;
   }

   /**
    * Returns compile options
    *
    * @return  the options; the empty string if no options are given
    */
   protected String compileOption() {
      return compileOption;
   }

   /**
    * Returns the array of file extensions
    *
    * @return  the array; null if no extensions are given
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
      namespace = "";
      isNameConflict = false;
      getSettingsInput(); // also assigns value to isPathname
      if (!isPathname) {
         String sourceRoot = root;
         if (!sourceDirName.isEmpty()) {
            sourceRoot = sourceRoot + "/" + sourceDirName;
         }
         setNamespace(sourceRoot, mainFileName + sourceExtension);
         if (!namespacePath.isEmpty()) {
            if (namespacePath.length() > sourceRoot.length()) {
               namespace = namespacePath.substring(sourceRoot.length() + 1);
            }
            else {
               namespace = ""; // no subdir in source or project root
            }
         }
      }
      setRelMainFilePath();
      mainFilePath = new File(root + "/" + relMainFilePath);
      return mainFilePath.exists();
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
                  if (!isNameConflict && namespacePath.length() > 0) {
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
      boolean success;
      if (!useMainFile) {
         File toTest = new File(root);
         success = toTest.exists() && toTest.isDirectory();
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
         setRelMainFilePath();
         mainFilePath = new File(root + F_SEP + relMainFilePath);
         success = mainFilePath.exists() && mainFilePath.isFile();
      }
      if (success) {
         projectRoot = root;
         displaySettings();
         if (pr == conf) {
            store(prefs);
         }
      }
      return success;
   }

   private void splitMainFileInput(String mainFileInput) {
      String formatted = mainFileInput.replace("\\", "/");
      int lastSepPos = formatted.lastIndexOf("/", mainFileInput.length());
      isPathname = lastSepPos != -1;
      if (isPathname) {
         namespace = sysFileSepPath(formatted.substring(0, lastSepPos));
         mainFileName = formatted.substring(lastSepPos + 1);
      }
      else {
         mainFileName = mainFileInput;
      }
   }
   
   private String sysFileSepPath(String path) {
      return
         path.replaceAll("/",
               java.util.regex.Matcher.quoteReplacement(F_SEP));
   }

   private void setRelMainFilePath() {
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
      relMainFilePath = sb.toString();
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
      if (isNameConflict && showNameConflictMsg) {
         showNameConflictMsg();
         return false;
      }
      boolean success = !rootToTest.isEmpty();
      if (!success) {
         if (useMainFile) {
            Dialogs.warnMessageOnTop(mainFileInputWarning());
         }
         else {
            showProjRootInputWarning();
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

   private void showProjRootInputWarning() {
      Dialogs.warnMessageOnTop(
         sw.projDirNameInput()
         + "\nThe entry for the project directory cannot be matched "
         + " with an existing directory.");
   }

   private String mainFileInputWarning() {
      return
         sw.fileNameInput()
         + sourceExtension
         + "\nThe entries in the project settings cannot be matched"
         + " with an existing file or filepath.";
   }

   private final static String DELETE_CONF_OPT
         = "Saving the \'ProjConfig\' file is no more selected.\n"
         + "Remove the file?";
         
   private void showNameConflictMsg() {
      Dialogs.warnMessage(
         mainFileName
         + sourceExtension()
         + " seems to exist more than once. In the current settings the file "
         + mainFileInputDisplay()
         + " is used.\n\n"
         + PATHNAME_MSG);

      showNameConflictMsg = false;
   }
   
   private String mainFileInputDisplay() {
      return sw.projDirNameInput()
         + F_SEP
         + sysFileSepPath(relMainFilePath);
   }

   private final static String PATHNAME_MSG
      = "To select a file in a sub-directory the pathname relative to the"
      + " source root can be specified in the input field for the main file."
      + "\nThe source root is the sources directory if specified or the"
      + " project directory.";
}
