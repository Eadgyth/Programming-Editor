package eg.projects;

import java.lang.reflect.InvocationTargetException;

/**
 * The interface to configure and run a project
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
    * what this means
    */
   public void build();
}