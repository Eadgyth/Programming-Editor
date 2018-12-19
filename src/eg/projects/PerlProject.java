package eg.projects;

//--Eadgyth--//
import eg.console.ProcessStarter;
import eg.ui.ProjectActionsControl;

/**
 * Represents a programming project in Perl
 */
public final class PerlProject extends AbstractProject implements ProjectActions {

   private final ProjectActionsControl update;
   private final ProcessStarter proc;

   private String startCmd = "";

   /**
    * @param update  the ProjectActionsControl
    * @param proc  the ProcessStarter
    */
   public PerlProject(ProjectActionsControl update, ProcessStarter proc) {
      super(ProjectTypes.PERL, true, "pl");
      this.update = update;
      this.proc = proc;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of Perl script")
            .addSourceDirInput()
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
      StringBuilder sb = new StringBuilder("perl ");
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
