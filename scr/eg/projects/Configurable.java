package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface to configure a project.
 * <p>
 * 'Configuration' firstly means the finding of the project's root
 * directory based on a given known directory structure of a project.
 * For example, the project root of a Java project could be the parent
 * of the path {sources Directory}/{package}/{main java file}. It
 * could as well be just the parent of the main file if subdirectories
 * are not specified.
 * <p>
 * Implementing class must have a reference to an object of type
 * {@link SettingsWin}.
 * <p>
 * The project may be configured by the entries in the settings window
 * or by reading in entries in a 'prefs' file
 */
public interface Configurable {
   
   /**
    * Adds an {@code ActionListener} to the ok button of this
    * {@code SettingsWin} 
    * @param al  the ActionListener
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
    * If a project was successfully configured based on
    * entries in the window of this {@code SettingsWin}
    * @param dir  the directory of any file that is part of
    * the project
    * @param suffix  the extension of the file
    */
   public boolean configFromSetWin(String dir, String suffix);
   
   /**
    * If a previously used project can be assigned to this class
    * @param dir  the directory that may include the root directory of a
    * previous project 
    * @return  if a prvious project could be assigned to this class
    */
   public boolean findPreviousProjectRoot(String dir);
   
   /**
    * Returns this project's root directory
    * @return  the project's root directory
    */
    public String applyProjectRoot();
   
   /**
    * If the specified directory includes the project's root
    * directory
    * @param dir  the directory that may include the project's root
    * directory
    * @return  if the specified directory includes the project's root
    * directory
    */
   public boolean isInProjectPath(String dir);
   
   /**
     * Returns the project's name
     * @return   the name of the project which is the parent directory
     * of the project's files or subdirecties containg the files
     */
   public String getProjectName();
   
   /**
    * Returns the directory that contains executable files or
    * the empty String if such directory does not exist
    */
   public String getExecutableDir();
   
   /**
    * Stores the current configuration to 'prefs'
    */
   public void storeConfig();
}