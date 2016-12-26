package eg.projects;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.console.ProcessStarter;

import eg.ui.filetree.FileTree;

/**
 * Represents a project using text files although the class does not
 * perform any actions
 */
public class TxtActions extends ProjectConfig implements ProjectActions {

   private final ProcessStarter proc;
   private final FileTree fileTree;

   public TxtActions(ProcessStarter proc, FileTree fileTree) {
      super(new SettingsWin("Name of a text file in the project", "Subdirectory",
           false, false, false, null),
           ".txt"
      );
      this.proc = proc;
      this.fileTree = fileTree;
   }
   
   /**
    * Passes the project's root to this {@code ProcessStarter}
    * and this {@code FileTree} 
    */
   @Override
   public void applyProject() {
      proc.addWorkingDir(getProjectPath());
      fileTree.setProjectTree(getProjectPath());
   }
   
   /**
    * Not used
    */
   @Override
   public void compile() {
   }
   
   /**
    * Not used
    */
   @Override
   public void runProject() {
   }
   
   /**
    * Not used
    */
   @Override
   public void build() {     
   }
}
