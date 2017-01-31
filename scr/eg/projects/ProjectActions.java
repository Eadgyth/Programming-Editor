package eg.projects;

/**
 * The interface to perform actions in a project
 */
public interface ProjectActions extends Configurable {
   
   /**
    * Compiles source files
    */
   public void compile();
   
   /**
    * Runs a project
    */
   public void runProject();
   
   /**
    * Creates a build of a project, where it is not specified
    * what 'build' refers to
    */
   public void build();
}
