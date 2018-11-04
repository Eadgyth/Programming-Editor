package eg.ui;

/**
 * Interface to update <code>MainWin</code> depending on the assigned
 * projects and the currently active project
 */
public interface ProjectStateUpdate {

   /**
    * Enables or disables to compile, run and build a project. The
    * specified booleans each are true to enable, false to disable
    *
    * @param isCompile  the boolean for compile actions
    * @param isRun  the boolean for run actions
    * @param isBuild  the boolean for build actions
    */
   public void enableProjectActions(boolean isCompile, boolean isRun,
         boolean isBuild);
         
   public void enableAssignProject(boolean b);

  /**
    * Sets a label text in the menu item for build actions
    *
    * @param label  the label
    */
   public void setBuildLabel(String label);
   
   /**
    * Enables or disables to open the project settings
    * window
    * 
    * @param b  true to enable, false to disable
    */
   public void enableOpenSettingsWin(boolean b);

   /**
    * Enables or disables to change project
    *
    * @param b  true to enable, false to disable
    */
   public void enableChangeProject(boolean b);

   /**
    * Displays the project name and type in the status bar
    *
    * @param projName  the name of the project
    * @param projType  the type of project
    */
   public void displayProjectName(String projName, String projType);
}
