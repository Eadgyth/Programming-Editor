package eg.projects;

/**
 * The interface to configure and run a project
 */
public interface ProjectActions extends Configurable {
   
   /**
    * Passes to interested objects the project's root directory or
    * any other directories
    */
   public void applyProject();
   
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
    * what this means
    */
   public void build();
}
