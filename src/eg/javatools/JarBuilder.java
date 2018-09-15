package eg.javatools;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

//--Eadgyth--/
import eg.console.Console;

/**
 * The creation of an executable jar file
 */
public class JarBuilder {

   private final static String F_SEP = File.separator;
   private final FilesFinder fFind = new FilesFinder();
   private final Console cons;

   private String successMsg = "";
   private String includedFilesErr = "";
   private String errorMsg = "";

   /**
    * @param cons  the reference to {@link Console}
    */
   public JarBuilder(Console cons) {
      this.cons = cons;
   }

   /**
    * Returns the message created if the jar was created
    *
    * @return  the message or the empty empty string if there is none
    */
    public String successMessage() {
       return successMsg;
    }

   /**
    * Returns the error message that indicates that non-Java files
    * for inclusion in the jar are not found
    *
    * @return  the message or the empty empty string if there is none
    */
   public String incudedFilesErr() {
      return includedFilesErr;
   }

   /**
    * Returns the error message that indicates that an error occured
    *
    * @return  the message or the empty empty string if there is none
    */
   public String errorMessage() {
      return errorMsg;
   }

   /**
    * Creates a jar file
    *
    * @param root  the root directory of the project
    * @param jarName  the name for the jar file
    * @param qualifiedMain  the fully qualified name of the main class
    * @param execDir  the name of the directory that contains class files.
    * Can be the empty string if nonClassExt is null.
    * @param sourceDir  the name of the directory that contains source
    * files. Can be the empty string if nonClassExt is null.
    * @param nonClassExt  the array of extensions of files that are
    * included in the jar file in addition to class files. May be null
    * @return  the booelan that is true if the process that creates the
    * jar terminates normally
    * @throws IOException  if the process that creates a jar cannot receive
    * any input
    * @throws InterruptedException  if the thread on which the process runs
    * is interrupted
    */
   public boolean createJar(String root, String jarName, String qualifiedMain,
         String execDir, String sourceDir, String[] nonClassExt)
         throws IOException, InterruptedException {

      includedFilesErr = "";
      successMsg = "";
      errorMsg = "";
      List<String> cmd = jarCmd(root, jarName, qualifiedMain, execDir, sourceDir,
            nonClassExt);

      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.directory(new File(root + "/" + execDir));
      pb.redirectErrorStream(true);
      Process p = pb.start();
      if (0 == p.waitFor()) {
         StringBuilder msg = new StringBuilder();
         String loc = new File(root).getName();
         if (execDir.length() > 0) {
            loc += F_SEP + execDir;
         }
         msg.append("Saved jar file named ")
               .append(jarName)
               .append(" in ").append(loc);

         successMsg = msg.toString();
         cons.printStatus(successMsg);
         return true;
      }
      else {
         errorMsg = "An error occured while creating the jar file";
         cons.printStatus(errorMsg);
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
      if (nonClassExt != null) {
         if (sourceDir.length() == 0 || execDir.length() == 0) {
            throw new IllegalArgumentException(
                  "A sources and a classes directory must be"
                  + " defined for copying non-java files");
         }
         for (String ext : nonClassExt) {
            List<File> toInclude = fFind.filteredFiles(searchRoot, ext, sourceDir);
            if (toInclude.isEmpty()) {
               StringBuilder msg = new StringBuilder();
               msg.append("Warning: ")
                     .append("Files with extension \"")
                     .append(ext)
                     .append("\" for inclusion in the jar were not found");

               includedFilesErr = msg.toString();
               cons.printStatus(includedFilesErr);
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
