package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--/
import eg.utils.FileUtils;

/**
 * Represents a coding project in HTML
 */
public final class HtmlProject extends AbstractProject implements ProjectActions {

   HtmlProject(String fileExtension) {
      super(fileExtension, false);
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
   
   /**
    * Not used
    */
   @Override
   protected void setCommandParameters() {}
}
