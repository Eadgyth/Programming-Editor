package eg.projects;

/**
 * The interface to perform actions in a project
 */
public interface ProjectActions extends Configurable {
   
   /**
    * Compiles source files
    */
   public default void compile() {
      throw new UnsupportedOperationException(
            "Compiling source files is not supported");
   };
   
   /**
    * Runs a project
    */
   public default void runProject() {
      throw new UnsupportedOperationException(
            "Running the project is not supported");
   };
   
   /**
    * Creates a build of a project, where it is not specified what
    * 'build' is
    */
   public default void build() {
      throw new UnsupportedOperationException(
            "Creating a build is not supported");
   };
}
