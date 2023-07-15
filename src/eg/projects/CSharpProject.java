package eg.projects;

import java.io.File;
import java.util.List;

//--Eadgyth--/
import eg.TaskRunner;
import eg.Projects.ProjectActionsUpdate;
import eg.javatools.FilesFinder;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.utils.SystemParams;

/**
 * Represents a programming project in C#.
 * <p>
 * Requirements:<br>
 * For Windows it is required that the PATH to the csc compiler,
 * e.g.'C:\Windows\Microsoft.NET\Framework64\v[?]\', is set as
 * environment variable. For Unix/Mac the assumptions are that
 * Mono is used to compile and run C# code and the paths to Mono
 * mcs compiler and mono executable are set permanently as well.
 */
public final class CSharpProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;
   private final FilesFinder finder = new FilesFinder();
   private final String invokeCompiler;
   private final String invokeRun;
   //
   // has no file extension if:
   // (1) no output name has been entered in which case the name
   // of the specified source file is the output name
   // (2) a name (or pathname) for the output has been entered
   // but the extension left out
   private String preOutputFile = "";
   //
   // if the extension is missing in 'preOutputFile', it is added
   // depending on compilation target
   private String outputFile = "";
   //
   // last name for display purposes
   private String outputFileName = "";

   private String startCmd = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public CSharpProject(TaskRunner runner) {
      super(ProjectTypes.CSHARP, "cs", File.separator);
      this.runner = runner;
      if (SystemParams.IS_WINDOWS) {
         invokeCompiler = "csc.exe";
         invokeRun = "cmd.exe /c";
      }
      else {
         invokeCompiler = "mcs";
         invokeRun = "mono";
      }
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addSourceDirInput(SRC_DIR_LABEL)
         .addFileInput(FILE_LABEL, true)
         .addLibrariesInput(LIB_LABEL)
         .addExecDirInput(OUTPUT_DIR_LABEL)
         .addCompileOptionsInput(COMPILE_OPT_LABEL)
         .addBuildNameInput(OUTPUT_NAME_LABEL)
         .addCmdOptionsInput()
         .addCmdArgsInput()
         .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      update.enableRun(false); // false means not to save files before run
      update.enableCompile();
   }

   @Override
   public void compile() {
      if (!locateSourceFile()) {
         return;
      }
      if (!createExecDir()) {
         return;
      }
      if (!validatePreOutputFile()) {
         return;
      }
      runner.runSystemCommand(compileCmd(), "Compile");
   }

   @Override
   public void run() {
      if (!validateFileToRun()) {
         return;
      }
      String intialMsg = "Run " + outputFileName;
      runner.runSystemCommand(startCmd, intialMsg);
   }

   @Override
   protected void setCommandParameters() {
      preOutputFile = parsedOutputFile();
      //
      // both -target: and and short form -t:
      // by chance contain 't:'
      if (compileOptions().contains("t:library")) {
         outputFile = FileUtils.addExtension(preOutputFile, ".dll");
      }
      else if (compileOptions().contains("t:module")) {
         outputFile = FileUtils.addExtension(preOutputFile, ".netmodule");
      }
      else {
         outputFile = FileUtils.addExtension(preOutputFile, ".exe");
      }
      outputFileName = new File(outputFile).getName();
      setStartCmd();
   }

   //
   //--private--/
   //

   private String parsedOutputFile() {
      String name;
      String buildName = buildName(false);
      if (buildName.isEmpty()) {
         name = outputPathInProject(sourceFileName());
      }
      else {
         File f = new File(buildName);
         if (f.isAbsolute()) {
            name = f.getPath();
         }
         else {
            f = new File(projectDir(), buildName);
            if (f.getParent().equals(projectDir())) {
               //
               // just a name is given
               name = outputPathInProject(buildName);
            }
            else {
               //
               // a relative pathname is given
               name = projectDir() + File.separator
                     + buildName.replace("/", File.separator);
            }
         }
      }
      return name;
   }

   private String outputPathInProject(String name) {
      if (executableDir().isEmpty() || name.equals(executableDir())) {
         return projectDir() + File.separator + name;
      }
      else {
         return projectDir() + File.separator + executableDir()
               + File.separator + name;
      }
   }

   private boolean createExecDir() {
      boolean b = true;
      if (!executableDir().isEmpty()) {
         File dir = new File(projectDir(), executableDir());
         dir.mkdir();
         if (!dir.exists() || !dir.isDirectory()) {
            b = false;
            Dialogs.errorMessage(executableDir()
                  + CREATE_EXEC_DIR_ERR, "Output directory");
         }
      }
      return b;
   }

   private boolean validatePreOutputFile() {
      String msg = "";
      File f = new File(preOutputFile);
      if (f.isDirectory()) {
         msg = f.getPath() + OUTPUT_IS_DIR_ERR;
      }
      if (!f.getParentFile().isDirectory()) {
         msg = f.getParent() + OUTPUT_DIR_NOT_FOUND_ERR;
      }
      if (!msg.isEmpty()) {
         Dialogs.errorMessage(msg, "Output name");
         return false;
      }
      return true;
   }

   private String compileCmd() {
      StringBuilder sb = new StringBuilder();
      sb.append(invokeCompiler);
      if (SystemParams.IS_WINDOWS) {
         sb.append(" -nologo");
      }
      sb.append(" \"-out:").append(outputFile).append("\"");
      if (!libraries().isEmpty()) {
         for (String l : libraries()) {
            sb.append(" -r:").append("\"").append(l).append("\"");
         }
      }
      if (!compileOptions().isEmpty()) {
         sb.append(" ").append(compileOptions());
      }
      List<File> files = finder.filteredFiles(sourceDir(), sourceExtension(), "", "");
      for (File f : files) {
         sb.append(" \"").append(f.getPath()).append("\"");
      }
      return sb.toString();
   }

   private boolean validateFileToRun() {
      String msg = "";
      if (!outputFile.endsWith(".exe")) {
         msg = TARGET_ERR;
      }
      else {
         //
         // Limitation:
         // running a program compiled to a location outside
         // the project directory (by entering an absolute
         // pathname for the output file) is disabled because
         // the process would nevertheless read or write data
         // in the project directory. Compiling to an outside
         // location is intended mainly to compile a DLL to
         // another "executable" project that uses/tries it.
         File f = new File(outputFile);
         boolean inProj = isInProject(f.getParent());
         if (!f.exists()) {
            if (inProj) {
               msg = outputFileName + EXE_NOT_FOUND_ERR;
            }
            else {
               msg = outputFile + EXE_NOT_FOUND_OUTSIDE_PROJ_ERR;
            }
         }
         else if (!inProj) {
            msg = outputFile + EXE_OUTSIDE_PROJ_ERR;
         }
      }
      if (!msg.isEmpty()) {
         Dialogs.errorMessage(msg, "Executable output file");
         return false;
      }
      return true;
   }

   private void setStartCmd() {
      StringBuilder sb = new StringBuilder();
      sb.append(invokeRun).append(" ");
      if (!cmdOptions().isEmpty()) {
         sb.append(cmdOptions()).append(" ");
      }
      sb.append("\"").append(outputFile).append("\"");
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      startCmd = sb.toString();
   }

   //
   //-- constants for labels/dialogs --//
   //

   private static final String FILE_LABEL =
         "Name of (main) source file";

   private static final String SRC_DIR_LABEL =
         "Source subdirectory (if present)";

   private static final String LIB_LABEL =
         "DLL files";

   private static final String OUTPUT_DIR_LABEL =
         "Destination subdirectory for output file";

   private static final String COMPILE_OPT_LABEL =
         "Compiler options (e.g., -t:winexe or -t:library)";

   private static final String OUTPUT_NAME_LABEL =
         "Name or pathname for output file";

   private static final String CREATE_EXEC_DIR_ERR =
         "\nThe specified output directory cannot be created within the project.";

   private static final String OUTPUT_IS_DIR_ERR =
         "\nThe specified output file is a directory.\nTo still use this name"
               + " the file extension may be added in the settings.";

   private static final String OUTPUT_DIR_NOT_FOUND_ERR =
         "\nThe destination directory for the output file cannot be found.";

   private static final String TARGET_ERR =
         "The program cannot be run with currently set target option.";

   private static final String EXE_NOT_FOUND_ERR =
         "\nThe currently set executable file cannot be found.";

   private static final String EXE_NOT_FOUND_OUTSIDE_PROJ_ERR =
         EXE_NOT_FOUND_ERR
               + "\nAlso note that an executable compiled to a location outside"
               + " of the project directory cannot be run within the editor.";

   private static final String EXE_OUTSIDE_PROJ_ERR =
         "\nThe currently set executable file is located outside of the"
               + " project directory and\ncannot be run within the editor.";
}
