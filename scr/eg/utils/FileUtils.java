package eg.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Static methods to work with files
 */
public class FileUtils {
   
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
   
   /**
    * If the folder given by the specified directory is empty
    * @param dir  the directory
    * @return  if the folder specifies by the directory is empty
    */
   public static boolean isFolderEmpty(File dir) {
      File[] content = dir.listFiles();
      return content.length == 0;
   }
   
   /**
    * Appends to the file 'log.txt' the class name and message of
    * an exception.
    * <p>
    * The "log" file is saved in the program's directory.
    * @param e  an {@code Exception}
    */
   public static void logMessage(Exception e) { 
      try (FileWriter writer = new FileWriter("log.txt", true)) {
         writer.write(e.getClass().getName() + ": " + e.getMessage()
               + eg.Constants.LINE_SEP);
      }
      catch(IOException ioe) {
         throw new RuntimeException("Could not write exception message to file", ioe);
      }
   }
   
   public static void logStack(Exception e) { 
      File logFile = new File("log.txt");
      try (PrintWriter pw = new PrintWriter(new FileOutputStream(logFile, true))) {
         e.printStackTrace(pw);
      }
      catch(IOException ioe) {
         throw new RuntimeException("Could not write stack trace to file", ioe);
      }
   }
   
   /**
    * Empties the 'log' file saved in the program's directory
    */
   public static void emptyLog() { 
      try (FileWriter writer = new FileWriter("log.txt", false)) {
         writer.write("");
      }
      catch(IOException ioe) {
          throw new RuntimeException("Could not empty the log file", ioe);
      }
   }   
}
