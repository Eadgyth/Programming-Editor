package eg.projects;

//--Eadgyth--//
import eg.console.ProcessStarter;
import eg.ui.ProjectActionsUpdate;
import eg.ui.ConsoleOpener;

/**
 * Represents a programming project in R
 */
public final class RProject extends AbstractProject implements ProjectActions {

   private final ProcessStarter proc;
   private final ConsoleOpener opener;

   private String startCmd = "";

   /**
    * @param proc  the ProcessStarter
    * @param opener  the ConsoleOpener
    */
   public RProject(ProcessStarter proc, ConsoleOpener opener) {
      super(ProjectTypes.R, true, "R");
      this.proc = proc;
      this.opener = opener;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of R script")
         .addCmdOptionsInput()
         .addCmdArgsInput()
         .buildWindow();
   }

   @Override
   public void enableActions(ProjectActionsUpdate update) {
      update.enable(false, true, false, null);
   }

   @Override
   public void run() {
      if (!locateMainFile()) {
         return;
      }
      opener.open();
      proc.startProcess(startCmd);
   }

   @Override
   protected void setCommandParameters() {
      StringBuilder sb = new StringBuilder("Rscript ");
      if (!cmdOptions().isEmpty()) {
         sb.append(cmdOptions()).append(" ");
      }
      sb.append(relMainFilePath());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      startCmd = sb.toString();
   }
}
