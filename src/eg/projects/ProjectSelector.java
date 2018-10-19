package eg.projects;

import eg.console.*;
import eg.ui.ConsoleOpenable;

/**
 * The selection and creation of a <code>ProjectActions</code>
 * based on the project type
 */
public class ProjectSelector {

   private final ConsoleOpenable co;
   private final Console console;

   /**
    * @param co  the reference to {@link ConsoleOpenable}
    * @param console  the reference to {@link Console}.
    */
   public ProjectSelector(ConsoleOpenable co, Console console) {
      this.co = co;
      this.console = console;
   }

   /**
    * Returns a new <code>ProjectActions</code>
    *
    * @param projType  the project type which has a valaue in
    * {@link ProjectTypes}
    * @return  a new {@link ProjectActions}
    */
   public ProjectActions createProject(ProjectTypes projType) {
      ProjectActions newProj = null;
      switch (projType) {
         case JAVA:
            newProj = new JavaProject(co, console);
            break;
         case HTML:
            newProj = new HtmlProject();
            break;
         case PERL:
            newProj = new PerlProject(co, console.processStarter());
            break;
         case R:
            newProj = new RProject(co, console.processStarter());
            break;
         case GENERIC:
            newProj = new GenericProject();
            break;
      }
      return newProj;
   }
}
