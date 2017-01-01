package eg.utils;

import java.util.ArrayList;

/**
 * Static methods to search for elements in text
 */
public class Finder {

   /**
    * @param in  the array of lines of a text
    * @return  an array of start positions of the lines of a passed in
    * text whose lines are splitted into an array 
    */
   public static int[] startOfLines(String[] in) {
      int[] startOfLines = new int[in.length];
      int startOfLine = 0;
      startOfLines[0] = 0;
      for (int i = 1; i < startOfLines.length; i++) {
         startOfLine += in[i - 1].length();
         startOfLines[i] = startOfLine + i; // +i to add the missing new lines
      }   
      return startOfLines;
   }

   public static String currLine(String in, int pos) {
      int indLastReturn = Finder.lastReturn(in, pos);
      int indNextReturn = in.indexOf("\n", pos);

      if (indLastReturn != -1 && indNextReturn != -1) {
         return in.substring(indLastReturn + 1, indNextReturn + 1);
      }
      else if (indLastReturn != -1 && indNextReturn == -1) {
         return in.substring(indLastReturn + 1);
      }
      else if (indLastReturn == -1 && indNextReturn != -1) {
         return in.substring(0, indNextReturn + 1);
      }
      else {
         return in;
      }
   }
   
   public static int lastReturn(String in, int pos) {
       int indLastReturn = in.lastIndexOf("\n", pos);
       if (indLastReturn == pos) {
         indLastReturn = in.lastIndexOf("\n", pos - 1);
      }
      return indLastReturn;
   }

   public static int countMotif(String in, String motif) {
      int count = 0;
      int index = 0;
      while (index != -1) {
         index = in.indexOf(motif, index);
         if (index != -1) {
            count++;
            index++;
         }
      }
      return count;
   }
}
