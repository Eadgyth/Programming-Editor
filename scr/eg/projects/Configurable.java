package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface to configure a project.
 * <p>
 * 'Configuration' means to define the directory structure of project
 * and the finding of the project's root directory based om this
 * structure.
 * For example, the project root of a Java project could be the parent
 * of the path {sources Directory}/{package}/{main java file}. It
 * could as well be just the parent of the main file if subdirectories
 * are not specified.
 * <p>
 * The implementing class must have a reference to an object of type
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
    * If a project can be successfully configured based on entries in
    * the window of this {@code SettingsWin}
    * @param dir  the directory of any file that is part of the project
    * @param suffix  the extension of the file
    */
   public boolean configFromSetWin(String dir, String suffix);
   
   /**
    * If a project that was active when the program was closed the
    * last time can be retrieved.
    * @param dir  the directory that may include the root directory of 
    * the last project 
    * @return  if a project that was active when the program was closed the
    * last time can be retrieved
    */
   public boolean retrieveLastProject(String dir);
   
   /**
    * Passes to other classes, specifically {@code ProcessStarter} and
    * {@code FileTree}, the project's root directory or other directories
    * defined for the project, as needed
    */
    public void applyProject();
   
   /**
    * If the specified directory includes the project's root directory
    * @param dir  the directory that may include the project's root
    * directory
    * @return  if the specified directory includes the project's root
    * directory
    */
   public boolean isInProjectPath(String dir);
   
   /**
     * Returns the name of the project's root directory
     * @return  the name of the project's root directory
     */
   public String getProjectName();
   
   /**
    * Stores the current configuration to 'prefs'
    */
   public void storeConfig();
}