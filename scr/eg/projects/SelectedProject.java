package eg.projects;

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
   
   private String sourceExt;

   public SelectedProject(DisplaySetter displSet, ProcessStarter proc,
         ConsolePanel consPnl, FileTree fileTree) {
      this.displSet = displSet;
      this.proc = proc;
      this.consPnl = consPnl;
      this.fileTree = fileTree;
   }
   
   public String getSourceExt() {
      return sourceExt;
   }

   /**
    * Returns an object of type {@code ProjectActions} and creates the
    * {@code SettingsWin} for the project
    *
    * @param fileExt  the extension of the file which a project is to be
    * defined for
    * @return  an object of type {@link ProjectActions} or null if no
    * class exists that implements ProjectActions for the given file
    * extension.
    */
   public ProjectActions createProject(String fileExt) {
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
         default:
            newProj = createProjByLang(fileExt);
            break;
      }
      if (newProj != null) {
         newProj.createSettingsWin();
      }
      return newProj;
   }
   
   public void enableActions(String className, int projCount) {
      switch (className) {
         case "JavaActions":
            displSet.enableProjActions(true, true, true, projCount);
            displSet.setBuildMenuItmText("Create jar");
            sourceExt = ".java";
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
   
   private ProjectActions createProjByLang(String fileExt) {
      ProjectActions newProj = null;
      prefs.readPrefs();
      String language = prefs.getProperty("language");
      switch (language) {
         case "JAVA":
            newProj = new JavaActions(displSet, proc, consPnl, fileTree);
            break;
         case "HTML":
            newProj = new HtmlActions();
            break;
         case "PERL":
            newProj = new PerlActions(displSet, proc);
            break;
         case "PLAIN_TEXT":
            if ("txt".equals(fileExt)) {
               newProj = new TxtActions();
            }
            break;
      }
      return newProj;
   }         
}
