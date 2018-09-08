package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface that defines the configuration of a project.
 * <p>
 * The implementing class works in combination with an object that defines a
 * a window where settings for a project are entered.
 */
public interface Configurable {

   /**
    * Builds the settings window depending on the input required for a
    * project
    */
   public void buildSettingsWindow();

   /**
    * Makes this settings window visible
    */
   public void makeSettingsWindowVisible();

   /**
    * Sets the <code>ActionListener</code> that is called when
    * the entries in the settings window are applied
    *
    * @param al  <code>the ActionListener</code>
    */
   public void setConfiguringAction(ActionListener al);

   /**
    * Configures a project based on the entries in this settings window
    * and the specified directory
    *
    * @param dir  the directory that may be or be contained in the
    * presumed root directory of the project
    * @return  the boolean value that is true if the project could be
    * configured
    */
   public boolean configureProject(String dir);

   /**
    * Retrieves a project stored in a preferences file based on the
    * specified directory
    *
    * @param dir  the directory that may be or be contained in the
    * presumed root directory of the project
    * @return  the boolean value that is true if a saved project could
    * be retrieved
    */
   public boolean retrieveProject(String dir);

   /**
    * Returns the type of project
    *
    * @return  the type of project which is a constant in {@link ProjectTypes}
    */
   public ProjectTypes getProjectType();

   /**
    * Returns if the project uses a main file
    *
    * @return  the boolean value. True if a main file is used.
    */
   public boolean usesProjectFile();

   /**
    * Returns if the specified directory belongs to the project
    *
    * @param dir  the directory
    * @return  the boolean value that is true if the directory is or is
    * contained in the project's root directory
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
    * Stores the configuration in a properties file
    */
   public void storeConfiguration();
}
