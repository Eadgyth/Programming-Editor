package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface to configure a project
 */
public interface Configurable {
   
   /**
    * Adds an event handler at the ok button of this {@code SettingsWin}
    * object 
    */
   public void addOkAction(ActionListener al);
   
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
    * @param dir  the directory of project's main file
    * @param suffix  the extension of the project's main file
    */
   public boolean configFromSetWin(String dir, String suffix);
   
  /**
    * Returns if a previously used project could be assigned to this class
    * @param dir  the directory that may include the root directory of a
    * previous project 
    * @return  if a prvious project could be assigned to this class
    */
   public boolean findPreviousProjectRoot(String dir);
   
    /**
     * Returns the project's name
     */
    public String getProjectName();
   
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
   
   /**
    * Stores current configuration to 'prefs'
    */
   public void storeConfig();
}