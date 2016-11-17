package eg.projects;

import java.awt.event.ActionListener;

/**
 * Represents a project using text files although the class does not
 * perform any actions
 */
public class TxtActions extends ProjectConfig implements ProjectActions {

   public TxtActions() {
      super(new SettingsWin("Name of a text file in the project", "Subdirectory",
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
       return super.configFromSetWin(dir, suffix);
   }
   
   @Override
   public boolean findPreviousProjectRoot(String dir) {
      return super.findPreviousProjectRoot(dir);
   }
   
   @Override
   public String getProjectRoot() {
       return super.getProjectRoot();
   }
   
   @Override
   public boolean isInProjectPath(String dir) {
      return super.isInProjectPath(dir);
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