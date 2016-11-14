package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.utils.ShowJOption;

/**
 * Represents a project to write a webpage in HTML
 */
public class HtmlActions implements ProjectActions {
   
   private final ProjectConfig projConf
         = new ProjectConfig(new SettingsWin("HTML file", "Subfolder",
           false, false, null));

   private File htmlFile;
   
   @Override
   public SettingsWin getSetWin() {
      return projConf.getSetWin();
   }
   
   @Override
   public void makeSetWinVisible(boolean enable) {
      projConf.makeSetWinVisible(enable);
   }
   
   @Override
   public void configFromSetWin(String dir, String suffix) {
      projConf.configFromSetWin(dir, suffix);
      if (projConf.getProjectRoot().length() > 0) {
         setHtmlFile();
      }
   }
   
   @Override
   public void findPreviousProjectRoot(String dir) {
      projConf.findPreviousProjectRoot(dir);
      if (projConf.getProjectRoot().length() > 0) {
         setHtmlFile();
      }
   }
   
   @Override
   public String getProjectRoot() {
       return projConf.getProjectRoot();
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return projConf.isInProjectPath(dir);
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
         ShowJOption.warnMessage("No defautl browser could be launched");
      }
   }
   
   /**
    * Not used
    */
   @Override
   public void build() {     
   }
   
   private void setHtmlFile() {
      htmlFile = new File(projConf.getProjectRoot() + File.separator
               + projConf.getMainFile() + ".html");
   }
}