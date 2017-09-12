package eg.utils;

/**
 * Static methods to search for elements in text
 */
public class LinesFinder {
   
   /**
    * Returns the line which includes the specified position
    *
    * @param text  the text of the document
    * @param pos  the pos that is in the searched line
    * @return  the line that includes '{@code pos}'
    */
   public static String lineAtPos(String text, int pos) {
      int lastNewline = LinesFinder.lastNewline(text, pos);
      int nextNewline = LinesFinder.nextNewline(text, pos);
      if (lastNewline != -1) {
         return text.substring(lastNewline + 1, nextNewline);
      }
      else {
         return text.substring(0, nextNewline);
      }
   }
   
   /**
    * Returns the full lines of text which include the specified section
    *
    * @param  text  the entire text
    * @param section  a section of the text
    * @param pos  the position where <code>section</code> starts
    * @return  the full lines of text which include <code>section</code>
    */
   public static String allLinesAtPos(String text, String section, int pos) {
      String lines = null;
      String[] sectionArr = section.split("\n");
      String firstLine = LinesFinder.lineAtPos(text, pos);
      if (sectionArr.length > 0 && sectionArr.length > 1) {
         sectionArr[0] = firstLine;
         String lastLine = LinesFinder.lineAtPos(text, pos + section.length());
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < sectionArr.length - 1; i++) {
            sb.append(sectionArr[i]);
            sb.append("\n");
         }
         sb.append(lastLine);
         lines = sb.toString();
      }
      else {
         lines = firstLine;
      }
      return lines;
   }
   
   /**
    * Returns the position of the last newline before the specified
    * position
    *
    * @param text  the entire text
    * @param pos  the position relative to which the last newline
    * is searched
    * @return  the position of the last newline character before '{@code pos}'
    */
   public static int lastNewline(String text, int pos) {
      int i = text.lastIndexOf("\n", pos);
      if (i == pos) {
         i = text.lastIndexOf("\n", pos - 1);
      }
      return i;
   }
   
   /**
    * Returns the position of the next newline after the specified
    * position
    *
    * @param text  the entire text
    * @param pos  the position relative to which the next newline
    * is searched
    * @return  the position of the next newline character
    */
   public static int nextNewline(String text, int pos) {
      int i = text.indexOf("\n", pos);
      if (i == -1) {
         i = text.length();
      }
      return i;
   }
}
