package eg.projects;

import java.io.File;
import java.io.IOException;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.console.*;
import eg.javatools.*;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.ui.ConsoleOpenable;

/**
 * Represents a programming project in Java
 */
public final class JavaProject extends AbstractProject implements ProjectActions {

   private final ConsoleOpenable co;
   private final ProcessStarter proc;
   private final ConsolePanel consPnl;
   private final Compilation comp;
   private final JarBuilder jar;

   private String startCommand = "";
   private String qualifiedMain = "";
   private String[] nonJavaExt = null;
   private boolean isNonJavaExtTested = true;

   JavaProject(ConsoleOpenable co, ProcessStarter proc, ConsolePanel consPnl) {
      super(ProjectTypes.JAVA, true, "java");
      this.co = co;
      this.proc = proc;
      this.consPnl = consPnl;
      comp = new Compilation(consPnl);
      jar = new JarBuilder(consPnl);
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

   /**
    * Compiles java files and shows output in the console panel
    */
   @Override
   public void compile() {
      if (!isNonJavaExtCorrect()) {
         return;
      }
      consPnl.setText("<<Compile " + getProjectName() + ">>\n");
      EventQueue.invokeLater(() -> {
         if (proc.isProcessEnded()) {
            comp.compile(getProjectPath(), getExecutableDirName(),
                  getSourceDirName(), nonJavaExt, getCompileOption());

            consPnl.setCaretUneditable(0);
            //
            // Set up dialog depending on results
            if (!co.isConsoleOpen()) {
               boolean needConfirm = false;
               StringBuilder msg = new StringBuilder();
               if (!comp.isCompiled()) {
                  msg.append("Compilation failed.\n");
                  msg.append(comp.getFirstCompileErr()).append(".\n");
                  needConfirm = true;
               }
               else {
                  msg.append("Compilation successful.\n");
               }
               if (comp.isNonErrMessage()) {
                  msg.append("Warning: One or more compiler messages are present.\n");
                  needConfirm = true;
               }
               if (comp.getCopyFilesErr().length() > 0) {
                  msg.append("Note: ")
                        .append(comp.getCopyFilesErr()).append(".\n");
               }
               if (comp.getOptionErr().length() > 0) {
                  msg.append("Note: ")
                        .append(comp.getOptionErr()).append(".\n");
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
         }
      });
   }

   /**
    * Runs the project and shows output in the console panel
    */
   @Override
   public void runProject() {
      if (!mainClassFileExists()) {
         return;
      }
      if (!co.isConsoleOpen()) {
         co.openConsole();
      }
      proc.startProcess(startCommand);
   }

   /**
    * Creates a jar file
    */
   @Override
   public void build() {
      if (!mainClassFileExists() || !isNonJavaExtCorrect()) {
         return;
      }
      consPnl.setText("");
      EventQueue.invokeLater(() -> {
         String jarName = getBuildName();
         if (jarName.length() == 0) {
            jarName = getMainFileName();
         }
         boolean existed = jarFileExists(jarName);
         try {
            jar.createJar(getProjectPath(), jarName, qualifiedMain,
                  getExecutableDirName(), getSourceDirName(), nonJavaExt);

            if (!existed) {
               boolean exists = false;
               while (!exists) {
                  try {
                     Thread.sleep(200);
                  }
                  catch (InterruptedException e) {
                     FileUtils.logStack(e);
                  }
                  exists = jarFileExists(jarName);
               }
            }
            if (co.isConsoleOpen()) {
               consPnl.appendText("<<Saved jar file named " + jarName + ">>");
               if (jar.getIncudedFilesErr().length() > 0) {
                  consPnl.appendText("\n<<Note: " + jar.getIncudedFilesErr() + ">>");
               }
            }
            else {
               StringBuilder msg = new StringBuilder("Saved jar file named " + jarName);
               if (jar.getIncudedFilesErr().length() > 0) {
                  msg.append("\n\nNote: ").append(jar.getIncudedFilesErr()).append(".");
               }
               Dialogs.infoMessage(msg.toString(), null);
            }
         }
         catch (IOException e) {
            FileUtils.logStack(e);
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
   //--private--//
   //

   private void setQualifiedMain() {
      StringBuilder sb = new StringBuilder();
      if (getNamespace().length() > 0) {
         sb.append(FileUtils.dottedFileSeparators(getNamespace())).append(".");
      }
      sb.append(getMainFileName());
      qualifiedMain = sb.toString();
   }

   private void setStartCommand() {
      StringBuilder sb = new StringBuilder("java ");
      if (getExecutableDirName().length() > 0) {
         sb.append("-cp ").append(getExecutableDirName()).append(" ");
      }
      sb.append(qualifiedMain);
      if (getCmdArgs().length() > 0) {
         sb.append(" ").append(getCmdArgs());
      }
      startCommand = sb.toString();
   }

   private void setNonJavaExtensions() {
      nonJavaExt = getFileExtensions();
      isNonJavaExtTested = nonJavaExt == null;
   }

   private boolean mainClassFileExists() {
      boolean exists = mainExecFileExists(".class");
      if (!exists) {
         Dialogs.warnMessage("A compiled main class file could not be found.");
      }
      return exists;
   }

   private boolean jarFileExists(String jarName) {
      String f = getProjectPath() + "/" + getExecutableDirName() + "/"
            + jarName + ".jar";

      return new File(f).exists();
   }

   private boolean isNonJavaExtCorrect() {
      if (isNonJavaExtTested) {
         return true;
      }
      boolean ok = true;
      if (getSourceDirName().length() == 0
            || getExecutableDirName().length() == 0) {

         nonJavaFilesNotSupportedMessage();
         ok = false;
      }
      else {
         for (String s : getFileExtensions()) {
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
         "Including non-java files not supported");
   }
}
