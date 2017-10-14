package eg.syntax;

/**
 * Static methods to search for text elements
 */
public class SyntaxUtils {

   private SyntaxUtils() {}
   
   /**
    * The "slash-star" block comment start */
   public final static String BLOCK_CMNT_START = "/*";
   /**
    * The "star-slash" block comment end */
   public final static String BLOCK_CMNT_END = "*/";
   /**
    * The "slash-slash" line comment start */
   public final static String LINE_CMNT = "//";

   /*
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
    * Returns the position of the last block start relative to the
    * the specified position
    *
    * @param text  the text
    * @param pos  the position in the text
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @return  the position of the last block start. -1 if a block end
    * is closer than a block start or if no block start is found 
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
    * Returns the position of the next block end relative to the
    * specified position
    *
    * @param text  the text
    * @param pos  the position in the text
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @return the position of the next block end. -1 if a block start is
    * closer than a block end or if no block end is found
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
    * Returns the length of a word that starts at the specified
    * position and ends at one of the characters in the specifies
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
   
   /**
    * Returns if the section starting at the specified position
    * and spanning the specified length is bordered by double quotes
    *
    * @param text  the text
    * @param pos  the start position of the portion that may be in quotes
    * @param length  the length of the section that may be in quotes
    * @return  if the section of text starting at <code>pos</code>
    * and spanning <code>length</code> is bordered by double quotes
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
    * Returns if the specified position is found inside a section
    * of text in double quotes
    *
    * @param text  the text
    * @param pos  the position that may be found in quoted
    * section
    * @return  if the <code>pos</code> is found inside a section
    * of text in double quotes
    */
   public static boolean isInQuotes(String text, int pos) {
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
         return (count) % 2 == 0;
      }
      else {
         return false;
      }     
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
   
   //
   //--private methods--/
   //
   
   private static boolean isWordEnd(String text, int pos) {
      if (text.length() > pos) {
         char c = text.charAt(pos);
         return !isLetterOrDigit(c);
      }
      else {
         return true;
      }
   }

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
