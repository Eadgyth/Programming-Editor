package eg.utils;

/**
 * Static methods to search for elements in text
 */
public class Finder {
   
   public static String currLine(String text, int pos) {
      int indLastReturn = Finder.lastReturn(text, pos);
      int indNextReturn = text.indexOf("\n", pos);

      if (indLastReturn != -1 && indNextReturn != -1) {
         return text.substring(indLastReturn + 1, indNextReturn);
      }
      else if (indLastReturn != -1 && indNextReturn == -1) {
         return text.substring(indLastReturn + 1);
      }
      else if (indLastReturn == -1 && indNextReturn != -1) {
         return text.substring(0, indNextReturn);
      }
      else {
         return text;
      }
   }
   
   public static int lastReturn(String text, int pos) {
       int indLastReturn = text.lastIndexOf("\n", pos);
       if (indLastReturn == pos) {
         indLastReturn = text.lastIndexOf("\n", pos - 1);
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
