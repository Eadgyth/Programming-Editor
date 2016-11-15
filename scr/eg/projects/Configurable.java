package eg.projects;

/**
 * The interface to configure a project
 */
public interface Configurable {
   
   /**
    * Returns this {@code SettingsWin} object
    * @return  this {@link SettingsWin} object
    */
   public SettingsWin getSetWin();
   
   /**
    * Makes the window of this {@code SettingsWin} object 
    * visible/invisible
    * @param enable  true to make the window for project settings
    * visible, false to make it invisible
    */
   public void makeSetWinVisible(boolean enable);
   
   /**
    * Returns whether a project was successfully configured based on
    * entries in the window of this {@code SettingsWin} object
    * @param dir  the directory of the opened file
    * @param suffix  the extension of the file
    */
   public boolean configFromSetWin(String dir, String suffix);
   
  /**
    * Returns whether a previously used project was found
    * @param dir  the directory that includes the directory of a
    * previous project or not  
    */
   public boolean findPreviousProjectRoot(String dir);
   
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