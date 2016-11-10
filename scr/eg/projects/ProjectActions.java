package eg.projects;

/**
 * The methods required to configure and run a programming project 
 * in the Editor
 */
public interface ProjectActions {
   
   /**
    * Sets a new object of {@code ProjectConfig}
    * @param projConf  a newly created  {@link ProjectConfig}
    */
   public void setProjectConfig(ProjectConfig projConf);
   
   /**
    * Returns the {@code SettingsWin} object which is of type
    * {@code ProjectConfig}
    * @return  the {@link SettingsWin} object of type
    * {@link ProjectConfig}
    */
   public SettingsWin getSetWin();
   
   /**
    * Makes the {@code SettingsWin} window visible/invisible
    */
   public void makeSetWinVisible(boolean enable);
   
   /**
    * Configures a project based on entries in {@code SettingsWin}
    */
   public void configFromSetWin(String dir);
   
   /**
    * Searches a previous project root directory based on
    * entries in prefs.properties file
    */
   public void findPreviousProjectRoot(String dir);
   
   /**
    * Returns the project's root directory
    * @return  the project's root directory
    */
   public String getProjectRoot();
   
   /**
    * Determines if the specified directory includes the project's
    * root directory
    * @return  whether the specified directory includes
    * the project's root
    */
   public boolean isInProjectPath(String dir);
   
   /**
    * compiles source files
    */
   public void compile();
   
   /**
    * Runs a project
    */
   public void runProject();
   
   /**
    * Creates a build of a project
    */
   public void build();
}