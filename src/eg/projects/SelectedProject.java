package eg.projects;

import eg.console.*;
import eg.Languages;
import eg.ProjectUpdate;
import eg.ui.filetree.FileTree;

/**
 * The selection and creation of an object of type {@code ProjectActions}
 */
public class SelectedProject {

   private final ProjectUpdate update;
   private final ProcessStarter proc;
   private final ConsolePanel consPnl;

   public SelectedProject(ProjectUpdate update, ProcessStarter proc,
         ConsolePanel consPnl) {
      this.update = update;
      this.proc = proc;
      this.consPnl = consPnl;
   }

   /**
    * Returns an object of type {@code ProjectActions} and creates the
    * {@code SettingsWin} for the project.
    * <p>
    * The first criterion to select a {@link ProjectActions} is the file
    * extension. Only if no corresponding class is found the language
    * is used.
    * <p>
    * @param fileExt  the file extension which a project is to be
    * defined for
    * @param lang  the language which has a value from {@link Languages}
    * @return  an object of type {@link ProjectActions} or null if no
    * class exists that implements ProjectActions for the given file
    * extension.
    */
   public ProjectActions createProject(String fileExt, Languages lang) {
      ProjectActions newProj = null;
      switch (fileExt) {
         case "java":
            newProj = new JavaActions(update, proc, consPnl);
            break;
         case "html": case "htm":
            newProj = new HtmlActions(fileExt);
            break;
         case "pl": case "pm":
            newProj = new PerlActions(update, proc);
            break;
      }
      if (newProj == null) {
         switch (lang) {
            case JAVA:
               newProj = new JavaActions(update, proc, consPnl);
               break;
            case HTML:
               newProj = new HtmlActions("html");
               break;
            case PERL:
               newProj = new PerlActions(update, proc);
               break;
            case PLAIN_TEXT:
               if ("txt".equals(fileExt)) {
                  newProj = new TxtActions();
               }
               break;
         }
      }
      if (newProj != null) {
         newProj.createSettingsWin();
      }
      return newProj;
   }
   
   /**
    * Enables action components in the menu and toolbar depending on the
    * type of project
    * @param className  the name of the class of type {@link ProjectActions}
    * @param projCount  the number of already configured projects
    */
   public void enableActions(String className, int projCount) {
      update.setBuildLabel("Build");
      switch (className) {
         case "JavaActions":
            update.enableProjActions(true, true, true, projCount);
            update.setBuildLabel("Create jar");
            break;
         case "HtmlActions":
            update.enableProjActions(false, true, false, projCount);
            break;
         case "PerlActions":
            update.enableProjActions(false, true, false, projCount);
            break;
         case "TxtActions":
            update.enableProjActions(false, false, false, projCount);
            break;
      }
   }         
}
