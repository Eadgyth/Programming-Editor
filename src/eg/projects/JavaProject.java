package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--/
import eg.TaskRunner;
import eg.javatools.*;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.Projects.ProjectActionsUpdate;

/**
 * Represents a programming project in Java
 */
public final class JavaProject extends AbstractProject implements ProjectCommands {

   private final TaskRunner runner;
   private final Compilation comp;
   private final JarBuilder jar = new JarBuilder();
   private final Libraries libs = new Libraries();

   private String startCommand = "";
   private String qualifiedMain = "";
   private File mainClassFile = null;
   private String jarName = "";
   private String[] nonJavaExt = null;
   private String inclExtErrMsg = "";
   private String jarNameErr = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public JavaProject(TaskRunner runner) {
      super(ProjectTypes.JAVA, true, "java");
      this.runner = runner;
      comp = new Compilation(runner.consolePrinter());
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of main class file")
            .addSourceDirInput()
            .addExecDirInput()
            .addLibrariesInput(LIB_LABEL)
            .addCmdOptionsInput()
            .addCmdArgsInput()
            .addCompileOptionInput("Compiler options")
            .addExtensionsInput("Extensions of included non-Java files")
            .addBuildNameInput(JAR_NAME_LABEL)
            .buildWindow();
   }

   @Override
   public void enable(ProjectActionsUpdate update) {
      update.enableRun(false);
      update.enableCompile();
      update.enableBuild("Create executable jar");
   }

   @Override
   public void compile() {
      if (!locateMainFile()) {
         return;
      }
      if (!libs.errorMessage().isEmpty()) {
         Dialogs.errorMessage(libs.errorMessage(), "Libraries");
         return;
      }
      if (!inclExtErrMsg.isEmpty()) {
         Dialogs.errorMessage(inclExtErrMsg, "Included non-java files");
         return;
      }
      String sourceDir = inProjectDir(sourceDirName());
      String classDir = inProjectDir(executableDirName());
      if (!executableDirName().isEmpty()) {
         new File(classDir).mkdirs();
      }
      Runnable compile = () -> comp.compile(
            classDir,
            sourceDir,
            nonJavaExt,
            libs.joinedAbsPaths(),
            compileOptions());

      String initialMsg = "Compile:";
      runner.runWithConsoleOutput(compile, initialMsg, true);
   }

   @Override
   public void run() {
      if (!existsMainClassFile()) {
         return;
      }
      runner.runSystemCommand(startCommand);
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
         String classDir = inProjectDir(executableDirName());
         String sourceDir = inProjectDir(sourceDirName());
         if (!libs.forJar().isEmpty()) {
            jar.createClasspathInfo(classDir, libs.forJar());
         }
         try {
            boolean created = jar.createJar(jarName, qualifiedMain, classDir,
                  sourceDir, nonJavaExt);

            StringBuilder msg = new StringBuilder();
            if (created) {
               msg.append(jar.successMessage()).append("\n");
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
         }
      });
   }

   @Override
   protected void setCommandParameters() {
      setQualifiedMain();
      setNonJavaExtensions();
      libs.configureLibraries(libraries(), projectPath());
      setStartCommand();
      setMainClassFile();
      setJarName();
   }

   //
   //--private--/
   //

   private void setQualifiedMain() {
      StringBuilder sb = new StringBuilder();
      if (!namespace().isEmpty()) {
         sb.append(namespace().replaceAll("[/\\\\]", ".")).append(".");
      }
      sb.append(mainFileName());
      qualifiedMain = sb.toString();
   }

   private void setStartCommand() {
      StringBuilder sb = new StringBuilder("java");
      if (!executableDirName().isEmpty() || !libs.joined().isEmpty()) {
         sb.append(" -cp \"");
         if (!executableDirName().isEmpty()) {
            sb.append(executableDirName());
         }
         if (!libs.joined().isEmpty()) {
            if (executableDirName().isEmpty()) {
                sb.append(".");
             }
             sb.append(File.pathSeparator);
             sb.append(libs.joined());
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
      startCommand = sb.toString();
   }

   private void setMainClassFile() {
      StringBuilder sb = new StringBuilder(projectPath() + "/");
      if (!executableDirName().isEmpty()) {
         sb.append(executableDirName()).append("/");
      }
      if (!namespace().isEmpty()) {
         sb.append(namespace()).append("/");
      }
      sb.append(mainFileName()).append(".class");
      mainClassFile = new File(sb.toString());
   }

   private void setJarName() {
      jarNameErr = "";
      String name = buildName();
      File f = new File(name);
      if (!f.isAbsolute()) {
         name = inProjectDir(name);
         f = new File(name);
      }
      if (f.isDirectory()) {
         jarNameErr
                = f.getPath()
                + "\n\nA directory cannot be used as name for the jar file.";
      }
      if (!f.getParentFile().isDirectory()) {
          jarNameErr
                = f.getParentFile()
                + "\n\nThe location for the jar file cannot be found.";
      }
      name = FileUtils.addExtension(f.getPath(), ".jar");
      jarName = name;
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

   private boolean existsMainClassFile() {
      boolean exists = mainClassFile.exists();
      if (!exists) {
         Dialogs.warnMessage(
            "A compiled main class file \'"
            + mainFileName()
            + ".class\' could not be found");
      }
      return exists;
   }

   private String inProjectDir(String subDir) {
      return (subDir.isEmpty()) ? projectPath() : projectPath() + "/" + subDir;
   }

   private String wrongExtMessage(String ext) {
      return
         "\'"
         + ext
         + "\' cannot be used as extension for included files.\n"
         + "An extension must begin with a period.";
   }

   private final String JAR_NAME_LABEL =
         "Name or pathname for jar file (relative to project or absolute)";

   private final String LIB_LABEL =
         "Folder or jar file (path relative to project or absolute):";
}
