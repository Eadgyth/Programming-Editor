package eg.projects;

import eg.Projects.ProjectActionsUpdate;
//--Eadgyth--/
import eg.TaskRunner;

/**
 * Represents a programming project in Python
 */
public final class PythonProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;

   private String startCmd = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public PythonProject(TaskRunner runner) {
      super(ProjectTypes.PYTHON, "py");
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions
         .addFileInput(PY_SCRIPT_LABEL, true)
         .addCmdOptionsInput()
         .addCmdArgsInput()
         .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      boolean b = !sourceFileName().isEmpty();
      update.enableRun(b, true);
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
      StringBuilder sb = new StringBuilder("python -u ");
      if (!cmdOptions().isEmpty()) {
         sb.append(cmdOptions()).append(" ");
      }
      sb.append(relativeSourceFile());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      startCmd = sb.toString();
   }

   private static final String PY_SCRIPT_LABEL =
         "Name of Python script file";
}
