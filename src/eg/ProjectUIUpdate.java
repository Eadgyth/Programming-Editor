package eg;

import eg.ui.MainWin;
import eg.ui.menu.Menu;
import eg.ui.filetree.FileTree;

/**
 * The settings of the view of the main window depending on project
 * actions and type of project
 */
public class ProjectUIUpdate {
   
   private final MainWin mw;
   private final Menu menu;
   private final FileTree fileTree;
   
   public ProjectUIUpdate(MainWin mw) {
      this.mw = mw;
      this.menu = mw.menu();
      this.fileTree = mw.fileTree();
   }
   
   /**
    * If the console panel is open
    * @return  if the console is open
    */
   public boolean isConsoleOpen() {
      return menu.viewMenu().isConsoleItmSelected();
   }
   
   /**
    * Shows the console panel
    */
   public void openConsole() {
      menu.viewMenu().doConsoleItmAct(true);
   }
   
   /**
    * Updates the file tree. Used if the file structure may have
    * changed.
    */
   public void updateFileTree() {
      mw.fileTree().updateTree();
   }
}
