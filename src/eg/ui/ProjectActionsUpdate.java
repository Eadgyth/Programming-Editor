package eg.ui;

/**
 * Interface to enable separately actions to compile, run and build a project
 */
public interface ProjectActionsUpdate {

   /**
    * Enables actions to compile a project
    */
   public void enableCompile();

   /**
    * Enables actions to run a project
    *
    * @param save  true to save project files before running,
    * false otherwise
    */
   public void enableRun(boolean save);

   /**
    * Enables actionds to build a project
    *
    * @param label  the label for the action that indicates the
    * kind of build
    */
   public void enableBuild(String label);
}
