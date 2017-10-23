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
   
   public static String fileSuffix(String file) {
      int indDot = file.lastIndexOf(".") + 1;
      int indFileSep = file.lastIndexOf(File.separator);
      if (indDot > indFileSep) {
         return file.substring(indDot);
      }
      else {
         return "";
      }
   }
   
   /**
    * Deletes a folder and its content
    *
    * @param dir  the directory to be deleted
    * @return  true if the directory has been deleted
    */
   public static boolean deleteFolder(File dir) {
      boolean ret = true;
      if (dir.isDirectory()){
         for (File f : dir.listFiles()) {
            ret = ret && FileUtils.deleteFolder(f);
         }
      }
      return ret && dir.delete();
   }
   
   /**
    * If the specified directory is empty
    *
    * @param dir  the directory
    * @return  if dir is empty
    */
   public static boolean isFolderEmpty(File dir) {
      File[] content = dir.listFiles();
      return content.length == 0;
   }
   
   /**
    * Appends to the file 'log.txt' the stack trace of an exception
    * and shows a warning in a dialog window.
    * <p>The "log" file is saved in the program's directory.
    *
    * @param e  an <code>Exception</code>
    */
   public static void logStack(Exception e) { 
      File logFile = new File("log.txt");
      try (PrintWriter pw = new PrintWriter(new FileOutputStream(logFile, true))) {
         e.printStackTrace(pw);
         Dialogs.errorMessage("Error:\n" + e.getMessage()
               + ".\nSee log.txt file");
      }
      catch(IOException ioe) {
         throw new RuntimeException(
               "Could not write the stack trace to the log file", ioe);
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
