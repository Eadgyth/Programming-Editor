package eg.javatools;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FilenameFilter;

public class SearchFiles {

   private final List<File> resultList = new ArrayList<>();
   
   public List<File> filteredFiles(String path, String suffix) {
      testPath(path);
      getFiles(path, suffix);
      return resultList;
   }
   
   public File[] filteredFilesToArr(String path, String suffix) {
      if (!testPath(path)) {
         return null;
      }
      getFiles(path, suffix);
      File[] f = resultList.toArray(new File[resultList.size()]);
      return f;
   }

   private void getFiles(String path, String suffix) {
      FilenameFilter filter = new FilenameFilter() {
         @Override
         public boolean accept(File direct, String name) {
            return name.endsWith(suffix);
         }
      };

      File[] filesInPath = new File(path).listFiles();
      File[] targets     = new File(path).listFiles(filter);
      
      for (File f : targets) {
         resultList.add(f);
      }

      for (int i = 0; i < filesInPath.length; i++) {
         if ( filesInPath[i].isDirectory() ) {    
            getFiles(filesInPath[i].toString(), suffix);
         }
      }
   }
   
   private boolean testPath(String path) {
      File f = new File(path);
      return f.exists() && f.isDirectory();
   }      
}