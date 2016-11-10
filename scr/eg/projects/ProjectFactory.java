package eg.projects;

import eg.Preferences;
import eg.console.*;
import eg.ui.MainWin;

/**
 * Creation of objects of classes that are used for a programming
 * project in the Eadgyth editor. Objects returned are of type
 * {@link ProjectActions}
 */
public class ProjectFactory {

   private Preferences prefs = new Preferences();

   private MainWin mw;
   private ProcessStarter proc;
   private ConsolePanel cw;

   private ProjectActions projActJava = null;
   private ProjectActions projActHtml = null;
   private ProjectActions projActTxt  = null;

   private boolean isCompile = false;
   private boolean isRun = false;
   private boolean isBuild = false;

   public ProjectFactory(MainWin mw, ProcessStarter proc,
         ConsolePanel cw) {
      this.mw = mw;
      this.proc = proc;
      this.cw = cw;
   }

   /**
    * Returns an object of a class that implements the ProjectActions
    * interface.
    * <p>
    * Already created objects are maintained and re-assigned to
    * ProjectActions of the language remains the same.
    * <p>
    * This method has to be modified if new project actions are implemented.
    * @return  an object of type {@link ProjectActions}.
    */
   public ProjectActions getProjAct(String filename) {      
      if (filename.endsWith(".java")) {
         createProjActJava();
         return projActJava;
      }
      else if (filename.endsWith(".html")) {
         createProjActHtml();
         return projActHtml;
      }
      else if (filename.endsWith(".txt")) {
         createProjActText();
         return projActTxt;
      }
      return null;
   }

   /**
    * @return  if {@link ProjectActions#compile()} is used
    */
   public boolean isCompile() {
      return isCompile;
   }

   /**
    * @return  if {@link ProjectActions#runProject()} is used
    */
   public boolean isRun() {
      return isRun;
   }

   /**
    * @return  if {@link ProjectActions#build()} is used
    */
   public boolean isBuild() {
      return isBuild;
   }
   
   private void createProjActJava() {
      if (projActJava == null) {
         projActJava = new JavaActions(mw, proc, cw);
         projActJava.setProjectConfig(new ProjectConfig("Main class", "Package",
               true, "jar file"));
      }
      actionOptions(true, true, true);
   }
   
   private void createProjActHtml() {
      if (projActHtml == null) {
         projActHtml = new HtmlActions();
         projActHtml.setProjectConfig(new ProjectConfig("HTML file", null, false, null));
      }
      actionOptions(false, true, false);
   }
   
   private void createProjActText() {
      if (projActTxt == null) {
         projActTxt = new TxtActions();
         projActTxt.setProjectConfig(new ProjectConfig("Text file", null, false, null));
      }
      actionOptions(false, false, false);
   }
   
   private void actionOptions(boolean isCompile, boolean isRun,
         boolean isBuild) {
      this.isCompile = isCompile;
      this.isRun = isRun;
      this.isBuild = isBuild;
   }
}