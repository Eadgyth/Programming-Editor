package eg.projects;

import java.io.File;
import java.io.IOException;

/**
 * Represents a placeholder project for text files with extension .txt
 */
public final class TxtActions extends ProjectConfig implements ProjectActions {
   
   TxtActions() {
      super(".txt");
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
