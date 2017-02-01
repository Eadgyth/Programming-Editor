package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface to configure a project.
 * <p>
 * 'Configuration' means to define the directory structure of a
 * project by entries in a settings window (see {@link SettingsWin}).
 * A project is retrieved by entries in a prefs file which a
 * configuration is saved to.
 */
public interface Configurable {
   
   /**
    * Creates and sets a new object that provides a settings
    * window.
    */
   public void createSettingsWin();
   
   /**
    * Adds an {@code ActionListener} to the ok button of this
    * settings window
    * @param al  the ActionListener
    */
   public void addOkAction(ActionListener al);
   
   /**
    * Makes the settings window visible/invisible
    * @param enable  true to make the window for project settings
    * visible, false to make it invisible
    */
   public void makeSetWinVisible(boolean enable);
   
   /**
    * If a project can be successfully configured based on the entries in
    * the ui of this settings window
    * @param dir  the directory of a file that maybe part of the project
    * @return  if a project can be successfully configured based on entries
    * in the window of this SettingsWin
    */
   public boolean configureProject(String dir);
   
   /**
    * If a project stored in a preferences file can be retrieved
    * @param dir  the directory of a file that maybe part of a stored
    * project 
    * @return  if the specified directory is part of a project whose 
    * configuration is stored in preferences file(s)
    */
   public boolean retrieveProject(String dir);
   
   /**
    * If the project's root directory is in the path of the
    * specified directory
    * @param dir  the directory that may include the project's root
    * directory
    * @return  if the project's root directory is in the path
    * of {@code dir}
    */
   public boolean isInProject(String dir);
   
   /**
    * Returns the path of the project's root directory
    * @return  the path of the project's root directory
    */
   public String getProjectPath();
   
   /**
    * Returns the name of the directory where executable files are
    * saved
    * @return  the name of the directory where executable files are
    * saved
    */
   public String getExecutableDirName();
   
   /**
    * Saves the current configuration to preferences
    */
   public void storeInPrefs();
}
