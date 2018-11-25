package eg.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import java.text.SimpleDateFormat;

/**
 * Static methods for file operations
 */
public class FileUtils {
   
   /**
    * The system's line separator */
   public final static String LINE_SEP = System.lineSeparator();
   
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
    * @return  the string with slashes replaced with periods
    */
   public static String dottedFileSeparators(String path) {
      String dottedPath = path.replace("\\", "/");
      dottedPath = dottedPath.replace("/", ".");
      return dottedPath;
   }
   
   /**
    * Returns an array in which the files in the specified array
    * of files are sorted such that directories are listed first
    * and files after it
    *
    * @param toSort  the array of files
    * @return  the array of sorted files
    */
   public static File[] sortedFiles(File[] toSort) {
      List<File> all = new ArrayList<>();
      List<File> files = new ArrayList<>();
      for (File f : toSort) {
           if (f.isDirectory()) {
               all.add(f);
           } else {
               files.add(f);
           }
       }
      all.addAll(files);
      File[] sortedList = all.toArray(new File[toSort.length]);
      return sortedList;
   }
   
   /**
    * Returns if a file is writeable and shows a message dialog
    * if not
    *
    * @param f  the file
    * @return  true if writeable
    */
   public static boolean isWriteable(File f) {
      File sameName = new File(f.toString());
      boolean isWriteable = !f.exists() || f.renameTo(sameName);
      if (!isWriteable) {
         Dialogs.errorMessage(
               f.getName()
               + " cannot be accessed."
               + " It may be used by another process.",
               null);
      }
      return isWriteable;
   }
   
   /**
    * Deletes a folder and its content
    *
    * @param dir  the directory to be deleted
    * @return  true if dir has been deleted
    */
   public static boolean deleteFolder(File dir) {
      boolean ret = true;
      if (dir.isDirectory()) {
         for (File f : dir.listFiles()) {
            ret = ret && FileUtils.deleteFolder(f);
         }
      }
      return ret && dir.delete();
   }
   
   /**
    * Returns if the specified directory is empty
    *
    * @param dir  the directory
    * @return  true if dir is empty
    */
   public static boolean isFolderEmpty(File dir) {
      File[] content = dir.listFiles();
      return content.length == 0;
   }
   
   /**
    * Appends to the file 'log.txt' in the program folder the date,
    * message and stack trace of an exception
    *
    * @param e  the Exception
    */
   public static void log(Exception e) {
      File f = new File("log.txt");
      String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
      try (FileWriter writer = new FileWriter(f, true)) {
         writer.write(date + "\n");
         writer.write(e.getMessage() + LINE_SEP);
         for (StackTraceElement el : e.getStackTrace()) {
            writer.write("   " + el.toString() + LINE_SEP);
         }
         writer.write("_________________" + LINE_SEP);
         Dialogs.errorMessage("Error: " + e.getMessage(), null);
      }
      catch(IOException ioe) {
         throw new RuntimeException(
               "Could not write to the log file", ioe);
      }
   }
}
