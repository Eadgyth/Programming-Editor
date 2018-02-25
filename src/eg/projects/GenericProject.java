package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.console.*;
import eg.utils.FileUtils;
import eg.ui.ConsoleOpenable;

/**
 * Defines a project that simply has a project root
 */
public final class GenericProject extends AbstractProject implements ProjectActions {

   GenericProject() {
      super(ProjectTypes.GENERIC, false, null);
   }
   
   @Override
   public void buildSettingsWindow() {
         inputOptions.buildWindow();
   }
   
   /**
    * Not implemented
    */
   @Override
   protected void setCommandParameters() {}
}
