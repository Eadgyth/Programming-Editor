package eg.syntax;

//import eg.utils.LinesFinder;

/**
 * Static variables and methods to search for text elements
 */
public class SyntaxUtils {

   /**
    * Returns the boolean that indicates if the portion of text starting
    * at the specified position and spanning the specified length is a
    * word. A word is initially defined such that it does not adjoin to
    * a letter or a digit at the start and/or the end.
    *
    * @param text  the text
    * @param pos  the position
    * @param length  the length
    * @param nonWordStart  the array of characters that do not precede
    * a word in addition to letters and digits. Can be null
    * @return  the boolean value
    */
   public static boolean isWord(String text, int pos, int length,
         char[] nonWordStart) {

      boolean startMatches = isWordStart(text, pos, nonWordStart);
      boolean endMatches   = isWordEnd(text, pos + length);
      return startMatches && endMatches;
   }

   /**
    * Returns the boolean that indicates if the specified position is
    * a word start
    *
    * @param text  the text
    * @param pos  the position
    * @param nonWordStart  the array of characters that must not precede
    * a word, in addition to letters and digits. Can be null
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
               }
            }
         }
      }
      return isWord;      
   }

   /**
    * Returns the boolean that indicates if the character that follows
    * the specified position is a word end
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
    * in addition to a white space
    * @return  the length of the word
    */
   public static int wordLength(String text, int pos, char[] endChars) {
      boolean found = false;
      int i;
      for (i = pos + 1; i < text.length() && !found; i++) {
         for (int j = 0; j < endChars.length; j++) {
            if (text.charAt(i) == endChars[j]) {
               found = true;
               i--;
            }
         }
      }
      return i - pos;
   }

   /**
    * Returns the position of the last block start
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @param allowInQuotes  the boolean value that specifies if blockStart
    * and blockEnd may be surrounded by quotation marks
    * @return  the position of the last block start. -1 if a block end
    * is closer than a block start or if no block start is found
    */
   public static int lastBlockStart(String text, int pos, String blockStart,
         String blockEnd, boolean allowInQuotes) {

      int lastStart = text.lastIndexOf(blockStart, pos);
      int lastEnd = text.lastIndexOf(blockEnd, pos - 1);
      if (!allowInQuotes) {
         while (lastStart != -1 && isBorderedByQuotes(text, lastStart,
               blockStart.length())) {
   
            lastStart = text.lastIndexOf(blockStart, lastStart - 1);
         }
         while (lastEnd != -1 && isBorderedByQuotes(text, lastEnd,
               blockEnd.length())) {
   
            lastEnd = text.lastIndexOf(blockEnd, lastEnd - 1);
         }
      }
      if (lastStart < lastEnd) {
         lastStart = -1;
      }
      return lastStart;
   }

   /**
    * Returns the position of the next block end
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @param allowInQuotes  the boolean value that specifies if blockStart
    * and blockEnd may be surrounded by quotation marks
    * @return  the position of the next block end. -1 if a block
    * start is closer than a block end or if no block end is found
    */
   public static int nextBlockEnd(String text, int pos, String blockStart,
         String blockEnd, boolean allowInQuotes) {

      int nextEnd = text.indexOf(blockEnd, pos);
      int nextStart = text.indexOf(blockStart, pos);
      if (!allowInQuotes) {
         while (nextEnd != -1 && isBorderedByQuotes(text, nextEnd,
               blockEnd.length())) {
   
            nextEnd = text.indexOf(blockEnd, nextEnd + 1);
         }
         while (nextStart != -1 && isBorderedByQuotes(text, nextStart,
               blockStart.length())) {
   
            nextStart = text.indexOf(blockStart, nextStart + 1);
         }
      }
      if (nextEnd > nextStart & nextStart != -1) {
         nextEnd = -1;
      }
      return nextEnd;
   }
   
