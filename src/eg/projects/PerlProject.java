package eg.projects;

//--Eadgyth--/
import eg.console.ProcessStarter;
import eg.ui.ProjectActionsControl;

/**
 * Represents a programming project in Perl
 */
public final class PerlProject extends AbstractProject implements ProjectActions {

   private final ProjectActionsControl update;
   private final ProcessStarter proc;

   private String startCmd = "";
   private String compileCmd = "";

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
            .addCmdOptionsInput()
            .addCmdArgsInput()
            .buildWindow();
   }

   @Override
   public void enableActions() {
      update.enable(true, true, false, null);
   }

   @Override
   public void compile() {
      if (!locateMainFile()) {
         return;
      }
      if (!update.isConsoleOpen()) {
         update.openConsole();
      }
      proc.startProcess(compileCmd);
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
      StringBuilder sb = new StringBuilder();
      if (!sourceDirName().isEmpty()) {
         sb.append(sourceDirName()).append("/");
      }
      sb.append(mainFileName()).append(sourceExtension());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      startCmd = "perl " + sb.toString();
      compileCmd = "perl -c " + sb.toString();
   }
}
