package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.utils.ShowJOption;

/**
 * A programming project in HTML
 */
public class HtmlActions implements ProjectActions {
   
   private ProjectConfig projConf;
   private File htmlFile;
   
   @Override
   public void setProjectConfig(ProjectConfig projConf) {
      this.projConf = projConf;
   }
   
   @Override
   public SettingsWin getSetWin() {
      return projConf.getSetWin();
   }
   
   @Override
   public void makeSetWinVisible(boolean enable) {
      projConf.makeSetWinVisible(enable);
   }
   
   @Override
   public void configFromSetWin(String dir) {
      projConf.configFromSetWin(dir, ".html");
      if (projConf.getProjectPath().length() > 0) {
         setHtmlFile();
      }
   }
   
   @Override
   public void findPreviousProjectRoot(String dir) {
      projConf.findPreviousProjectRoot(dir);
      if (projConf.getProjectPath().length() > 0) {
         setHtmlFile();
      }
   }
   
   @Override
   public String getProjectRoot() {
       return projConf.getProjectPath();
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
      htmlFile = new File(projConf.getProjectPath() + File.separator
               + projConf.getMainMethod() + ".html");
   }
}