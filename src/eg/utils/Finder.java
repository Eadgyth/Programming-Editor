package eg.utils;

/**
 * Static methods to search for elements in text
 */
public class Finder {

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
