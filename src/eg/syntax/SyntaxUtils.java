package eg.syntax;

/**
 * Static methods to search for text elements
 */
public class SyntaxUtils {

   private SyntaxUtils() {}

   /**
    * Returns if the portion of text starting at the specified
    * position and spanning the specified length does not adjoin
    * to a letter or a digit at one or both ends.
    *
    * @param text  the text
    * @param pos  the position that may be a the start of a word
    * @param length  the length of the portion of text that may be
    * word
    * @return  if the portion of text starting at the specified
    * position and spanning the specified length does not adjoin
    * to a letter or a digit.
    */
   public static boolean isWord(String text, int pos, int length) {
      boolean startMatches = isWordStart(text, pos);
      boolean endMatches   = isWordEnd(text, pos + length);
      return startMatches && endMatches;
   }

   /**
    * Returns if the character preceding the specified position is
    * not a letter or a digit
    *
    * @param text  the text
    * @param pos  the position that may be a the start of a word
    * @return  if the character preceding <code>pos</code> is
    * not a letter or a digit
    */
   public static boolean isWordStart(String text, int pos) {
      if (pos > 0) {
         char c = text.charAt(pos - 1);
         return !isLetterOrDigit(c);
      }
      else {
         return true;
      }
   }

   /**
    * Returns if the character following the <code>pos</code> is
    * not a letter or a digit
    *
    * @param text  the text
    * @param pos  the position that may be a the end of a word
    * @return  if the character following the <code>pos</code> is
    * not a letter or a digit
    */
   public static boolean isWordEnd(String text, int pos) {
      if (text.length() > pos) {
         char c = text.charAt(pos);
         return !isLetterOrDigit(c);
      }
      else {
         return true;
      }
   }

   /**
    * Returns the position of the last block start where block is a portion
    * of text that is bordered by a block start and a block end signal.
    *
    * @param text  the text
    * @param pos  the position relative to which the last block start is
    * searched
    * @param blockStart  the String that signals the start of a block
    * @param blockEnd  the String that signals the end of a block
    * @return  the position of the last block start before '{@code pos}'. -1
    * if no block start is found or a block end is closer to '{@code pos}'
    * than a block start
    */
   public static int lastBlockStart(String text, int pos, String blockStart,
         String blockEnd) {

      int lastStart = text.lastIndexOf(blockStart, pos);
      int lastEnd = text.lastIndexOf(blockEnd, pos - 1);
      while (lastStart != -1 && isInQuotes(text, lastStart, blockStart.length())) {
         lastStart = text.lastIndexOf(blockStart, lastStart - 1);
      }
      while (lastEnd != -1 && isInQuotes(text, lastEnd, blockEnd.length())) {
         lastEnd = text.lastIndexOf(blockEnd, lastEnd - 1);
      }
      if (lastStart < lastEnd) {
         lastStart = -1;
      }
      return lastStart;
   }

   /**
    * Returns the position of the next block end. -1 if the a block start
    * is found before an end
    *
    * @param text  the text
    * @param pos  the position relative to which the next block end is
    * searched
    * @param blockStart  the String that signals the start of a block
    * @param blockEnd  the String that signals the end of a block
    * @return the position of the next block end. -1 if the a block start is
    * found before an end
    */
   public static int nextBlockEnd(String text, int pos, String blockStart,
         String blockEnd) {

      int nextEnd = text.indexOf(blockEnd, pos);
      int nextStart = text.indexOf(blockStart, pos);
      while (nextEnd != -1 && isInQuotes(text, nextEnd, blockEnd.length())) {
         nextEnd = text.indexOf(blockEnd, nextEnd + 1);
      }
      while (nextStart != -1 && isInQuotes(text, nextStart, blockStart.length())) {
         nextStart = text.indexOf(blockStart, nextStart + 1);
      }
      if (nextEnd > nextStart & nextStart != -1) {
         nextEnd = -1;
      }
      return nextEnd;
   }

