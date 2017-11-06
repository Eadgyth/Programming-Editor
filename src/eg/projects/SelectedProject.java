package eg.projects;

import eg.console.*;
import eg.ui.ConsoleOpenable;

/**
 * The selection and creation of an object of type {@code ProjectActions}
 */
public class SelectedProject {

   private final ConsoleOpenable co;
   private final ProcessStarter proc;
   private final ConsolePanel console;

   /**
    * Creates a <code>SelectedProject</code> and sets the references
    * that may be used by <code>ProjectActions</code>
    *
    * @param co  the {@link ConsoleOpenable}
    * @param proc  the {@link ProcessStarter}
    * @param console  the {@link ConsolePanel} tht is also shared
    * by <code>ProcessStarter</code>
    */
   public SelectedProject(ConsoleOpenable co,
         ProcessStarter proc, ConsolePanel console) {

      this.co = co;
      this.proc = proc;
      this.console = console;
   }

   /**
    * Returns a <code>ProjectActions</code> selected based on the file
    * extension
    *
    * @param suffix  the file extension
    * @return  a new {@link ProjectActions}. Null if <code>suffix</code>
    * does not specify a project
    */
   public ProjectActions createProject(String suffix) {
      ProjectActions newProj = null;
      switch (suffix) {
         case "java":
            newProj = new JavaActions(co, proc, console);
            break;
         case "html": case "htm":
            newProj = new HtmlActions(suffix);
            break;
         case "pl": case "pm":
            newProj = new PerlActions(co, proc);
            break;
      }
      if (newProj != null) {
         newProj.createSettingsWin();
      }
      return newProj;
   }
}
