package eg.projects;

import java.io.File;
import java.io.IOException;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.console.ProcessStarter;

import eg.utils.JOptions;

import eg.ui.filetree.FileTree;

/**
 * Represents a project to write a webpage in HTML
 */
public class HtmlActions extends ProjectConfig implements ProjectActions {

   private final ProcessStarter proc;
   private final FileTree fileTree;
   
   private File htmlFile;
   
   public HtmlActions(ProcessStarter proc, FileTree fileTree) {
      super(new SettingsWin("HTML file", "Subdirectory",
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
      boolean success = super.configFromSetWin(dir, suffix);
      if (success) {
         setHtmlFile();
      }
      return success;
   }
   
   @Override
   public boolean retrieveLastProject(String dir) {
      boolean success = super.retrieveLastProject(dir);
      if (success) {
         setHtmlFile();
      }
      return success;
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
    * Shows the html document in the default file browser
    */
   @Override
   public void runProject() {
      try{
         if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(htmlFile);
         }
      }
      catch (IOException e) {
         System.out.println(e.getMessage());
         JOptions.warnMessage("No browser could be launched");
      }
   }
   
   /**
    * Not used
    */
   @Override
   public void build() {     
   }
   
   private void setHtmlFile() {
      htmlFile = new File(getProjectRoot() + File.separator
            + getModuleName() + File.separator + getMainFile() + ".html");
   }
}