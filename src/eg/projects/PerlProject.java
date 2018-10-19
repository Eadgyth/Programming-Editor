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

   /**
    * @param co  the reference to {@link ConsoleOpenable}
    * @param proc  the reference to {@link ProcessStarter}
    */
   public PerlProject(ConsoleOpenable co, ProcessStarter proc) {
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
