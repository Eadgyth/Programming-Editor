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
      StringBuilder sb = new StringBuilder("perl ");
      if (!getSourceDirName().isEmpty()) {
         sb.append(getSourceDirName()).append("/");
      }
      if (!getNamespace().isEmpty()) {
         sb.append(getNamespace()).append("/");
      }
      sb.append(getMainFileName()).append(getSourceExtension());
      if (!getCmdArgs().isEmpty()) {
         sb.append(" ").append(getCmdArgs());
      }
      startCmd = sb.toString();
   }
}
