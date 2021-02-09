package eg.javatools;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FilenameFilter;

import eg.Prefs;

/**
 * The list of files in a directory and its sub-directories with a given
 * file extension
 */
public class FilesFinder {

   private List<File> resultList;

   /**
    * Returns a <code>List</code> of all files with the specified
    * extension in the specified directory and its sub-directories.
    * The file 'ProjectConfig.properties' is alsways excluded.
    *
    * @param dir  the directory
    * @param extension  the file extension which starts with a period.
    * @param excludedDir  the directory that is excluded from the search.
    * Ignored if equal to dir
    * @param excludedFileName  the name of a file to be excluded
    * @return  the List of the files
    */
   public List<File> filteredFiles(String dir, String extension,
         String excludedDir, String excludedFileName) {

      if (!extension.startsWith(".")) {
         throw new IllegalArgumentException(
               extension
               + " must be specified"
               + " with preceding peroid");
      }
      File fDir = new File(dir);
      if (!fDir.exists() || !fDir.isDirectory()) {
         throw new IllegalArgumentException(
               dir + " is not a directory");
      }
      File fExcl = new File(excludedDir);
      String excl = fExcl.getPath().equals(fDir.getPath()) ? "" : fExcl.getPath();
      resultList = new ArrayList<>();
      setFilteredFiles(fDir, extension, excl, excludedFileName);
      return resultList;
   }

   //
   //--private--//
   //

   private void setFilteredFiles(File f, String extension, String exclDir,
         String exclFileName) {

      FilenameFilter filter = (File dir, String name)
            -> name.endsWith(extension)
                  && !name.equals(exclFileName)
                  && !name.equals(Prefs.PROJ_CONFIG_FILE);

      File[] list = f.listFiles();
      File[] targets  = f.listFiles(filter);
      if (list == null || targets == null) {
         throw new IllegalArgumentException(
               f + " is not a directory");
      }
      resultList.addAll(Arrays.asList(targets));
      for (File fInList : list) {
         if (fInList.isDirectory()
        	   && (exclDir.isEmpty() || !fInList.getPath().equals(exclDir))) {
        	
            setFilteredFiles(fInList, extension, exclDir, exclFileName);
         }
      }
   }
}
