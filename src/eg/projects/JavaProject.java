package eg.projects;

import java.io.IOException;
import java.awt.EventQueue;

//--Eadgyth--//
import eg.console.*;
import eg.javatools.*;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.ui.ConsoleOpenable;;

/**
 * Represents a programming project in Java
 */
public final class JavaProject extends AbstractProject implements ProjectActions {

   private final ConsoleOpenable co;
   private final ProcessStarter proc;
   private final Console cons;
   private final Compilation comp;
   private final JarBuilder jar;

   private String startCommand = "";
   private String qualifiedMain = "";
   private String[] nonJavaExt = null;
   private boolean isNonJavaExtTested = true;

   /**
    * @param co  the reference to {@link ConsoleOpenable}
    * @param cons  the reference to {@link Console}
    */
   public JavaProject(ConsoleOpenable co, Console cons) {
      super(ProjectTypes.JAVA, true, "java");
      this.co = co;
      this.cons = cons;
      proc = cons.processStarter();
      comp = new Compilation(cons);
      jar = new JarBuilder(cons);
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
   public void compile() {
      if (!cons.canPrint()) {
         return;
      }
      if (!isNonJavaExtCorrect()) {
         cons.clear();
         return;
      }
      cons.clear();
      cons.printBr("Compile " + projectName());
      EventQueue.invokeLater(() -> {
         comp.compile(projectPath(), executableDirName(),
               sourceDirName(), nonJavaExt, compileOption());

         cons.toTop();
         if (!co.isConsoleOpen()) {
            boolean needConfirm = false;
            StringBuilder msg = new StringBuilder();
            if (!comp.isCompiled()) {
               msg.append("Compilation failed.\n");
               msg.append(comp.firstCompileErr()).append(".\n");
               needConfirm = true;
            }
            else {
               msg.append("Compilation successful.\n");
            }
            if (comp.isNonErrMessage()) {
               msg.append("Warning: One or more compiler messages are present.\n");
               needConfirm = true;
            }
            if (!comp.copyFilesErr().isEmpty()) {
               msg.append(comp.copyFilesErr()).append(".\n");
            }
            if (!comp.optionErr().isEmpty()) {
               msg.append(comp.optionErr()).append(".\n");
            }
            if (needConfirm) {
               msg.append("\nOpen the console window to view messages?\n");
               int res = Dialogs.warnConfirmYesNo(msg.toString());
               if (0 == res) {
                  co.openConsole();
               }
            }
            else {
               Dialogs.infoMessage(msg.toString(), null);
            }
         }
      });
   }

   @Override
   public void runProject() {
      if (!existsMainClassFile()) {
         return;
      }
      if (!co.isConsoleOpen()) {
         co.openConsole();
      }
      proc.startProcess(startCommand);
   }

   /**
    * Creates an executable jar file
    */
   @Override
   public void build() {
      if (!cons.canPrint()) {
         return;
      }
      if (!existsMainClassFile() || !isNonJavaExtCorrect()) {
         cons.clear();
         return;
      }
      cons.clear();
      EventQueue.invokeLater(() -> {
         String jarName = buildName();
         if (jarName.length() == 0) {
            jarName = mainFileName();
         }
         try {
            boolean created = jar.createJar(projectPath(), jarName,
                  qualifiedMain, executableDirName(), sourceDirName(),
                  nonJavaExt);

            if (!co.isConsoleOpen()) {            
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
         }
         catch (IOException | InterruptedException e) {
            FileUtils.log(e);
         }
      });
   }

   @Override
   protected void setCommandParameters() {
      setQualifiedMain();
      setStartCommand();
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

   private void setNonJavaExtensions() {
      nonJavaExt = fileExtensions();
      isNonJavaExtTested = nonJavaExt == null;
   }

   private boolean existsMainClassFile() {
      boolean exists = existsMainExecFile(".class");
      if (!exists) {
         Dialogs.warnMessage("A compiled main class file could not be found.");
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
         "\"" + ext + "\" cannot be used.\n"
         + "An extension must begin with a period.",
         //
         // title
         "Extensions of included non-Java files");
   }

   private void nonJavaFilesNotSupportedMessage() {
      Dialogs.errorMessage(
         "Including non-java files is supported only if the project"
         + " contains separate directories for source files and for class files.",
         //
         // title
         "Included non-Java files");
   }
}
