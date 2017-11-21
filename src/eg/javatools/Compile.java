/**
 * The Java(TM) Compiler is invoked in the method compile() essentially as
 * shown in the docu for the javax.tools.JavaCompiler interface(
 * https://docs.oracle.com/javase/7/docs/api/javax/tools/JavaCompiler.html).
 */ 
package eg.javatools;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.Dialogs;
import eg.console.ConsolePanel;

/**
 * Compiles java files in a given working directory using the
 * JavaCompiler API.
 */
public class Compile {

   private final static Preferences PREFS = new Preferences();
   private final static String F_SEP = File.separator;
   
   private final ConsolePanel consPnl;
   
   private boolean success = false;
   private ArrayList<String> errorInfo;
   
   /**
    * @param consPnl  the reference to {@link ConsolePanel} in whose
    * text area messages are displayed
    */
   public Compile(ConsolePanel consPnl) {
      this.consPnl = consPnl;
   }
   
   /**
    * @return  true if all java files compiled without
    * errors; false otherwise
    */
   public boolean success() {
      return success;
   }
   
   /**
    * @return  the first of all listed error messages that contain
    * the info about the line number an the source file
    */
   public String getMessage() {   
      return errorInfo.get(0);
   }

   /**
    * Invokes the javac compiler
    *
    * @param projectPath  the root directory of the project
    * @param classDir  the target directory of the class files
    * @param sourceDir  the directory that contains java files
    */
   public void compile(String projectPath, String classDir, String sourceDir) {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      if (compiler == null) {
         Dialogs.errorMessage("The programm may not be run in the JRE of a JDK.");
      }
      success = false;
      errorInfo = new ArrayList<>();
      String targetDir = targetDir(projectPath, classDir);
      String[] compileOptions = new String[] {"-d", targetDir} ;
      Iterable<String> compilationOptions = Arrays.asList(compileOptions);      
      DiagnosticCollector<JavaFileObject> diagnostics
            = new DiagnosticCollector<>();
      StandardJavaFileManager fileManager
            = compiler.getStandardFileManager(null, null, null);
      Iterable<? extends JavaFileObject> units;
      List<File> classes = new SearchFiles().filteredFiles(projectPath
            + F_SEP + sourceDir, ".java");
      File[] fileArr = classes.toArray(new File[classes.size()]);
      units = fileManager.getJavaFileObjects(fileArr);
      CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
              compilationOptions, null, units);
      success = task.call();       
      output(diagnostics);
      try {
         fileManager.close();
      } catch (IOException e) {
         System.out.println(e.getMessage());
      }
   }
   
   private void output(DiagnosticCollector<JavaFileObject> diagnostics) {
      if (success) {
         consPnl.appendText("<<Compilation successful>>");
      }
      else {
         for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            consPnl.appendText(diagnostic.getKind().toString() + ":\n");
            consPnl.appendText(diagnostic.getCode() + ": ");
            consPnl.appendText(diagnostic.getMessage( null ) + "\n");
            consPnl.appendText("at line: " + diagnostic.getLineNumber() + "\n");
            consPnl.appendText("at column: " + diagnostic.getColumnNumber() + "\n");
            if (diagnostic.getSource() != null) { // can be null!
               consPnl.appendText(diagnostic.getSource().toString() + "\n");
               String file = new File(diagnostic.getSource().toString()).getName();
               file = file.substring(0, file.length() - 1);
               errorInfo.add("First listed error is found in " + file + ", line " 
                     + diagnostic.getLineNumber());
            }
            String devider = "_";
            for (int i = 0; i <= 90; i++) {
               consPnl.appendText(devider);
            }
            consPnl.appendText("\n");
         }
      }
   }

   /**
    * @return  the directory where class files/packages are saved
    */
   private String targetDir(String projectPath, String classDir) {
      String targetDir;
      if (classDir.length() > 0) {
         File target = new File(projectPath + F_SEP + classDir);
         target.mkdirs();
         targetDir = projectPath + F_SEP + classDir;
      }
      else {
         targetDir = projectPath;
      }
      return targetDir;
   }
}
