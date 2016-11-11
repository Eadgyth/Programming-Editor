package eg.projects;

import eg.console.*;
import eg.ui.MainWin;

/**
 * The creation of objects of the classes that represent a project. These
 * classes are of type {@link ProjectActions}
 */
public class ProjectFactory {

   private final MainWin mw;
   private final ProcessStarter proc;
   private final ConsolePanel cw;

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
    * interface and that is selected based on the extension of the
    * file which belongs to the project.
    * <p>
    * This method has to be modified if new project actions (languages,
    * file types) are implemented.
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
         SettingsWin setWin = new SettingsWin("Main class", "Package",
               true, true, "jar file");
         projActJava.setProjectConfig(new ProjectConfig(setWin));
      }
      actionOptions(true, true, true);
   }
   
   private void createProjActHtml() {
      if (projActHtml == null) {
         projActHtml = new HtmlActions();
         SettingsWin setWin = new SettingsWin("HTML file", "Subfolder",
               false, false, null);
         projActHtml.setProjectConfig(new ProjectConfig(setWin));
      }
      actionOptions(false, true, false);
   }
   
   private void createProjActText() {
      if (projActTxt == null) {
         projActTxt = new TxtActions();
         SettingsWin setWin = new SettingsWin("Text file", "Subfolder",
               false, false, null);
         projActTxt.setProjectConfig(new ProjectConfig(setWin));
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