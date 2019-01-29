package eg.projects;

//--Eadgyth--/
import eg.console.ProcessStarter;
import eg.ui.ProjectActionsUpdate;
import eg.ui.ConsoleOpener;

/**
 * Represents a programming project in Perl
 */
public final class PerlProject extends AbstractProject implements ProjectActions {

   private final ProcessStarter proc;
   private final ConsoleOpener opener;

   private String runCmd = "";
   private String compileCmd = "";

   /**
    * @param proc  the ProcessStarter
    * @param opener  the ConsoleOpener
    */
   public PerlProject(ProcessStarter proc, ConsoleOpener opener) {
      super(ProjectTypes.PERL, true, "pl");
      this.proc = proc;
      this.opener = opener;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of Perl script file")
            .addSourceDirInput()
            .addCmdOptionsInput()
            .addCmdArgsInput()
            .buildWindow();
   }

   @Override
   public void enableActions(ProjectActionsUpdate update) {
      update.enable(true, true, false, null);
   }

   @Override
   public void compile() {
      if (!locateMainFile()) {
         return;
      }
      opener.open();
      proc.startProcess(compileCmd);
   }

   @Override
   public void run() {
      if (!locateMainFile()) {
         return;
      }
      opener.open();
      proc.startProcess(runCmd);
   }

   @Override
   protected void setCommandParameters() {
      setRunCmd();
      setCompileCmd();
   }

   //
   //--private--/
   //

   private void setRunCmd() {
      StringBuilder sb = new StringBuilder("perl ");
      if (!cmdOptions().isEmpty()) {
         sb.append(cmdOptions()).append(" ");
      }
      sb.append(relMainFilePath());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      runCmd = sb.toString();
   }

   private void setCompileCmd() {
      StringBuilder sb = new StringBuilder("perl -c ");
      sb.append(relMainFilePath());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      compileCmd = sb.toString();
   }
}
