package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.Dialogs;

/**
 * The configuration of a project.
 * <p>
 * An overriding project works in combination with an object of
 * {@link SettingsWin}. This object is adapted to display the needed
 * input options.
 * <p>
 * The parameters that describe the configuration of a project are
 * stored in the "prefs.properties" file in the program folder and
 * optionally in an "eadconfig.properies" file that is stored in the
 * root folder of the project.
 */
public abstract class AbstractProject implements Configurable {

   /**
    * The object of <code>SettingsWin</code> associated with this project.
    * Is initially null.
    */
   protected SettingsWin setWin = null;

   private final static String F_SEP = File.separator;
   /*
    * Reads prefs from the program's Prefs file */
   private final static Preferences PREFS = new Preferences();
   /*
    * Reads prefs from an 'eadconfig' file that may be saved in a project */
   private final static Preferences EAD_CONFIG = new Preferences();
   /*
    * The extension of source files used in a preject */
   private final String ext;
   /*
    * Indicates if a project uses a main project file */
   private final boolean useProjectFile;
   //
   // Variables available to a project and partly to classes that
   // have a reference to the project
   private String projectRoot = "";
   private String mainFileName = "";
   private String namespace = "";
   private String execDirName = "";
   private String sourceDirName = "";
   private String args = "";
   private String includedFiles = "";
   private String buildName = "";
   //
   // Variables to control the configuration */
   private String rootToTest = "";
   private boolean usePathToMain = false;
   private boolean isNameConflict = false;

   @Override
   public void setConfiguringAction(ActionListener al) {
      setWin.okAct(al);
   }

   @Override
   public void makeSetWinVisible() {
      setWin.setVisible(true);
   }

   @Override
   public boolean configureProject(String dir) {
      String root = findRoot(dir);
      if (root.length() > 0) {
         if (useProjectFile) {
            configureByTextFieldsInput(root);
         }
         else {
            rootToTest = root;
         }
      }
      boolean success = isConfigSuccessful();
      if (success) {
         setWin.setVisible(false);
      }
      return success;
   }

   /**
    * {@inheritDoc}
    * <p>
    * First it is tried to find an "eadconfig" file in <code>dir</code>
    * or further upward the directory path and, if this is not present,
    * it is tested <code>dir</code> is part of the recent project saved
    * in the "prefs" file in the program folder.
    */
   @Override
   public boolean retrieveProject(String dir) {
      String root = findRootByFile(dir, Preferences.EAD_CONFIG_FILE);
      if (root.length() > 0) {
         setWin.setSaveConfigSelected(true);
         EAD_CONFIG.readConfig(root);
         configByPropertiesFile(root, EAD_CONFIG);
      }
      else {
         setWin.setSaveConfigSelected(false);
         PREFS.readPrefs();
         root = PREFS.getProperty("projectRoot");
         if (isInProject(dir, root)) {
             configByPropertiesFile(root, PREFS);
         }
      }
      return projectRoot.length() > 0;
   }

   @Override
   public boolean usesProjectFile() {
      return useProjectFile;
   }

   @Override
   public boolean isInProject(String dir) {
      return isInProject(dir, projectRoot);
   }

   @Override
   public String getProjectPath() {
      return projectRoot;
   }

   @Override
   public String getProjectName() {
      File f = new File(projectRoot);
      return f.getName();
   }

   @Override
   public String getExecutableDirName() {
      return execDirName;
   }

   @Override
   public String getSourceFileExtension() {
      return ext;
   }

   @Override
   public void storeConfiguration() {
      storeConfigurationImpl();
   }

   /**
    * Creates an <code>AbstractProject</code>.
    * Sets the file extension of source files and the boolean that,
    * if true, indicates that the project uses a main file.
    *
    * @param extension  the extension
    * @param useProjectFile  the boolean value
    */
   protected AbstractProject(String extension, boolean useProjectFile) {
      ext = extension;
      this.useProjectFile = useProjectFile;
   }

   /**
    * Returns the name of the project's main file (without extension)
    *
    * @return  the name
    */
   protected String getMainFileName() {
      return mainFileName;
   }

   /**
    * Returns the namespace of the main file. This is a relative
    * directory or directory path based at the sources directory or,
    * if a sources directory is not given, at the  project's root
    * directory.
    *
    * @return  the namespace. The empty string if no namespace is given
    */
   protected String getNamespace() {
      return namespace;
   }

