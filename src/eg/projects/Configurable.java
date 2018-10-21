package eg.projects;

import java.awt.event.ActionListener;

/**
 * The interface that defines the configuration of a project.
 * <p>
 * The configuration depends on a class that defines a window
 * where the settings for a project are entered.
 */
public interface Configurable {

   /**
    * Builds the settings window depending on the input options
    * required in a project
    */
   public void buildSettingsWindow();

   /**
    * Opens the settings window
    */
   public void openSettingsWindow();

   /**
    * Sets the <code>ActionListener</code> that is called when the
    * entries in the settings window are confirmed. The listener
    * calls {@link #configure} and assigns/updates the project if the
    * configuration is successful
    *
    * @param al  <code>the ActionListener</code>
    */
   public void setConfiguringAction(ActionListener al);

   /**
    * Configures a project based on the entries in the settings window
    *
    * @param dir  the directory that may be or be contained in the
    * project directory named in the settings window
    * @return  true in the case of a successful configuration
    */
   public boolean configure(String dir);

   /**
    * Tries to retrieve a project stored in a preferences file
    *
    * @param dir  the directory that may be or be contained in a stored
    * project
    * @return  true if a stored project could be retrieved
    */
   public boolean retrieve(String dir);

   /**
    * Returns the type of the project
    *
    * @return  the type of project which is a constant in
    * {@link ProjectTypes}
    */
   public ProjectTypes projectType();

   /**
    * Returns if the project uses a main file that is executed when
    * the project is run
    *
    * @return  true if a main file is used, false otherwise
    */
   public boolean usesProjectFile();

   /**
    * Returns if the specified directory is or is contained in the
    * project root directory
    *
    * @param dir  the directory
    * @return  true if the directory is in the project, false otherwise
    */
   public boolean isInProject(String dir);

   /**
    * Returns the path of the project directory
    *
    * @return  the directory
    */
   public String projectPath();

   /**
    * Returns the last name of the project directory
    *
    * @return  the name
    */
   public String projectName();

   /**
    * Returns the last name of the directory where executable files are
    * saved. This directory, if given, is deletable in the project
    * explorer.
    *
    * @return  the name, the empty string if no such directory is given
    */
   public String executableDirName();

   /**
    * Stores the configuration in a preferences file
    */
   public void storeConfiguration();
}
