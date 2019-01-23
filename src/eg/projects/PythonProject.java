package eg.projects;

//--Eadgyth--/
import eg.console.ProcessStarter;
import eg.ui.ProjectActionsUpdate;
import eg.ui.ConsoleOpener;

/**
 * Represents a programming project in Python
 */
public final class PythonProject extends AbstractProject implements ProjectActions {

   private final ProcessStarter proc;
   private final ConsoleOpener opener;

   private String startCmd = "";

   /**
    * @param proc  the ProcessStarter
    * @param opener  the ConsoleOpener
    */
   public PythonProject(ProcessStarter proc, ConsoleOpener opener) {
      super(ProjectTypes.PYTHON, true, "py");
      this.proc = proc;
      this.opener = opener;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of main python script")
            .addSourceDirInput()
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
      StringBuilder sb = new StringBuilder("python ");
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
