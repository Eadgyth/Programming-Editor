package eg.projects;

//--Eadgyth--/
import eg.TaskRunner;
import eg.Projects.ProjectActionsUpdate;

/**
 * Represents a project to define custom system commands to
 * compile, run or build a project
 */
public final class GenericProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;

   public GenericProject(TaskRunner runner) {
      super(ProjectTypes.GENERIC, null, null);
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addCustomCommandInput()
            .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      if (!customCompileCmd().isEmpty()) {
         update.enableCompile();
      }
      if (!customRunCmd().isEmpty()) {
         update.enableRun(false);
      }
      if (!customBuildCmd().isEmpty()) {
         update.enableBuild(null);
      }
   }

   @Override
   protected void setCommandParameters() {
      // not used
   }

   @Override
   public void compile() {
      runner.runSystemCommand(customCompileCmd());
   }

   @Override
   public void run() {
      runner.runSystemCommand(customRunCmd());
   }

   @Override
   public void build() {
      runner.runSystemCommand(customBuildCmd());
   }
}
