package eg.javatools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * The creation of an executable jar file
 */
public class JarBuilder {

   private final static String MANIFEST_INFO_FILE = "ManifestInfo.txt";
   private final static String F_SEP = File.separator;
   private final FilesFinder fFind = new FilesFinder();

   private boolean isManifestInfo;
   private String successMsg = "";
   private String includedFilesErr = "";
   private String errorMsg = "";

   /**
    * Creates the file 'ManifestInfo.txt" that contains the classpaths
    * added to the Class-Path header in the manifest of the jar
    *
    * @param dir  the directory where the 'info'-file is saved
    * @param classpaths  the list of classpaths
    */
   public void createClasspathInfo(String dir, List<String> classpaths) {
      isManifestInfo = true;
      File f = new File(dir + "/" + MANIFEST_INFO_FILE);
      try (FileWriter writer = new FileWriter(f)) {
         writer.write("Class-Path:");
         for (String s : classpaths) {
            writer.write(" ");
            writer.write(s);
         }
         writer.write("\n");
      }
      catch (IOException e) {
         eg.utils.FileUtils.log(e);
      }
   }

   /**
    * Creates an executable jar file
    *
    * @param jarName  the name or pathname for the jar file. If not
    * a pathname the location of the jar file is classDir
    * @param qualifiedMain  the fully qualified name of the main class
    * @param classDir  the directory that contains class files
    * @param sourceDir  the directory that contains source files
    * @param nonClassExt  the array of extensions of files that are
    * included in the jar file. May be the zero length array
    * @return  true if the process that creates the jar terminates
    * normally
    * @throws IOException  if an IO error occurs
    * @throws InterruptedException  if the process that creates the
    * jar file is interupted by another thread
    */
   public boolean createJar(
            String jarName,
            String qualifiedMain,
            String classDir,
            String sourceDir,
            String[] nonClassExt)
            throws IOException, InterruptedException {

      includedFilesErr = "";
      successMsg = "";
      errorMsg = "";
      List<String> cmd = jarCmd(
            jarName,
            qualifiedMain,
            classDir,
            sourceDir,
            nonClassExt);

      StringBuilder msg = new StringBuilder();
      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.directory(new File(classDir));
      pb.redirectErrorStream(true);
      Process p = pb.start();
      if (0 == p.waitFor()) {
         File f = new File(jarName);
         msg.append("Saved jar file named ")
               .append(f.getName())
               .append(".\n\nThe location is:\n")
               .append(f.getParent());

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

   /**
    * Returns the message that is set if the jar was created
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

   //
   //--private--/
   //

   private List<String> jarCmd(
            String jarName,
            String qualifiedMain,
            String classDir,
            String sourceDir,
            String[] nonClassExt) {

      List<String> cmd = new ArrayList<>();
      if (!isManifestInfo) {
         Collections.addAll(cmd, "jar", "cfe", jarName, qualifiedMain);
      }
      else {
         Collections.addAll(cmd, "jar", "cfme", jarName, MANIFEST_INFO_FILE,
               qualifiedMain);
      }
      List<File> classes
            = fFind.filteredFiles(classDir, ".class", sourceDir, "");

      List<File> relativeClassFilePaths
            = relativePaths(classDir, classes);

      relativeClassFilePaths.forEach((i) -> {
          cmd.add(i.toString());
      });
      if (nonClassExt.length > 0) {
         for (String ext : nonClassExt) {
            List<File> toInclude = fFind.filteredFiles(classDir, ext, sourceDir,
                  MANIFEST_INFO_FILE);

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
                     = relativePaths(classDir, toInclude);

               relativeInclFilePaths.forEach((f) -> {
                  cmd.add(f.toString());
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
