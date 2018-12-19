package eg.projects;

/**
 * The interface that defines the actions to compile, run and build
 * a project
 */
public interface ProjectActions extends Configurable {

   /**
    * Enables and disables actions
    */
   public void enableActions();

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
   public default void runProject() {
      throw new UnsupportedOperationException(
            "Running the project is not supported");
   };

   /**
    * Runs the project using the specified file
    *
    * @param  filepath  the full filepath
    */
   public default void runProject(String filepath) {
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
