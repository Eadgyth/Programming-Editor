package eg.utils;

import java.io.File;

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
}