package eg.projects;

import eg.ui.ProjectActionsUpdate;

/**
 * The interface that defines the actions to compile, run and build
 * a project
 */
public interface ProjectActions extends Configurable {

   /**
    * Enables and disables actions
    *
    * @param update  the ProjectActionsUpdate
    */
   public void enableActions(ProjectActionsUpdate update);

   /**
    * Compiles source files
    */
   public default void compile() {
      throw new UnsupportedOperationException(
            "Compiling source files is not supported");
   };

   /**
    * Runs the project
    */
   public default void run() {
      throw new UnsupportedOperationException(
            "Running the project is not supported");
   };

   /**
    * Runs the project using the specified file
    *
    * @param  filepath  the full filepath
    */
   public default void run(String filepath) {
      throw new UnsupportedOperationException(
            "Running the project is not supported");
   };

   /**
    * Creates a build of the project
    */
   public default void build() {
      throw new UnsupportedOperationException(
            "Creating a build is not supported");
   };
}
