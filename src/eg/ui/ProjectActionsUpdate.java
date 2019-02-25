package eg.ui;

/**
 * The interface to update the actions to run, compile and build a project
 */
@FunctionalInterface
public interface ProjectActionsUpdate {

   /**
    * Enables or disables separately to compile, run and build a
    * project and may set a label for the menu item for creating a
    * build of a project. The specified booleans each are true to
    * enable, false to disable the action.
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
