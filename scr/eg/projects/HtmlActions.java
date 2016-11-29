package eg.projects;

import java.io.File;
import java.io.IOException;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.utils.JOptions;

/**
 * Represents a project to write a webpage in HTML
 */
public class HtmlActions extends ProjectConfig implements ProjectActions {

   private File htmlFile;
   
   public HtmlActions() {
      super(new SettingsWin("HTML file", "Subdirectory",
           false, false, null));
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
   public boolean findPreviousProjectRoot(String dir) {
      boolean success = super.findPreviousProjectRoot(dir);
      if (success) {
         setHtmlFile();
      }
      return success;
   }
   
   @Override
   public String getProjectName() {
      return super.getProjectName();
   }
   
   @Override
   public String getProjectRoot() {
      return super.getProjectRoot();
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return super.isInProjectPath(dir);
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
         JOptions.warnMessage("No defautl browser could be launched");
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
            + getModuleDir() + File.separator + getMainFile() + ".html");
   }
}