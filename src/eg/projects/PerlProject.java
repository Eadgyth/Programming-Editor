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

   private String startCmd = "";
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
      inputOptions.addFileInput("Name of Perl script")
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
      proc.startProcess(startCmd);
   }

   @Override
   protected void setCommandParameters() {
      StringBuilder sb = new StringBuilder();
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
      startCmd = "perl " + sb.toString();
      compileCmd = "perl -c " + sb.toString();
   }
}
