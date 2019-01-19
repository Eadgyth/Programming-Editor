package eg.projects;

//--Eadgyth--/
import eg.console.ProcessStarter;
import eg.ui.ProjectActionsControl;

/**
 * Represents a programming project in Python
 */
public final class PythonProject extends AbstractProject implements ProjectActions {

   private final ProjectActionsControl update;
   private final ProcessStarter proc;

   private String startCmd = "";

   /**
    * @param update  the ProjectActionsControl
    * @param proc  the ProcessStarter
    */
   public PythonProject(ProjectActionsControl update, ProcessStarter proc) {
      super(ProjectTypes.PYTHON, true, "py");
      this.update = update;
      this.proc = proc;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of main python script")
            .addCmdOptionsInput()
            .addCmdArgsInput()
            .buildWindow();
   }

   @Override
   public void enableActions() {
      update.enable(false, true, false, null);
   }

   @Override
   public void run() {
      if (!locateMainFile()) {
         return;
      }
      if (!update.isConsoleOpen()) {
         update.openConsole();
      }
      proc.startProcess(startCmd);
   }

   @Override
   protected void setCommandParameters() {
      StringBuilder sb = new StringBuilder("python ");
      /*if (!sourceDirName().isEmpty()) {
         sb.append(sourceDirName()).append("/");
      }*/
      if (!cmdOptions().isEmpty()) {
         sb.append(cmdOptions()).append(" ");
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
