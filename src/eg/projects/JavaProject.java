package eg.projects;

import java.io.File;
import java.io.IOException;

import eg.Projects.ProjectActionsUpdate;
//--Eadgyth--/
import eg.TaskRunner;
import eg.javatools.Compilation;
import eg.javatools.JarBuilder;
import eg.javatools.Libraries;
import eg.javatools.ModLibraries;
import eg.ui.projectsetting.SettingsWindow;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.utils.SystemParams;

/**
 * Represents a programming project in Java
 */
public final class JavaProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;
   private final Compilation comp;
   private final JarBuilder jar = new JarBuilder();
   private final Libraries libs = new Libraries();
   private final ModLibraries modLibs = new ModLibraries();

   private String qualifiedMain = "";
   private File mainClassFile = null;
   private boolean isMultiModuleMode = false;
   private boolean moduleNameConflict = false;
   private String classDir = "";
   private String relClassDir = "";
   private String startCmd = "";
   private String jarName = "";
   private String[] nonJavaExt = null;
   private String inclExtErrMsg = "";
   private String jarNameErr = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public JavaProject(TaskRunner runner) {
      super(ProjectTypes.JAVA, "java");
      this.runner = runner;
      comp = new Compilation(runner.consolePrinter(), libs, modLibs);
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addSourceDirInput(SRC_DIR_LABEL)
         .addFileInput(MAIN_FILE_LABEL, true);

      if (SystemParams.IS_JAVA_9_OR_HIGHER) {
         inputOptions.addLibrariesInput(LIB_MOD_LABEL,
               SettingsWindow.InputOptionsBuilder.JAVA_MOD_LIB_OPT)

            .addModuleNameInput(MODULE_LABEL);
      }
      inputOptions.addLibrariesInput(LIB_LABEL,
            SettingsWindow.InputOptionsBuilder.JAVA_CP_LIB_OPT)

         .addExecDirInput(CLASS_DIR_LABEL)
         .addCompileOptionsInput()
         .addFileExtensionsInput(INCLUDED_FILES_LABEL)
         .addBuildNameInput(JAR_NAME_LABEL)
         .addCmdOptionsInput()
         .addCmdArgsInput()
         .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      boolean b = !sourceFileName().isEmpty();
      update.enableCompile(b);
      update.enableRun(b, false);
      if (module().isEmpty()) {
         update.enableBuild(b, "Create executable jar");
      }
      else {
         update.enableBuild(false, null);
      }
   }

   @Override
   public void compile() {
      if (!locateSourceFile()) {
         return;
      }
      if (!createClassDir()) {
         return;
      }
      if (moduleNameConflict) {
         Dialogs.errorMessage(moduleNameConflictMsg, "Project structure");
         return;
      }
      if (!libs.errorMessage().isEmpty()) {
         Dialogs.errorMessage(libs.errorMessage(), "Libraries");
         return;
      }
      if (!modLibs.errorMessage().isEmpty()) {
         Dialogs.errorMessage(modLibs.errorMessage(), "Modules");
         return;
      }
      if (!inclExtErrMsg.isEmpty()) {
         Dialogs.errorMessage(inclExtErrMsg, "Included non-java files");
         return;
      }
      Runnable compile = () -> comp.compile(
            classDir,
            sourceDir(),
            nonJavaExt,
            compileOptions(),
            isMultiModuleMode);


      String initialMsg = "Compile:";
      runner.runWithConsoleOutput(compile, initialMsg, true);
   }

   @Override
   public void run() {
      if (!existsMainClassFile()) {
         return;
      }
      runner.runSystemCommand(startCmd);
   }

   /**
    * Creates an executable jar file
    */
   @Override
   public void build() {
      if (!existsMainClassFile()) {
         return;
      }
      if (!jarNameErr.isEmpty()) {
         Dialogs.errorMessage(jarNameErr, "Jar name");
         return;
      }
      if (!libs.errorMessage().isEmpty()) {
         Dialogs.errorMessage(libs.errorMessage(), "Libraries");
         return;
      }
      if (!inclExtErrMsg.isEmpty()) {
         Dialogs.errorMessage(inclExtErrMsg, "Included non-class files");
         return;
      }
      runner.runBusy(() -> {
         try {
            jar.createClasspathInfo(classDir, libs.forJar());
            boolean created = jar.createJar(jarName, qualifiedMain, classDir,
                  sourceDir(), nonJavaExt);

            StringBuilder msg = new StringBuilder();
            if (created) {
               msg.append(jar.successMessage());
               if (!jar.incudedFilesErr().isEmpty()) {
                  msg.append(jar.incudedFilesErr()).append(".");
               }
               Dialogs.infoMessage(msg.toString(), null);
            }
            else {
               msg.append(jar.errorMessage()).append(".");
               Dialogs.errorMessage(msg.toString(), null);
            }
         }
         catch (IOException | InterruptedException e) {
            FileUtils.log(e);
            Thread.currentThread().interrupt();
         }
      });
   }

   @Override
   protected void setCommandParameters() {
      setQualifiedMain();
      setNonJavaExtensions();
      libs.configure(libraries(), projectDir());
      if (SystemParams.IS_JAVA_9_OR_HIGHER) {
         modLibs.configure(libModules(), projectDir());
      }
      setClassDir();
      moduleNameConflict = false;
      isMultiModuleMode = false;
      if (module().isEmpty()) {
         setCpStartCmd();
         isMultiModuleMode = false;
      }
      else {
         setModStartCmd();
         File moduleTestDir = new File(sourceDir(), module());
         File moduleInfoTestFile = new File(sourceDir(), "module-info.java");
         if (moduleTestDir.exists() && !moduleInfoTestFile.exists()) {
            isMultiModuleMode = true;
         }
         if (!moduleTestDir.exists() && !moduleInfoTestFile.exists()) {
            moduleNameConflict = true;
         }

      }
      setMainClassFile();
      setJarName();
   }

   //
   //--private--/
   //

   private void setQualifiedMain() {
      StringBuilder sb = new StringBuilder();
      if (!namespace().isEmpty()) {
         sb.append(namespace()).append(".");
      }
      sb.append(sourceFileName());
      qualifiedMain = sb.toString();
   }

   private void setClassDir() {
      relClassDir = executableDir();
      classDir = relClassDir.isEmpty() ?
            projectDir() : projectDir() + File.separator + relClassDir;
   }

   private void setCpStartCmd() {
      StringBuilder sb = new StringBuilder("java -Dfile.encoding=UTF-8");

      if (!relClassDir.isEmpty() || !libs.joined().isEmpty()) {
         sb.append(" -cp \"");

         if (!relClassDir.isEmpty()) {
            sb.append(relClassDir);
         }

         if (!libs.joined().isEmpty()) {

            if (relClassDir.isEmpty()) {
                sb.append(".");
            }
            sb.append(File.pathSeparator)
               .append(libs.joined());
         }
         sb.append("\"");
      }

      if (!cmdOptions().isEmpty()) {
         sb.append(" ").append(cmdOptions());
      }

      sb.append(" ").append(qualifiedMain);

      if (!cmdArgs().isEmpty()) {
         sb.append(" ").append(cmdArgs());
      }
      startCmd = sb.toString();
   }

   private void setModStartCmd() {
      StringBuilder sb = new StringBuilder("java -Dfile.encoding=UTF-8");

      sb.append(" -p \"")
         .append(classDir); // dir in project or the project root

      if (!modLibs.joined().isEmpty()) {
         sb.append(File.pathSeparator)
            .append(modLibs.joined());
      }
      sb.append("\"");

      if (!libs.joined().isEmpty()) {
         sb.append(" ")
            .append("-cp \"")
            .append(libs.joined())
            .append("\"");
      }

      if (!cmdOptions().isEmpty()) {
         sb.append(" ")
            .append(cmdOptions());
      }

      sb.append(" -m ")
         .append(module())
         .append("/")
         .append(qualifiedMain);

      if (!cmdArgs().isEmpty()) {
         sb.append(" ")
            .append(cmdArgs());
      }
      startCmd = sb.toString();
   }

   private void setMainClassFile() {
      StringBuilder sb = new StringBuilder(projectDir() + File.separator);
      if (!relClassDir.isEmpty()) {
         sb.append(relClassDir).append(File.separator);
      }
      if (!module().isEmpty() && isMultiModuleMode) {
         sb.append(module()).append(File.separator);
      }
      if (!namespaceDir().isEmpty()) {
         sb.append(namespaceDir()).append(File.separator);
      }
      sb.append(sourceFileName()).append(".class");
      mainClassFile = new File(sb.toString());
   }

   private boolean createClassDir() {
      boolean b = true;
      if (classDir.length() > projectDir().length()) {
         File f = new File(classDir);
         f.mkdirs();
         if (!f.exists() || !f.isDirectory()) {
            b = false;
            StringBuilder sb = new StringBuilder();
            sb.append(relClassDir)
               .append("\nCould not create the destination directory")
               .append(" for class files in the project directory.");

            Dialogs.errorMessage(
                 sb.toString(), "Classes directory");
         }
      }
      return b;
   }

   private void setJarName() {
      jarNameErr = "";
      String name = buildName(true);
      File f = new File(name);
      if (!f.isAbsolute()) {
         name = projectDir() + File.separator + name;
         f = new File(name);
      }
      if (f.isDirectory()) {
         jarNameErr = f.getPath()
               + "\n\nThe specified jar name is a directory"
               + " (to still use this name add the .jar extension in the settings).";
      }
      if (!f.getParentFile().isDirectory()) {
          jarNameErr = f.getParentFile()
               + "\n\nThe destination directory for the jar file cannot be found.";
      }
      name = FileUtils.addExtension(f.getPath(), ".jar");
      jarName = name;
   }

   private boolean existsMainClassFile() {
      boolean exists = mainClassFile.exists();
      if (!exists) {
         StringBuilder sb = new StringBuilder();
         sb.append(qualifiedMain).append(".class ")
            .append("\nA compiled main class with this (qualified) name ")
            .append("cannot be found.");

         Dialogs.warnMessage(sb.toString());
      }
      return exists;
   }

   private void setNonJavaExtensions() {
      inclExtErrMsg = "";
      nonJavaExt = fileExtensions();
      if (nonJavaExt.length > 0) {
         for (String s : nonJavaExt) {
            if (!s.startsWith(".")) {
               inclExtErrMsg = wrongExtMessage(s);
               break;
            }
         }
      }
   }

   private String wrongExtMessage(String ext) {
      StringBuilder sb = new StringBuilder();
      sb.append(ext).append(" cannot be used as extension for included files.\n")
         .append("An extension must begin with a period.");

      return sb.toString();
   }

   //--Messages:

   private String moduleNameConflictMsg =
         "<html>" +
         "The project structure could not be determined.One of two module modes may be defined:<br>" +
         "<ul>" +
         "<li><b>Single-module:</b> a \"module-info\" is saved directly in the source directory.</li>" +
         "<li><b>Multi-module:</b> a \"module-info\" is saved in module directory that is a direct<br>" +
         "subdirectory of the source directory and has a name matching the module name.</li>" +
         "</html>";

   //--Labels for settings:
   //
   private static final String SRC_DIR_LABEL =
         "Source directory name (if present)";

   private static final String MAIN_FILE_LABEL =
         "Name of main Java file (may be qualified)";

   private static final String MODULE_LABEL =
         "Main module name (for modular projects only)";

   private static final String CLASS_DIR_LABEL =
         "Destination directory name for class files";

   private static final String JAR_NAME_LABEL =
         "Name or pathname for JAR file";

   private static final String LIB_LABEL =
         "JAR file (relative to project root or absolute)";

   private static final String LIB_MOD_LABEL =
         "JAR file or parent directory (relative to project root or absolute)";

   private static final String INCLUDED_FILES_LABEL =
         "Extensions of included non-Java files";
}
