package eg.projects;

//--Eadgyth--//
import eg.TaskRunner;
import eg.Projects.ProjectActionsUpdate;

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
      super(ProjectTypes.R, "R", null);
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addSourceDirInput(SRC_DIR_LABEL)
         .addFileInput(R_SCRIPT_LABEL, true)
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
      if (!locateSourceFile()) {
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
      sb.append(relativeSourceFile());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      startCmd = sb.toString();
   }

   private static final String R_SCRIPT_LABEL =
         "Name of R script file";

   private static final String SRC_DIR_LABEL =
         "Subdirectory containing R script (if present)";
}
