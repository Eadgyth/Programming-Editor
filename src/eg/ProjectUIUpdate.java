package eg;

import eg.ui.MainWin;
import eg.ui.Toolbar;
import eg.ui.menu.Menu;
import eg.ui.filetree.FileTree;

/**
 * The settings of the view of the main window depending on project
 * actions and type of project
 */
public class ProjectUIUpdate {
   
   private final MainWin mw;
   private final Menu menu;
   private final Toolbar tBar;
   private final FileTree fileTree;
   
   ProjectUIUpdate(MainWin mw) {

      this.mw = mw;
      this.menu = mw.getMenu();
      this.tBar = mw.getToolbar();
      this.fileTree = mw.getFileTree();
   }
   
   /**
    * If the console panel is open
    * @return  if the console is open
    */
   public boolean isConsoleOpen() {
      return menu.getViewMenu().isConsoleItmSelected();
   }
   
   /**
    * Shows the console panel
    */
   public void openConsole() {
      menu.getViewMenu().doConsoleItmAct(true);
   }
   
   /**
    * Sets the label for the menu item for creating a build
    * @param label  the label that describes the build
    */
   public void setBuildLabel(String label) {
      menu.getProjectMenu().setBuildLabel(label);
   }
   
   /**
    * Enables/disables menu items and toolbar buttons for project actions
    * @param isCompile  true to enable the compilation action
    * @param isRun  true to enable the run action
    * @param isBuild  true to enable the build action
    * @param projCount  the number of loaded projects. If 1 the action
    * to show the fileview is enabled, if 2 the action to change between
    * projects is enabled
    */
   public void enableProjActions(boolean isCompile, boolean isRun,
         boolean isBuild, int projCount) {
      if (projCount == 1) {
         menu.getViewMenu().enableFileView();
      }
      if (projCount == 2) {
         enableChangeProj();
      }
      menu.getProjectMenu().enableProjItms(isCompile, isRun, isBuild);
      tBar.enableProjBts(isCompile, isRun);
   }
   
   /**
    * Enables the menu item and toolbar button for changing project
    */
   public void enableChangeProj() {
      menu.getProjectMenu().enableChangeProjItm();
      tBar.enableChangeProjBt();
   }
   
   /**
    * Updates the file tree. Used if the file structure may have
    * changed.
    */
   public void updateFileTree() {
      fileTree.updateTree();
   }
   
   /**
    * Sets the filetree at the specified root
    * @param root  the root for the tree
    */
   public void setProjectTree(String root) {
      fileTree.setProjectTree(root);
   }
   
   /**
    * Sets the name of the directory that is allowed to be deleted
    * @param name  the name of the deletable directory
    */
   public void setDeletableDirName(String name) {
      fileTree.setDeletableDirName(name);
   }
   
   /**
    * Displays the project name in the status bar
    * @param name  the name of the project
    */
   public void showProjectInfo(String name) {
      mw.showProjectInfo(name);
   }
   
   /**
    * Sets the busy or default curser
    * @param isBusy  true/false to set a busy/default cursor
    */
   public void setBusyCursor(boolean isBusy) {
      mw.setBusyCursor(isBusy);
   }
}
