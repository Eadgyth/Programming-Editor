package eg.projects;

import eg.utils.JOptions;

/**
 * The interface to perform actions in a project
 */
public interface ProjectActions extends Configurable {
   
   /**
    * Compiles source files
    * @throws UnsupportedOperationException  if compiling sources
    * is not implemented
    */
   public default void compile() {
      throw new UnsupportedOperationException(
            "Compiling source files is not supported");
   };
   
   /**
    * Runs a project
    * @throws UnsupportedOperationException  if running the project
    * is not implemented
    */
   public default void runProject() {
      throw new UnsupportedOperationException(
            "Running the project is not supported");
   };
   
   /**
    * Creates a build of a project, where it is not specified what
    * 'build' is
    * @throws UnsupportedOperationException  if creating a build
    * is not implemented
    */
   public default void build() {
      throw new UnsupportedOperationException(
            "Creating a build is not supported");
   };
}
