package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.Preferences;

import eg.utils.JOptions;

/**
 * Represents the configuration of a project.
 * <p>
 * 'Configuration' firstly means the finding of the project root
 * which in the simplest case would be the parent of the main project
 * file (or of a specifiable subdirectory where the file is saved). <br>
 * It depends on the parameters passed to the contructor of
 * {@link SettingsWin} which other properties are asked for. For example,
 * the project root of a Java project may be the parent of the path
 * {sources Directory}/{package}/{main java file}. <br>
 * The project may be configured by the entries in the settings window
 * or by reading in entries in the 'prefs' file
 */
public abstract class ProjectConfig implements Configurable {

   private final static String F_SEP = File.separator;
   
   private final static Preferences PREFS = new Preferences();
   private final SettingsWin setWin;

   private String projectPath = "";
   private String mainFile = "";
   private String moduleDir = "";
   private String execDir = "";
   private String sourceDir = "";
   private String args = "";
   private String buildName = "";
   
   /**
    * @param setWin  the reference to an object of {@link SettingsWin}
    * which is set up to ask for the desired inputs
    */
   public ProjectConfig(SettingsWin setWin) {
      this.setWin = setWin;
      PREFS.readPrefs();
   }

   @Override
   public void addOkAction(ActionListener al) {
      setWin.okAct(al);
   }
   
   @Override
   public void makeSetWinVisible(boolean isVisible) {
      setWin.makeVisible(isVisible);
   }
   
   @Override
   public boolean configFromSetWin(String dir, String suffix) {
      boolean success = configureProject(dir, suffix);
      if (success) {
         setWin.makeVisible(false);
      }
      return success;
   }
   
   @Override
   public boolean findPreviousProjectRoot(String dir) {
      configurePreviousProject(dir);
      return projectPath.length() > 0;
   }
   
   @Override
   public String getProjectRoot() {
      return projectPath;
   }
   
   @Override
   public String getProjectName() {
      File f = new File(projectPath);
      return f.getName();
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return previousProjectRoot(dir) != null;
   }
   
   @Override
   public void storeConfig() {
      storeInputs();
   }
   
   /**
    * Returns the project's main file
    * @return  the name of project's main file
    */ 
   protected String getMainFile() {
      return mainFile;
   }

   /**
    * Returns the name of the directory of a module
    * @return  the name directory of a module. In the case of a Java
    * project module would be a package
    */ 
   protected String getModuleDir() {
      return moduleDir;
   }
   
   /**
    * Returns the name of the directory where executable files are
    * saved
    * @return  the name of the directory where executable files are
    * saved
    */ 
   protected String getExecutableDir() {
      return execDir;
   }
   
   /**
    * Returns the name of the directoy where source files are
    * saved
    * @return  the name of the directoy where source files are
    * saved
    */ 
   protected String getSourceDir() {
      return sourceDir;
   }

   /**
    * @return  the name for a build entered in the
    * text field of this {@code SettingsWin}
    */ 
   protected String getBuildName() {
      return buildName;
   }
   
   /**
    * @return  the arguments for a start command entered in the
    * text field of this {@code SettingsWin}
    */ 
   protected String getArgs() {
      return args;
   }
   
   /**
    * If the main executable file exists
    * <p>
    * @param suffix  the extension of the project's main file
    * @return  true if the main executable file exists
    */
   protected boolean mainProgramFileExists(String suffix) { 
      File target = new File(projectPath + F_SEP + execDir + F_SEP + moduleDir
            + F_SEP + mainFile + suffix);
      return target.exists();
   }
   
   //
   //--private--
   //

   private void configurePreviousProject(String dir) {
      String previousProjectRoot = previousProjectRoot(dir);
         
      if (previousProjectRoot != null) {
         
         mainFile = PREFS.prop.getProperty("recentMain");
         setWin.displayFile(mainFile);
         
         moduleDir = PREFS.prop.getProperty("recentModule");
         setWin.displayModule(moduleDir);
         
         sourceDir = PREFS.prop.getProperty("recentSourceDir");
         setWin.displaySourcesDir(sourceDir);
         
         execDir = PREFS.prop.getProperty("recentExecDir");
         setWin.displayExecDir(execDir);
         
         projectPath = previousProjectRoot;
      }
   }

   private String previousProjectRoot(String dir) { 
      File newFile = new File(dir);
      File project;
      if (projectPath.length() > 0) {
         project = new File(projectPath);
      }
      else {  
         project = new File(PREFS.prop.getProperty("recentProject"));
      }
      String newFileStr = newFile.getPath();
      String projStr = project.getPath();

      boolean isEqual = projStr.equals(newFileStr);
      while(!isEqual) {
         if (newFile.getParentFile() == null) {
            newFileStr = null;
            break;
         }       
         newFile    = new File(newFile.getParent());
         newFileStr = newFile.getAbsolutePath();
         isEqual    = projStr.equals(newFileStr);
      }
      return newFileStr;         
   }

   private boolean configureProject(String dir, String suffix) {
      getTextFieldsInput();

      String dirRelToRoot = "";
      if (sourceDir.length() > 0 & moduleDir.length() == 0) {
         dirRelToRoot += sourceDir;
      }
      else if (sourceDir.length() == 0 & moduleDir.length() > 0) {
         dirRelToRoot += moduleDir;
      }
      else if (sourceDir.length() > 0 & moduleDir.length() > 0) {
         dirRelToRoot += sourceDir + F_SEP + moduleDir;
      }
      
      String filePathRelToRoot = dirRelToRoot + F_SEP
            + mainFile + suffix;
      
      String parent = "";
      if (dirRelToRoot.length() > 0) {
         int start = dir.indexOf(dirRelToRoot);
         if (start != -1) {
            parent = dir.substring(0, start - 1);
         }
      }
      else {
         parent = dir;
      }
      boolean isSet = new File(parent + F_SEP + filePathRelToRoot).exists();
      if (!isSet) {
         JOptions.warnMessageToFront("A valid filepath could not be found");
      }
      else {
         projectPath = parent;
         storeInputs();
      }
         
      return isSet;
   }
   
   private void getTextFieldsInput() {
      mainFile = setWin.projectFileIn();
      moduleDir = setWin.moduleIn();
      sourceDir = setWin.sourcesDirIn();
      execDir = setWin.execDirIn();
      args = setWin.argsIn();
      buildName = setWin.buildNameIn();
   }
   
   private void storeInputs() {
      PREFS.storePrefs("recentProject", projectPath);
      PREFS.storePrefs("recentMain", mainFile);
      PREFS.storePrefs("recentModule", moduleDir);
      PREFS.storePrefs("recentSourceDir", sourceDir);
      PREFS.storePrefs("recentExecDir", execDir);
   }
}