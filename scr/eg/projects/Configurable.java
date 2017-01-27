package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface to configure a project.
 * <p>
 * 'Configuration' means to define the directory structure of a project
 * and the finding of the project's root directory based on this
 * structure.
 * <p>
 * For example, the project's root of a Java project could be the parent
 * of the path {sources Directory}/{package}/{main java file} if this
 * path exists. It could as well be just the parent of the main file if
 * subdirectories are not specified.
 */
public interface Configurable {
   
   /**
    * Creates and sets a new {@link SettingsWin} and configures the window
    * to display the input options needed for a given project
    */
   public void createSettingsWin();
   
   /**
    * Adds an {@code ActionListener} to the ok button of this
    * SettingsWin
    * @param al  the ActionListener
    */
   public void addOkAction(ActionListener al);
   
   /**
    * Makes the window of this SettingsWin object visible/invisible
    * @param enable  true to make the window for project settings
    * visible, false to make it invisible
    */
   public void makeSetWinVisible(boolean enable);
   
   /**
    * If a project can be successfully configured based on the entries in
    * the ui of this {@code SettingsWin}
    * @param dir  the directory of a file that maybe part of the project
    * @return  if a project can be successfully configured based on entries
    * in the window of this SettingsWin
    */
   public boolean configureProject(String dir);
   
   /**
    * If a project stored in some preferences file(s) can be retrieved
    * @param dir  the directory of a file that maybe part of a stored
    * project 
    * @return  if the specified directory is part of a project whose 
    * configuration is stored a some preferences file(s)
    */
   public boolean retrieveProject(String dir);
   
   /**
    * If the specified directory includes the project's root directory
    * @param dir  the directory that may include the project's root
    * directory
    * @return  if the specified directory includes the project's root
    * directory
    */
   public boolean isProjectInPath(String dir);
   
   /**
     * Returns the name of the project's root directory
     * @return  the name of the project's root directory
     */
   public String getProjectName();
   
   /**
    * Saves the current configuration to a preferences file
    */
   public void storeInPrefs();
}
