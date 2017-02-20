package eg.projects;

import eg.utils.JOptions;

/**
 * The interface to perform actions in a project. By default the methods
 * show in a message window that the operation is not supported
 */
public interface ProjectActions extends Configurable {
   
   /**
    * Compiles source files
    */
   public default void compile() {
       message();
   };
   
   /**
    * Runs a project
    */
   public default void runProject() {
       message();
   };
   
   /**
    * Creates a build of a project, where it is not specified
    * what 'build' is
    */
   public default void build() {
       message();
   };
   
   static void message() {
      JOptions.warnMessage("This operation is not supprted");
   }
}
