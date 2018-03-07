/**
 * The compilation of a Java project.
 * <p>
 * The Java(TM) Compiler is invoked in the method compile() essentially as
 * shown in the docu for the javax.tools.JavaCompiler interface
 * (https://docs.oracle.com/javase/7/docs/api/javax/tools/JavaCompiler.html).
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

import java.nio.file.Files;

import java.util.List;
import java.util.Arrays;

import static java.nio.file.StandardCopyOption.*;

//--Eadgyth--//
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.console.ConsolePanel;

/**
 * Compiles java files in a given working directory using the
 * JavaCompiler API.
 */
public class Compilation {

   private final static String DIVIDING_LINE
         = new String(new char[90]).replace('\0', '_');

   private final FilesFinder fFind = new FilesFinder();
   private final ConsolePanel consPnl;

   private boolean success = false;
   private String firstCompileError = "";
   private String copyError = "";

   /**
    * @param consPnl  the reference to {@link ConsolePanel} in whose
    * text area messages are displayed
    */
   public Compilation(ConsolePanel consPnl) {
      this.consPnl = consPnl;
   }

   /**
    * Returns the boolean that indicates if java files could be
    * compiled successfully
    *
    * @return  the boolean value which true in the case of success
    */
   public boolean isCompiled() {
      return success;
   }

   /**
    * Gets a shortened error message which indicates the source
    * file and the line number in the first listed compilation
    * error.<br>
    * The entire list of errors messages is printed to this
    * <code>ConsolePanel</code>.
    *
    * @return  the message or the empty empty string of there is no
    * error
    */
   public String getFirstCompileErr() {
      return firstCompileError;
   }
   
   /**
    * Returns the error message that indicates that non-Java files
    * for copying are not found
    *
    * @return  the message or the empty empty string of there is no
    * error
    */
   public String getCopyErr() {
      return copyError;
   }

   /**
    * Invokes the javac compiler
    *
    * @param root  the root directory of the project
    * @param execDir  the name of the destination directory for the
    * compiled class files
    * @param sourceDir  the name of the directory that contains java files
    * or packages
    * @param nonJavaExt  the array of extensions of files that are copied
    * to the compilation. May be null.
    */
   public void compile(String root, String execDir, String sourceDir,
         String[] nonJavaExt) {

      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      if (compiler == null) {
         Dialogs.errorMessage(
               "The programm may not be run using the JRE in a JDK.", null);

         return;
      }
      String targetDir = createTargetDir(root, execDir);
      String[] options = new String[] {"-d", targetDir} ;
      Iterable<String> compileOptions = Arrays.asList(options);
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
      StandardJavaFileManager fileManager
            = compiler.getStandardFileManager(null, null, null);

      Iterable<? extends JavaFileObject> units;
      List<File> classes = fFind.filteredFiles(root + "/" + sourceDir,
            ".java", execDir);

      File[] fileArr = classes.toArray(new File[classes.size()]);
      units = fileManager.getJavaFileObjects(fileArr);
      CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
              compileOptions, null, units);

      success = task.call();
      output(diagnostics);
      try {
         fileManager.close();
      } catch (IOException e) {
         FileUtils.logStack(e);
      }
      if (nonJavaExt != null) {
         copyFiles(root, sourceDir, execDir, nonJavaExt);
      }
   }

   //
   //--private--/
   //

   private String createTargetDir(String root, String execDir) {
      String targetDir;
      if (execDir.length() > 0) {
         File target = new File(root + "/" + execDir);
         target.mkdirs();
         targetDir = root + "/" + execDir;
      }
      else {
         targetDir = root;
      }
      return targetDir;
   }

   private void copyFiles(String root, String sourceDir, String execDir,
         String[] nonJavaExt) {

      copyError = "";
      if (sourceDir.length() == 0 || execDir.length() == 0) {
         throw new IllegalArgumentException(
               "Including non-java file requires a sources and a classes directory"
               + " to be defined");
      }
      String searchRoot = root + "/" + sourceDir;
      for (String ext : nonJavaExt) {
         List<File> toCopy = fFind.filteredFiles(searchRoot, ext, execDir);
         if (toCopy.isEmpty()) {
            copyError = "Files with extension \"" + ext
                      + "\" for copying to the compilation were not found";
         }
         else {
            try {
               for (File f : toCopy) {
                  String source = f.getPath();                
                  String destination = source.replace(sourceDir, execDir);                
                  if (destination != null) {
                     File fDest = new File(destination);
                     File destDir = fDest.getParentFile();
                     if (!destDir.exists()) {
                        destDir.mkdirs();
                     }
                     Files.copy(f.toPath(), fDest.toPath(),
                           REPLACE_EXISTING);
                  }
               }
            }
            catch (IOException e) {
               FileUtils.logStack(e);
            }
         }
      }
      if (copyError.length() > 0) {
         consPnl.appendText("\n<<Note: " + copyError + ">>");
      }
   }

   private void output(DiagnosticCollector<JavaFileObject> diagnostics) {
      firstCompileError = "";
      if (success) {
         consPnl.appendText("<<Compilation successful>>");
      }
      else {
         Diagnostic<?> firstSource = diagnostics.getDiagnostics().get(0);
         if (firstSource != null) {
            String file = new File(firstSource.getSource().toString()).getName();
            file = file.substring(0, file.length() - 1);
            firstCompileError = "First listed error is found in " + file + ", line "
                  + firstSource.getLineNumber();
         }
         for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            consPnl.appendText(diagnostic.getKind().toString() + ":\n");
            consPnl.appendText(diagnostic.getCode() + ": ");
            consPnl.appendText(diagnostic.getMessage( null ) + "\n");
            consPnl.appendText("at line: " + diagnostic.getLineNumber() + "\n");
            consPnl.appendText("at column: " + diagnostic.getColumnNumber() + "\n");
            if (diagnostic.getSource() != null) {
               consPnl.appendText(diagnostic.getSource().toString() + "\n");
            }
            consPnl.appendText(DIVIDING_LINE + "\n");
         }
      }
   }
}
