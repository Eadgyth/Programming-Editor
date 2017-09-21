package eg;

import eg.ui.MainWin;
import eg.projects.ProjectActions;

/**
 * Subset of methods to modify the view related to project actions
 */
public class ProjectUIUpdate {
   
   private final MainWin mw;
   
   public ProjectUIUpdate(MainWin mw) {
      this.mw = mw;
   }
   
   /**
    * If the console panel is open
    *
    * @return  if the console is open
    */
   public boolean isConsoleOpen() {
      return mw.menu().viewMenu().isConsoleItmSelected();
   }
   
   /**
    * Shows the console panel
    */
   public void openConsole() {
      mw.menu().viewMenu().doConsoleItmAct(true);
   }
   
   /**
    * Updates the file tree
    */
   public void updateFileTree() {
      mw.fileTree().updateTree();
   }
   
   /**
    * Updates main window when a new project is set
    *
    * @param projToSet  the {@link ProjectActions} that is newly set active
    * @param nProjects  the number of stored configured projects
    */
   public void updateProjectSetting(ProjectActions projToSet, int nProjects) {
      enableActions(projToSet, nProjects);
      mw.showProjectInfo(projToSet.getProjectName());
      mw.fileTree().setDeletableDirName(projToSet.getExecutableDirName());
      mw.fileTree().setProjectTree(projToSet.getProjectPath());
   }
   
   /**
    * Enables to change project if two configured projects are 
    * stored
    *
    * @param nProjects  the number of stored configured projects
    */
   public void enableChangeProject(int nProjects) {
      if (nProjects == 2) {
         mw.menu().projectMenu().enableChangeProjItm();
         mw.toolbar().enableChangeProjBt();
      }
   }
   
   private void enableActions(ProjectActions projToSet, int nProjects) {
      if (nProjects == 1) {
         mw.menu().viewMenu().enableFileView();
      }
      enableChangeProject(nProjects);
      enableActions(projToSet.getClass().getSimpleName());
   }

   private void enableActions(String className) {
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
