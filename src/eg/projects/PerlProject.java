package eg.projects;

//--Eadgyth--/
import eg.TaskRunner;
import eg.Projects.ProjectActionsUpdate;

/**
 * Represents a programming project in Perl
 */
public final class PerlProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;

   private String runCmd = "";
   private String compileCmd = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public PerlProject(TaskRunner runner) {
      super(ProjectTypes.PERL, "pl", null);
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addSourceDirInput(SRC_DIR_LABEL)
            .addFileInput(PERL_SCRIPT_LABEL, true)
            .addCmdOptionsInput()
            .addCmdArgsInput()
            .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      update.enableRun(true);
      update.enableCompile();
   }

   @Override
   public void compile() {
      if (!locateSourceFile()) {
         return;
      }
      runner.runSystemCommand(compileCmd);
   }

   @Override
   public void run() {
      if (!locateSourceFile()) {
         return;
      }
      runner.runSystemCommand(runCmd);
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
      sb.append(relativeSourceFile());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      runCmd = sb.toString();
   }

   private void setCompileCmd() {
      StringBuilder sb = new StringBuilder("perl -c ");
      sb.append(relativeSourceFile());
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      compileCmd = sb.toString();
   }

   private static final String PERL_SCRIPT_LABEL =
         "Name of Perl script file";

   private static final String SRC_DIR_LABEL =
         "Subdirectory containing Perl script (if present)";
}
