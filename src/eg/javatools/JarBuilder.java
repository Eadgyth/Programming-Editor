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
import eg.utils.Dialogs;

/**
 * The creation of an executable jar file.
 * <p>
 * The jar file is saved in the directory that is specified as executables
 * directory or in the root directory of a project.
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
    * Can be the empty string but cannot be null.
    * @param sourceDir  the name of the directory that contains source
    * files. Can be the empty string but cannot be not null.
    * @param includedFiles  the array of filenames and/or extensions of files
    * that are included in the jar file in addition to class files. May be
    * null.
    * @throws IOException  if the process that creates a jar cannot receive
    * any input
    */
   public void createJar(String root, String jarName, String qualifiedMain,
         String execDir, String sourceDir, String[] includedFiles)
         throws IOException {

      List<String> cmd = jarCmd(root, jarName, qualifiedMain, execDir, sourceDir,
            includedFiles);

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
   //--private--//
   //

   private List<String> jarCmd(String root, String jarName, String qualifiedMain,
          String execDir, String sourceDir, String[] includedFiles) {

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
      if (includedFiles != null) {
         for (String fStr : includedFiles) {
            List<File> included
                  = fFind.filteredFiles(searchRoot, fStr, sourceDir);

            if (included.size() == 0) {
               Dialogs.errorMessage(
                     "<html>"
                     + "\"" + fStr + "\" could not be found.<br>"
                     + "This is indicated as file or file type to be included"
                     + " in a jar file."
                     + "</html>",
                     null);
            }
            else {
               List<File> relativeInclFilePaths
                     = relativePaths(searchRoot, included);

               relativeInclFilePaths.forEach((f) -> {
                  String path = f.getPath();
                    if (!path.endsWith("eadconfig.properties")) {

                        cmd.add(f.toString());
                    }
                });
             }
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
