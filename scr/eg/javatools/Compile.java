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
import eg.utils.JOptions;
import eg.console.ConsolePanel;

/**
 * Compiles java files in a given working directory using the JavaCompiler API
 */
public class Compile {

   private final static String SEP = File.separator;
   private final static Preferences PREFS = new Preferences();
   private static String jdkPath = null;
   
   private final ConsolePanel cp;
   
   private boolean success = false;
   private ArrayList<String> errorInfo;
   
   /**
    * @param cp  the reference to {@link ConsolePanel} in whose
    * text area messages are displeayed
    */
   public Compile(ConsolePanel cp) {
      this.cp = cp;
   }
   
   public boolean success() {
      return success;
   }
   
   public String getMessage() {   
      return errorInfo.get(0);
   }

   /**
    * Invokes the javac compiler
    * @param projectPath  the root directory of the project
    * @param classDir  the target directory of the class files
    * @param sourceDir  the directory that contains java files
    */
   public void compile(String projectPath, String classDir, String sourceDir) {
      success = false;
      errorInfo = new ArrayList<>();
      if (jdkPath == null) {
         setJdkPath();
         if (jdkPath == null) {
            errorInfo.add("The filepath of the JDK is not defined");
            cp.appendText("ERROR:\nThe file path of the JDK is not defined"
                 + " in'settings.properties'.");
            return;
         }
      }

      String targetDir = targetDir(projectPath, classDir);
      try {
         String[] compileOptions = new String[] {"-d", targetDir} ;
         Iterable<String> compilationOptions = Arrays.asList(compileOptions);      
         JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
         DiagnosticCollector<JavaFileObject> diagnostics
               = new DiagnosticCollector<>();
         try (StandardJavaFileManager fileManager
                  = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> units;
            List<File> classes = new SearchFiles().filteredFiles(projectPath
                    + SEP + sourceDir, ".java");
            File[] fileArr = classes.toArray(new File[classes.size()]);
            units = fileManager.getJavaFileObjects(fileArr);
            CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
                    compilationOptions, null, units);
            success = task.call();
         }

         if (success) {
            cp.appendText("Compilation successful");
         }
         else {
            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
               cp.appendText(diagnostic.getKind().toString() + ":\n");
               cp.appendText(diagnostic.getCode().toString() + ": ");
               cp.appendText(diagnostic.getMessage( null ).toString() + "\n");
               cp.appendText("at line: " + diagnostic.getLineNumber() + "\n");
               cp.appendText("at column: " + diagnostic.getColumnNumber() + "\n");
               if (diagnostic.getSource() != null) { // can be null!
                  cp.appendText(diagnostic.getSource().toString() + "\n");
                  String file = new File(diagnostic.getSource().toString()).getName();
                  file = file.substring(0, file.length() - 1);
                  errorInfo.add("First listed error in " + file + ", line " 
                        + diagnostic.getLineNumber());
               }
               else {
                  errorInfo.add("Unreported error");
                  cp.appendText("Unreported error");
               }
               cp.appendText("----------------------------------------------------------"
                     + "--------------------------------------------------------------\n");
            }
         }
      }
      catch (IOException e) {
         errorInfo.add(e.getMessage());
         System.out.println(e.getMessage());
      }
   }

   /**
    * @return  the directory where class files/packages are saved
    */
   private String targetDir(String projectPath, String classDir) {
      String targetDir;
      if (classDir.length() > 0) {
         File target = new File(projectPath + SEP + classDir);
         target.mkdirs();
         targetDir = projectPath + SEP + classDir;
      }
      else {
         targetDir = projectPath;
      }
      return targetDir;
   }

   /**
    * Sets the path to the Java JDK and asks for a new path if no valid path is
    * found in Settings.properties
    */
   private void setJdkPath() {
      PREFS.readSettings();
      jdkPath = PREFS.getProperty("LocationOfJDK");
      if (!new File(jdkPath).exists()) {
         String notFound = "The JDK was not found."
               + " Enter or correct the filepath of the JDK.";
         jdkPath = JOptions.dialogRes(notFound, "Location of JDK", jdkPath);
         if (jdkPath != null) { // if ok clicked
            PREFS.storeSettings(jdkPath);
            setJdkPath();
         }
      }
      else {
         System.setProperty("java.home", jdkPath);
         System.out.println("Set Location of JDK: " + jdkPath);
      }
   }
}