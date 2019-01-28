package eg.javatools;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * The creation of an executable jar file
 */
public class JarBuilder {

   private final static String F_SEP = File.separator;
   private final FilesFinder fFind = new FilesFinder();

   private String successMsg = "";
   private String includedFilesErr = "";
   private String errorMsg = "";

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
    * @return  the message; the empty empty string if there is none
    */
   public String incudedFilesErr() {
      return includedFilesErr;
   }

   /**
    * Returns the error message that indicates that the jar file
    * could not be created
    *
    * @return  the message; the empty empty string if there is none
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
    * @throws IOException  as specified by ProcessBuilder
    * @throws InterruptedException  as specified by Process
    */
   public boolean createJar(String root, String jarName, String qualifiedMain,
         String execDir, String sourceDir, String[] nonClassExt)
         throws IOException, InterruptedException {

      includedFilesErr = "";
      successMsg = "";
      errorMsg = "";
      List<String> cmd = jarCmd(root, jarName, qualifiedMain, execDir, sourceDir,
            nonClassExt);

      StringBuilder msg = new StringBuilder();
      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.directory(new File(root + "/" + execDir));
      pb.redirectErrorStream(true);
      Process p = pb.start();
      if (0 == p.waitFor()) {
         String loc = new File(root).getName();
         if (execDir.length() > 0) {
            loc += F_SEP + execDir;
         }
         msg.append("Saved jar file named ")
               .append(jarName)
               .append(" in ")
               .append(loc);

         successMsg = msg.toString();
         return true;
      }
      else {
         msg.append("An error occured while creating the jar file ")
               .append(jarName);
       
         errorMsg = msg.toString();
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
      if (!execDir.isEmpty()) {
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
         if (sourceDir.isEmpty() || execDir.isEmpty()) {
            throw new IllegalArgumentException(
                  "A sources and a classes directory must be"
                  + " defined for copying non-java files");
         }
         for (String ext : nonClassExt) {
            List<File> toInclude = fFind.filteredFiles(searchRoot, ext, sourceDir);
            if (toInclude.isEmpty()) {
               StringBuilder msg = new StringBuilder();
               msg.append("\nNOTE: ")
                     .append("Files with extension \"")
                     .append(ext)
                     .append("\" for inclusion in the jar were not found");

               includedFilesErr = msg.toString();
            }
            else {
               List<File> relativeInclFilePaths
                     = relativePaths(searchRoot, toInclude);

               for (File f : relativeInclFilePaths) {
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
