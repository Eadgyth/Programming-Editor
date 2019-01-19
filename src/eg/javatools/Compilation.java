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

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.console.Console;

/**
 * The compilation of java files using the Java Compiler API
 */
public class Compilation {

   private final static String DIVIDING_LINE
         = new String(new char[90]).replace('\0', '_');

   private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
   private final FilesFinder fFind = new FilesFinder();
   private final Console cons;

   private boolean success = false;
   private String copyFilesErr = "";
   private String optionErr = "";

   /**
    * @param console  the reference to {@link Console}
    */
   public Compilation(Console console) {
      cons = console;
   }

   /**
    * Returns the boolean that indicates if java files were compiled
    * successfully
    *
    * @return  the boolean value which is true in the case of success
    */
   public boolean isCompiled() {
      return success;
   }

   /**
    * Returns the error message that indicates that an error occured
    * during copying non-java files
    *
    * @return  the message or the empty empty string of there is none
    */
   public String copyFilesErr() {
      return copyFilesErr;
   }

   /**
    * Returns the error message that indicates that the input for
    * the Xlint compiler option is invalid
    *
    * @return  the message or the empty string of there is none
    */
   public String optionErr() {
      return optionErr;
   }

   /**
    * Invokes the javac compiler
    *
    * @param root  the root directory of the java project
    * @param execDir  the name of the destination directory for the
    * compiled class files/packages. Can be the empty string if nonJavaExt
    * is null.
    * @param sourceDir  the name of the directory that contains java files/
    * packages. Can be the empty string if nonJavaExt is null.
    * @param nonJavaExt  the array of extensions of files that are copied
    * to the compilation. Requires that both a sources and a classes
    * directory is present. May be null.
    * @param xlintOption  the Xlint compiler option. Other compiler options
    * are ignored.
    */
   public void compile(String root, String execDir, String sourceDir,
         String[] nonJavaExt, String xlintOption) {

      if (compiler == null) {
         Dialogs.errorMessage(
               "The compiler was not found.", null);

         return;
      }
      reset();
      DiagnosticCollector<JavaFileObject> diagnostics
            = new DiagnosticCollector<>();
      StandardJavaFileManager fileManager
            = compiler.getStandardFileManager(null, null, null);
      //
      // Java files
      List<File> classes = fFind.filteredFiles(root + "/" + sourceDir,
            ".java", execDir);
      File[] fileArr = classes.toArray(new File[classes.size()]);
      Iterable<? extends JavaFileObject>units
            = fileManager.getJavaFileObjects(fileArr);
      //
      // Compiler options
      String targetDir = createTargetDir(root, execDir);
      Iterable<String> compileOptions = options(targetDir, xlintOption);
      //
      // compile
      try {
         CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
               compileOptions, null, units);

         success = task.call();
         if (nonJavaExt != null) {
            copyFiles(root, sourceDir, execDir, nonJavaExt);
         }
         printDiagnostics(diagnostics);
      }
      catch (IllegalArgumentException | IllegalStateException e) {
         FileUtils.log(e);
      }
      finally {
         try {
            fileManager.close();
         } catch (IOException e) {
            FileUtils.log(e);
         }
      }
   }

   //
   //--private--/
   //

   private void reset() {
      success = false;
      copyFilesErr = "";
      optionErr = "";
   }

   private String createTargetDir(String root, String execDir) {
      String targetDir;
      if (!execDir.isEmpty()) {
         File target = new File(root + "/" + execDir);
         target.mkdirs();
         targetDir = root + "/" + execDir;
      }
      else {
         targetDir = root;
      }
      return targetDir;
   }

   private Iterable<String> options(String targetDir, String xlintOption) {
      String[] opt;
      if (xlintOption.isEmpty()) {
         opt = new String[] {"-d", targetDir};
      }
      else {
         boolean ok = true;
         String[] test = xlintOption.split("\\s+");
         for (String s : test) {
            ok = s.startsWith("-Xlint")
                  && -1 < compiler.isSupportedOption(s);

            if (!ok) {
               break;
            }
         }
         if (ok) {
            opt = new String[2 + test.length]; // 2 for -d and target dir
            opt[0] = "-d";
            opt[1] = targetDir;
            for (int i = 2; i < opt.length; i++) {
               opt[i] = test[i - 2];
            }
         }
         else {
            opt = new String[] {"-d", targetDir};
            optionErr =
                  xlintOption
                  + "\" cannot be used as"
                  + " Xlint compiler option and was ignored";

            cons.printBr(optionErr);
         }
      }
      return Arrays.asList(opt);
   }

   private void copyFiles(String root, String sourceDir, String execDir,
         String[] nonJavaExt) {

      if (sourceDir.isEmpty() || execDir.isEmpty()) {
         throw new IllegalArgumentException(
               "A sources and a classes directory must be"
               + " defined for copying non-java files");
      }
      String searchRoot = root + "/" + sourceDir;
      for (String ext : nonJavaExt) {
         List<File> toCopy = fFind.filteredFiles(searchRoot, ext, execDir);
         if (toCopy.isEmpty()) {
            copyFilesErr =
                  " Files with extension \""
                  + ext
                  + "\" for copying to the compilation were not found";

            cons.printBr(copyFilesErr);
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
               FileUtils.log(e);
            }
         }
      }
   }

   private void printDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
      if (success) {
         cons.printBr("Compilation successful");
      }
      if (diagnostics.getDiagnostics().size() > 0) {
         for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            cons.print(diagnostic.getKind().toString() + ":\n");
            cons.print(diagnostic.getCode() + ": ");
            cons.print(diagnostic.getMessage( null ) + "\n");
            cons.print("at line: " + diagnostic.getLineNumber() + "\n");
            cons.print("at column: " + diagnostic.getColumnNumber() + "\n");
            if (diagnostic.getSource() != null) {
               cons.print(diagnostic.getSource().toString() + "\n");
            }
            cons.print(DIVIDING_LINE + "\n");
         }
      }
   }
}
