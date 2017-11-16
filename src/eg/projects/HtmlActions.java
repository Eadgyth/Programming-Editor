package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.utils.FileUtils;

/**
 * Represents a project to write a webpage in HTML
 */
public final class HtmlActions extends ProjectConfig implements ProjectActions {
   
   public HtmlActions(String suffix) {
      super(suffix, false);
   }
   
  @Override
   public void createSettingsWin() {
      setWin = SettingsWin.projectRootWindow();
   }
   
   /**
    * {@inheritDoc}.
    * Creates the path for the html file to show it in a fil browser
    */
   @Override
   public boolean configureProject(String dir) {
      boolean success = super.configureProject(dir);
      return success;
   }

   /**
    * {@inheritDoc}.
    * Creates the path for the html file to show it in a fil browser
    */
   @Override
   public boolean retrieveProject(String dir) {
      boolean success = super.retrieveProject(dir);
      return success;
   }
   
   /**
    * {@inheritDoc}.
    * Shows the html file in the default file browser
    */
   @Override
   public void runProject(String filepath) {
      if (!filepath.endsWith(getSourceSuffix())) {
         eg.utils.Dialogs.warnMessage("No " + getSourceSuffix() + " file is selected");
         return;
      }
      File htmlFile = new File(filepath);
      try {
         if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(htmlFile);
         }
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }
   }
}
