package eg.projects;

import eg.TaskRunner;

/**
 * The selection and creation of a <code>ProjectCommands</code>
 * based on the project type
 */
public class ProjectSelector {
   
   private final TaskRunner runner;

   /**
    * @param runner  the reference to Task
    */
   public ProjectSelector(TaskRunner runner) {
      this.runner = runner;
   }

   /**
    * Returns a new <code>ProjectCommands</code>
    *
    * @param projType  the project type
    * @return  the ProjectActions
    */
   public ProjectCommands createProject(ProjectTypes projType) {
      ProjectCommands newProj = null;
      switch (projType) {
         case JAVA:
            newProj = new JavaProject(runner);
            break;
         case HTML:
            newProj = new HtmlProject();
            break;
         case PERL:
            newProj = new PerlProject(runner);
            break;
         case PYTHON:
            newProj = new PythonProject(runner);
            break;
         case R:
            newProj = new RProject(runner);
            break;
         case GENERIC:
            newProj = new GenericProject(runner);
            break;
      }
      return newProj;
   }
}
