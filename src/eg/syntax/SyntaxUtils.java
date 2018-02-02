package eg.syntax;

/**
 * Static variables and methods to search for text elements
 */
public class SyntaxUtils {

   /**
    * The single quote mark */
   public final static char SINGLE_QUOTE = '\'';
   /**
    * The double quote mark */
   public  final static char DOUBLE_QUOTE = '"';
   /**
    * The "slash-star" block comment start */
   public final static String BLOCK_CMNT_START = "/*";
   /**
    * The "star-slash" block comment end */
   public final static String BLOCK_CMNT_END = "*/";
   /**
    * The "slash-slash" line comment start */
   public final static String LINE_CMNT = "//";

   /**
    * Returns the boolean that indicates if the portion of text starting
    * at the specified position and spanning the specified length is a
    * word. A word is defined such that it does not adjoin to a letter
    * or a digit at the start and/or the end.
    *
    * @param text  the text
    * @param pos  the position
    * @param length  the length
    * @param nonWordStart  the array of characters that do not precede,
    * a word, in addition to letters and digits. Can be null
    * Can be null
    * a word. Can be null
    * @return  the boolean value
    */
   public static boolean isWord(String text, int pos, int length,
         char[] nonWordStart) {

      boolean startMatches = isWordStart(text, pos, nonWordStart);
      boolean endMatches   = isWordEnd(text, pos + length);
      return startMatches && endMatches;
   }

   /**
    * Returns the boolean that indeicates if the specified position
    * is a word start
    *
    * @param text  the text
    * @param pos  the position
    * @param nonWordStart  the array of characters that do not precede
    * a word,
    * in addition to letters and digits. Can be null
    * @return  the boolean value
    * @see #isWord(String, int, int, char[])
    */
   public static boolean isWordStart(String text, int pos, char[] nonWordStart) {
      boolean isWord = true;
      if (pos > 0) {
         char c = text.charAt(pos - 1);
         isWord = !isLetterOrDigit(c);
         if (isWord && nonWordStart != null) {
            for (int i = 0; i < nonWordStart.length; i++) {
               if (c == nonWordStart[i]) {
                  isWord = false;
                  break;
               }
            }
         }
      }
      return isWord;      
   }

   /**
    * Returns the boolean that indicates if the character that follows
    * the specified position is word end
    *
    * @param text  the text
    * @param pos  the position
    * @return the boolean value
    * @see #isWord(String, int, int, char[])
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
    * Returns the length of a word that starts at the specified position 
    * and ends with one of the characters in <code>endChars</code>
    *
    * @param text  the text
    * @param pos   the position
    * @param endChars  the array of characters that mark the end of a word
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
    * Returns the position of the last block start before the specified
    * position
    *
    * @param text  the text
    * @param pos  the position
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @return  the position of the last block start. -1 if a block end
    * is closer than a block start or if no block start is found
    */
   public static int lastBlockStart(String text, int pos, String blockStart,
         String blockEnd) {

      int lastStart = text.lastIndexOf(blockStart, pos);
      int lastEnd = text.lastIndexOf(blockEnd, pos - 1);
      while (lastStart != -1 && isBorderedByQuotes(text, lastStart,
            blockStart.length())) {

         lastStart = text.lastIndexOf(blockStart, lastStart - 1);
      }
      while (lastEnd != -1 && isBorderedByQuotes(text, lastEnd,
            blockEnd.length())) {

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
    * @return  the position of the next block end. -1 if a block
    * start is closer than a block end or if no block end is found
    */
   public static int nextBlockEnd(String text, int pos, String blockStart,
         String blockEnd) {

      int nextEnd = text.indexOf(blockEnd, pos);
      int nextStart = text.indexOf(blockStart, pos);
      while (nextEnd != -1 && isBorderedByQuotes(text, nextEnd,
            blockEnd.length())) {

         nextEnd = text.indexOf(blockEnd, nextEnd + 1);
      }
      while (nextStart != -1 && isBorderedByQuotes(text, nextStart,
            blockStart.length())) {

         nextStart = text.indexOf(blockStart, nextStart + 1);
      }
      if (nextEnd > nextStart & nextStart != -1) {
         nextEnd = -1;
      }
      return nextEnd;
   }
   
   /**
    * Returns the boolean that is true if an opening brace but not a
    * closing brace is found ahead of the specified position in the
    * case that the specified <code>openingBrace</code> is true or
    * rather if a closing brace or no brace is found in the case that
    * <code>openingBrace</code> is false
    *
    * @param text  the text
    * @param pos  the position
    * @param openingBrace  the boolean that indicates if the last
    * brace must (true) or must not (false) be an opening brace
    * @return  the boolean value
    */ 
   public static boolean testLastBrace(String text, int pos, boolean openingBrace) {
      int lastBlockStart
            = SyntaxUtils.lastBlockStart(text, pos, "{", "}");
        
      return (openingBrace && lastBlockStart != -1)
            || (!openingBrace && lastBlockStart == -1);
   }

   /**
    * Returns if the section starting at the specified position and
    * spanning the specified length is bordered by double quotes
    *
    * @param text  the text
    * @param pos  the position
    * @param length  the length
    * @return  if the section of text starting at <code>pos</code>
    * and spanning <code>length</code> is bordered by double quotes
    */
   public static boolean isBorderedByQuotes(String text, int pos, int length) {
      boolean isInQuotes = false;
      int startPos = pos - 1;
      int endPos = pos + length;
      if (pos > 0 & text.length() > endPos) {
         isInQuotes = (text.charAt(startPos) == DOUBLE_QUOTE
               || text.charAt(startPos) == SINGLE_QUOTE)
               && (text.charAt(endPos) == DOUBLE_QUOTE
               || text.charAt(endPos) == SINGLE_QUOTE);
      }
      return isInQuotes;
   }

   /**
    * Returns the boolean that indicated if the specified position is found
    * inside a section of text in quotes
    *
    * @param text  the text
    * @param pos  the position
    * @param quoteMark  the quoteMark
    * @return  the boolean value
    */
   public static boolean isInQuotes(String text, int pos, char quoteMark) {
      int count = 0;
      int i = 0;
      while (i != -1) {
         i = text.indexOf(quoteMark, i);
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
      if (count > 1) {
         if (pos < i) {
            return (count) % 2 == 0;
         }
         else {
            return false;
         }
      }
      else {
         return false;
      }
   }

   /**
    * Returns the next position, relative to the specified position, of the
    * specified string <code>toSearch</code> that is not preceded with a
    * backslash
    *
    * @param text  the text
    * @param toSearch  the string to search
    * @param pos  the position
    * @return  the position of the next non-escaped string
    */
   public static int nextNotEscaped(String text, char toSearch, int pos) {
      int index = text.indexOf(toSearch, pos);
      while (SyntaxUtils.isEscaped(text, index)) {
         index = text.indexOf(toSearch, index + 1);
      }
      return index;
   }

   //
   //--private--
   //

   private SyntaxUtils() {}

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
