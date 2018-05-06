package eg.projects;

import eg.console.*;
import eg.ui.ConsoleOpenable;

/**
 * The selection and creation of an object of type {@code ProjectActions}
 */
public class ProjectSelector {

   private final ConsoleOpenable co;
   private final ProcessStarter proc;
   private final ConsolePanel console;

   /**
    * Sets the references which (or part of which) may be used by
    * a selected <code>ProjectActions</code>
    *
    * @param co  the {@link ConsoleOpenable}
    * @param proc  the {@link ProcessStarter}
    * @param console  the {@link ConsolePanel} that is also shared
    * by <code>ProcessStarter</code>
    */
   public ProjectSelector(ConsoleOpenable co, ProcessStarter proc,
         ConsolePanel console) {

      this.co = co;
      this.proc = proc;
      this.console = console;
   }

   /**
    * Returns a <code>ProjectActions</code> selected based on a project type
    * in <code>ProjectTypes</code>
    *
    * @param projType  the project type which has a valaue in
    * {@link ProjectTypes}
    * @return  a new {@link ProjectActions}
    */
   public ProjectActions createProject(ProjectTypes projType) {
      ProjectActions newProj = null;
      switch (projType) {
         case JAVA:
            newProj = new JavaProject(co, proc, console);
            break;
         case HTML:
            newProj = new HtmlProject();
            break;
         case PERL:
            newProj = new PerlProject(co, proc);
            break;
         case R:
            newProj = new RProject(co, proc);
            break;
         case GENERIC:
            newProj = new GenericProject();
            break;
      }
      if (newProj != null) {
         newProj.buildSettingsWindow();
      }
      return newProj;
   }
}
