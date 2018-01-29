package eg.projects;

import java.io.File;
import java.io.IOException;

import java.awt.EventQueue;

import javax.swing.SwingWorker;

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
   private String[] includedFiles = null;

   JavaProject(ConsoleOpenable co, ProcessStarter proc, ConsolePanel consPnl) {
      super("java", true);
      this.co = co;
      this.proc = proc;
      this.consPnl = consPnl;
      comp = new Compilation(consPnl);
      jar = new JarBuilder(consPnl);
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.addFileOption("Name of main class file")
            .addSourceDirOption()
            .addExecDirOption()
            .addArgsOption()
            .addIncludeFilesOption("Included non-Java files or file types")
            .addBuildOption("jar file")
            .buildWindow();
   }

   /**
    * Compiles java files and shows output in the console panel
    */
   @Override
   public void compile() {
      consPnl.setText("<<Compile " + getProjectName() + ">>\n");
      EventQueue.invokeLater(() -> {
         if (proc.isProcessEnded()) {
            comp.compile(getProjectPath(), getExecutableDirName(), getSourceDirName(),
                  includedFiles);

            consPnl.setCaretUneditable(0);
            if (!co.isConsoleOpen()) {
               if (!comp.isCompiled()) {
                  int res = Dialogs.warnConfirmYesNo(
                        "Compilation failed.\n"
                        + comp.getFirstErrSource() + ".\n"
                        + "Open the console window to view messages?");

                  if (0 == res) {
                     co.openConsole();
                  }
               }
               else {
                  Dialogs.infoMessage("Compilation successful", null);
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
      if (!mainClassFileExists()) {
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
                  getExecutableDirName(), getSourceDirName(), includedFiles);

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
            consPnl.appendText("<<Saved jar file named " + jarName + ">>\n");
            Dialogs.infoMessage("Saved jar file named " + jarName, null);
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
      setIncludedFilesArr();
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
      if (getArgs().length() > 0) {
         sb.append(" ").append(getArgs());
      }
      startCommand = sb.toString();
   }

   private void setIncludedFilesArr() {
       if (getIncludedFiles().length() == 0) {
          includedFiles = null;
       }
       else {
          includedFiles = getIncludedFiles().split(",");
       }
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
}
