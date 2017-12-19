package eg.utils;

/**
 * Static methods to search for line parameters
 */
public class LinesFinder {
   
   private final static char NEW_LINE = '\n';
   
   /**
    * Returns the line which includes the specified position. The line
    * starts at the position that follows the last newline
    *
    * @param text  the text
    * @param pos  the pos that is in the searched line
    * @return  the line
    */
   public static String lineAtPos(String text, int pos) {
      int lastNewline = LinesFinder.lastNewline(text, pos);
      int nextNewline = LinesFinder.nextNewline(text, pos);
      return line(text, lastNewline, nextNewline);
   }
   
   /**
    * Returns the line between the specified line start and line end
    * positions. The line starts at the position that follows the last
    * newline. Is called by {@link #lineAtPos}
    *
    * @param text  the text
    * @param lineStart  the start position
    * @param lineEnd  the end position
    * @return  the line
    */
   public static String line(String text, int lineStart, int lineEnd) {
      return text.substring(lineStart + 1, lineEnd);
   }
   
   /**
    * Returns the full lines of text which include the specified section
    *
    * @param  text  the text
    * @param section  the section
    * @param pos  the position where <code>section</code> starts
    * @return  the full lines of text
    */
   public static String allLinesAtPos(String text, String section, int pos) {
      StringBuilder sb = new StringBuilder();
      sb.append(LinesFinder.lineAtPos(text, pos));
      if (LinesFinder.isMultiline(section)) {
         sb.append("\n");        
         String[] sectionArr = section.split("\n");
         for (int i = 1; i < sectionArr.length - 1; i++) {
            sb.append(sectionArr[i]);
            sb.append("\n");
         }
         int endPos = pos + section.length();
         if (section.endsWith("\n")) {
            endPos -= 1;
         } 
         sectionArr[sectionArr.length - 1]
                = LinesFinder.lineAtPos(text, endPos);
         sb.append(sectionArr[sectionArr.length - 1]);
      }
      return sb.toString();
   }
   
   /**
    * Returns the position of the last newline before the specified
    * position
    *
    * @param text  the text
    * @param pos  the position
    * @return  the newline position. 0 if the line is the first line
    */
   public static int lastNewline(String text, int pos) {
      int i = text.lastIndexOf(NEW_LINE, pos);
      if (i == pos) {
         i = text.lastIndexOf(NEW_LINE, pos - 1);
      }
      else if (i == -1) {
         i = 0;
      }
      return i;
   }
   
   /**
    * Returns the position of the next newline after the specified
    * position
    *
    * @param text  the text
    * @param pos  the position relative to which the next newline
    * is searched
    * @return  the position of the next newline character
    */
   public static int nextNewline(String text, int pos) {
      int i = text.indexOf(NEW_LINE, pos);
      if (i == -1) {
         i = text.length();
      }
      return i;
   }
   
   /**
    * Returns the number of the line where the specified position
    * is located
    *
    * @param text  the text
    * @param pos  the position in the text
    * @return  the number of the line where the specified
    * position is located
    */ 
   public static int lineNrAtPos(String text, int pos) {
      int count = 0;
      int i = 0;
      while (i != -1) {
         i = text.indexOf(NEW_LINE, i);
         if (i != -1) {
            if (i >= pos) {
               break;
            }
            count++;
            i++;
         }
      }
      return count + 1;
   }
   
   /**
    * Returns the number of lines of the specified text
    *
    * @param text  the text
    * @return  the line number
    */
   public static int lineNumber(String text) {
      int count = 1;
      int i = 0;
      while (i != -1) {
         i = text.indexOf(NEW_LINE, i);
         if (i != -1) {
            count++;
            i++;
         }
      }
      return count;
   }
   
   /**
    * Returns the boolean that indicates if the specified text
    * contains at least one newline
    *
    * @param text  the text
    * @return  the boolean value
    */
   public static boolean isMultiline(String text) {
      return text.length() > 1 && text.indexOf(NEW_LINE) > -1;
   }
}
