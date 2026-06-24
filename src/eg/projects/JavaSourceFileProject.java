package eg.projects;

import java.io.File;

//--Eadgyth--/
import eg.Projects.ProjectActionsUpdate;
import eg.TaskRunner;
import eg.javatools.Libraries;
import eg.javatools.ModLibraries;
import eg.ui.projectsetting.SettingsWindow;
import eg.utils.Dialogs;
import eg.utils.SystemParams;

/**
 * Represents a programming project in Java with source file launcher
 * mode.
 *
 * This class does not distinguish between a single-file source program
 * (available since Java 11+) and a multi-file source program (available
 * since Java 22+). However, it disinguishes between classpath and
 * module path if the Java version ist 22+. Module path is assumed based
 * on the presence of a module-info in the path to the main file.
 */
public final class JavaSourceFileProject extends AbstractProject
      implements ProjectCommands {

   private final TaskRunner runner;
   private final Libraries libs = new Libraries();
   private final ModLibraries modLibs = new ModLibraries();
   private String startCmd = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public JavaSourceFileProject(TaskRunner runner) {
      super(ProjectTypes.JAVA_SOURCE_FILE, "java");
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput(JAVA_FILE_LABEL, true);
      if (SystemParams.IS_JAVA_22_OR_HIGHER) {
         inputOptions.addLibrariesInput(MOD_LIB_LABEL,
               SettingsWindow.InputOptionsBuilder.JAVA_MOD_LIB_OPT)
               
            .addLibrariesInput(LIB_LABEL,
                  SettingsWindow.InputOptionsBuilder.JAVA_CP_LIB_OPT);
      }
      else {
         inputOptions.addLibrariesInput(LIB_LABEL,
               SettingsWindow.InputOptionsBuilder.GENERAL_LIB_OPT);
      }
      inputOptions.addCmdOptionsInput()
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
      if (!modLibs.errorMessage().isEmpty()) {
         Dialogs.errorMessage(modLibs.errorMessage(), "Modules");
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
      modLibs.configure(modLibraries(), projectDir());
      if (SystemParams.IS_JAVA_22_OR_HIGHER
            && isModular(projectDir(), relativeSourceFile())) {

         setModStartCmd();

      }
      else {
         setCpStartCmd();
      }
   }

   //
   //--private--/

   private void setCpStartCmd() {
      StringBuilder sb = new StringBuilder(UTF_8_FLAGS);

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

      sb.append(" \"")
         .append(relativeSourceFile())
         .append("\"");

      if (!cmdArgs().isEmpty()) {
         sb.append(" ")
            .append(cmdArgs());
      }
      startCmd = sb.toString();
   }

   private void setModStartCmd() {
      StringBuilder sb = new StringBuilder(UTF_8_FLAGS);
      
      if (!libs.joined().isEmpty()) {
         sb.append(" -cp \"")
           .append(libs.joined())
           .append("\"");
      }

      if (!modLibs.joined().isEmpty()) {
         sb.append(" -p \"")
            .append(modLibs.joined())
            .append("\"");
      }

      if (!cmdOptions().isEmpty()) {
         sb.append(" ")
            .append(cmdOptions());
      }

      sb.append(" \"")
         .append(relativeSourceFile())
         .append("\"");

      if (!cmdArgs().isEmpty()) {
         sb.append(" ")
            .append(cmdArgs());
      }
      startCmd = sb.toString();
   }

   private boolean isModular(String root, String path) {
      File dir = new File(root, path).getParentFile();
      File rootDir = new File(root);
      while (dir != null && !dir.equals(rootDir)) {
         if (new File(dir, "module-info.java").isFile()) {
            return true;
         }
         dir = dir.getParentFile();
      }
      return dir != null && new File(dir, "module-info.java").isFile();
   }

   private static final String UTF_8_FLAGS =
         "java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8";

   private static final String JAVA_FILE_LABEL =
         "Name of (main) Java file";

   private static final String LIB_LABEL =
         "JAR file (relative to project or absolute)";

   private static final String MOD_LIB_LABEL =
         "JAR file or parent directory (relative to project or absolute)";
}

