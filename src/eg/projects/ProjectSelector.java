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
    * Returns a <code>ProjectActions</code> selected based on the extension
    * of files which a project is to be assigned to
    *
    * @param ext  the file extension
    * @return  a new {@link ProjectActions}. Null if the extension does
    * not specify a project
    */
   public ProjectActions createProject(String ext) {
      ProjectActions newProj = null;
      switch (ext) {
         case "java":
            newProj = new JavaProject(co, proc, console);
            break;
         case "html": case "htm":
            newProj = new HtmlProject(ext);
            break;
         case "pl": case "pm":
            newProj = new PerlProject(co, proc);
            break;
      }
      if (newProj != null) {
         newProj.createSettingsWin();
      }
      return newProj;
   }
}
