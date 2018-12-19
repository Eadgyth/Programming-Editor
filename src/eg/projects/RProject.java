package eg.projects;

//--Eadgyth--//
import eg.console.ProcessStarter;
import eg.ui.ProjectActionsControl;

/**
 * Represents a programming project in R
 */
public final class RProject extends AbstractProject implements ProjectActions {

   private final ProjectActionsControl update;
   private final ProcessStarter proc;

   private String startCmd = "";

   /**
    * @param update  the ProjectActionsControl
    * @param proc  the ProcessStarter
    */
   public RProject(ProjectActionsControl update, ProcessStarter proc) {
      super(ProjectTypes.R, true, "R");
      this.update = update;
      this.proc = proc;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of R script")
         .addCmdOptionsInput()
         .addCmdArgsInput()
         .buildWindow();
   }

   @Override
   public void enableActions() {
      update.enableProjectActions(false, true, false, null);
   }

   @Override
   public void runProject() {
      if (!update.isConsoleOpen()) {
         update.openConsole();
      }
      proc.startProcess(startCmd, false);
   }

   @Override
   protected void setCommandParameters() {
      StringBuilder sb = new StringBuilder("Rscript ");
      if (!cmdOptions().isEmpty()) {
         sb.append(cmdOptions()).append(" ");
      }
      if (!sourceDirName().isEmpty()) {
         sb.append(sourceDirName()).append("/");
      }
      if (!namespace().isEmpty()) {
         sb.append(namespace()).append("/");
      }
      sb.append(mainFileName()).append(sourceExtension());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      startCmd = sb.toString();
   }
}
