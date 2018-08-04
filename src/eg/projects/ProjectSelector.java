package eg.projects;

import eg.console.*;
import eg.ui.ConsoleOpenable;

/**
 * The selection and creation of a <code>ProjectActions</code>
 * based on the project type
 */
public class ProjectSelector {

   private final ConsoleOpenable co;
   private final ProcessStarter proc;
   private final ConsolePanel console;

   /**
    * @param co  the {@link ConsoleOpenable} of {@link eg.ui.MainWin}
    * @param proc  the reference to {@link ProcessStarter}
    * @param console  the {@link ConsolePanel} that is also shared by
    * <code>ProcessStarter</code>
    */
   public ProjectSelector(ConsoleOpenable co, ProcessStarter proc,
         ConsolePanel console) {

      this.co = co;
      this.proc = proc;
      this.console = console;
   }

   /**
    * Returns a new <code>ProjectActions</code>
    *
    * @param projType  the project type which has a valaue in {@link ProjectTypes}
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
      return newProj;
   }
}
