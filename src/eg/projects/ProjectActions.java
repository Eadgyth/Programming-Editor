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
      JOptions.warnMessage("This operation is not supported");
   };
   
   /**
    * Runs a project
    */
   public default void runProject() {
      JOptions.warnMessage("This operation is not supported");
   };
   
   /**
    * Runs a project using the specified filename
    * @param filename  the name of a file of the project
    */
   public default void runProject(String filename) {
      JOptions.warnMessage("This operation is not supported");
   };
   
   /**
    * Creates a build of a project, where it is not specified
    * what 'build' is
    */
   public default void build() {
      JOptions.warnMessage("This operation is not supported");
   };
   
   /**
    * Overridden to signify that {@link#runProject(String)} is called to run
    * the project.
    * @return if {@link#runProject(String)} is called to run the project.
    * Default is false.
    */
   public default boolean isRunByFile() {
      return false;
   }
}
