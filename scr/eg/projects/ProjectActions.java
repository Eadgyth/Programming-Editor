package eg.projects;

import java.lang.reflect.InvocationTargetException;

/**
 * The interface to configure and run a project
 */
public interface ProjectActions extends Configurable {
   
   /**
    * Compiles source filel
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