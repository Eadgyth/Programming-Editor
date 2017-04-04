package eg.utils;

/**
 * Static methods to search for elements in text
 */
public class Finder {
   
   /**
    * Returns the line which includes the specified position
    *
    * @param text  the entire text
    * @param pos  the pos that includes the searched line
    * @return  the line that includes '{@code pos}'
    */
   public static String lineAtPos(String text, int pos) {
      int lastReturn = Finder.lastReturn(text, pos);
      int nextReturn = Finder.nextReturn(text, pos);

      if (lastReturn != -1) {
         return text.substring(lastReturn + 1, nextReturn);
      }
      else {
         return text.substring(0, nextReturn);
      }
   }
   
   /**
    * Returns the full lines of text which include the specified section
    *
    * @param  text  the entire text
    * @param section  a section of the text
    * @param pos  the position within the entire text where section starts
    * @return the full lines of text which include the specified section
    */
   public static String allLinesAtPos(String text, String section, int pos) {
      String[] sectionArr = section.split("\n");
      String firstLine = Finder.lineAtPos(text, pos);  
      sectionArr[0] = firstLine;
      if (sectionArr.length > 1) {
         String lastLine = Finder.lineAtPos(text, pos + section.length());
         sectionArr[sectionArr.length - 1] = lastLine;
      }
      StringBuffer sb = new StringBuffer();
      for (String s : sectionArr) {
         sb.append(s);
         sb.append("\n");
      }
      return sb.toString();
   }
   
   public static int lastReturn(String text, int pos) {
       int lastReturn = text.lastIndexOf("\n", pos);
       if (lastReturn == pos) {
         lastReturn = text.lastIndexOf("\n", pos - 1);
      }
      return lastReturn;
   }
   
   public static int nextReturn(String text, int pos) {
      int nextReturn = text.indexOf("\n", pos);
      if (nextReturn == -1) {
         nextReturn = text.length();
      }
      return nextReturn;
   }

   public static int countMotif(String text, String motif) {
      int count = 0;
      int index = 0;
      while (index != -1) {
         index = text.indexOf(motif, index);
         if (index != -1) {
            count++;
            index++;
         }
      }
      return count;
   }
}
