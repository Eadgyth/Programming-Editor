package eg.projects;

import java.io.File;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.Preferences;

/**
 * Represents the configuration of a project.
 * <p>
 * 'Configuration' firstly refers to the finding of the project root
 * which in the simplest case would be the parent of the main project
 * file (or of a specifiable subdirectory where the file is saved). <br>
 * It depends on the parameters passed to the contructor of
 * {@link SettingsWin} which other properties are asked for. For example,
 * the project root of a Java project with a subdirectory for sources and a
 * main class in a package is found by returning the parent of the path
 * [sources Dir.]/[package Dir]/[main java file]. <br>
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
      findNewProjectRoot(dir, suffix);
      boolean success = projectPath.length() > 0;
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
   
   /**
    * Returns the project's main file
    * @return  the name of project's main file
    */ 
   protected String getMainFile() {
      return mainFile;
   }

   /**
    * Returns the name of the directory that represents a module
    * @return  the directory of a module. The module is a subdirectory
    * of the project root (if asked for) a subdirectory of the
    * 'sources' directory.
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
    * Returns if the main exectubale file exists.
    * <p>
    * The filepath consists in the project's root directory, the executables'
    * directory, the module's directory and the project's main file.
    * @param suffix  the extension of the project's main file
    * @return  true if the filepath specified by this project configuration
    * exists.
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

         projectPath = previousProjectRoot;
         
         mainFile = PREFS.prop.getProperty("recentMain");
         setWin.displayFile(mainFile);
         
         moduleDir = PREFS.prop.getProperty("recentModule");
         setWin.displayModule(moduleDir);
         
         sourceDir = PREFS.prop.getProperty("recentSourceDir");
         setWin.displaySourcesDir(sourceDir);
         
         execDir = PREFS.prop.getProperty("recentExecDir");
         setWin.displayExecDir(execDir);
      }
      else {
         projectPath = "";
         setWin.displayModule("");
         setWin.displayFile("");
         setWin.displaySourcesDir("");
         setWin.displayExecDir("");
         mainFile = "";
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

   private void findNewProjectRoot(String dir, String suffix) {
      projectPath = "";
      getTextFieldsInput();
      /*
       * may include sourceDir and/or moduleDir to begin with */
      File search = new File(dir);

      final String pathRelToRoot = sourceDir + F_SEP + moduleDir + F_SEP
            + mainFile + suffix;
      String pathToSearch = dir + F_SEP + pathRelToRoot;    
      File searchPath = new File(pathToSearch);
      
      while(!searchPath.exists()) {
         if (search.getParentFile() == null) {
            search = null;
            break;
         }
         String newPath = search.getParent();
         search = new File(newPath);     
         pathToSearch = newPath + F_SEP + pathRelToRoot;
         searchPath = new File(pathToSearch);
      }

      if (search != null) {
         projectPath = search.toString();
      }
   }
   
   private void getTextFieldsInput() {
      mainFile = setWin.projectFileIn();
      PREFS.storePrefs("recentMain", mainFile);

      moduleDir = setWin.moduleIn();
      PREFS.storePrefs("recentModule", moduleDir );
      
      sourceDir = setWin.sourcesDirIn();
      PREFS.storePrefs("recentSourceDir", sourceDir);
      
      execDir = setWin.execDirIn();
      PREFS.storePrefs("recentExecDir", execDir );
      
      args = setWin.argsIn();
      
      buildName = setWin.buildNameIn();
   }
}