   /**
    * Returns if the portion of text starting at the specified position
    * and spanning the specified length is surrounded by double quotes
    *
    * @param text  the text
    * @param pos  the start position of the portion that may be in quotes
    * @param length  the of the portion that may be in quotes
    * @return  if the portion of text starting at <code>pos</code>
    * and spanning <code>length</code> is surrounded by double quotes
    */
   public static boolean isInQuotes(String text, int pos, int length) {
      boolean isInQuotes = false;
      int endPos = pos + length;
      if (pos > 0 & text.length() > endPos) {
         isInQuotes = text.charAt(pos - 1) == '\"'
               & text.charAt(endPos) == '\"';
      }
      return isInQuotes;
   }
   
   /**
    * Returns the position of the specified String <code>toSearch</code>
    * that is not preceded with a backslash
    *
    * @param text  the text
    * @param toSearch  the String to seach
    * @param pos  the position within <code>text</code> where the search
    * starts
    * @return  the position of <code>toSearch</code> that is not preceded
    * with a backslash
    */
   public static int nextNotEscaped(String text, String toSearch, int pos) {
      int index = text.indexOf(toSearch, pos);
      while (SyntaxUtils.isEscaped(text, index)) {
         index = text.indexOf(toSearch, index + 1);
      }
      return index;
   }
   
   /**
    * Returns if the portion of text between the specified start and end
    * positions is an html tag
    *
    * @param text  the text
    * @param start  the start position of the tag keyword
    * @param end  the end position of the tag keyword
    * @return  if the portion of text between <code>start</code> and
    * <code>end</code> is an html tag
    */
   public static boolean isHtmlTag(String text, int start, int end) {
      return isTagStart(text, start) && isTagEnd(text, end);
   }

   public static boolean isTagStart(String text, int start) {
      boolean isTagStart = false;
      if (start > 0) {
         isTagStart = text.charAt(start - 1) == '<';
      }
      if (!isTagStart && start > 1) {
         isTagStart = text.charAt(start - 1) == '/'
               && text.charAt(start - 2) == '<';
      }
      return isTagStart;
   }

   public static boolean isTagEnd(String text, int end) {
      if (text.length() > end) {
         char c = text.charAt(end);
         return c == '>' || c == ' ';
      }
      else {
         return true;
      }
   }
   
   /**
    * Returns the length of a word that starts at the specified
    * position and ends at one of the characters saved in
    * <code>endChars</code>
    *
    * @param text  the text
    * @param pos   the position where the word starts
    * @param endChars  the array of characters that mark the end of the word
    * @return  the length of the word
    */
   public static int wordLength(String text, int pos, char[] endChars) {
      boolean found = false;
      int i;
      for (i = pos + 1; i < text.length() && !found; i++) {
         for (int j = 0; j < endChars.length; j++) {
            if (i == pos + 1) {
               if (text.charAt(i) == ' ') {
                  found = true;
                  break;
               }
            }
            else {
               if (text.charAt(i) == endChars[j]) {
                  found = true;
                  i--;
                  break;
               }
            }
         }
      }
      return i - pos;
   }

   public static boolean isNotQuoted(String text, int pos) {
      int count = 0;
      int i = 0;
      while (i != -1) {
         i = text.indexOf("\"", i);
         if (i != -1) {
            if (!isEscaped(text, i)) {
               count++;
            }
            if (i > pos) {
               break;
            }
            i++;
         }
      }
      if (pos < i) {
         return (count - 1) % 2 == 0;
      }
      else {
         return count <= 1 || count % 2 == 0;
      }
   }
   
   //
   //--private methods--/
   //

   private static boolean isLetterOrDigit(char c) {
      return Character.isLetter(c) || Character.isDigit(c);
   }
   
   private static boolean isEscaped(String text, int pos) {
      if (pos > 0) {
         return text.charAt(pos - 1) == '\\' && !isEscaped(text, pos - 1);
      }
      else {
         return false;
      }
   }
}
