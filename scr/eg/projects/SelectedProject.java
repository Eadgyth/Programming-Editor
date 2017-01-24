package eg.projects;

import eg.Constants;
import eg.console.*;
import eg.Preferences;
import eg.DisplaySetter;
import eg.ui.filetree.FileTree;

/**
 * The creation of an object of type {@code ProjectActions}
 */
public class SelectedProject {

   private final Preferences prefs = new Preferences();
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
    * Returns an object of type {@code ProjectActions}
    * @param fileExt  the extension of the file which a project
    * is to be defined for (has the form ".java", for example)
    * @param isSearchByLang  true to create a {@link ProjectActions}
    * based on the currently set language. True has still no effect if
    * {@code fileExt} specifies the type of {@code ProjectActions}
    * @return  an object of type {@link ProjectActions} or null if no
    * class exists that implements ProjectActions for the given file
    * extension.
    */
   public ProjectActions createProject(String fileExt, boolean isSearchByLang) {
      ProjectActions newProj = null;
      switch (fileExt) {
         case Constants.JAVA_EXT:
            newProj = new JavaActions(displSet, proc, consPnl, fileTree);
            break;
         case Constants.HTML_EXT:
            newProj = new HtmlActions(proc, fileTree);
            break;
         case Constants.PERL_PL_EXT: case Constants.PERL_PM_EXT:
            newProj = new PerlActions(displSet, proc, consPnl, fileTree);
            break;
         default:
            if (isSearchByLang) {
               newProj = createProjByLang();
            }                 
      }    
      return newProj;
   }
   
   private ProjectActions createProjByLang() {
      ProjectActions newProj = null;
      prefs.readPrefs();
      String language = prefs.getProperty("language");
      switch (language) {
         case "JAVA":
            newProj = new JavaActions(displSet, proc, consPnl, fileTree);
            break;
         case "HTML":
            newProj = new HtmlActions(proc, fileTree);
            break;
         case "PERL":
            newProj = new PerlActions(displSet, proc, consPnl, fileTree);
            break;
      }
      return newProj;
   }         
}
