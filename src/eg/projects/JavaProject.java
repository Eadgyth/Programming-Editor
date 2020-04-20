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
   private String classDir = "";
   private String relClassDir = "";
   private String jarName = "";
   private String[] nonJavaExt = null;
   private String inclExtErrMsg = "";
   private String jarNameErr = "";

   /**
    * @param runner  the reference to TaskRunner
    */
   public JavaProject(TaskRunner runner) {
      super(ProjectTypes.JAVA, "java", ".");
      this.runner = runner;
      comp = new Compilation(runner.consolePrinter());
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions
            .addSourceDirInput()
            .addFileInput(MAIN_FILE_LABEL)
            .addExecDirInput(CLASS_DIR_LABEL)
            .addLibrariesInput(LIB_LABEL)
            .addCmdOptionsInput()
            .addCmdArgsInput()
            .addCompileOptionsInput()
            .addFileExtensionsInput(INCLUDED_FILES_LABEL)
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
      if (!locateSourceFile()) {
         return;
      }
      if (!createClassDir()) {
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
      Runnable compile = () -> comp.compile(
            classDir,
            sourceDir(),
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
         if (!libs.forJar().isEmpty()) {
            jar.createClasspathInfo(classDir, libs.forJar());
         }
         try {
            boolean created = jar.createJar(jarName, qualifiedMain, classDir,
                  sourceDir(), nonJavaExt);

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
            Thread.currentThread().interrupt();
         }
      });
   }

   @Override
   protected void setCommandParameters() {
      setQualifiedMain();
      setNonJavaExtensions();
      libs.configureLibraries(libraries(), projectDir());
      setClassDir();
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

   private void setStartCommand() {
      StringBuilder sb = new StringBuilder("java");
      if (!relClassDir.isEmpty() || !libs.joined().isEmpty()) {
         sb.append(" -cp \"");
         if (!relClassDir.isEmpty()) {
            sb.append(relClassDir);
         }
         if (!libs.joined().isEmpty()) {
            if (relClassDir.isEmpty()) {
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
      StringBuilder sb = new StringBuilder(projectDir() + "/");
      if (!relClassDir.isEmpty()) {
         sb.append(relClassDir).append("/");
      }
      if (!namespaceDir().isEmpty()) {
         sb.append(namespaceDir()).append("/");
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
            Dialogs.errorMessage(
                 relClassDir
                 + "\nCould not create the destination directory"
                 + " for class files in the project directory.",
                 "Classes directory");
         }
      }
      return b;
   }

   private void setJarName() {
      jarNameErr = "";
      String name = buildName();
      File f = new File(name);
      if (!f.isAbsolute()) {
         name = projectDir() + File.separator + name;
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
            + qualifiedMain
            + ".class\' could not be found");
      }
      return exists;
   }

   private String wrongExtMessage(String ext) {
      return
         "\'"
         + ext
         + "\' cannot be used as extension for included files.\n"
         + "An extension must begin with a period.";
   }

   private static final String MAIN_FILE_LABEL =
         "Name of main Java file";

   private static final String CLASS_DIR_LABEL =
         "Destination directory for class files (relative to project)";

   private static final String JAR_NAME_LABEL =
         "Name or pathname for jar file (relative to project or absolute)";

   private static final String LIB_LABEL =
         "Directory or jar file (relative to project or absolute):";

   private static final String INCLUDED_FILES_LABEL =
         "Extensions of included non-Java files";
}
