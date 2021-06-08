package eg.syntax;

import eg.utils.LinesFinder;

/**
 * Static methods to search for text elements
 */
public class SyntaxUtils {

   /**
    * Returns if a section with the specified length is a word
    *
    * @param text  the text
    * @param pos  the position where the section starts
    * @param length  the length of the section
    * @param nonStart  the array of characters that must not precede
    * a word, in addition to letters and digits. Can be null
    * @return  true if the section is a word, false otherwise
    */
   public static boolean isWord(String text, int pos, int length, char[] nonStart) {
      boolean startMatches = isWordStart(text, pos, nonStart);
      boolean endMatches   = isWordEnd(text, pos + length);
      return startMatches && endMatches;
   }

   /**
    * Returns if the specified position is a word start
    *
    * @param text  the text
    * @param pos  the position
    * @param nonStart  the array of characters that must not
    * precede a word, in addition to letters and digits. Can be
    * null
    * @return  true if the position is a word start, false
    * otherwise
    */
   public static boolean isWordStart(String text, int pos, char[] nonStart) {
      boolean isWord = true;
      if (pos > 0) {
         char c = text.charAt(pos - 1);
         isWord = !isLetterOrDigit(c);
         if (isWord && nonStart != null) {
            for (int i = 0; i < nonStart.length; i++) {
               if (c == nonStart[i]) {
                  isWord = false;
               }
            }
         }
      }
      return isWord;
   }

   /**
    * Returns if the specified position is a word end
    *
    * @param text  the text
    * @param pos  the position
    * @return  true if position is a word end, false otherwise
    */
   public static boolean isWordEnd(String text, int pos) {
      if (text.length() > pos) {
         return !isLetterOrDigit(text.charAt(pos));
      }
      else {
         return true;
      }
   }

   /**
    * Returns the length that corresponds to the length of the
    * string in <code>keywords</code> that can be found at the
    * specified position
    *
    * @param text  the text
    * @param pos  the position
    * @param keywords   the keywords
    * @return  the length of the section; 0 if a keyword is not
    * found
    */
   public static int wordLength(String text, int pos, String[] keywords) {
      int l = 0;
      for (String s : keywords) {
         if (text.startsWith(s, pos) && s.length() > l) {
            l = s.length();
         }
      }
      return l;
   }

   /**
    * Returns the length of a section that starts at the specified
    * position and ends before one of the characters in
    * <code>endMarks</code>
    *
    * @param text  the text
    * @param pos   the position
    * @param endMarks  the characters that mark the end of the section
    * @param successors  the characters that are not endMarks if they
    * directly follow pos
    * @return  the length of the section
    */
   public static int sectionLength(String text, int pos, char[] endMarks,
         char[] successors) {

      boolean found = false;
      int start = pos + 1;
      int offset = 0;
      if (successors != null && text.length() > start
            && isCharEqualTo(text, start, successors)) {

         offset = 1;
      }
      int i;
      for (i = start + offset; i < text.length() && !found; i++) {
         for (int j = 0; j < endMarks.length; j++) {
            if (text.charAt(i) == endMarks[j]) {
               found = true;
               i--;
               break;
            }
         }
      }
      return i - pos;
   }

   /**
    * Returns the length of a section that starts at the specified
    * position and ends before one of the characters in
    * <code>endMarks</code> if this character is not escaped
    *
    * @param text  the text
    * @param pos   the position; its
    * @param endMark  the characters that mark the end of the section
    * @return  the length of the section
    */
   public static int sectionLengthSkipEscaped(String text, int pos, char endMark) {
      int start = pos + 1;
      int i;
      for (i = start; i < text.length(); i++) {
         if (text.charAt(i) == endMark && !SyntaxUtils.isEscaped(text, i)) {
            break;
         }
      }
      return i - pos;
   }