   /**
    * Returns the name of the directoy where source files are saved
    *
    * @return  the name. The empty string if no source directory is given
    */
   protected String getSourceDirName() {
      return sourceDirName;
   }

   /**
    * Returns the arguments for a start script
    *
    * @return  the arguments. The empty string if no arguments are given
    */
   protected String getArgs() {
      return args;
   }

   /**
    * Returns the string that contains the input for files that are
    * included in a build and a compilation. The input is formatted as
    * comma separated.
    *
    * @return  the input. The empty string if no files are given
    */
   protected String getIncludedFiles() {
      return includedFiles;
   }

   /**
    * Returns the name for a build
    *
    * @return  the name
    */
   protected String getBuildName() {
      return buildName;
   }

   /**
    * Returns if the main executable file exists
    *
    * @param ext  the extension of executable file (with period)
    * @return  the boolean value that is true if exists
    */
   protected boolean mainExecFileExists(String ext) {
      StringBuilder sb = new StringBuilder(projectRoot + F_SEP);
      if (execDirName.length() > 0) {
         sb.append(execDirName).append(F_SEP);
      }
      if (namespace.length() > 0) {
         sb.append(namespace).append(F_SEP);
      }
      sb.append(mainFileName).append(ext);
      File f = new File(sb.toString());
      return f.exists();
   }

   //
   //--private--//
   //

   private String findRoot(String dir) {
      rootToTest = "";
      File root = new File(dir);
      String existingName;
      String rootInput = setWin.projDirNameInput();
      while (root != null) {
         existingName = root.getName();
         if (rootInput.equals(existingName) && root.exists()) {
            return root.getPath();
         }
         root = root.getParentFile();
      }
      return "";
   }

   private void configureByTextFieldsInput(String root) {
      namespace = "";
      isNameConflict = false;
      getTextFieldsInput();
      if (!usePathToMain) {
         String sourceRoot = root;
         if (sourceDirName.length() > 0) {
            sourceRoot = sourceRoot + F_SEP + sourceDirName;
         }
         findNamespace(sourceRoot, mainFileName + "." + ext);
         if (namespace.length() > 0) {
            if (namespace.length() > sourceRoot.length()) {
               namespace = namespace.substring(sourceRoot.length() + 1);
            }
            else {
               namespace = "";
            }
            rootToTest = root;
         }
      }
      else {
         File fToTest = new File(root + F_SEP + pathRelToRoot());
         if (fToTest.exists()) {
            rootToTest = root;
         }
      }
   }

   private void getTextFieldsInput() {
      String mainFileInput = setWin.fileNameInput();
      splitMainFilePath(mainFileInput);
      sourceDirName = setWin.sourcesDirNameInput();
      execDirName = setWin.execDirNameInput();
      args = setWin.argsInput();
      includedFiles = setWin.includedFilesInput();
      buildName = setWin.buildNameInput();
   }

   private void findNamespace(String sourceRoot, String name) {
      File f = new File(sourceRoot);
      File[] list = f.listFiles();
      if (list != null) {
         for (File fInList : list) {
           if (fInList.isFile()) {
               if (fInList.getName().equals(name)) {
                  if (namespace.length() > 0) {
                     namespace = "";
                     isNameConflict = true;
                  }
                  else {
                     namespace = fInList.getParent();
                  }
               }
            }
            else {
               findNamespace(fInList.getPath(), name);
            }
         }
      }
   }

   private void configByPropertiesFile(String root, Preferences prefs) {
      String mainFileInput = prefs.getProperty("mainProjectFile");
      splitMainFilePath(mainFileInput);
      if (usePathToMain) {
         setWin.displayFile(namespace + "/" + mainFileName);
      }
      else {
         setWin.displayFile(mainFileName);
         namespace = prefs.getProperty("namespace");
      }
      sourceDirName = prefs.getProperty("sourceDir");
      setWin.displaySourcesDir(sourceDirName);
      execDirName = prefs.getProperty("execDir");
      setWin.displayExecDir(execDirName);
      includedFiles = prefs.getProperty("includedFiles");
      setWin.displayIncludedFiles(includedFiles);
      buildName = prefs.getProperty("buildName");
      setWin.displayBuildName(buildName);

      File fToTest = new File(root + F_SEP + pathRelToRoot());
      if (fToTest.exists()) {
         boolean ok = (useProjectFile && fToTest.isFile())
               || (!useProjectFile && fToTest.isDirectory());

         if (ok) {
            projectRoot = root;
            setWin.displayProjDirName(getProjectName());
            if (prefs == EAD_CONFIG) {
               storeInPrefs();
            }
         }
      }
   }

