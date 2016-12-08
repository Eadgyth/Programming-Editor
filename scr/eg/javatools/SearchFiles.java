package eg.javatools;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FilenameFilter;

public class SearchFiles {

   private final List<File> resultList = new ArrayList<>();
   
   /**
    * @param dir  the directory that contains the files to
    * collect
    * @param suffix  the extension of the files to collect. Has
    * the form '.java', for example.
    * @return  a List of the files in the specified directory
    * with the specified file extension
    */
   public List<File> filteredFiles(String dir, String suffix) {
      if (!testPath(dir)) {
         return null;
      }
      getFilteredFiles(dir, suffix);
      return resultList;
   }
   
   /**
    * @param dir  the directory that contains the files to
    * collect
    * @param suffix  the extension of the files to collect. Has
    * the form '.java', for example.
    * @return  an array of the files in the specified directory
    * with the specified file extension
    */
   public File[] filteredFilesToArr(String dir, String suffix) {
      if (!testPath(dir)) {
         return null;
      }
      getFilteredFiles(dir, suffix);
      File[] f = resultList.toArray(new File[resultList.size()]);
      return f;
   }

   private void getFilteredFiles(String dir, String suffix) {
      FilenameFilter filter = new FilenameFilter() {
         @Override
         public boolean accept(File direct, String name) {
            return name.endsWith(suffix);
         }
      };
      File[] filesInPath = new File(dir).listFiles();
      File[] targets     = new File(dir).listFiles(filter);      
      for (File f : targets) {
         resultList.add(f);
      }
      for (int i = 0; i < filesInPath.length; i++) {
         if (filesInPath[i].isDirectory()) {    
            getFilteredFiles(filesInPath[i].toString(), suffix);
         }
      }
   }
   
   private boolean testPath(String path) {
      File f = new File(path);
      return f.exists() && f.isDirectory();
   }      
}