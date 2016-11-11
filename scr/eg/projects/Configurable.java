package eg.projects;

/**
 * The interface to configure a project
 */
public interface Configurable {
   
   /**
    * Returns the {@code SettingsWin} object which is of type
    * {@code ProjectConfig}
    * @return  the {@link SettingsWin} object of type
    * {@link ProjectConfig}
    */
   public SettingsWin getSetWin();
   
   /**
    * Makes the window of this {@code SettingsWin} object
    * visible/invisible
    */
   public void makeSetWinVisible(boolean enable);
   
   /**
    * Configures a project based on entries in the window
    * of this {@code SettingsWin} object
    * @param dir  the directory of the opened file
    * @param suffix  the extension of the file
    */
   public void configFromSetWin(String dir, String suffix);
   
  /**
    * Tries to find the previously used project based on the specified
    * directory and the recent directory saved in 'Prefs'.
    * <p>
    * The found project root is stored in this and retrieved by
    * {@link #getProjectRoot()}.
    * @param dir  the directory that includes the directory of a
    * previous project or not  
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
    * @return  if the specified directory includes the project's root
    * directory
    */
   public boolean isInProjectPath(String dir);
}