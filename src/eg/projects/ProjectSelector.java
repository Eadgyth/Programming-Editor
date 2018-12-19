package eg.projects;

import eg.console.Console;
import eg.ui.ProjectActionsControl;

/**
 * The selection and creation of a <code>ProjectActions</code>
 * based on the project type
 */
public class ProjectSelector {

   private final ProjectActionsControl update;
   private final Console console;

   /**
    * @param update  the ProjectActionsControl
    * @param console  the Console
    */
   public ProjectSelector(ProjectActionsControl update, Console console) {
      this.update = update;
      this.console = console;
   }

   /**
    * Returns a new <code>ProjectActions</code>
    *
    * @param projType  the project type
    * @return  the ProjectActions
    */
   public ProjectActions createProject(ProjectTypes projType) {
      ProjectActions newProj = null;
      switch (projType) {
         case JAVA:
            newProj = new JavaProject(update, console);
            break;
         case HTML:
            newProj = new HtmlProject(update);
            break;
         case PERL:
            newProj = new PerlProject(update, console.processStarter());
            break;
         case R:
            newProj = new RProject(update, console.processStarter());
            break;
         case GENERIC:
            newProj = new GenericProject(update);
            break;
      }
      return newProj;
   }
}
