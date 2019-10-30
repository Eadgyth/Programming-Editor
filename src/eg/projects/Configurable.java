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
    * Builds the content of the settings window depending on
    * the input options required by a project
    */
   public void buildSettingsWindow();

   /**
    * Opens the settings window
    *
    * @param dir  the directory that maybe or be contained in the
    * project directory
    */
   public void openSettingsWindow(String dir);

   /**
    * Sets the <code>Runnable</code> that is called when the
    * entries in the settings window are confirmed
    *
    * @param r  the Runnable invoked by the action
    */
   public void setConfiguringAction(Runnable r);

   /**
    * Configures a project based on the entries in the settings
    * window
    *
    * @return  true if the project could be configured
    */
   public boolean configure();

   /**
    * Tries to retrieve a project stored in a preferences file
    *
    * @param dir  the directory that may be or be contained in a
    * stored project
    * @return  true if a stored project could be retrieved, false
    * otherwise
    */
   public boolean retrieve(String dir);

   /**
    * Returns the type of the project
    *
    * @return  the type of project
    */
   public ProjectTypes projectType();

   /**
    * Returns if the project uses a main source file that is executed
    * when the project is run
    *
    * @return  true if a main file is used, false otherwise
    */
   public boolean usesMainFile();

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
