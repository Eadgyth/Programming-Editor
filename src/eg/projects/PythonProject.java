package eg.projects;

import java.io.File;

//--Eadgyth--/
import eg.TaskRunner;
import eg.Projects.ProjectActionsUpdate;

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
      super(ProjectTypes.PYTHON, "py", File.separator);
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions
            .addSourceDirInput(SRC_DIR_LABEL)
            .addFileInput(PY_SCRIPT_LABEL)
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
   
   private static final String SRC_DIR_LABEL =
         "Location of Python script file within project";
   
   private static final String PY_SCRIPT_LABEL =
         "Name of Python script file";
}
