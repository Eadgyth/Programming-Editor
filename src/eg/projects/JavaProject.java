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

   private final static String F_SEP = File.separator;

   private final ConsoleOpenable co;
   private final ProcessStarter proc;
   private final ConsolePanel consPnl;
   private final Compilation comp;
   private final JarBuilder jar;

   private String startCommand = "";
   private String qualifiedMain = "";
   private String[] includedFiles = null;
   private boolean isIncludedFilesTested = false;

   JavaProject(ConsoleOpenable co, ProcessStarter proc, ConsolePanel consPnl) {
      super("java", true);
      this.co = co;
      this.proc = proc;
      this.consPnl = consPnl;
      comp = new Compilation(consPnl);
      jar = new JarBuilder(consPnl);
   }

   @Override
   public void createSettingsWin() {
      setWin = SettingsWin.adaptableWindow();
      setWin.addFileOption("Name of main class file")
            .addSourceDirOption()
            .addExecDirOption()
            .addArgsOption()
            .addIncludeFilesOption("Included non-Java files or file types")
            .addBuildOption("jar file")
            .setupWindow();
   }

   @Override
   public boolean configureProject(String dir) {
      boolean success = super.configureProject(dir);
      if (success) {
         setCommandParams();
      }
      return success;
   }

   @Override
   public boolean retrieveProject(String dir) {
      boolean success = super.retrieveProject(dir);
      if (success) {
         setCommandParams();
      }
      return success;
   }

   /**
    * Compiles java files and shows output in the console panel
    */
   @Override
   public void compile() {
      if (!containIncludedFilesPeriod()) {
         return;
      }
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
      if (!mainClassFileExists() || !containIncludedFilesPeriod()) {
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

   //
   //--private--//
   //

   private void setCommandParams() {
      setQualifiedMain();
      setStartCommand();
      setIncludedFilesArr();
   }

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
          isIncludedFilesTested = false;
       }
   }

   private boolean mainClassFileExists() {
      boolean exists = mainExecFileExists(".class");
      if (!exists) {
         Dialogs.warnMessage("A compiled main class file could not be found.");
      }
      return exists;
   }

   private boolean containIncludedFilesPeriod() {
      boolean isOk = true;
      if (includedFiles != null && !isIncludedFilesTested) {
         System.out.println("test");
         for (String s : includedFiles) {
            if (s.length() < 2 || !s.contains(".")) {
               Dialogs.errorMessage(
                   "<html>"
                   + "The term \"" + s + "\" which is indicated as file or file type"
                   + " to be included in a compilation and a jar file cannot be"
                   + " used.<br>"
                   + "<ul>"
                   + "<li>Names of files must contain the extension."
                   + "<li>To include all files of a given type their extension"
                   + "  must contain the preceding period (ex.: .png)."
                   + "</ul>"
                   + "</html>",
                   "Included files in compilation and jar file");

               isOk = false;
               break;
            }
            else {
               isIncludedFilesTested = true;
            }
         }
      }
      return isOk;
   }

   private boolean jarFileExists(String jarName) {
      String execDir = getProjectPath() + F_SEP + getExecutableDirName();
      return new File(execDir + F_SEP + jarName + ".jar").exists();
   }
}
