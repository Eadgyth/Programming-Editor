package eg.javatools;

import java.io.File;
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
public class JarBuilder {

   private final static String F_SEP = File.separator;
   
   private final FilesFinder fFind = new FilesFinder();
   private final ConsolePanel consPnl;

   /**
    * @param consPnl  the reference to {@link ConsolePanel}
    */
   public JarBuilder(ConsolePanel consPnl) {
      this.consPnl = consPnl;
   }

   /**
    * Creates a jar file
    *
    * @param root  the root directory of the project
    * @param jarName  the name for the jar file
    * @param qualifiedMain  the fully qualified name of the main class
    * @param execDir  the name of the directory that contains class files.
    *       Can be the empty string but cannot be null.
    * @param sourceDir  the name of the directory that contains source
    *       files. Can be the empty string but cannot be not null.
    * @param includedExt  the array of extensions of files that are
    *       included in the jar file in addition to class files. May be
    *       null.
    * @throws IOException  if the process that creates a jar cannot receive
    *       any input
    */
   public void createJar(String root, String jarName, String qualifiedMain,
         String execDir, String sourceDir, String[] includedExt)
         throws IOException {

      List<String> cmd = jarCmd(root, jarName, qualifiedMain, execDir, sourceDir,
            includedExt);

      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.directory(new File(root + F_SEP + execDir));
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

   //
   //--private--
   //

   private List<String> jarCmd(String root, String jarName, String qualifiedMain,
          String execDir, String sourceDir, String[] includedExt) {

      List<String> cmd = new ArrayList<>();
      Collections.addAll(cmd, "jar", "-cvfe", jarName + ".jar", qualifiedMain);
      String searchRoot = root;
      if (execDir.length() > 0) {
         searchRoot += F_SEP + execDir;
      }
      List<File> classes
            = fFind.filteredFiles(searchRoot, ".class", sourceDir);
      List<File> relativeClassFilePaths
            = relativePaths(searchRoot, classes);
      relativeClassFilePaths.forEach((i) -> {
          cmd.add(i.toString());
       });
      if (includedExt != null) {
         for (String ext : includedExt) {
            List<File> includedFiles
                  = fFind.filteredFiles(searchRoot, ext, sourceDir);
            List<File> relativeInclFilePaths
                  = relativePaths(searchRoot, includedFiles);
            relativeInclFilePaths.forEach((f) -> {
                String path = f.getPath();
                 if (!(".properties".equals(ext)
                         && path.endsWith("eadconfig.properties"))) {
                     cmd.add(f.toString());
                 }
             });
         }
      }
      return cmd;
   }

   private List<File> relativePaths(String searchPath, List<File> listOfFiles) {
      if (searchPath.endsWith(F_SEP)) {
         searchPath = searchPath.substring(0, searchPath.length() - 1);
      }
      List<File> relativePath = new ArrayList<>();
      for (File i : listOfFiles) {
         String filePath = i.getAbsolutePath();
         relativePath.add(new File(filePath.substring(searchPath.length() + 1)));
      }
      return relativePath;
   }
}
