package eg.projects;

import java.io.File;
import java.io.IOException;

/**
 * Represents a project using text files although the class can only
 * define a project's directories but does not perform any actions
 */
public class TxtActions implements ProjectActions {
   
   private ProjectConfig projConf;
   
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
   public void configFromSetWin(String dir, String suffix) {
      projConf.configFromSetWin(dir, suffix);
   }
   
   @Override
   public void findPreviousProjectRoot(String dir) {
      projConf.findPreviousProjectRoot(dir);
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
    * not used
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