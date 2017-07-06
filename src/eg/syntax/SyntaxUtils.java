package eg.syntax;

/**
 * Static methods to search for text elements
 */
public class SyntaxUtils {

   private SyntaxUtils() {}

   public final static String[] BRACKETS = {
      "(", ")"
   };

   public final static String[] BRACES = {
      "{", "}"
   };

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
    * Returns if the portion of text starting at the specified
    * position and spanning the specified length is surrounded by
    * double quotes
    *
    * @param text  the text
    * @param pos  the start position of the portion that may be in
    * quotes
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
   
   public static int nextNotEscaped(String text, String toSearch,
         boolean escape, int pos) {

      int index = text.indexOf(toSearch, pos);
      if (escape) {
         while (SyntaxUtils.isEscaped(text, index)) {
            index = text.indexOf(toSearch, index + 1);
         }
      }
      return index;
   }

   public static boolean isTagStart(String text, int pos) {
      boolean isTagStart = false;
      if (pos > 0) {
         char c = text.charAt(pos - 1);
         isTagStart = c == '<';
      }
      if (!isTagStart && pos > 1) {
         char c1 = text.charAt(pos - 2);
         char c2 = text.charAt(pos - 1);
         isTagStart = c2 == '/' && c1 == '<';
      }
      return isTagStart;
   }

   public static boolean isTagEnd(String text, int length, int pos) {
      int endPos = pos + length;
      if (text.length() > endPos) {
         char c = text.charAt(endPos);
         return c == '>' || c == ' ';
      }
      else {
         return true;
      }
   }

   public static boolean isNotQuoted(String text, int pos) {
      int count = 0;
      int i = 0;
      while (i < pos && i != -1) {
         i = text.indexOf("\"", i);
         if (i != -1) {
            if (!isEscaped(text, i)) {
               count++;
            }
            if (i >= pos) {
               count--;
            }
            i++;
         }
      }
      return count <= 1 || count % 2 == 0;
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
