package eg.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class FileUtils {
   
   /**
    * @param fileStr  the String that represents a file
    * @return  the extension of a file in the form of, e.g., [.txt]
    * or the empty String if an extension can not be safely determined
    */
   public static String extension(String fileStr) {
      int indDot = fileStr.lastIndexOf(".");
      int indFileSep = fileStr.lastIndexOf(eg.Constants.F_SEP);
      if (indDot > indFileSep) {
         return fileStr.substring(indDot);
      }
      else {
         return "";
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
   
   /**
    * Appends to the file 'log.txt' the class name and message of
    * an exception
    * @param e  an {@code Exception}
    */
   public static void log(Exception e) { 
      try (FileWriter writer = new FileWriter("log.txt", true)) {
         writer.write(e.getClass().getName() + ": " + e.getMessage()
               + eg.Constants.SYS_LINE_SEP);
      }
      catch(IOException ioe) {
         FileUtils.log(ioe);
      }
   }
   
   public static void emptyLog() { 
      try (FileWriter writer = new FileWriter("log.txt", false)) {
         writer.write("");
      }
      catch(IOException ioe) {
         FileUtils.log(ioe);
      }
   }   
}
