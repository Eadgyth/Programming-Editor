package eg.projects;

import eg.console.*;
import eg.Languages;
import eg.ProjectUIUpdate;
import eg.ui.MainWin;
import eg.ui.filetree.FileTree;

/**
 * The selection and creation of an object of type {@code ProjectActions}
 */
public class SelectedProject {

   private final ProjectUIUpdate update;
   private final MainWin mw;
   private final ProcessStarter proc;
   private final ConsolePanel console;

   public SelectedProject(MainWin mw, ProcessStarter proc,
         ConsolePanel console) {

      this.mw = mw;
      this.proc = proc;
      this.console = console;
      update = new ProjectUIUpdate(mw);
   }

   /**
    * Returns a {@code ProjectActions} and creates the {@code SettingsWin}
    * for the project.
    * <p>The first criterion to select a {@link ProjectActions} is the file
    * extension. Only if no corresponding class is found the language is used.
    *
    * @param fileExt  the file extension which a project is to be defined for
    * @param lang  the language which has a value from {@link Languages}
    * @return  an object of type {@link ProjectActions}
    */
   public ProjectActions createProject(String fileExt, Languages lang) {
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
      if (newProj == null) {
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
    * name of the class that implements {@code ProjectActions}
    *
    * @param className  the name of the class
    */
   public void enableActions(String className) {
      mw.menu().projectMenu().setBuildLabel("Build");
      switch (className) {
         case "JavaActions":
            enableProjActions(true, true, true);
            mw.menu().projectMenu().setBuildLabel("Create jar");
            break;
         case "HtmlActions":
            enableProjActions(false, true, false);
            break;
         case "PerlActions":
            enableProjActions(false, true, false);
            break;
         case "TxtActions":
            enableProjActions(false, false, false);
            break;
      }
   }
   
   private void enableProjActions(boolean isCompile, boolean isRun,
         boolean isBuild) {

      mw.menu().projectMenu().enableProjItms(isCompile, isRun, isBuild);
      mw.toolbar().enableProjBts(isCompile, isRun);
   }
}
