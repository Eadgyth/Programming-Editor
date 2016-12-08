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
           false, false, null));
      this.proc = proc;
      this.fileTree = fileTree;
   }
   
   @Override
   public void addOkAction(ActionListener al) {
      super.addOkAction(al);
   }
   
   @Override
   public void makeSetWinVisible(boolean enable) {
      super.makeSetWinVisible(enable);
   }
   
   @Override
   public boolean configFromSetWin(String dir, String suffix) {
       return super.configFromSetWin(dir, suffix);
   }
   
   @Override
   public boolean retrieveLastProject(String dir) {
      return super.retrieveLastProject(dir);
   }
   
   /**
    * Passes the project's root to this {@code ProcessStarter}
    * and this {@code FileTree} 
    */
   @Override
   public void applyProject() {
      proc.addWorkingDir(getProjectRoot());
      fileTree.setProjectTree(getProjectRoot());
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return super.isInProjectPath(dir);
   }
   
   @Override
   public String getProjectName() {
      return super.getProjectName();
   }
   
   @Override
   public void storeConfig() {
      super.storeConfig();
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