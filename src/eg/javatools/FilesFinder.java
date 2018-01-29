package eg.javatools;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FilenameFilter;

/**
 * The list of files in a directory and its sub-directories with a given
 * file extension
 */
public class FilesFinder {

   private List<File> resultList;

   /**
    * Returns a <code>List</code> of all files with the specified
    * extension in the specified directory and its sub-directories
    *
    * @param dir  the directory
    * @param extension  the file extension. Has the form '.java', for example.
    * @param excludedDirName  the name of an exclueded directory
    * @return  the List of the files
    */
   public List<File> filteredFiles(String dir, String extension,
         String excludedDirName) {         

      File f = new File(dir);
      if (!f.exists() || !f.isDirectory()) {
         throw new IllegalArgumentException(dir + " is not a directory");
      }
      resultList = new ArrayList<>();
      setFilteredFiles(f, extension, excludedDirName);
      return resultList;
   }
   
   public static String notFoundMessage(String name) {
      if (name.startsWith(".")) {
         return "No files with the extension " + name + " was found";
      }
      else {
         return name + " was not found";
      }
   }

   //
   //--private--//
   //

   private void setFilteredFiles(File f, String extension, String excl) {
      FilenameFilter filter = (File dir, String name) -> name.endsWith(extension);
      File[] list = f.listFiles();
      File[] targets  = f.listFiles(filter);
      resultList.addAll(Arrays.asList(targets));
      for (File fInList : list) {
         if (fInList.isDirectory()) {
            if (excl.length() == 0 || !fInList.getName().equals(excl)) {
                  setFilteredFiles(fInList, extension, excl);
            }
         }
      }
   }
}
