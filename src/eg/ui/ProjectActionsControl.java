package eg.ui;

/**
 * The interaface for the control of UI items used for the project
 * actions run, compile and build 
 */
public interface ProjectActionsControl {
   
   /**
    * Enables or disables separately to compile, run and build a
    * project. The specified booleans each are true to enable, false
    * to disable.
    *
    * @param isCompile  the boolean for compile actions
    * @param isRun  the boolean for run actions
    * @param isBuild  the boolean for build actions
    * @param buildLabel  the label for the menu item for build actions;
    * may be null if isBuild is false
    */
   public void enable(boolean isCompile, boolean isRun,
         boolean isBuild, String buildLabel);
   
   /**
    * Disables all project actions (compile, run and build)
    */
   public void disable();

   /**
    * Returns if the console is currently open
    *
    * @return  true if open, false otherwise
    */
   public boolean isConsoleOpen();

   /**
    * Opens the console
    */
   public void openConsole();
}
