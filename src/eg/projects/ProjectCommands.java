package eg.projects;

import eg.ui.ProjectActionsUpdate;

/**
 * The interface that defines the commands to compile, run and build
 * a project. The commands are optional but throw an exception if
 * called although an overriding method is not present. The actions
 * that call the commands are therefore enabled or disabled by
 * {@link #enable}.
 */
public interface ProjectCommands extends Configurable {

   /**
    * Updates controls that invoke commands
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
   };

   /**
    * Runs the project using a main file defined by the project
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
