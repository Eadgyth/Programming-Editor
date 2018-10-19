package eg.projects;

//--Eadgyth--//
import eg.console.*;
import eg.ui.ConsoleOpenable;

/**
 * Represents a programming project in R
 */
public final class RProject extends AbstractProject implements ProjectActions {
   
   private final ConsoleOpenable co;
   private final ProcessStarter proc;

   private String startCmd = "";

   /**
    * @param co  the reference to {@link ConsoleOpenable}
    * @param proc  the reference to {@link ProcessStarter}
    */
   public RProject(ConsoleOpenable co, ProcessStarter proc) {
      super(ProjectTypes.R, true, "R");
      this.co = co;
      this.proc = proc;
   }
   
   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of R script")
         .addCmdOptionsInput()
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
      StringBuilder sb = new StringBuilder("Rscript ");
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
