package eg.projects;

import java.io.File;
import java.io.IOException;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.javatools.*;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.ui.ProjectActionsUpdate;
import eg.ui.ConsoleOpener;

/**
 * Represents a programming project in Java
 */
public final class JavaProject extends AbstractProject implements ProjectActions {

   private final ProcessStarter proc;
   private final Console cons;
   private final ConsoleOpener opener;
   private final Runnable fileTreeUpdate;
   private final Compilation comp;
   private final JarBuilder jar;

   private String startCommand = "";
   private String qualifiedMain = "";
   private File mainClassFile = null;
   private String[] nonJavaExt = null;
   private boolean isNonJavaExtTested = true;

   /**
    * @param cons  the Console
    * @param opener  the ConoleOpener
    * @param fileTreeUpdate  the updating of the file tree
    */
   public JavaProject(Console cons, ConsoleOpener opener, Runnable fileTreeUpdate) {
      super(ProjectTypes.JAVA, true, "java");
      this.cons = cons;
      this.opener = opener;
      this.fileTreeUpdate = fileTreeUpdate;
      proc = cons.processStarter();
      comp = new Compilation(cons);
      jar = new JarBuilder();
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileInput("Name of main class file")
            .addSourceDirInput()
            .addExecDirInput()
            .addCmdArgsInput()
            .addCompileOptionInput("Xlint compiler option")
            .addExtensionsInput("Extensions of included non-Java files")
            .addBuildNameInput("jar file")
            .buildWindow();
   }

   @Override
   public void enableActions(ProjectActionsUpdate update) {
      update.enable(true, true, true, "Create jar");
   }

   @Override
   public void compile() {
      if (!cons.canPrint()) {
         return;
      }
      if (!locateMainFile()) {
         return;
      }
      if (!isNonJavaExtCorrect()) {
         cons.clear();
         return;
      }
      opener.open();
      cons.clear();
      cons.printBr("Compile " + projectName());
      EventQueue.invokeLater(() -> {
         comp.compile(projectPath(), executableDirName(),
               sourceDirName(), nonJavaExt, compileOption());

         cons.toTop();
      });
      EventQueue.invokeLater(fileTreeUpdate);
   }

   @Override
   public void run() {
      if (!existsMainClassFile()) {
         return;
      }
      opener.open();
      proc.startProcess(startCommand);
   }

   /**
    * Creates an executable jar file
    */
   @Override
   public void build() {
      if (!existsMainClassFile() || !isNonJavaExtCorrect()) {
         return;
      }
      EventQueue.invokeLater(() -> {
         String jarName = buildName();
         if (jarName.length() == 0) {
            jarName = mainFileName();
         }
         try {
            boolean created = jar.createJar(projectPath(), jarName,
                  qualifiedMain, executableDirName(), sourceDirName(),
                  nonJavaExt);

            if (created) {
               StringBuilder msg = new StringBuilder();
               msg.append(jar.successMessage()).append(".\n");
               if (!jar.incudedFilesErr().isEmpty()) {
                  msg.append(jar.incudedFilesErr()).append(".");
               }
               Dialogs.infoMessage(msg.toString(), null);
            }
            else {
               Dialogs.errorMessage(jar.errorMessage() + ".", null);
            }
         }
         catch (IOException | InterruptedException e) {
            FileUtils.log(e);
         }
      });
      EventQueue.invokeLater(fileTreeUpdate);
   }

   @Override
   protected void setCommandParameters() {
      setQualifiedMain();
      setStartCommand();
      setMainClassFile();
      setNonJavaExtensions();
   }

   //
   //--private--/
   //

   private void setQualifiedMain() {
      StringBuilder sb = new StringBuilder();
      if (!namespace().isEmpty()) {
         sb.append(FileUtils.dottedFileSeparators(namespace())).append(".");
      }
      sb.append(mainFileName());
      qualifiedMain = sb.toString();
   }

   private void setStartCommand() {
      StringBuilder sb = new StringBuilder("java ");
      if (!executableDirName().isEmpty()) {
         sb.append("-cp ").append(executableDirName()).append(" ");
      }
      sb.append(qualifiedMain);
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

   private void setNonJavaExtensions() {
      nonJavaExt = fileExtensions();
      isNonJavaExtTested = nonJavaExt == null;
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

   private boolean isNonJavaExtCorrect() {
      if (isNonJavaExtTested) {
         return true;
      }
      boolean ok = true;
      if (sourceDirName().length() == 0 || executableDirName().length() == 0) {
         nonJavaFilesNotSupportedMessage();
         ok = false;
      }
      else {
         for (String s : fileExtensions()) {
            if (!s.startsWith(".")) {
               wrongExtMessage(s);
               ok = false;
               break;
            }
         }
      }
      isNonJavaExtTested = ok;
      return ok;
   }

   private void wrongExtMessage(String ext) {
      Dialogs.errorMessage(
         "\'"
         + ext
         + "\' cannot be used as extension for included non-Java files.\n"
         + "An extension must begin with a period.",
         null);
   }

   private void nonJavaFilesNotSupportedMessage() {
      Dialogs.errorMessage(
         "Including non-Java files is supported only if the project\n"
         + "contains separate directories for source files and for class files.",
         null);
   }
}
