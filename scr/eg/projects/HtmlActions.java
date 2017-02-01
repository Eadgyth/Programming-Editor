package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.console.ProcessStarter;
import eg.utils.JOptions;
import eg.utils.FileUtils;
import eg.ui.filetree.FileTree;

/**
 * Represents a project to write a webpage in HTML
 */
public final class HtmlActions extends ProjectConfig implements ProjectActions {

   private final static String F_SEP = File.separator;
   private final ProcessStarter proc;
   private final FileTree fileTree;
   
   private File htmlFile;
   
   HtmlActions(ProcessStarter proc, FileTree fileTree) {
      super(".html");
      this.proc = proc;
      this.fileTree = fileTree;
   }
   
   /**
    * Creates an adapted {@link SettingsWin}.
    */
   @Override
   public void createSettingsWin() { 
      SettingsWin setWin = SettingsWin.adaptableWindow();
      setWin.useNewFileLabel("Name of HTML file")
            .setupWindow();
      setSettingsWin(setWin);
   }
   
   @Override
   public boolean configureProject(String dir) {
      boolean success = super.configureProject(dir);
      if (success) {
         setHtmlFile();
      }
      return success;
   }
   
   @Override
   public boolean retrieveProject(String dir) {
      boolean success = super.retrieveProject(dir);
      if (success) {
         setHtmlFile();
      }
      return success;
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
         FileUtils.logStack(e);
      }
   }
   
   /**
    * Not used
    */
   @Override
   public void build() {
   }
   
   private void setHtmlFile() {
      htmlFile = new File(getProjectPath() + F_SEP
            + getModuleName() + F_SEP + getMainFile() + ".html");
   }
}
