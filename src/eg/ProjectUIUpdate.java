package eg;

import eg.ui.menu.ViewMenu;
import eg.ui.filetree.FileTree;

/**
 * Subset of methods to modify the view related to project actions
 */
public class ProjectUIUpdate {
   
   private final ViewMenu vMenu;
   private final FileTree fileTree;
   
   public ProjectUIUpdate(ViewMenu vMenu, FileTree fileTree) {
      this.vMenu = vMenu;
      this.fileTree = fileTree;
   }
   
   /**
    * If the console panel is open
    *
    * @return  if the console is open
    */
   public boolean isConsoleOpen() {
      return vMenu.isConsoleItmSelected();
   }
   
   /**
    * Shows the console panel
    */
   public void openConsole() {
      vMenu.doConsoleItmAct(true);
   }
   
   /**
    * Updates the file tree. Used if the file structure may have
    * changed.
    */
   public void updateFileTree() {
      fileTree.updateTree();
   }
}
