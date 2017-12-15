package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.Dialogs;

/**
 * Represents the configuration of a project.
 * <p>
 * Class works in combination with {@link SettingsWin}. To configure a project
 * the full path of the parent directory of any file that is supposed to belong
 * to the project is the argument in {@link #configureProject(String)}. This
 * directory may also be a sub-directory of the supposed project root.
 * <p>
 * There are two modes to configure a project:
 * <p>
 * 1) Only a project root is defined. It is tested if the name for the project
 * root entered in the settings window matches the name of the passed in directory.
 * If this is not the case, a match is searched further upwards the directory path.
 * The directory where a match is found is defined as the project root.
 * <p>
 * 2) A project (main) file is defined. It is tested if this file exists in
 * the passed in directory or further upwards the directory path. The directory
 * where the file is found is defined as the project root. The project file
 * may itself be found in sub-directories relative to the supposed root.
 * These must be specified in the settings window and have the order 'sourcesDirName'
 * /'moduleName' if both of these properties are specified. In this case the test
 * file is the relative path of the project file. The name of the root may also be
 * entered in the settings window to require that the found project root has this
 * name.
 * <p>
 * The configuration of a project is stored in the prefs file of the program and
 * optionally in a 'config' file that is saved in the project's root folder. A
 * stored project may be retrieved using {@link #retrieveProject(String)} in which
 * the directory of any file that is part of the project may be passed in.
 */
public abstract class ProjectConfig implements Configurable {

   private final static String F_SEP = File.separator;

   /**
    * The object of <code>SettingsWin</code> associated with this project.
    * Is initially null
    */
   protected SettingsWin setWin = null;
   /*
    * Used to read prefs from the program's Prefs file */
   private final static Preferences PREFS = new Preferences();
   /*
    * Used to read prefs from an 'eadconfig' file that may be saved in a project */
   private final static Preferences EAD_CONFIG = new Preferences();

   private final String ext;
   private final boolean useProjectFile;

