package eg.projects;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//--Eadgyth--/
import eg.Projects.ProjectActionsUpdate;
import eg.TaskRunner;
import eg.utils.Dialogs;
import eg.utils.SystemParams;

/**
 * Represents a programming project in C# using .NET SDK.
 * <p>
 * The class supports a single project.
 */
public final class CSharpProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;

   private static final String INVOKE_DOTNET = "dotnet";

   private String csprojFile     = "";
   private String outputFile     = "";
   private String outputFileName = "";
   private String startCmd       = "";

   public CSharpProject(TaskRunner runner) {
      super(ProjectTypes.CSHARP, null);
      this.runner = runner;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions
         .addCompileOptionsInput(COMPILE_OPT_LABEL)
         .addBuildNameInput(OUTPUT_NAME_LABEL)
         .addCmdOptionsInput()
         .addCmdArgsInput()
         .buildWindow();
   }

   @Override
   public boolean hasSetSourceFile() {
      return true;
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      update.enableRun(true, false);
      update.enableCompile(true);
   }

   @Override
   public void compile() {
      if (!generateCsproj()) {
         return;
      }
      runner.runSystemCommand(compileCmd(), "Compile");
   }

   @Override
   public void run() {
      if (!validateFileToRun()) {
         return;
      }
      runner.runSystemCommand(startCmd, "Run");
   }

   @Override
   public String executableDir() {
      return "bin";
   }

   @Override
   protected void setCommandParameters() {
      outputFile = resolveOutputFile();
      outputFileName = buildName(true);
      setStartCmd();
   }

   //
   //--private--/
   //

   /**
    * Generates a minimal .csproj in the project directory
    * based on the current editor settings.
    */
   private boolean generateCsproj() {
      String outputType = resolveOutputType();
      String targetFramework = dotnetTargetFramework();
      if (targetFramework == null) {
         return false;
      }
      csprojFile = projectDir() + File.separator + outputFileName + ".csproj";
      if (new File(csprojFile).exists()) {
         return true;
      }

      StringBuilder xml = new StringBuilder();
      xml.append("<!-- This is an Eadgyth-generated file. It is generated once and will not be overwritten\n");
      xml.append("to preserve manual changes.\n");
      xml.append("Only deletion will recreate the file with the minimal settings in '<PropertyGroup>' on the\n");
      xml.append("next \'Save and compile\' action. -->\n");
      xml.append("<Project Sdk=\"Microsoft.NET.Sdk\">\n");
      xml.append("  <PropertyGroup>\n");
      xml.append("    <OutputType>").append(outputType).append("</OutputType>\n");
      xml.append("    <TargetFramework>").append(targetFramework).append("</TargetFramework>\n");
      xml.append("    <AssemblyName>").append(outputFileName).append("</AssemblyName>\n");
      xml.append("    <Nullable>enable</Nullable>\n");
      xml.append("    <ImplicitUsings>enable</ImplicitUsings>\n");
      xml.append("  </PropertyGroup>\n");
      xml.append("  <ItemGroup>\n");
      xml.append("    <!-- Add references here, e.g.: -->\n");
      xml.append("    <!-- <PackageReference Include=\"PackageName\" Version=\"PackageVersion\" /> -->\n");
      xml.append("    <!-- <Reference Include=\"libs/YourLibrary.dll\" /> -->\n");
      xml.append("  </ItemGroup>\n");
      xml.append("</Project>\n");

      try {
         Path path = Paths.get(csprojFile);
         Files.write(path, xml.toString().getBytes(StandardCharsets.UTF_8));
         return true;
      }
      catch (IOException e) {
         Dialogs.errorMessage(
               csprojFile + "\nThe .csproj file could not be written:\n"
               + e.getMessage(),
               "Project file");

         return false;
      }
   }

   private static String dotnetTargetFramework() {
      try {
         Process p = new ProcessBuilder(INVOKE_DOTNET, "--version").start();
         BufferedReader reader = new BufferedReader(
               new InputStreamReader(p.getInputStream()));

         String version = reader.readLine();
         int major = Integer.parseInt(version.split("\\.")[0]);
         return "net" + major + ".0";
      }
      catch (IOException | NumberFormatException | NullPointerException e) {
         Dialogs.errorMessage(
               "The .NET SDK could not be found.\n"
               + "Make sure 'dotnet' is available on the PATH.",
               "dotnet");

         return null;
      }
   }

   private String resolveOutputType() {
      String opts = compileOptions();
      if (opts.contains("Library")) {
         return "Library";
      }
      else if (opts.contains("WinExe")) {
         return "WinExe";
      }
      else {
         return "Exe";
      }
   }

   private String resolveOutputFile() {
      String name = buildName(true);
      return projectDir() + File.separator + "bin" + File.separator + name + ".dll";
   }


   private String compileCmd() {
      return INVOKE_DOTNET + " build \"" + csprojFile + "\""
            + " --output \"" + projectDir() + File.separator + "bin\"";
   }

   private boolean validateFileToRun() {
     File f = new File(outputFile);
     if (!f.exists()) {
         Dialogs.errorMessage(
               outputFile
               + ":\nThe executable expected based on the current settings cannot be found.",
               "Executable file");

         return false;
      }
      return true;
   }

   private void setStartCmd() {
      StringBuilder sb = new StringBuilder();
      if (SystemParams.IS_WINDOWS) {
         sb.append("cmd.exe /c \"chcp 65001 > nul && ");
      }
      sb.append(INVOKE_DOTNET).append(" ");
      if (!cmdOptions().isEmpty()) {
         sb.append(cmdOptions()).append(" ");
      }
      sb.append("\"").append(outputFile).append("\"");
      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      if (SystemParams.IS_WINDOWS) {
         sb.append("\"");
      }
      startCmd = sb.toString();
   }

   //
   //-- constants for labels/dialogs --//
   //

   private static final String COMPILE_OPT_LABEL =
         "Non-Exe output option (Library or WinExe)";

   private static final String OUTPUT_NAME_LABEL =
         "Name for output file (without extension)";
}
