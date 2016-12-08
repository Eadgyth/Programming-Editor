package eg.utils;

import java.io.File;
import java.io.FileNotFoundException;

public class FileUtils {
   
   private static final String F_SEP = File.separator;
   
   public static String extension(String fileStr) {
      int indDot = fileStr.lastIndexOf(".");
      int indFileSep = fileStr.lastIndexOf(F_SEP);
      if (indDot > indFileSep) {
         return fileStr.substring(indDot);
      }
      else {
         return null;
      }
   }
   
   /**
    * Deletes a folder that contains data
    * @param dir  the directory to be deleted
    * @return  true if the directory has been deleted
    */
   public static boolean deleteFolder(File dir) {
      boolean ret = true;
      if (dir.isDirectory()){
         for (File f : dir.listFiles()){
            ret = ret && FileUtils.deleteFolder(f);
         }
      }
      return ret && dir.delete();
   }
   
   /**
    * Deletes a folder if it does not contain data
    * @param dir  the directory to be deleted
    * @return  true if the directory has been deleted
    */ 
   public static boolean deleteEmptyFolder(File dir) {
      boolean ret = false;
      File[] content = dir.listFiles();
      if (content.length == 0) {
         ret = dir.delete();
      }
      return ret;
   }
   
   public static boolean isFolderEmpty(File path) {
      File[] content = path.listFiles();
      return content.length == 0;
   }
}