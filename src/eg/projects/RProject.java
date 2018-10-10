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

   RProject(ConsoleOpenable co, ProcessStarter proc) {
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
      String main = getMainFileName() + getSourceExtension();
      StringBuilder sb = new StringBuilder("Rscript ");
      if (!getSourceDirName().isEmpty()) {
         sb.append(getSourceDirName()).append("/");
      }
      if (!getNamespace().isEmpty()) {
         sb.append(getNamespace()).append("/");
      }
      sb.append(getMainFileName()).append(getSourceExtension());
      if (getCmdArgs().length() > 0) {
         sb.append(" ").append(getCmdArgs());
      }
      startCmd = sb.toString();
   }
}
