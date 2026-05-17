package eg.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.Date;

import java.text.SimpleDateFormat;

/**
 * Static methods for file operations
 */
public class FileUtils {

   /**
    * Adds the specified extension to the specified string if it does
    * not end with this extension already
    *
    * @param file  the string that represents a file
    * @param ext  the extension
    * @return  the string with the extension
    */
   public static String addExtension(String file, String ext) {
      if (file.endsWith(ext)) {
         return file;
      }
      else {
         return file + ext;
      }
   }

   /**
    * Returns if a file is writeable and shows a message dialog if not
    *
    * @param f  the file
    * @return  true if writeable
    */
   public static boolean isWriteable(File f) {
      boolean isWriteable = !f.exists() || f.renameTo(f);
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
    * Writes to the file 'log.txt' in the '.eadgyth' folder the date,
    * message and stack trace of an exception. However, a new message
    * replaces an older one.
    *
    * @param e  the Exception
    */
   public static void log(Exception e) {
      File f = new File(SystemParams.EADGYTH_DATA_DIR + "/log.txt");
      String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
      String msg = "";
      if (e.getMessage() != null) {
         msg = e.getMessage();
      }
      else if (msg.isEmpty() && e.getCause() != null) {
         msg = e.getCause().toString();
      }
      else {
         msg = "A problem occured";
      }
      if (f.exists() && f.length() > 1024 * 1024) {
         f.delete();
      }
      try (FileOutputStream fos = new FileOutputStream(f, true); 
         OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
         PrintWriter writer = new PrintWriter(osw)) {
         
         writer.println();
         writer.println();
         writer.println(date);
         writer.println("_________________");
         e.printStackTrace(writer);
         writer.println();
         
         Dialogs.errorMessage(
               "Error: "
               + msg
               + "\nSee "
               + f.toString(),
               null);
      }
      catch (IOException fne) {
         Dialogs.errorMessage(
              "Error: "
               + msg // the message that could not be logged
               + "\nNOTE: Could not write to log file",
               null);
      }
   }

   //
   //--private--/
   //

   private FileUtils() {}
}
