package eg.projects;

//--Eadgyth--/
import eg.TaskRunner;
import eg.utils.Dialogs;
import eg.Projects.ProjectActionsUpdate;

/**
 * Represents a project to run a custom system command
 */
public final class GenericProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;

   boolean isCmd;

   public GenericProject(TaskRunner runner) {
      super(ProjectTypes.GENERIC, false, null);
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addCustomCommandInput()
            .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      update.enableRun(false);
   }

   @Override
   protected void setCommandParameters() {
      isCmd = !customRunCmd().isEmpty();
   }

   @Override
   public void run() {
     if (isCmdDefined()) {
         runner.runSystemCommand(customRunCmd());
      }
   }

   //
   //--private--/
   //

   private boolean isCmdDefined() {
      boolean isCmd = !customRunCmd().isEmpty();
      if (!isCmd) {
         Dialogs.errorMessage(
            "A system command is not specified.", null);
      }
      return isCmd;
   }
}