   private String projTestName = "";
   private String projectRoot = "";
   private String mainFile = "";
   private String moduleDirName = "";
   private String execDirName = "";
   private String sourceDirName = "";
   private String args = "";
   private String includedExt = "";
   private String buildName = "";

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
      if (useProjectFile) {
         projectRoot = findRootByFile(dir, pathRelToRoot(true));
      }
      else {
         projectRoot = findRootByTestName(dir);
      }
      boolean success =  isConfigSuccessful();
      if (success) {
         setWin.setVisible(false);
      }
      return success;
   }

   /**
    * {@inheritDoc}
    * <p>Method first looks for an 'eadconfig' file in <code>dir</code> or in
    * parents of it and, if this is not present, in the preferences file in
    * the program folder.
    */
   @Override
   public boolean retrieveProject(String dir) {
      String root = findRootByFile(dir, Preferences.CONFIG_FILE);
      if (root.length() > 0) {
         setWin.setSaveConfigSelected(true);
         EAD_CONFIG.readConfig(root);
         configByPropertiesFile(root, EAD_CONFIG);
      }
      else {
         setWin.setSaveConfigSelected(false);
         PREFS.readPrefs();
         root = PREFS.getProperty("recentProject");
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
    * @param fileExtension  the file extension of source files
    * @param useProjectFile  specifies if the project uses a main project file.
    */
   protected ProjectConfig(String fileExtension, boolean useProjectFile) {
      ext = fileExtension;
      this.useProjectFile = useProjectFile;
   }

   /**
    * Returns the name of the project's main file
    *
    * @return  the name
    */
   protected String getMainFile() {
      return mainFile;
   }

   /**
    * Returns the name of the directory of a module.
    *
    * @return  the name
    */
   protected String getModuleName() {
      return moduleDirName;
   }

   /**
    * Returns the name of the directoy where source files are saved
    *
    * @return  the name
    */
   protected String getSourceDirName() {
      return sourceDirName;
   }
   
   /**
    * Returns the arguments for a start script
    *
    * @return  the arguments
    */
   protected String getArgs() {
      return args;
   }
   
   /**
    * Returns the extensions of files to be included in a build or
    * compilation. This is simply the input that was entered in the
    * corresponding text field of the settings window or read in from
    * a properties file.
    *
    * @return  the extensions. The empty string if no extensions where
    * read in
    */
   protected String getIncludedExtensions() {
      return includedExt;
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
    * If the main executable file exists
    *
    * @param aSuffix  the extension of the project's executable file(s)
    * @return  if the main executable file exists
    */
   protected boolean mainExecFileExists(String aSuffix) {
      File f = new File(projectRoot + F_SEP + execDirName + F_SEP
             + moduleDirName + F_SEP + mainFile + aSuffix);

      return f.exists();
   }

   //
   //--private--/
   //

   private void configByPropertiesFile(String root, Preferences prefs) {
      mainFile = prefs.getProperty("recentMain");
      setWin.displayFile(mainFile);
      moduleDirName = prefs.getProperty("recentModule");
      setWin.displayModule(moduleDirName);
      sourceDirName = prefs.getProperty("recentSourceDir");
      setWin.displaySourcesDir(sourceDirName);
      execDirName = prefs.getProperty("recentExecDir");
      setWin.displayExecDir(execDirName);
      includedExt = prefs.getProperty("recentIncludedExt");
      setWin.displayIncludedExt(includedExt);
      buildName = prefs.getProperty("recentBuildName");
      setWin.displayBuildName(buildName);

      File fToTest = new File(root + F_SEP + pathRelToRoot(false));
      if (fToTest.exists()) {
         projectRoot = root;
         setWin.displayProjDirName(getProjectName());
         if (prefs == EAD_CONFIG) {
            storeInPrefs();
         }
      }
      else {
         if (prefs == EAD_CONFIG) {
            Dialogs.errorMessage(
                  "The eadconfig file found in "
                  + root + " may be incorrect");
         }
      }
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

   private String findRootByTestName(String dir) {
      File root = new File(dir);
      String existingName;
      projTestName = setWin.projDirNameInput();
      while(root != null) {
         existingName = root.getName();
         if (projTestName.equals(existingName) && root.exists()) {
            return root.getPath();
         }
         root = root.getParentFile();
      }
      return "";
   }

   private String findRootByFile(String dir, String file) {
      File root = new File(dir);
      String relToRoot = relToRoot = F_SEP + file;
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

   private String pathRelToRoot(boolean bySetWin) {
      if (bySetWin) {
         getTextFieldsInput();
      }
      StringBuilder sb = new StringBuilder();
      if (sourceDirName.length() > 0) {
         sb.append(sourceDirName);
         sb.append(F_SEP);
      }
      if (moduleDirName.length() > 0) {
         sb.append(moduleDirName);
         sb.append(F_SEP);
      }
      if (useProjectFile) {
         sb.append(mainFile);
         sb.append(".");
         sb.append(ext);
      }
      return sb.toString();
   }

   private void getTextFieldsInput() {
      projTestName = setWin.projDirNameInput();
      mainFile = setWin.projectFileNameInput();
      moduleDirName = setWin.moduleNameInput();
      sourceDirName = setWin.sourcesDirNameInput();
      execDirName = setWin.execDirNameInput();
      args = setWin.argsInput();
      includedExt = setWin.includedExtInput().replaceAll("\\s", "");
      buildName = setWin.buildNameInput();
   }

   private boolean isConfigSuccessful() {
      boolean success = projectRoot.length() > 0;
      if (success && projTestName.length() == 0) {
         setWin.displayProjDirName(getProjectName());
      }
      else if (success && projTestName.length() > 0) {
         success = projTestName.equals(getProjectName());
      }
      if (!success) {
         Dialogs.warnMessageOnTop(
                  "The entries cannot be matched with an existing file");
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
      PREFS.storePrefs("recentProject", projectRoot);
      PREFS.storePrefs("recentMain", mainFile);
      PREFS.storePrefs("recentModule", moduleDirName);
      PREFS.storePrefs("recentSourceDir", sourceDirName);
      PREFS.storePrefs("recentExecDir", execDirName);
      PREFS.storePrefs("recentIncludedExt", includedExt);
      PREFS.storePrefs("recentBuildName", buildName);
   }

   private void storeInEadConfig() {
      if (setWin.isSaveConfig()) {
         EAD_CONFIG.storeConfig("recentMain", mainFile, projectRoot);
         EAD_CONFIG.storeConfig("recentModule", moduleDirName, projectRoot);
         EAD_CONFIG.storeConfig("recentSourceDir", sourceDirName, projectRoot);
         EAD_CONFIG.storeConfig("recentExecDir", execDirName, projectRoot);
         EAD_CONFIG.storeConfig("recentIncludedExt", includedExt, projectRoot);
         EAD_CONFIG.storeConfig("recentBuildName", buildName, projectRoot);
      }
      else {
         deleteConfigFile();
      }
   }

   private void deleteConfigFile() {
      File configFile = new File(projectRoot + F_SEP
            + Preferences.CONFIG_FILE);
      if (configFile.exists()) {
         int res = Dialogs.warnConfirmYesNo(
                 "Saving the 'eadconfig' is disabled."
               + " Remove the config file?");

         if (res == 0) {
            boolean success = configFile.delete();
            if (!success) {
               Dialogs.warnMessage(
                     "Deleting the 'eadconfig' file failed");
            }
         }
         else {
            setWin.setSaveConfigSelected(true);
         }
      }
   }
}
