package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface that defines the configuration of a project.
 * <p>
 * <code>Configurable</code> is made to be used with {@link ProjectConfig}. This
 * works with {@link SettingsWin} which defines a settings window for a project.
 */
public interface Configurable {
   
   /**
    * Creates and sets a new object that defines a settings window.
    */
   public void createSettingsWin();
   
   /**
    * Adds an <code>ActionListener</code> to the ok button of this settings
    * window
    *
    * @param al  the ActionListener
    */
   public void addOkAction(ActionListener al);
   
   /**
    * Makes this settings window visible/invisible
    *
    * @param isVisible  true to make the window visible, false to make
    * it invisible
    */
   public void makeSetWinVisible(boolean isVisible);
   
   /**
    * If a project can be successfully configured based on the entries in
    * the settings window.
    *
    * @param dir  the directory that may equal or maybe in the project's
    * root directory
    * @return  if a project can be successfully configured
    */
   public boolean configureProject(String dir);
   
   /**
    * If a project stored in a preferences file can be retrieved.
    *
    * @param dir  the directory that may equal or maybe in the project's
    * root directory
    * @return  if a saved project could be retrieved
    */
   public boolean retrieveProject(String dir);
   
   /**
    * If the specified directory equals or is in the project's root
    * directory
    *
    * @param dir  the directory that may equal or maybe in the project's
    * root directory
    * @return  if the specified directory equals or is in the project's root
    * directory
    */
   public boolean isInProject(String dir);
   
   /**
    * Returns the project's root directory
    *
    * @return  the project's root directory
    */
   public String getProjectPath();
   
   /**
    * Returns the name of the project's root directory
    *
    * @return  the name of the project's root directory
    */
   public String getProjectName();
   
   /**
    * Returns the name of the directory where executable files are saved
    *
    * @return  the name of the directory where executable files are saved
    */
   public String getExecutableDirName();
   
   /**
    * Returns the suffix of source files
    *
    * @return  the suffix of source files (has the form '.java', for
    * example
    */
   public String getSourceSuffix();
   
   /**
    * Saves the current configuration to a preferences file
    */
   public void storeInPrefs();
}
