package eg.projects;

import eg.console.*;
import eg.Languages;
import eg.ProjectUIUpdate;
import eg.ui.MainWin;

/**
 * The selection and creation of an object of type {@code ProjectActions}
 */
public class SelectedProject {

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
    * @param fileExt  the file extension which a project is to be defined for
    * @return  an object of type {@link ProjectActions}
    */
   public ProjectActions createProjectByExt(String fileExt) {
      ProjectActions newProj = null;
      switch (fileExt) {
         case "java":
            newProj = new JavaActions(update, proc, console);
            break;
         case "html": case "htm":
            newProj = new HtmlActions(fileExt);
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
   
   /**
    * Returns a {@code ProjectActions} based on the specified language and
    * creates the {@code SettingsWin} for the project.
    *
    * @param lang  the language which has a value from {@link Languages}
    * @return  an object of type {@link ProjectActions}
    */
   public ProjectActions createProjectByLang(Languages lang) {
      ProjectActions newProj = null;
      switch (lang) {
         case JAVA:
               newProj = new JavaActions(update, proc, console);
               break;
            case HTML:
               newProj = new HtmlActions("html");
               break;
            case PERL:
               newProj = new PerlActions(update, proc);
               break;
      }
      if (newProj != null) {
         newProj.createSettingsWin();
      }
      return newProj;
   }

   /**
    * Enables action components in the menu and toolbar depending on the
    * name of the class that implements {@code ProjectActions}
    *
    * @param className  the name of the class
    * @param mw  the {@link MainWin}
    */
   public void enableActions(String className, MainWin mw) {
      mw.menu().projectMenu().setBuildLabel("Build");
      switch (className) {
         case "JavaActions":
            enableProjActions(true, true, true, mw);
            mw.menu().projectMenu().setBuildLabel("Create jar");
            break;
         case "HtmlActions":
            enableProjActions(false, true, false, mw);
            break;
         case "PerlActions":
            enableProjActions(false, true, false, mw);
            break;
         case "TxtActions":
            enableProjActions(false, false, false, mw);
            break;
      }
   }
   
   private void enableProjActions(boolean isCompile, boolean isRun,
         boolean isBuild, MainWin mw) {

      mw.menu().projectMenu().enableProjItms(isCompile, isRun, isBuild);
      mw.toolbar().enableProjBts(isCompile, isRun);
   }
}
