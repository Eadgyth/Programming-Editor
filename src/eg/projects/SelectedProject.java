package eg.projects;

import eg.console.*;
import eg.Languages;
import eg.ProjectUIUpdate;
import eg.ui.MainWin;

/**
 * The selection and creation of an object of type {@code ProjectActions}
 */
public class SelectedProject {
   
   /**
    * The file extensions that can identify a project category
    */
   public String[] projectSuffixes = {
      "htm", "html", "java", "pl"
   };

   private final ProjectUIUpdate update;
   private final ProcessStarter proc;
   private final ConsolePanel console;

   /**
    * Creates a <code>SelectedProject</code> and sets the references
    * that may be used by <code>ProjectActions</code>
    *
    * @param update  the {@link ProjectUIUpdate}
    * @param proc  the {@link ProcessStarter}
    * @param console  the {@link ConsolePanel} tht is also shared
    * by <code>ProcessStarter</code>
    */
   public SelectedProject(ProjectUIUpdate update,
         ProcessStarter proc, ConsolePanel console) {

      this.update = update;
      this.proc = proc;
      this.console = console;
   }

   /**
    * Returns a {@code ProjectActions} based on the file extension and
    * creates the {@code SettingsWin} for the project.
    *
    * @param suffix  the file extension which a project is to be defined for
    * @return  an object of type {@link ProjectActions}
    */
   public ProjectActions createProject(String suffix) {
      ProjectActions newProj = null;
      switch (suffix) {
         case "java":
            newProj = new JavaActions(update, proc, console);
            break;
         case "html": case "htm":
            newProj = new HtmlActions(suffix);
            break;
         case "pl": case "pm":
            newProj = new PerlActions(update, proc);
            break;
      }
      if (newProj != null) {
         newProj.createSettingsWin();
      }
      return newProj;
   }
}
