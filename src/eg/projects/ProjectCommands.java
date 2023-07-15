package eg.projects;

import eg.Projects.ProjectActionsUpdate;

/**
 * The interface that defines the commands to compile, run and build
 * a project
 */
public interface ProjectCommands extends Configurable {

   /**
    * Enables the controls in the UI that invoke the commands used by
    * the project
    *
    * @param update  the ProjectActionsUpdate
    */
   public void enable(ProjectActionsUpdate update);

   /**
    * Compiles source files
    */
   public default void compile() {
      throw new UnsupportedOperationException(
            "Compiling source files is not supported");
   }

   /**
    * Runs the project
    */
   public default void run() {
      throw new UnsupportedOperationException(
            "Running the project is not supported");
   }

   /**
    * Runs the project using the specified file
    *
    * @param  filepath  the full filepath
    */
   public default void run(String filepath) {
      throw new UnsupportedOperationException(
            "Running the project is not supported");
   }

   /**
    * Creates a build of the project.
    * It is not defined what a "build" is
    */
   public default void build() {
      throw new UnsupportedOperationException(
            "Creating a build is not supported");
   }
}
