package eg.projects;

//--Eadgyth--//
import eg.ui.ConsoleOpenable;
import eg.console.*;

/**
 * Represents a programming project in Perl
 */
public final class PerlProject extends AbstractProject implements ProjectActions {

   private final ConsoleOpenable co;
   private final ProcessStarter proc;

   private String startCmd = "";

   PerlProject(ConsoleOpenable co, ProcessStarter proc) {
      super(ProjectTypes.PERL, true, "pl");
      this.co = co;
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
   public void runProject() {
      if (!co.isConsoleOpen()) {
         co.openConsole();
      }
      proc.startProcess(startCmd);
   }
   
   @Override
   protected void setCommandParameters() {
      String main = getMainFileName() + getSourceExtension();
      if (getCmdArgs().length() > 0) {
         main += " " + getCmdArgs();
      }
      startCmd = "perl ";
      if (getSourceDirName().length() == 0 ) {
         startCmd += main;
      }
      else {
         String pathToMain = getSourceDirName() + "/" + main;
         startCmd += pathToMain;
      }
   }
}
