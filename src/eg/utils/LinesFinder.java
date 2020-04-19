package eg.utils;

/**
 * Static methods to search for lines and line parameters
 */
public class LinesFinder {

   private static final char NEW_LINE = '\n';

   /**
    * Returns the content of the line that contains the specified
    * position
    *
    * @param text  the text
    * @param pos  the pos
    * @return  the line
    */
   public static String lineAtPos(String text, int pos) {
      int lastNewline = LinesFinder.lastNewline(text, pos);
      return line(text, lastNewline);
   }

   /**
    * Returns the content of the line that follows <code>lastNewline</code>
    *
    * @param text  the text
    * @param lastNewline  the position of the last newline
    * @return  the line
    * @see #lastNewline
    */
   public static String line(String text, int lastNewline) {
      if (text.length() == 0) {
         return text;
      }
      else {
         int start = lastNewline + 1;
         int lineEnd = LinesFinder.nextNewline(text, start);
         return text.substring(start, lineEnd);
      }
   }

   /**
    * Returns the possibly multiline content that follows
    * <code>lastNewline</code>
    *
    * @param  text  the text
    * @param lastNewline  the position of the last newline
    * @param length  the length of the section that is contained in
    * the line or lines
    * @return  the line or lines
    * @see #lastNewline
    */
   public static String lines(String text, int lastNewline, int length) {
      int linesEnd = LinesFinder.nextNewline(text, lastNewline + length);
      return text.substring(lastNewline + 1, linesEnd);
   }

   /**
    * Returns the position of the last newline before the specified
    * position even if a newline is found at the position
    *
    * @param text  the text
    * @param pos  the position
    * @return  the last newline position, -1 if the line is the first line
    */
   public static int lastNewline(String text, int pos) {
      int i = text.lastIndexOf(NEW_LINE, pos);
      if (i == pos) {
         i = text.lastIndexOf(NEW_LINE, pos - 1);
      }
      return i;
   }

   /**
    * Returns the position of the next newline behind the specified
    * position
    *
    * @param text  the text
    * @param pos  the position relative to which the next newline
    * is searched
    * @return  the position of the next newline character. The length
    * of text if no newline character is found
    */
   public static int nextNewline(String text, int pos) {
      int i = text.indexOf(NEW_LINE, pos);
      if (i == -1) {
         i = text.length();
      }
      return i;
   }

   /**
    * Returns the number of the line that contains the specified position
    *
    * @param text  the text
    * @param pos  the position
    * @return  the number
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
    * Returns the count of lines in the specified text
    *
    * @param text  the text
    * @return  the line number
    */
   public static int lineCount(String text) {
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
    * Returns if the specified text contains at least one
    * newline character
    *
    * @param text  the text
    * @return  the boolean value; true if multiline
    */
   public static boolean isMultiline(String text) {
      return text.length() > 1 && text.indexOf(NEW_LINE) > -1;
   }
   
   //
   //--private--/
   //
   
   private LinesFinder() {}
}
