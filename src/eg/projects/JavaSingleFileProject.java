package eg.projects;

import java.io.File;

//--Eadgyth--/
import eg.Projects.ProjectActionsUpdate;
import eg.TaskRunner;
import eg.javatools.Libraries;
import eg.utils.Dialogs;
import eg.ui.projectsetting.SettingsWindow;

/**
 * Represents a programming project in Java with single file mode
 */
public final class JavaSingleFileProject extends AbstractProject
      implements ProjectCommands {

   private final TaskRunner runner;
   private final Libraries libs = new Libraries();
   private String startCmd = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public JavaSingleFileProject(TaskRunner runner) {
      super(ProjectTypes.JAVA_SINGLE_FILE, "java");
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput(JAVA_FILE_LABEL, true)
         .addLibrariesInput(LIB_LABEL,
               SettingsWindow.InputOptionsBuilder.JAVA_CP_LIB_OPT)
         .addCmdOptionsInput()
         .addCmdArgsInput()
         .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      boolean b = !sourceFileName().isEmpty();
      update.enableRun(b, true);
   }

   @Override
   public void run() {
      if (!libs.errorMessage().isEmpty()) {
         Dialogs.errorMessage(libs.errorMessage(), "Libraries");
         return;
      }
      if (!locateSourceFile()) {
         return;
      }
      runner.runSystemCommand(startCmd);
   }

   @Override
   protected void setCommandParameters() {
      libs.configure(libraries(), projectDir());
      setStartCmd();
   }

   //
   //--private--/

   private void setStartCmd() {
      StringBuilder sb = new StringBuilder("java -Dfile.encoding=UTF-8");

      if (!libs.joined().isEmpty()) {
         sb.append(" -cp \"")
            .append(".")
            .append(File.pathSeparator)
            .append(libs.joined())
            .append("\"");
      }

      if (!cmdOptions().isEmpty()) {
         sb.append(" ")
            .append(cmdOptions());
      }

      sb.append(" ").append(sourceDir() + File.separator + relativeSourceFile());

      if (!cmdArgs().isEmpty()) {
         sb.append(" ")
            .append(cmdArgs());
      }
      startCmd = sb.toString();
   }

   private static final String JAVA_FILE_LABEL =
         "Name of Java file";

   private static final String LIB_LABEL =
         "JAR file (relative to project or absolute)";
}