   /**
    * Returns the boolean that is true if the section starting at the
    * specified position and spanning the specified length is bordered
    * by double or single quotes
    *
    * @param text  the text
    * @param pos  the position
    * @param length  the length
    * @return  the boolean value
    */
   public static boolean isBorderedByQuotes(String text, int pos, int length) {
      boolean isInQuotes = false;
      int startPos = pos - 1;
      int endPos = pos + length;
      if (pos > 0 & text.length() > endPos) {
         isInQuotes = (text.charAt(startPos) == SyntaxConstants.DOUBLE_QUOTE
               && text.charAt(endPos) == SyntaxConstants.DOUBLE_QUOTE)
               || (text.charAt(startPos) == SyntaxConstants.SINGLE_QUOTE
               && text.charAt(endPos) == SyntaxConstants.SINGLE_QUOTE);
      }
      return isInQuotes;
   }
   
   /**
    * Returns the position of the next <code>toSearch</code> that is not
    * in quotes
    *
    * @param text  the text
    * @param toSearch  the string that is searched
    * @param pos  the position where the search starts
    * @return  the position of toSearch, -1 if not found
    */
   public static int nextUnquoted(String text, String toSearch, int pos) {
      int index = text.indexOf(toSearch, pos);
      while (index != -1
            && (isInQuotes(text, index, SyntaxConstants.DOUBLE_QUOTE)
            || isInQuotes(text, index, SyntaxConstants.SINGLE_QUOTE))) {
               
         index = text.indexOf(toSearch, index + 1);
      }
      return index;
   }
   
   /**
    * Returns the position of the first quote mark (single or
    * double) in the text before <code>pos</code>
    *
    * @param text  the text
    * @param pos  the position before which a quote mark is searched
    * @return  the position of the quote mark; -1 if no quote mark
    * is found before pos
    */
   public static int firstQuoteMark(String text, int pos) {
      int index = -1;
      int d = text.indexOf("\"", 0);
      int s = text.indexOf("\'", 0);
      if (d != -1 && d < pos && d < s) {
         index = d;
      }
      else if (s != -1 && s < pos && s < d) {
         index = s;
      }
      return index;
   }
   
   /**
    * Returns the position of the last quote mark (single or
    * double) behind <code>pos</code>
    *
    * @param text  the text
    * @param pos  the position behind which a quote mark is searched
    * @return  the position; -1 if no quote mark is found behind
    * pos
    */
   public static int lastQuoteMark(String text, int pos) {
      int index = -1;
      int d = text.lastIndexOf("\"", text.length());
      int s = text.lastIndexOf("\'", text.length());
      if (d != -1 && d > pos && d > s) {
         index = d;
      }
      if (s != -1 && s > pos && s > d) {
         index = s;
      }
      return index;
   }

   /**
    * Returns the boolean that is true if the specified position is found
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
    * Returns the last position of the specified <code>toSearch</code>
    * that is not preceded with a backslash that is itself is not
    * preceded with a backslash.
    *
    * @param text  the text
    * @param toSearch  the char that is searched
    * @param pos  the position where the search starts
    * @return  the position
    */
   public static int lastNonEscaped(String text, char toSearch, int pos) {
      int index = text.lastIndexOf(toSearch, pos);
      while (SyntaxUtils.isEscaped(text, index)) {
         index = text.lastIndexOf(toSearch, index - 1);
      }
      return index;
   }

   /**
    * Returns the next position of the specified <code>toSearch</code>
    * that is not preceded with a backslash
    *
    * @param text  the text
    * @param toSearch  the char that is searched
    * @param pos  the position where the search starts
    * @return  the position
    */
   public static int nextNonEscaped(String text, char toSearch, int pos) {
      int index = text.indexOf(toSearch, pos);
      while (SyntaxUtils.isEscaped(text, index)) {
         index = text.indexOf(toSearch, index + 1);
      }
      return index;
   }
   
   /**
    * Returns the next position that is a space character
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @return  the position of the next space or the specifies pos
    * if it is the text length
    */
   public static int nextNonSpace(String text, int pos) {
      if (pos == text.length()) {
         return pos;
      }
      int i;
      for (i = pos; i < text.length(); i++) {
         if (text.charAt(i) != ' ') {
            break;
         }
      }
      return i;
   }

   //
   //--private--/
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
