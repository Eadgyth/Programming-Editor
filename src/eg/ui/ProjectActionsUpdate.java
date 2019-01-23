package eg.ui;

/**
 * The interaface enable actions to run, compile and build a project
 */
@FunctionalInterface
public interface ProjectActionsUpdate {

   /**
    * Enables or disables separately to compile, run and build a
    * project. The specified booleans each are true to enable, false
    * to disable.
    *
    * @param isCompile  the boolean for compile actions
    * @param isRun  the boolean for run actions
    * @param isBuild  the boolean for build actions
    * @param buildLabel  the label for the menu item for build actions;
    * may be null
    */
   public void enable(boolean isCompile, boolean isRun,
         boolean isBuild, String buildLabel);
}
