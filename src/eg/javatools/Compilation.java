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
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.TaskRunner.ConsolePrinter;

/**
 * The compilation of java files using the Java Compiler API
 */
public class Compilation {

   private final static String DIVIDING_LINE
         = new String(new char[90]).replace('\0', '_');

   private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
   private final FilesFinder fFind = new FilesFinder();
   private final ConsolePrinter pr;

   private boolean success = false;

   /**
    * @param printer  the reference <code>ConsolePrinter</code>
    */
   public Compilation(ConsolePrinter printer) {
      pr = printer;
   }

   /**
    * Compiles java files and copies non java files with the specified
    * extensions to the compilation
    *
    * @param classDir  the destination directory for the compiles class
    * files/packages
    * @param sourceDir  the directory that contains java files/packages
    * @param nonJavaExt  the array of extensions of files that are copied
    * to the compilation if classDir and sourceDir differ. May be the zero
    * length array.
    * @param libs  the libraries in which individual paths are separated
    * by the system's path separator. May be the empty string
    * @param xlintOption  the Xlint compiler options, in which several
    * options are separated by spaces. Options other than Xlint are
    * ignored
    */
   public void compile(
            String classDir,
            String sourceDir,
            String[] nonJavaExt,
            String libs,
            String xlintOption) {

      if (compiler == null) {
         Dialogs.errorMessage("The compiler was not found.", null);
         return;
      }
      success = false;
      DiagnosticCollector<JavaFileObject> diagnostics
            = new DiagnosticCollector<>();

      StandardJavaFileManager fileManager
            = compiler.getStandardFileManager(null, null, null);
      //
      // Java files
      List<File> sources = fFind.filteredFiles(sourceDir, ".java", classDir, "");
      File[] fileArr = sources.toArray(new File[sources.size()]);
      Iterable<? extends JavaFileObject>units
            = fileManager.getJavaFileObjects(fileArr);
      //
      // Compiler options
      Iterable<String> compileOptions = options(classDir, sourceDir, libs, xlintOption);
      //
      // compile, print messages
      try {
         CompilationTask task = compiler.getTask(
               null,
               fileManager,
               diagnostics,
               compileOptions,
               null,
               units);

         success = task.call();
         if (nonJavaExt.length > 0) {
            copyFiles(sourceDir, classDir, nonJavaExt);
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

   private Iterable<String> options(String classDir, String sourceDir, String libs,
            String xlintOption) {

      List <String> options = new ArrayList<>();
      options.add("-d");
      options.add(classDir);
      if (!sourceDir.isEmpty()) {
         options.add("-sourcepath");
         options.add(sourceDir);
      }
      if (!libs.isEmpty()) {
         options.add("-cp");
         options.add(libs);
      }
      if (!xlintOption.isEmpty()) {
         String[] test = xlintOption.split("\\s+");
         List <String> unsupported = new ArrayList<>();
         for (String s : test) {
            boolean ok = s.startsWith("-Xlint")
                  && -1 < compiler.isSupportedOption(s);

            if (!ok) {
               unsupported.add(s);
            }
            else {
               options.add(s);
            }
         }
         if (unsupported.size() > 0) {
            for (String s : unsupported) {
               String err =
                     "NOTE: \'"
                     + s
                     + "\' cannot be used as compiler"
                     + " option and was ignored";

               pr.printBr(err);
            }
         }
      }
      return options;
   }

   private void copyFiles(String sourceDir, String classDir,
         String[] nonJavaExt) {

      if (classDir.equals(sourceDir)) {
         return;
      }

      for (String ext : nonJavaExt) {
         List<File> toCopy = fFind.filteredFiles(sourceDir, ext, classDir, "");
         if (toCopy.isEmpty()) {
            String copyFilesErr =
                  "NOTE: Files with extension \""
                  + ext
                  + "\" for copying to the compilation were not found";

            pr.printBr(copyFilesErr);
         }
         else {
            try {
               for (File f : toCopy) {
                  String source = f.getPath().replace("\\", "/");
                  String destination = source.replace(
                        sourceDir.replace("\\", "/"), classDir.replace("\\", "/"));

                  File fDest = new File(destination);
                  if (fDest.isAbsolute()) {
                     File destDir = fDest.getParentFile();
                     if (!destDir.exists()) {
                        destDir.mkdirs();
                     }
                     Files.copy(f.toPath(), fDest.toPath(),
                           StandardCopyOption.REPLACE_EXISTING);
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
         pr.printBr("Compilation successful");
      }
      if (diagnostics.getDiagnostics().size() > 0) {
         for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            pr.print(diagnostic.getKind().toString() + ":\n");
            pr.print(diagnostic.getCode() + ":\n   ");
            pr.printLine(diagnostic.getMessage( null ));
            pr.printLine("   at line: " + diagnostic.getLineNumber());
            pr.printLine("   at column: " + diagnostic.getColumnNumber());
            if (diagnostic.getSource() != null) {
               pr.printLine(diagnostic.getSource().toString());
            }
            pr.printLine(DIVIDING_LINE);
         }
      }
   }
}
