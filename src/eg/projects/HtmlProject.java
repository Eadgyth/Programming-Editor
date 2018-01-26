package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.utils.FileUtils;

/**
 * Represents a project to open HTML files contained in the project's root directory
 * in the default browser
 */
public final class HtmlProject extends AbstractProject implements ProjectActions {

   public HtmlProject(String fileExtension) {
      super(fileExtension, false);
   }

  @Override
   public void createSettingsWin() {
      setWin = SettingsWin.basicWindow();
   }

   @Override
   public boolean configureProject(String dir) {
      boolean success = super.configureProject(dir);
      return success;
   }

   @Override
   public boolean retrieveProject(String dir) {
      boolean success = super.retrieveProject(dir);
      return success;
   }

   /**
    * Shows the HTML file that is specified by the filepath
    * in the default file browser
    *
    * @param filepath  the filepath
    */
   @Override
   public void runProject(String filepath) {
      File htmlFile = new File(filepath);
      if (!filepath.endsWith(getSourceFileExtension())) {
         eg.utils.Dialogs.warnMessage(
               htmlFile.getName() + " cannot be opened in a browser");

         return;
      }
      try {
         if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(htmlFile);
         }
      }
      catch (IOException | IllegalArgumentException | UnsupportedOperationException  e) {
         FileUtils.logStack(e);
      }
   }
}
