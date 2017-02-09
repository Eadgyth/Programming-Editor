package eg.projects;

import eg.console.*;
import eg.Languages;
import eg.DisplaySetter;
import eg.ui.filetree.FileTree;

/**
 * The selection and creation of an object of type {@code ProjectActions}
 */
public class SelectedProject {

   private final DisplaySetter displSet;
   private final ProcessStarter proc;
   private final ConsolePanel consPnl;
   private final FileTree fileTree;

   public SelectedProject(DisplaySetter displSet, ProcessStarter proc,
         ConsolePanel consPnl, FileTree fileTree) {
      this.displSet = displSet;
      this.proc = proc;
      this.consPnl = consPnl;
      this.fileTree = fileTree;
   }

   /**
    * Returns an object of type {@code ProjectActions} and creates the
    * {@code SettingsWin} for the project.
    * <p>
    * The first criterion to select a {@link ProjectActions} is the file
    * extension. Only if no corresponding class is found the language
    * is used.
    * <p>
    * @param fileExt  the extension of the file which a project is to be
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
            newProj = new JavaActions(displSet, proc, consPnl, fileTree);
            break;
         case "html":
            newProj = new HtmlActions();
            break;
         case "pl": case "pm":
            newProj = new PerlActions(displSet, proc);
            break;
      }
      if (newProj == null) {
         switch (lang) {
            case JAVA:
               newProj = new JavaActions(displSet, proc, consPnl, fileTree);
               break;
            case HTML:
               newProj = new HtmlActions();
               break;
            case PERL:
               newProj = new PerlActions(displSet, proc);
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
    * Enables action components in the menu and toolbar depending onn type of
    * project
    * @param className  the name of the class of type {@link ProjectActions}
    * @param projCount  the number of already configured projects
    */
   public void enableActions(String className, int projCount) {
      switch (className) {
         case "JavaActions":
            displSet.enableProjActions(true, true, true, projCount);
            displSet.setBuildMenuItmText("Create jar");
            break;
         case "HtmlActions":
            displSet.enableProjActions(false, true, false, projCount);
            break;
         case "PerlActions":
            displSet.enableProjActions(false, true, false, projCount);
            break;
         case "TxtActions":
            displSet.enableProjActions(false, false, false, projCount);
            break;        
      }
   }         
}
