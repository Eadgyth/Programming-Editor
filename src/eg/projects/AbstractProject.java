package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.Dialogs;

/**
 * The configuration of a project.
 * <p>
 * This class works in combination with an object of
 * {@link SettingsWindow} where values required for a configuration
 * are entered.
 * <p>
 * Parameters that describe the configuration of a project are
 * stored in the "prefs.properties" file in the program folder
 * and optionally in an "eadproject.properies" file that is
 * stored in the root folder of a project.
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
   private final static Preferences PREFS = Preferences.readProgramPrefs();
   private final static Preferences EAD_PROJ = Preferences.prefs();
   private final SettingsWindow sw;  
   private final String ext;
   private final boolean useProjectFile;

   //
   // Variables available to a project and partly to a class that
   // creates a project
   private String projectRoot = "";
   private String mainFileName = "";
   private String namespace = "";
   private String execDirName = "";
   private String sourceDirName = "";
   private String startOptions = "";
   private String args = "";
   private String searchExtensions = "";
   private String buildName = "";
   //
   // Variables to control the configuration
   private String rootToTest = "";
   private boolean isPathname = false;
   private boolean isNameConflict = false;
   
   /**
    * {@inheritDoc}.
    * <p>
    * The window shows the text field to enter the name of the presumed
    * root directory of the project. An overriding method may use this
    * {@link SettingsWindow.InputOptionsBuilder} to build a window with
    * additional input options but must not call
    * <code>super.buildSettingsWindow()</code>
    */
   @Override
   public void buildSettingsWindow() {
      inputOptions.buildWindow();
   }

   @Override
   public final void setConfiguringAction(ActionListener al) {
      sw.okAct(al);
   }

   @Override
   public final void makeSettingsWindowVisible() {
      sw.setVisible(true);
   }

   @Override
   public final boolean configureProject(String dir) {
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
         setCommandParameters();
         sw.setVisible(false);
      }
      return success;
   }

   /**
    * {@inheritDoc}.
    * <p>
    * First it is tried to find an "eadproject" file in <code>dir</code>
    * or further upward the directory path and, if this is not found,
    * it is tested if <code>dir</code> is contained in the recent project
    * saved in the "prefs" file in the program folder.
    */
   @Override
   public final boolean retrieveProject(String dir) {
      String root = findRootByFile(dir, Preferences.EAD_PROJ_FILE);
      if (root.length() > 0) {
         sw.setSaveEadprojectSelected(true);
         EAD_PROJ.readEadproject(root);
         configByPropertiesFile(root, EAD_PROJ);
      }
      else {
         sw.setSaveEadprojectSelected(false);
         PREFS.readPrefs();
         root = PREFS.getProperty("projectRoot");
         if (isInProject(dir, root)) {
             configByPropertiesFile(root, PREFS);
         }
      }
      boolean success = projectRoot.length() > 0;
      if (success) {
         setCommandParameters();
      }
      return success;
   }

   @Override
   public final boolean usesProjectFile() {
      return useProjectFile;
   }

   @Override
   public final boolean isInProject(String dir) {
      return isInProject(dir, projectRoot);
   }

   @Override
   public final String getProjectPath() {
      return projectRoot;
   }

   @Override
   public final String getProjectName() {
      File f = new File(projectRoot);
      return f.getName();
   }

   @Override
   public final String getExecutableDirName() {
      return execDirName;
   }

   @Override
   public final String getSourceFileExtension() {
      return ext;
   }

   @Override
   public final void storeConfiguration() {
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
      sw = new SettingsWindow();
      inputOptions = sw.getInputOptionsBuilder();
   }
   
   /**
    * Sets command parameters that are necessary for actions defined in
    * <code>ProjectActions</code>
    *
    * @see ProjectActions
    */
   protected abstract void setCommandParameters();

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
    * Returns the options for a start script
    *
    * @return  the options. The empty string if no arguments are given
    */
   protected String getStartOptions() {
      return startOptions;
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
    * Returns the array that contains extensions that may be used for
    * a file search
    *
    * @return  the array or null of no extensions are given
    */
   protected String[] getSearchExtensions() {
      if (searchExtensions.length() == 0) {
          return null;
       }
       else {
          return searchExtensions.split(",");
       }
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
      StringBuilder sb = new StringBuilder(projectRoot + "/");
      if (execDirName.length() > 0) {
         sb.append(execDirName).append("/");
      }
      if (namespace.length() > 0) {
         sb.append(namespace).append("/");
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
      String rootInput = sw.projDirNameInput();
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
      if (!isPathname) {
         String sourceRoot = root;
         if (sourceDirName.length() > 0) {
            sourceRoot = sourceRoot + "/" + sourceDirName;
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
         File fToTest = new File(root + "/" + pathRelToRoot());
         if (fToTest.exists()) {
            rootToTest = root;
         }
      }
   }

   private void getTextFieldsInput() {
      String mainFileInput = sw.fileNameInput();
      splitMainFilePath(mainFileInput);
      sourceDirName = sw.sourcesDirNameInput();
      execDirName = sw.execDirNameInput();
      startOptions = sw.startOptInput();
      args = sw.argsInput();
      searchExtensions = sw.searchExtensionsInput();
      buildName = sw.buildNameInput();
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
      String extToTest = prefs.getProperty("sourceExtension");
      if (!ext.equals(extToTest)) {
         return;
      }
      String mainFileInput = prefs.getProperty("mainProjectFile");
      splitMainFilePath(mainFileInput);
      if (isPathname) {
         sw.displayFile(namespace + F_SEP + mainFileName);
      }
      else {
         sw.displayFile(mainFileName);
         namespace = prefs.getProperty("namespace");
      }
      sourceDirName = prefs.getProperty("sourceDir");
      sw.displaySourcesDir(sourceDirName);
      execDirName = prefs.getProperty("execDir");
      sw.displayExecDir(execDirName);
      searchExtensions = prefs.getProperty("includedFiles");
      sw.displaySearchExtensions(searchExtensions);
      buildName = prefs.getProperty("buildName");
      sw.displayBuildName(buildName);

      File fToTest = new File(root + F_SEP + pathRelToRoot());
      if (fToTest.exists()) {
         boolean ok = (useProjectFile && fToTest.isFile())
               || (!useProjectFile && fToTest.isDirectory());

         if (ok) {
            projectRoot = root;
            sw.displayProjDirName(getProjectName());
            if (prefs == EAD_PROJ) {
               storeInPrefs();
            }
         }
      }
   }

   private void splitMainFilePath(String mainFileInput) {
      String formatted = mainFileInput.replace("\\", "/");
      int lastSepPos = formatted.lastIndexOf("/", mainFileInput.length());
      if (lastSepPos != -1) {
         namespace = formatted.substring(0, lastSepPos).replaceAll("/",
               java.util.regex.Matcher.quoteReplacement(F_SEP));

         mainFileName = formatted.substring(lastSepPos + 1);
         isPathname = true;
      }
      else {
         mainFileName = mainFileInput;
         isPathname = false;
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
         sb.append(sourceDirName).append("/");
      }
      if (namespace.length() > 0) {
         sb.append(namespace).append("/");
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
            if (useProjectFile) {
               Dialogs.warnMessageOnTop(INPUT_ERROR_GENERAL);
            }
            else {
               Dialogs.warnMessageOnTop(INPUT_ERROR_PROJ_ROOT);
            }
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
      storeInEadprojectFile();
   }

   private void storeInPrefs() {
      PREFS.storePrefs("projectRoot", projectRoot);
      PREFS.storePrefs("sourceDir", sourceDirName);
      PREFS.storePrefs("execDir", execDirName);
      PREFS.storePrefs("includedFiles", searchExtensions);
      PREFS.storePrefs("buildName", buildName);
      PREFS.storePrefs("sourceExtension", ext);
      if (isPathname) {
         PREFS.storePrefs("mainProjectFile", namespace + F_SEP + mainFileName);
         PREFS.storePrefs("namespace", "");
      }
      else {
         PREFS.storePrefs("mainProjectFile", mainFileName);
         PREFS.storePrefs("namespace", namespace);
      }
   }

   private void storeInEadprojectFile() {
      if (sw.isSaveToEadproject()) {
         EAD_PROJ.storeEadproject("sourceDir", sourceDirName, projectRoot);
         EAD_PROJ.storeEadproject("execDir", execDirName, projectRoot);
         EAD_PROJ.storeEadproject("includedFiles", searchExtensions, projectRoot);
         EAD_PROJ.storeEadproject("buildName", buildName, projectRoot);
         EAD_PROJ.storeEadproject("sourceExtension", ext, projectRoot);
         if (isPathname) {
            EAD_PROJ.storeEadproject("mainProjectFile", namespace + F_SEP + mainFileName,
                  projectRoot);
            EAD_PROJ.storeEadproject("namespace", "", projectRoot);
         }
         else {
            EAD_PROJ.storeEadproject("mainProjectFile", mainFileName, projectRoot);
            EAD_PROJ.storeEadproject("namespace", namespace, projectRoot);
         }
      }
      else {
         deleteEadprojectFile();
      }
   }

   private void deleteEadprojectFile() {
      File configFile = new File(projectRoot + "/" + Preferences.EAD_PROJ_FILE);
      if (configFile.exists()) {
         int res = Dialogs.warnConfirmYesNo(DELETE_EAD_PROJ_OPT);
         if (res == 0) {
            boolean success = configFile.delete();
            if (!success) {
               Dialogs.warnMessage("Deleting the \"eadproject\" file failed");
            }
         }
         else {
            sw.setSaveEadprojectSelected(true);
            storeInEadprojectFile();
         }
      }
   }
   
   //
   //--Strings for messages
   //

   private String nameConflictMessage() {
      return
         "<html>"
         + "The filename \"" + mainFileName + "\" seems to exist more"
         + " than once in the project.<br>"
         + "<br>"
         + "If this name should be maintained its pathname relative"
         + " to the source root must be specified.<br>"
         + "The source root is the sources directory if available or"
         + " the root directory of the project."
         + "</html>";
   }
   
   private final static String DELETE_EAD_PROJ_OPT
         =  "<html>"
         + "Saving the project settings in the project folder is"
         + " no more selected.<br>"
         + " Remove the \"eadproject\" file?"
         + "</html>";
   
   private final static String INPUT_ERROR_PROJ_ROOT
         =  "The entry for the project root cannot be matched with an"
         + " existing file.";
        
   private final static String INPUT_ERROR_GENERAL
         =  "The entries cannot be matched with an existing file.";
}