   /**
    * Returns if the character at the specified position is equal to
    * one of the characters in <code>targets</code>
    *
    * @param text  the text
    * @param pos  the position
    * @param targets  the target characters
    * @return  true if equal
    */
   public static boolean isCharEqualTo(String text, int pos, char[] targets) {
      char c = text.charAt(pos);
      for (int i = 0; i < targets.length; i++) {
         if (c == targets[i]) {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns if a text element A occurs before a text element B.
    *
    * @param posA  the position of A, -1 means that A was not found
    * @param posB  the position of B, -1 means that B was not found
    * @return  true if A occurs first, false if B occurs first or if
    * both are not found
    */
   public static boolean firstOccurence(int posA, int posB) {
      boolean foundAOnly = posA != -1 && posB == -1;
      boolean foundBoth = posA != -1 && posB != -1;
      return foundBoth ? posA < posB : foundAOnly;
   }

   /**
    * Returns if the specified position is found inside a block where
    * the specified blockStart and blockEnd must differ
    *
    * @param text  the text
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @param pos  the position
    * @return  true if inside a block, false otherwise
    */
    public static int inBlock(String text, String blockStart, String blockEnd,
         int pos) {

      int lastStart = SyntaxUtils.lastBlockStart(text, pos, blockStart, blockEnd);
      int nextEnd = -1;
      if (lastStart != -1) {
         nextEnd = text.indexOf(blockEnd, pos);
      }
      if ((lastStart != -1 && nextEnd != -1) && nextEnd != lastStart) {
         return lastStart;
      }
      else {
         return -1;
      }
   }

   /**
    * Returns the position of the last block start where the specified
    * blockStart and blockEnd must differ
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @return  the position of the last block start. -1 if a block
    * end is closer than a block start or if no block start is found
    */
   public static int lastBlockStart(String text, int pos, String blockStart,
         String blockEnd) {

      int lastStart = text.lastIndexOf(blockStart, pos);
      int lastEnd = text.lastIndexOf(blockEnd, pos);
      if (lastStart < lastEnd) {
         lastStart = -1;
      }
      return lastStart;
   }

   /**
    * Returns the position of the next block end where the specified
    * blockStart and blockEnd must differ
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @return  the position of the next block end. -1 if a block
    * start is closer than a block end or if no block end is found
    */
    public static int nextBlockEnd(String text, int pos, String blockStart,
         String blockEnd) {

      int nextEnd = text.indexOf(blockEnd, pos);
      int nextStart = text.indexOf(blockStart, pos);
      if (nextEnd > nextStart & nextStart != -1) {
         nextEnd = -1;
      }
      return nextEnd;
   }

   /**
    * Returns if the specified position is found somewhere behind the
    * specified mark in the same line
    *
    * @param text  the text
    * @param mark  the mark
    * @param pos  the position
    * @return  the position of the last mark, -1 if the mark is not
    * found
    */
   public static int behindMark(String text, String mark, int pos) {
      int lineStart = LinesFinder.lastNewline(text, pos) + 1;
      int i = text.lastIndexOf(mark, pos);
      return (i != -1 && i >= lineStart) ? i : -1;
   }

   /**
    * Returns if the specified position is found in a section that
    * is enclosed with single or double quote marks within the line
    * where the position is found
    *
    * @param text  the text
    * @param pos  the position
    * @return  true if quoted
    */
   public static boolean isQuotedInLine(String text, int pos) {
      String line;
      int relStart;
      line = LinesFinder.lineAtPos(text, pos);
      relStart = pos - LinesFinder.lastNewline(text, pos);
      return isQuoted(line, relStart - 1); // <--here changed to -1
   }

   /**
    * Returns if the specified position is found in a section that is
    * enclosed with single or double quote marks
    *
    * @param text  the text
    * @param pos  the position
    * @return  true if quoted
    */
   public static boolean isQuoted(String text, int pos) {
      int i = 0;
      boolean found = false;
      while (i != -1 && !found) {
         int startDouble = text.indexOf(SyntaxConstants.DOUBLE_QUOTE, i);
         int startSingle = text.indexOf(SyntaxConstants.SINGLE_QUOTE, i);
         boolean isDouble = SyntaxUtils.firstOccurence(startDouble, startSingle);
         i = isDouble ? startDouble : startSingle;
         char endMark = isDouble ?
                  SyntaxConstants.DOUBLE_QUOTE : SyntaxConstants.SINGLE_QUOTE;

         if (i != -1) {
            if (i >= pos) {
               break;
            }
            int close = SyntaxUtils.nextNotEscaped(text, endMark, i + 1);
            if (close != -1) {
               if (close >= pos) {
                  found = true;
               }
               else {
                 i = close + 1;
               }
            }
            else {
               i++;
            }
         }
      }
      return found;
   }

   /**
    * Returns the next position of <code>toSearch</code> that is not
    * preceded by a backslash that is itself not preceded by a backslash.
    *
    * @param text  the text
    * @param toSearch  the char that is searched
    * @param pos  the position where the search starts
    * @return  the position
    */
    public static int nextNotEscaped(String text, char toSearch, int pos) {
      int index = text.indexOf(toSearch, pos);
      while (SyntaxUtils.isEscaped(text, index)) {
         index = text.indexOf(toSearch, index + 1);
      }
      return index;
   }

   /**
    * Returns the position of the next non whitespace character
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @param lineEnd  true stop at the next newline character
    * @return  the position
    */
   public static int nextNonSpace(String text, int pos, boolean lineEnd) {
      if (pos == text.length()) {
         return pos;
      }
      int i;
      for (i = pos; i < text.length(); i++) {
         char c = text.charAt(i);
         if (!Character.isWhitespace(c) || (lineEnd && c == '\n')) {
            break;
         }
      }
      return i == text.length() ? i - 1 : i;
   }

   /**
    * Retuns if the specified character is letter or a digit
    *
    * @param c  the character
    * @return  true if c is a letter or a digit; false otherwise
    */
   public static boolean isLetterOrDigit(char c) {
      return Character.isLetter(c) || Character.isDigit(c);
   }

   //
   //--private--/
   //

   private static boolean isEscaped(String text, int pos) {
      if (pos > 0) {
         return text.charAt(pos - 1) == '\\' && !isEscaped(text, pos - 1);
      }
      else {
         return false;
      }
   }

   private SyntaxUtils() {}
}
