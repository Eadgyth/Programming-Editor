package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;
import java.util.ArrayList;

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

   private final static String F_SEP = File.separator;
   //
   // The Prefs object that reads from and writes to the Prefs file in
   // the program folder
   private final Prefs prefs = new Prefs();
   //
   // Set in constructor
   private final ProjectTypes projType;
   private final String sourceExtension;
   private final boolean useMainFile;
   private final SettingsWindow sw;
   //
   // Variables that correspond to or depend on input in the settings window
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
   private String absNamespace = "";
   private boolean isPathname = false;
   private boolean isNameConflict = false;
   private boolean showNameConflictMsg = true;
   //
   // The Prefs object that reads from and writes to a ProjConfig file
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
    * First, a 'ProjConfig' file is searched in the specified directory
    * or further upward the directory path. If this is not found, it is
    * tested if the directory is contained in the project directory saved
    * in the Prefs file in the program folder.
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
         root = prefs.property("ProjectRoot");
         if (root != null && isInProject(dir, root)) {
             success = configByPrefs(root, prefs);
         }
      }
      if (success) {
         showNameConflictMsg = false;
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
      boolean locate = mainFilePath.exists();
      int openSettings = -1;
      if (!locate) {
         locate = configBySettingsInput(projectRoot);
         if (locate) {
            setCommandParameters();
            storeConfiguration();
            if (isNameConflict) {
               locate = false;
               openSettings = Dialogs.warnConfirmYesNo(
                     nameConflictMsg()
                     + "\n\nOpen the project settings?");

               showNameConflictMsg = false;
            }
         }
         else {
            openSettings = Dialogs.warnConfirmYesNo(
                  mainFileInputWarning()
                  + "\n\nOpen the project settings?");
         }
      }
      if (openSettings == 0) {
         sw.setVisible(true);
      }
      return locate;
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
    * Returns the pathname of the main project file relative to the
    * project root
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
    * Returns the name or the relative path of the sources directory
    *
    * @return  the name; the empty string if no source directory is given
    */
   protected String sourceDirName() {
      return sourceDirName;
   }

   /**
    * Returns the extension of source files (or of the main file if
    * extensions differ) with the beginning period
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
      absNamespace = "";
      namespace = "";
      isNameConflict = false;
      getSettingsInput(); // also assigns value to isPathname
      if (!isPathname) {
         String sourceRoot = root;
         if (!sourceDirName.isEmpty()) {
            sourceRoot = sourceRoot + "/" + sourceDirName;
         }
         setNamespace(sourceRoot, mainFileName + sourceExtension);
         if (!absNamespace.isEmpty()) {
            if (absNamespace.length() > sourceRoot.length()) {
               namespace = absNamespace.substring(sourceRoot.length() + 1);
            }
            else {
               namespace = ""; // no subdir in source or project root
            }
         }
      }
      setRelMainFilePath();
      mainFilePath = new File(root + F_SEP + relMainFilePath);
      return mainFilePath.exists();
   }

   private void getSettingsInput() {
      splitMainFileInput(sw.fileNameInput());
      sourceDirName = sw.sourcesDirNameInput();
      execDirName = sw.execDirNameInput();
      cmdOptions = sw.cmdOptionsInput();
      cmdArgs = sw.cmdArgsInput();
      compileOption = sw.compileOptionInput();
      extensions = sw.extensionsInput();
      buildName = sw.buildNameInput();
   }

   private void setNamespace(String root, String name) {
      File fRoot = new File(root);
      File[] files = fRoot.listFiles();
      List<File> dirs = new ArrayList<>(10);
      if (files != null) {
         for (File f : files) {
            if (f.isFile()) {
               if (f.getName().equals(name)) {
                  if (absNamespace.length() > 0) {
                     isNameConflict = true;
                  }
                  else {
                     absNamespace = f.getParent();
                  }
               }
            }
            else {
               dirs.add(f);
            }
         }
      }
      if (dirs.size() > 0) {
         for (File f : dirs) {
            setNamespace(f.getPath(), name);
         }
      }
   }

   private boolean configByPrefs(String root, Prefs pr) {
      String projTypeToTest = pr.property("ProjectType");
      if (!projTypeToTest.equals(projType.toString())) {
         return false;
      }
      boolean success;
      if (!useMainFile) {
         File toTest = new File(root);
         success = toTest.exists() && toTest.isDirectory();
      }
      else {
         splitMainFileInput(pr.property("MainProjectFile"));
         if (!isPathname) {
            namespace = pr.property("Namespace");
         }
         sourceDirName = pr.property("SourceDir");
         execDirName = pr.property("ExecDir");
         extensions = pr.property("IncludedFiles");
         buildName = pr.property("BuildName");
         setRelMainFilePath();
         mainFilePath = new File(root + F_SEP + relMainFilePath);
         success = mainFilePath.exists() && mainFilePath.isFile();
      }
      if (success) {
         projectRoot = root;
         displaySettings();
         if (pr == conf) {
            store(pr);
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

   private void setRelMainFilePath() {
      StringBuilder sb = new StringBuilder();
      if (!sourceDirName.isEmpty()) {
         sb.append(sysFileSepPath(sourceDirName)).append(F_SEP);
      }
      if (!namespace.isEmpty()) {
         sb.append(namespace).append(F_SEP);
      }
      sb.append(mainFileName).append(sourceExtension);
      relMainFilePath = sb.toString();
   }

   private String sysFileSepPath(String path) {
      return
         path.replaceAll("/",
               java.util.regex.Matcher.quoteReplacement(F_SEP));
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
      sw.displayCmdOptions(cmdOptions);
      sw.displayCmdArgs(cmdArgs);
      sw.displayCompileOption(compileOption);
   }

   private boolean isConfigured(String rootToTest) {
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
      return success && !isShowNameConflictMsg();
   }
   
   private boolean isShowNameConflictMsg() {
      if (isNameConflict && showNameConflictMsg) {
         Dialogs.warnMessageOnTop(nameConflictMsg());
         showNameConflictMsg = false;
         return true;
      }
      return false;
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

   private String nameConflictMsg() {
      return
         "<html>"
         + mainFileName
         + sourceExtension()
         + " seems to exist more than once. The currently set"
         + " file is<br><blockquote>"
         + projectName()
         + F_SEP
         + relMainFilePath
         + "</blockquote><br>"
         + PATHNAME_INFO
         + "</html>";
   }

   private final static String PATHNAME_INFO
      = "<html><hr>"
      + "TO SELECT A FILE IN A SUB-DIRECTORY:<br>"
      + "Enter a path for the sources directory and/or a pathname for"
      + " the file.<br>"
      + "A path is relative to the project directory and a pathname of"
      + " the file is<br>relative to the sources directory if this is"
      + " specified."
      + "</html>";
}
