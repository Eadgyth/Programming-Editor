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
import java.io.StringWriter;

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

   private static final String DIVIDING_LINE
         = new String(new char[90]).replace('\0', '_');

   private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
   private final FilesFinder fFind = new FilesFinder();
   private final ConsolePrinter pr;
   private final Libraries libs;
   private final LibModules mods;

   private boolean success = false;

   /**
    * @param printer  the <code>ConsolePrinter</code>
    * @param libs  the <code>Libraries</code> that may contain external
    * libraries that are added to the classpath
    * @param mods  the <code>LibModules</code> that may contain external
    * library modules that are added to the module path
    */
   public Compilation(ConsolePrinter printer, Libraries libs, LibModules mods) {
      pr = printer;
      this.libs = libs;
      this.mods = mods;
   }

   /**
    * Compiles java files and copies non java files with the specified
    * extensions to the compilation
    *
    * @param classDir  the destination directory for the compiles class
    * files/packages
    * @param sourceDir  the directory that contains java files/packages
    * @param nonJavaExt  the array of extensions of files that are copied
    * to the compilation if classDir and sourceDir differ. May be the
    * zero length array.
    * @param options  Compiler options, in which several options and
    * arguments are separated by spaces.
    */
   public void compile(String classDir, String sourceDir, String[] nonJavaExt,
         String options) {

      if (compiler == null) {
         Dialogs.errorMessage("The compiler was not found.", null);
         return;
      }
      success = false;
      DiagnosticCollector<JavaFileObject> diagnostics
            = new DiagnosticCollector<>();

      StandardJavaFileManager fileManager
            = compiler.getStandardFileManager(null, null, null);

      List<File> sources = fFind.filteredFiles(sourceDir, ".java", classDir, "");
      File[] fileArr = sources.toArray(new File[sources.size()]);
      Iterable<? extends JavaFileObject>units
            = fileManager.getJavaFileObjects(fileArr);

      Iterable<String> compileOptions = compileOptions(classDir, options);
      StringWriter writer = new StringWriter();
      try {
         CompilationTask task = compiler.getTask(
               writer,
               fileManager,
               diagnostics,
               compileOptions,
               null,
               units);

         success = task.call();
         if (success && nonJavaExt.length > 0 && !classDir.equals(sourceDir)) {
            copyFiles(sourceDir, classDir, nonJavaExt);
         }
         pr.printLine(writer.toString());
         printDiagnostics(diagnostics);
      }
      catch (IllegalArgumentException | IllegalStateException e) {
         //
         // A compiler option or its argument may be wrong
         String msg = e.getMessage() != null
               ? "for the following reason:\n" + e.getMessage() : "";

         pr.printBr("Unable to compile " + msg);
      }
      catch (IOException | RuntimeException e) {
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

   private Iterable<String> compileOptions(String classDir, String options) {
      List <String> optList = new ArrayList<>();
      optList.add("-d");
      optList.add(classDir);
      addDepsOptions(optList);
      addOptionsInput(optList, options);
      return optList;
   }

   private void addDepsOptions(List <String> optList) {
      if (!libs.joinedAbs().isEmpty()) {
         optList.add("-cp");
         optList.add(libs.joinedAbs());
      }
      if (!mods.joinedParentsAbs().isEmpty()) {
         optList.add("-p");
         optList.add(mods.joinedParentsAbs());
         optList.add("--add-modules");
         optList.add(mods.joinedNames());
      }
   }

   private void addOptionsInput(List <String> optList, String options) {
      if (options.isEmpty()) {
         return;
      }
      String[] test = options.split("\\s+");
      for (int i = 0; i < test.length; i++) {
         if (test[i].startsWith("-") && -1 == compiler.isSupportedOption(test[i])) {
            pr.printBr("NOTE: " + test[i] + " is invalid or cannot be used");
         }
         optList.add(test[i]);
      }
   }

   private void copyFiles(String sourceDir, String classDir, String[] nonJavaExt)
         throws IOException {

      for (String ext : nonJavaExt) {
         List<File> toCopy = fFind.filteredFiles(sourceDir, ext, classDir, "");
         if (toCopy.isEmpty()) {
            printNoFileToCopyMsg(ext);
         }
         else {
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
      }
   }

   private void printNoFileToCopyMsg(String ext) {
      String s =
            "NOTE: Files with extension \'"
            + ext
            + "\' for copying to the compilation cannot be found";

      pr.printBr(s);
   }

   private void printDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
      if (success) {
         pr.printBr("Compilation successful");
      }
      if (!diagnostics.getDiagnostics().isEmpty()) {
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
