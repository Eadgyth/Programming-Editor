package eg.projects;

import eg.console.*;
import eg.Preferences;
import eg.ui.ViewSettings;
import eg.ui.filetree.FileTree;

/**
 * The creation of an object of type {@code ProjectActions} that is selected
 * based on the extension of the file which a project is defined for
 */
public class SelectedProject {

   private final Preferences prefs = new Preferences();
   private final ViewSettings viewSet;
   private final ProcessStarter proc;
   private final ConsolePanel consPnl;
   private final FileTree fileTree;

   public SelectedProject(ViewSettings viewSet, ProcessStarter proc,
         ConsolePanel consPnl, FileTree fileTree) {
      this.viewSet = viewSet;
      this.proc = proc;
      this.consPnl = consPnl;
      this.fileTree = fileTree;
   }

   /**
    * Returns an object of type {@code ProjectActions}
    * @param fileExt  the extension of the file which a project
    * is to be defined for (has the form ".java", for example)
    * @param isSearchByLang  true to create a {@link ProjectActions}
    * based on the currently set language
    * @return  an object of type {@link ProjectActions} or null if no
    * class exists that implements ProjectActions for the given file
    * extension.
    */
   public ProjectActions createProject(String fileExt, boolean isSearchByLang) {
      ProjectActions newProj = null;
      switch (fileExt) {
         case ".java":
            newProj = new JavaActions(viewSet, proc, consPnl, fileTree);
            break;
         case ".html":
            newProj = new HtmlActions(proc, fileTree);
            break;
         case ".pl": case ".pm":
            newProj = new PerlActions(viewSet, proc, consPnl, fileTree);
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
            newProj = new JavaActions(viewSet, proc, consPnl, fileTree);
            break;
         case "HTML":
            newProj = new HtmlActions(proc, fileTree);
            break;
         case "PERL":
            newProj = new PerlActions(viewSet, proc, consPnl, fileTree);
            break;
      }
      return newProj;
   }         
}
