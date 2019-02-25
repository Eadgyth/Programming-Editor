package eg.projects;

import java.awt.Desktop;

import java.io.File;
import java.io.IOException;

//--Eadgyth--/
import eg.utils.FileUtils;
import eg.utils.Dialogs;
import eg.ui.ProjectActionsUpdate;

/**
 * Represents a coding project in HTML
 */
public final class HtmlProject extends AbstractProject implements ProjectCommands {

   public HtmlProject() {
      super(ProjectTypes.HTML, false, null);
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      update.enable(false, true, false, null);
   }

   /**
    * Shows the HTML file that is specified by the filepath in the
    * default file browser
    *
    * @param filepath  the filepath
    */
   @Override
   public void run(String filepath) {
      File htmlFile = new File(filepath);
      if (!filepath.endsWith(".html") && !filepath.endsWith(".htm")) {
         Dialogs.warnMessage("No HTML file is open or in the selected tab.");
         return;
      }
      try {
         if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(htmlFile);
         }
      }
      catch (IOException | IllegalArgumentException
            | UnsupportedOperationException  e) {

         FileUtils.log(e);
      }
   }

   /**
    * Not implemented
    */
   @Override
   protected void setCommandParameters() {}
}
