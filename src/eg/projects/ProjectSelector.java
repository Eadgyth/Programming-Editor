package eg.projects;

import eg.console.*;
import eg.ui.ConsoleOpenable;

/**
 * The selection and creation of an object of type {@code ProjectActions}
 */
public class ProjectSelector {
   
   /**
    * The extension of files that can define a project category
    */
   public final static String[] PROJ_EXTENSIONS = {
      "htm", "html", "java", "pl", "R"
   };

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
    * of files which define the project caterory
    *
    * @param ext  the file extension
    * @return  a new {@link ProjectActions}. Null if the extension does
    * not identify a project category
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
         case "R":
            newProj = new RProject(co, proc);
      }
      if (newProj != null) {
         newProj.buildSettingsWindow();
      }
      return newProj;
   }
}
