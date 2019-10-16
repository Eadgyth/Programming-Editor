package eg.projects;

//--Eadgyth--//
import eg.TaskRunner;
import eg.ui.ProjectActionsUpdate;

/**
 * Represents a programming project in R
 */
public final class RProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;

   private String startCmd = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public RProject(TaskRunner runner) {
      super(ProjectTypes.R, true, "R");
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of R script file")
         .addCmdOptionsInput()
         .addCmdArgsInput()
         .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      update.enableRun(true);
   }

   @Override
   public void run() {
      if (!locateMainFile()) {
         return;
      }
      runner.runSystemCommand(startCmd);
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
