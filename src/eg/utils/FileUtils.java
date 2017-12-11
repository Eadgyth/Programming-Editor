package eg.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//--Eadgyth--/
import eg.Constants;

/**
 * Static methods for file operations
 */
public class FileUtils {
   
   /**
    * Returns the extension of the specified file
    *
    * @param file  the file
    * @return  the extension. The empty string if no period is found
    * after the last file separator
    */
   public static String fileExtension(String file) {
      int i = file.lastIndexOf(".") + 1;
      int j = file.lastIndexOf(File.separator);
      if (i > j) {
         return file.substring(i);
      }
      else {
         return "";
      }
   }
   
   /**
    * Replaces slashes in the specified string (forward or backward) with
    * periods
    *
    * @param path  the string
    * @return  the string with slashes replaecd with periods
    */
   public static String dottedFileSeparators(String path) {
      String dottedPath = path.replace("\\", "/");
      dottedPath = dottedPath.replace("/", ".");
      return dottedPath;
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
    * Appends to the file 'log.txt' the message and stack trace of an exception
    * <p>The file is saved in the program's directory.
    *
    * @param e  the <code>Exception</code>
    */
   public static void logStack(Exception e) {
      File f = new File("log.txt");
      try (FileWriter writer = new FileWriter(f, true)) {
         writer.write(e.getMessage() + Constants.LINE_SEP);
         for (StackTraceElement el : e.getStackTrace()) {
            writer.write("   " + el.toString() + Constants.LINE_SEP);
         }
         writer.write("_________________" + Constants.LINE_SEP);
         Dialogs.errorMessage("Error: " + e.getMessage());
      }
      catch(IOException ioe) {
         throw new RuntimeException(
               "Could not write to the log file", ioe);
      }
   }
}
