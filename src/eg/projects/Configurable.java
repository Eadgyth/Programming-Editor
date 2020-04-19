package eg.projects;

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
    * @param dir  the directory that may be identical to or
    * contained in the (presumed) project directory
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
    * Tries to retrieve a stored project
    *
    * @param dir  the directory that may be identical to or
    * contained in the directory of a stored project
    *
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
    * Returns if the project uses a (main) source file that is
    * specified in the settings window
    *
    * @return  true if a set file is used, false otherwise
    */
   public boolean hasSetSourceFile();

   /**
    * Returns if the specified directory is identical to or contained
    * in the project directory
    *
    * @param dir  the directory
    * @return  true if in the project, false otherwise
    */
   public boolean isInProject(String dir);

   /**
    * Returns the project directory
    *
    * @return  the directory
    */
   public String projectDir();

   /**
    * Returns the last name of the project directory
    *
    * @return  the name
    */
   public String projectName();

   /**
    * Returns the directory where executable files are saved.
    * This directory is deletable in the project explorer if
    * given as relative directory that is creatable in the
    * project directory. Initially, the directory does not
    * have to exist
    *
    * @return  the directory, the empty string if none is given
    */
   public String executableDir();

   /**
    * Stores the configuration of the project
    */
   public void storeConfiguration();
}
