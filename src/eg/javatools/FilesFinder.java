package eg.javatools;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Static methods to find and list files in a directory and
 * sub-directories from that
 */
public class FilesFinder {

   private final List<File> resultList = new ArrayList<>();

   /**
    * Returns a <code>List</code> of all files in the specified
    * directory with the specified extension
    *
    * @param dir  the directory
    * @param extension  the file extension. Has the form '.java', for example.
    * @param excludedDirName  the name of an excluded directory
    * @return  the List of the files
    */
   public List<File> filteredFiles(String dir, String extension,
         String excludedDirName) {

      if (!testPath(dir)) {
         return null;
      }
      getFilteredFiles(dir, extension, excludedDirName);
      return resultList;
   }      

   //
   //--private--/
   //

   private void getFilteredFiles(String dir, String extension,
         String excludedDirName) {

      FilenameFilter filter = new FilenameFilter() {
         @Override
         public boolean accept(File direct, String name) {
            return name.endsWith(extension);
         }
      };
      File[] filesInPath = new File(dir).listFiles();
      File[] targets     = new File(dir).listFiles(filter);
      for (File f : targets) {
         resultList.add(f);
      }
      for (File f : filesInPath) {
         if (f.isDirectory()
             && (excludedDirName.length() == 0
             || !f.getName().equals(excludedDirName))) {

            getFilteredFiles(f.toString(), extension, excludedDirName);
         }
      }
   }

   private boolean testPath(String path) {
      File f = new File(path);
      return f.exists() && f.isDirectory();
   }
}
