package eg.javatools;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

//--Eadgyth--//
import eg.console.ConsolePanel;

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
   
   private String includedFilesErr = "";

   /**
    * @param consPnl  the reference to {@link ConsolePanel}
    */
   public JarBuilder(ConsolePanel consPnl) {
      this.consPnl = consPnl;
   }
   
   /**
    * Returns the error message that indicates that non-Java files
    * for inclusion are not found
    *
    * @return  the message or the empty empty string
    */
   public String getIncudedFilesErr() {
      return includedFilesErr;
   }

   /**
    * Creates a jar file
    *
    * @param root  the root directory of the project
    * @param jarName  the name for the jar file
    * @param qualifiedMain  the fully qualified name of the main class
    * @param execDir  the name of the directory that contains class files.
    * Can be the empty string but cannot be null
    * @param sourceDir  the name of the directory that contains source
    * files. Can be the empty string but cannot be not null
    * @param nonClassExt  the array of extensions of files that are included
    * in the jar file in addition to class files. May be null
    * @return  the booelan that is true if the process that creates the jar
    * terminates normally
    * @throws IOException  if the process that creates a jar cannot receive
    * any input
    * @throws InterruptedException  if the thread on which the process runs
    * is interrupted
    */
   public boolean createJar(String root, String jarName, String qualifiedMain,
         String execDir, String sourceDir, String[] nonClassExt)
         throws IOException, InterruptedException {

      List<String> cmd = jarCmd(root, jarName, qualifiedMain, execDir, sourceDir,
            nonClassExt);

      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.directory(new File(root + "/" + execDir));
      pb.redirectErrorStream(true);
      Process p = pb.start();
      if (0 == p.waitFor()) {
         return true;
      }
      else {
         return false;
      }
   }

   //
   //--private--/
   //

   private List<String> jarCmd(String root, String jarName, String qualifiedMain,
          String execDir, String sourceDir, String[] nonClassExt) {

      List<String> cmd = new ArrayList<>();
      Collections.addAll(cmd, "jar", "-cfe", jarName + ".jar", qualifiedMain);
      String searchRoot = root;
      if (execDir.length() > 0) {
         searchRoot += "/" + execDir;
      }
      List<File> classes
            = fFind.filteredFiles(searchRoot, ".class", sourceDir);
      List<File> relativeClassFilePaths
            = relativePaths(searchRoot, classes);
      relativeClassFilePaths.forEach((i) -> {
          cmd.add(i.toString());
      });
      includedFilesErr = "";
      if (nonClassExt != null) {
         if (sourceDir.length() == 0 || execDir.length() == 0) {
            throw new IllegalArgumentException(
                  "A sources and a classes directory must be"
                  + " defined for copying non-java files");
         }
         for (String ext : nonClassExt) {
            List<File> toInclude = fFind.filteredFiles(searchRoot, ext, sourceDir);
            if (toInclude.isEmpty()) {
               includedFilesErr =
                     "Files with extension \"" + ext + "\" for inclusion"
                     + " in the jar were not found.";
            }
            else {
               List<File> relativeInclFilePaths
                     = relativePaths(searchRoot, toInclude);

               for (File f : relativeInclFilePaths) {
                  String path = f.getPath();
                  cmd.add(f.toString());
               }
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
