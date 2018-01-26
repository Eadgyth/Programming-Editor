package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface that defines a configured project.
 * <p>
 * "Configuration" initially means that files can be associated with
 * a project. It than means to set variables needed to perform actions
 * that are defined in {@link ProjectActions}. The minimally required
 * variable is the root directory of a project. Depending on the object
 * that represents a project variables that specify a main project
 * file and the purpose of sub-directories of the project root may be
 * needed. Parameters required to configure a project are entered in
 * a settings window.
 * <p>
 * A "successful" configuration depends on whether the project root can
 * be found but may additionally depend on the existence of a particular
 * file in a given directory structure.
 */
public interface Configurable {
   
   /**
    * Creates an object the defines a settings window
    */
   public void createSettingsWin();
   
   /**
    * Sets the <code>ActionListener</code> that is called when
    * the entries in the settings window are applied
    *
    * @param al  <code>the ActionListener</code>
    */
   public void setConfiguringAction(ActionListener al);
   
   /**
    * Makes this settings window visible
    */
   public void makeSetWinVisible();
   
   /**
    * Returns if a project can be successfully configured based on the
    * entries in this settings window and the specified directory.
    *
    * @param dir  the directory that may be or be contained in the
    * presumed root directory of the project
    * @return  the boolean value that is true if the project could be
    * configured
    */
   public boolean configureProject(String dir);
   
   /**
    * Returns if a project stored in a preferences file can be retrieved
    * based on the specified directory.
    *
    * @param dir  the directory that may be or be contained in the
    * presumed root directory of the project
    * @return  the boolean value that is true if a saved project could
    * be retrieved
    */
   public boolean retrieveProject(String dir);
   
   /**
    * Returns if the project uses a main project file.
    *
    * @return  the boolean value. True if a project file is used.
    */
   public boolean usesProjectFile();
   
   /**
    * Returns if the specified directory belongs to the project
    *
    * @param dir  the directory
    * @return  the boolean value that id true if the directory is or is
    * contained in the project root
    */
   public boolean isInProject(String dir);
   
   /**
    * Returns the path of the project's root directory
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
    * Returns the extension of source files used in the project
    *
    * @return  the file extension
    */
   public String getSourceFileExtension();
   
   /**
    * Stores the configuration in a properties file
    */
   public void storeConfiguration();
}
