package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--/
import eg.utils.FileUtils;
import eg.utils.Dialogs;
import eg.ui.ProjectActionsControl;

/**
 * Represents a coding project in HTML
 */
public final class HtmlProject extends AbstractProject implements ProjectActions {
   
   private final ProjectActionsControl update;

   /**
    * @param update  the ProjectActionsControl
    */
   public HtmlProject(ProjectActionsControl update) {
      super(ProjectTypes.HTML, false, null);
      this.update = update;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.buildWindow();
   }

   @Override
   public void enableActions() {
      update.enable(false, true, false, null);
   }

   /**
    * Shows the HTML file that is specified by the filepath
    * in the default file browser
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
         if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(htmlFile);
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
