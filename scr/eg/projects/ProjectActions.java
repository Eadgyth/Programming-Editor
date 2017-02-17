package eg.projects;

/**
 * The interface to perform actions in a project
 */
public interface ProjectActions extends Configurable {
   
   /**
    * Compiles source files
    */
   public default void compile() {
       throw new UnsupportedOperationException("Method not used");
   };
   
   /**
    * Runs a project
    */
   public default void runProject() {
       throw new UnsupportedOperationException("Method not used");
   };
   
   /**
    * Creates a build of a project, where it is not specified
    * what 'build' is
    */
   public default void build() {
       throw new UnsupportedOperationException("Method not used");
   };
}
