package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface to configure a project
 */
public interface Configurable {
   
   /**
    * Creates an object the defines a setttings window
    */
   public void createSettingsWin();
   
   /**
    * Sets the <code>ActionListener</code> that is called when
    * the entries in the settings window are applied
    *
    * @param al  the ActionListener
    */
   public void setConfiguringAction(ActionListener al);
   
   /**
    * Makes this settings window visible
    */
   public void makeSetWinVisible();
   
   /**
    * Returns if a project can be successfully configured based on the
    * entries in this settings window.
    *
    * @param dir  the directory that may equal or may be in the
    * project's root directory
    * @return  if a project could be configured
    */
   public boolean configureProject(String dir);
   
   /**
    * Returns if a project stored in a preferences file can be retrieved
    *
    * @param dir  the directory that may equal or may be in the
    * project's root directory
    * @return  if a saved project could be retrieved
    */
   public boolean retrieveProject(String dir);
   
   /**
    * Returns the boolean that indicates if the project uses a (main)
    * project file. False means that only the project's root directory
    * is specified
    *
    * @return  the boolean value
    */
   public boolean usesProjectFile();
   
   /**
    * If the specified directory equals or is in the project's root
    * directory
    *
    * @param dir  the directory
    * @return  if the specified directory belongs to the project
    */
   public boolean isInProject(String dir);
   
   /**
    * Returns the project's root directory
    *
    * @return  the directory
    */
   public String getProjectPath();
   
   /**
    * Returns the name of the project's root directory
    *
    * @return  the name
    */
   public String getProjectName();
   
   /**
    * Returns the name of the directory where executable files
    * are saved
    *
    * @return  the name
    */
   public String getExecutableDirName();
   
   /**
    * Returns the extension of source files for the project
    *
    * @return  the file extension
    */
   public String getSourceFileExtension();
   
   /**
    * Stores the configuration in a preferences file
    */
   public void storeConfiguration();
}
