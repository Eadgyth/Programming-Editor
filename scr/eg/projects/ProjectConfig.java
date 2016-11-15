package eg.projects;

import java.io.File;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.ShowJOption;

/**
 * Represents the configuration of a project
 */
public abstract class ProjectConfig implements Configurable {

   private static final String F_SEP = File.separator;
   
   private final Preferences prefs = new Preferences();
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
      prefs.readPrefs();
   }

   @Override
   public SettingsWin getSetWin() {
      return setWin;
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
      findPreviousProject(dir);
      return projectPath.length() > 0;
   }
   
   @Override
   public String getProjectRoot() {
      return projectPath;
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return previousProjectRoot(dir) != null;
   }
   
   /**
    * @return  the of the (main program) file which a project was
    * was defined for and that was entered in the text field of
    * {@code SettingsWin} or found in prefs
    */ 
   public String getMainFile() {
      return mainFile;
   }

   /**
    * @return  the directory of the module or path of modules entered
    * in the text field of {@code SettingsWin} or found in prefs
    */ 
   public String getPackageDir() {
      return moduleDir;
   }
   
   /**
    * @return  the name for the executables directory entered in the
    * text field of {@code SettingsWin} or found in prefs
    */ 
   public String getExecutableDir() {
      return execDir;
   }
   
   /**
    * @return  the name for the sources directory entered in the
    * text field of {@code SettingsWin} or found in prefs
    */ 
   public String getSourceDir() {
      return sourceDir;
   }

   /**
    * @return  the name for a build entered in the
    * text field of {@code SettingsWin}
    */ 
   public String getBuildName() {
      return buildName;
   }
   
   /**
    * @return  the arguments for a start command entered in the
    * text field of {@code SettingsWin}
    */ 
   public String getArgs() {
      return args;
   }
   
   /**
    * Returns true if the main (program) file exists in the path specified
    * by the executables directory and the module directory
    */
   public boolean mainProgramFileExists(String suffix) { 
      File target = new File(projectPath + F_SEP + execDir + F_SEP + moduleDir
            + F_SEP + mainFile + suffix);
      return target.exists();
   }
   
   //
   //--private--
   //

   private void findPreviousProject(String dir) {
      String previousProjectRoot = previousProjectRoot(dir);
         
      if (previousProjectRoot != null) {

         projectPath = previousProjectRoot;
         
         mainFile = prefs.prop.getProperty("recentMain");
         setWin.displayFile(mainFile);
         
         moduleDir = prefs.prop.getProperty("recentModule");
         setWin.displayModule(moduleDir);
         
         sourceDir = prefs.prop.getProperty("recentSourceDir");
         setWin.displaySourcesDir(sourceDir);
         
         execDir = prefs.prop.getProperty("recentExecDir");
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
      File project = new File(prefs.prop.getProperty("recentProject"));
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

      if (search == null) {
         ShowJOption.warnMessageToFront("A valid filepath could not be built");
      }
      else {
         projectPath = search.toString();
         prefs.storePrefs("recentProject", projectPath);
      }
   }
   
   private void getTextFieldsInput() {
      mainFile = setWin.projectFileIn();
      prefs.storePrefs("recentMain", mainFile);

      moduleDir = setWin.moduleIn();
      prefs.storePrefs("recentModule", moduleDir );
      
      sourceDir = setWin.sourcesDirIn();
      prefs.storePrefs("recentSourceDir", sourceDir);
      
      execDir = setWin.execDirIn();
      prefs.storePrefs("recentExecDir", execDir );
      
      args = setWin.argsIn();
      
      buildName = setWin.buildNameIn();
   }
}