   private void splitMainFilePath(String mainFileInput) {
      String formatted = mainFileInput.replace("\\", "/");
      int lastSepPos = formatted.lastIndexOf("/", mainFileInput.length());
      if (lastSepPos != -1) {
         namespace = formatted.substring(0, lastSepPos);
         mainFileName = formatted.substring(lastSepPos + 1);
         usePathToMain = true;
      }
      else {
         mainFileName = mainFileInput;
         usePathToMain = false;
      }
   }

   private String findRootByFile(String dir, String file) {
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

   private String pathRelToRoot() {
      StringBuilder sb = new StringBuilder();
      if (sourceDirName.length() > 0) {
         sb.append(sourceDirName).append(F_SEP);
      }
      if (namespace.length() > 0) {
         sb.append(namespace).append(F_SEP);
      }
      if (mainFileName.length() > 0) {
         sb.append(mainFileName).append(".").append(ext);
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

   private boolean isConfigSuccessful() {
      boolean success = rootToTest.length() > 0;
      if (!success) {
         if (isNameConflict) {
            Dialogs.warnMessageOnTop(nameConflictMessage());
         }
         else {
            Dialogs.warnMessageOnTop(
                  "The entries cannot be matched with an existing file.");
         }
      }
      else {
         if (projectRoot.length() == 0 || (!projectRoot.equals(rootToTest))) {
            projectRoot = rootToTest;
         }
      }
      return success;
   }

   private void storeConfigurationImpl() {
      if (projectRoot.length() == 0) {
         throw new IllegalStateException("The project is not configured");
      }
      storeInPrefs();
      storeInEadConfig();
   }

   private void storeInPrefs() {
      PREFS.storePrefs("projectRoot", projectRoot);
      PREFS.storePrefs("sourceDir", sourceDirName);
      PREFS.storePrefs("execDir", execDirName);
      PREFS.storePrefs("includedFiles", includedFiles);
      PREFS.storePrefs("buildName", buildName);
      if (usePathToMain) {
         PREFS.storePrefs("mainProjectFile", namespace + "/" + mainFileName);
         PREFS.storePrefs("namespace", "");
      }
      else {
         PREFS.storePrefs("mainProjectFile", mainFileName);
         PREFS.storePrefs("namespace", namespace);
      }
   }

   private void storeInEadConfig() {
      if (setWin.isSaveConfig()) {
         EAD_CONFIG.storeEadConfig("sourceDir", sourceDirName, projectRoot);
         EAD_CONFIG.storeEadConfig("execDir", execDirName, projectRoot);
         EAD_CONFIG.storeEadConfig("includedFiles", includedFiles, projectRoot);
         EAD_CONFIG.storeEadConfig("buildName", buildName, projectRoot);
         if (usePathToMain) {
            EAD_CONFIG.storeEadConfig("mainProjectFile", namespace + "/" + mainFileName,
                  projectRoot);
            EAD_CONFIG.storeEadConfig("namespace", "", projectRoot);
         }
         else {
            EAD_CONFIG.storeEadConfig("mainProjectFile", mainFileName, projectRoot);
            EAD_CONFIG.storeEadConfig("namespace", namespace, projectRoot);
         }
      }
      else {
         deleteConfigFile();
      }
   }

   private void deleteConfigFile() {
      File configFile = new File(projectRoot + F_SEP
            + Preferences.EAD_CONFIG_FILE);

      if (configFile.exists()) {
         int res = Dialogs.warnConfirmYesNo(
               "Saving the 'eadconfig' is disabled."
               + " Remove the config file?");

         if (res == 0) {
            boolean success = configFile.delete();
            if (!success) {
               Dialogs.warnMessage("Deleting the 'eadconfig' file failed");
            }
         }
         else {
            setWin.setSaveConfigSelected(true);
            storeInEadConfig();
         }
      }
   }

   private String nameConflictMessage() {
      return
         "<html>"
         + "The filename \"" + mainFileName + "\" seems to exist more"
         + " than once in the project.<br>"
         + "<ul>"
         + "<li>If this name should be maintained its pathname relative"
         + " to the source root must be specified in the text field"
         + " for the filename.<br>"
         + "(The source root is the sources directory if available or"
         + " the root directory of the project otherwise)."
         + "</html>";
   }
}
