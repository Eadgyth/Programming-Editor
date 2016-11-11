package eg.projects;

/**
 * The methods required to configure and run a programming project 
 * in the Editor
 */
public interface ProjectActions extends Configurable {
   
   /**
    * Sets a new object of {@code ProjectConfig} which is
    * of type {@code Configurable}
    * @param projConf  a newly created  {@link ProjectConfig}
    * which must implement {@link Configurable}
    */
   public void setProjectConfig(ProjectConfig projConf);
   
   /**
    * compiles source files
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