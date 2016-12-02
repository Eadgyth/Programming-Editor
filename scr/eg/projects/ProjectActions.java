package eg.projects;

/**
 * The interface to configure and run a project
 */
public interface ProjectActions extends Configurable {
   
   /**
    * Tries to compile source files
    */
   public void compile();
   
   /**
    * Runs a project
    */
   public void runProject();
   
   /**
    * Creates a build of a project
    */
   public void build();
}