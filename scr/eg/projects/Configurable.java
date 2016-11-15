package eg.projects;

/**
 * The interface to configure a project
 */
public interface Configurable {
   
   /**
    * Returns the {@code SettingsWin} object that is used to
    * configure this project
    * @return  the {@link SettingsWin} object
    */
   public SettingsWin getSetWin();
   
   /**
    * Makes the window of the {@code SettingsWin} object that
    * is used to configure this project visible/invisible
    * @param enable  true to make the window for project settings
    * visible, false to make it invisible
    */
   public void makeSetWinVisible(boolean enable);
   
   /**
    * Configures a project based on entries in the window of the
    * {@code SettingsWin} object that is used to configure
    * this project
    * @param dir  the directory of the opened file
    * @param suffix  the extension of the file
    */
   public void configFromSetWin(String dir, String suffix);
   
  /**
    * Tries to find the previously used project
    * @param dir  the directory that includes the directory of a
    * previous project or not  
    */
   public void findPreviousProjectRoot(String dir);
   
   /**
    * Returns this project's root directory
    * @return  the project's root directory
    */
   public String getProjectRoot();
   
   /**
    * Determines if the specified directory includes the project's
    * root directory
    * @return  if the specified directory includes the project's root
    * directory
    */
   public boolean isInProjectPath(String dir);
}