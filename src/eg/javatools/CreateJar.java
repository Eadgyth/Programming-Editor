package eg.javatools;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

//--Eadgyth--//
import eg.console.ConsolePanel;

/**
 * The creation of a jar file of a project.
 * <p>
 * The jar is saved in the folder containing the class files (packages)
 * which is the classpath.
 */
public class CreateJar {

   private final static String F_SEP = File.separator;
   private final ConsolePanel consPnl;
   
   /**
    * @param consPnl  the reference to {@link ConsolePanel} in whose
    * text area messages are displeayed
    */
   public CreateJar(ConsolePanel consPnl) {
      this.consPnl = consPnl;
   }

   /**
    * Creates a jar file
    * <p>
    * @param root  the project's root directory
    * @param main  the name of the main class
    * @param packageName the name of the package that incluses the
    * main class. Can be the empty String but is not null
    * @param classDir  the name of the subdirectory that contains class
    * files. Can be the empty String but is not null
    * @param jarName  the name for the jar. If jarName is the empty
    * String the name of the main class is used
    * @throws IOException  if not input can be read in
    */
   public void createJar(String root, String main, String packageName,
         String classDir, String jarName) throws IOException {
   
      File manifest = new File(root + F_SEP + classDir
            + F_SEP + "manifest.txt");
      createManifest(manifest, main, packageName);
      ProcessBuilder pb = new ProcessBuilder(commandForJar(root, jarName, classDir));
      pb.directory(new File(root + F_SEP + classDir));
      pb.redirectErrorStream(true);
      Process p = pb.start();
      try (BufferedReader br = new BufferedReader(
            new InputStreamReader(p.getInputStream()))) {

         String ch;
         while((ch = br.readLine()) != null) {
            consPnl.appendText(ch + "\n");
         }
      }
   }

   private List<String> commandForJar(String path, String jarName, String classDir) {
      List<String> commandForJar = new ArrayList<>();

      /* ( c: create jar file; v: verbose output; f: output to file, not stdout;
         m: include manifest info ) */ 
      Collections.addAll(commandForJar, "jar", "-cvfm", jarName + ".jar",
            "manifest.txt" );
      List<File> classesPath
           = new SearchFiles().filteredFiles(path + F_SEP
                   + classDir, ".class" );
      List<File> classesRelativePath
           = relativePath(path + F_SEP + classDir, classesPath );
      for (File i : classesRelativePath) {
         commandForJar.add(i.toString());
      }
      return commandForJar;
   }

   private void createManifest(File manifest, String main, String packageName) {
      try (PrintWriter write = new PrintWriter(manifest)) {
         if (packageName.length() > 0) {
            write.println("Main-Class: " + packageName + "." + main);
         }
         else {
            write.println("Main-Class: " + main);
         }
      }
      catch(IOException e) {
         eg.utils.FileUtils.logStack(e);
      }
   }

   private List<File> relativePath(String path, List<File> listOfFiles) {
      if (path.endsWith(F_SEP)) {
         path = path.substring(0, path.length() - 1);
      }
      List<File> relativePath = new ArrayList<>();
      for (File i : listOfFiles) {
         String filePath = i.getAbsolutePath();
         relativePath.add(new File(filePath.substring(path.length() + 1)));
      }
      return relativePath;
   }
}
