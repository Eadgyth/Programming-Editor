package eg.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Date;

import java.text.SimpleDateFormat;

/**
 * Static methods for file operations
 */
public class FileUtils {
   
   private final static String LINE_SEP = System.lineSeparator();

   /**
    * Adds the specified extension to the string if it does not end with
    * with this extension already
    *
    * @param file  the string that represents a file
    * @param ext  the extension
    * @return  the string with the extension
    */
   public static String addExtension (String file, String ext) {
      if (file.endsWith(ext)) {
         return file;
      }
      else {
         return file + ext;
      }
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
    * Appends to the file 'log.txt' in the '.eadgyth' folder the date,
    * message and stack trace of an exception
    *
    * @param e  the Exception
    */
   public static void log(Exception e) {
      File f = new File(SystemParams.EADGYTH_DATA_DIR + "/log.txt");
      String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
      try (FileWriter writer = new FileWriter(f, false)) {
         writer.write(" " + date + "\n");
         writer.write(e.getMessage() + LINE_SEP);
         for (StackTraceElement el : e.getStackTrace()) {
            writer.write("   " + el.toString() + LINE_SEP);
         }
         writer.write("_________________" + LINE_SEP);
         Dialogs.errorMessage(
               "Error: "
               + e.getMessage()
               + ".\nSee also "
               + f.toString(),
               null);
      }
      catch (IOException ioe) {
         Dialogs.errorMessage(
           "Error: "
            + e.getMessage() // the message that could not be logged
            + "\nNOTE: Could not write to log file",
            null);
      }
   }

   //
   //--private--/
   //

   private FileUtils() {}
}
