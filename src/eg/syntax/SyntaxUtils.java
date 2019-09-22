package eg.syntax;

import eg.utils.LinesFinder;

/**
 * Static variables and methods to search for text elements
 */
public class SyntaxUtils {

   /**
    * Option for no effect if a text element is in quotes */
   public static int IGNORE_QUOTED = 0;
   /**
    * Option for searching quoted text elements in which the quotation
    * may be multiline */
   public static int BLOCK_QUOTED = 1;
   /**
    * Option for searching quoted text elements in which the quotation
    * must be found within line */
   public static int LINE_QUOTED = 2;

   /**
    * Returns if a section of text is a word
    *
    * @param text  the text
    * @param pos  the position where the section starts
    * @param length  the length of the section
    * @param nonWordStart  the array of characters that must not precede
    * a word, in addition to letters and digits. Can be null
    * @return  true if a word is found
    */
   public static boolean isWord(String text, int pos, int length,
         char[] nonWordStart) {

      boolean startMatches = isWordStart(text, pos, nonWordStart);
      boolean endMatches   = isWordEnd(text, pos + length);
      return startMatches && endMatches;
   }

   /**
    * Returns if the specified position is a word start
    *
    * @param text  the text
    * @param pos  the position
    * @param nonWordStart  the array of characters that must not precede
    * a word, in addition to letters and digits. Can be null
    * @return  true if word start is found, false otherwise
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
    * Returns if the specified position is a word end
    *
    * @param text  the text
    * @param pos  the position
    * @return  true if a word end is found, false otherwise
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
    * Returns the length of a section that starts at the specified
    * position and ends before one of the characters in
    * <code>endMarks</code>
    *
    * @param text  the text
    * @param pos   the position
    * @param endMarks  the characters that mark the end of the section
    * @param successors  the characters that disable endMarks if they
    * directly follow pos
    * @return  the length of the section
    */
   public static int sectionLength(String text, int pos, char[] endMarks,
         char[] successors) {

      boolean found = false;
      int start = pos + 1;
      int offset = 0;
      if (successors != null && text.length() > start) {
         if (isCharEqualTo(text, successors, start)) {
            offset = 1;
         }
      }
      int i;
      for (i = start + offset; i < text.length() && !found; i++) {
         for (int j = 0; j < endMarks.length; j++) {
            if (text.charAt(i) == endMarks[j]) {
               found = true;
               i--;
            }
         }
      }
      return i - pos + offset;
   }

   /**
    * Returns the length of a section that corresponds to the length of
    * one of the strings in <code>keywords</code> if it is contained in
    * the text at the specified position
    *
    * @param text  the text
    * @param pos  the position
    * @param keywords   the keywords
    * @return  the length of the section; 0 if a keyword is not found
    */
   public static int sectionLength(String text, int pos, String[] keywords) {
      int l = 0;
      for (String s : keywords) {
         if (text.startsWith(s, pos) && s.length() > l) {
            l = s.length();
         }
      }
      return l;
   }

   /**
    * Returns if the character at the specified position is equal to one
    * of the characters in <code>targets</code>
    *
    * @param text  the text
    * @param targets  the target characters
    * @param pos  the position
    * @return  true if equal
    */
   public static boolean isCharEqualTo(String text, char[] targets, int pos) {
      char c = text.charAt(pos);
      for (int i = 0; i < targets.length; i++) {
         if (c == targets[i]) {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns the position of the last block start
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @param quoteOpt  the option to define if matches in quotes
    * are skipped: IGNORE_QUOTED, BLOCK_QUOTED, LINE_QUOTED
    * @return  the position of the last block start. -1 if a block end
    * is closer than a block start or if no block start is found
    */
   public static int lastBlockStart(String text, int pos, String blockStart,
         String blockEnd, int quoteOpt) {

      int lastStart = -1;
      int lastEnd = -1;
      if (quoteOpt == IGNORE_QUOTED) {
         lastStart = text.lastIndexOf(blockStart, pos);
         lastEnd = text.lastIndexOf(blockEnd, pos);
      }
      else {
         lastStart = lastUnquoted(text, pos, blockStart, quoteOpt);
         lastEnd = lastUnquoted(text, pos, blockEnd, quoteOpt);
      }
      if (lastStart < lastEnd) {
         lastStart = -1;
      }
      return lastStart;
   }

    /**
    * Returns the position of the next block start
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @param quoteOpt  the option to define if matches in quotes
    * are skipped: IGNORE_QUOTED, BLOCK_QUOTED, LINE_QUOTED
    * @return  the position of the next block start. -1 if a block end
    * is closer than a block start or if no block start is found
    */
   public static int nextBlockStart(String text, int pos, String blockStart,
         String blockEnd, int quoteOpt) {

      int nextStart = -1;
      int nextEnd = -1;
      if (quoteOpt == IGNORE_QUOTED) {
         nextStart = text.indexOf(blockStart, pos);
         nextEnd = text.indexOf(blockEnd, pos);
      }
      else {
         nextStart = nextUnquoted(text, pos, blockStart, quoteOpt);
         nextEnd = nextUnquoted(text, pos, blockEnd, quoteOpt);
      }
      if (nextStart > nextEnd & nextEnd != -1) {
         nextStart = -1;
      }
      return nextStart;
   }

   /**
    * Returns the position of the next block end
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    * @param quoteOpt  the option to define if matches in quotes
    * are skipped: IGNORE_QUOTED, BLOCK_QUOTED, LINE_QUOTED
    * @return  the position of the next block end. -1 if a block
    * start is closer than a block end or if no block end is found
    */
   public static int nextBlockEnd(String text, int pos, String blockStart,
         String blockEnd, int quoteOpt) {

      int nextEnd = -1;
      int nextStart = -1;
      if (quoteOpt == IGNORE_QUOTED) {
         nextEnd = text.indexOf(blockEnd, pos);
         nextStart = text.indexOf(blockStart, pos);
      }
      else {
         nextEnd = nextUnquoted(text, pos, blockEnd, quoteOpt);
         nextStart = nextUnquoted(text, pos, blockStart, quoteOpt);
      }
      if (nextEnd > nextStart & nextStart != -1) {
         nextEnd = -1;
      }
      return nextEnd;
   }

   /**
    * Returns the position of the next <code>toSearch</code> that is not
    * quoted
    *
    * @param text  the text
    * @param toSearch  the string that is searched
    * @param pos  the position where the search starts
    * @return  the position of toSearch, -1 if not found
    */
   public static int nextUnquoted(String text, String toSearch, int pos) {
      int index = text.indexOf(toSearch, pos);
      while (index != -1
            && (isQuoted(text, index, SyntaxConstants.DOUBLE_QUOTE)
            || isQuoted(text, index, SyntaxConstants.SINGLE_QUOTE))) {

         index = text.indexOf(toSearch, index + 1);
      }
      return index;
   }

   /**
    * Returns if the specified position is found in a section that is
    * quoted with single or double quote marks
    *
    * @param text  the text
    * @param pos  the position
    * @param quoteOpt  the option to define if the quotation must be found
    * found within a line: on of BLOCK_QUOTED and LINE_QUOTED
    * @return  true if quoted
    */
   public static boolean isQuoted(String text, int pos, int quoteOpt) {
      if (quoteOpt == BLOCK_QUOTED) {
         return isQuoted(text, pos);
      }
      else if (quoteOpt == LINE_QUOTED) {
         return isQuotedInLine(text, pos);
      }
      else {
         throw new IllegalArgumentException(
               "The value of 'quoteOpt' is not allowed");
      }
   }

   /**
    * Returns if the specified position is found in a section that is
    * quoted with single or double quote marks
    *
    * @param text  the text
    * @param pos  the position
    * @return  true if quoted
    */
   public static boolean isQuoted(String text, int pos) {
      return SyntaxUtils.isQuoted(text, pos, SyntaxConstants.DOUBLE_QUOTE)
            || SyntaxUtils.isQuoted(text, pos, SyntaxConstants.SINGLE_QUOTE);
   }

   /**
    * Returns if the specified position is found in a quoted section.
    * Does not take into account if the quotation spans lines or not.
    *
    * @param text  the text
    * @param pos  the position
    * @param quoteMark  the quoteMark
    * @return  true if quoted
    */
   public static boolean isQuoted(String text, int pos, char quoteMark) {
      int count = 0;
      int i = 0;
      int iCounted = 0;
      while (i != -1) {
         i = text.indexOf(quoteMark, i);
         if (i != -1) {
            int countNext = count + 1;
            if (countNext % 2 != 0 || (countNext % 2 == 0 & !isEscaped(text, i))) {
               count++;
               iCounted = i;
            }
            if (iCounted > pos) {
               break;
            }
            i++;
         }
      }
      if (count > 1) {
         if (pos < i) {
            return count % 2 == 0;
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
    * Returns the first position where either <code>s1</code> or <code>s2</code>
    * is found before <code>endIndex</code>
    *
    * @param text  the text
    * @param s1  the one alternative string to search
    * @param s2  the other alternative string to search
    * @param endIndex  the position before which either s1 or s2 must be found
    * @return  the position, -1 if s1 and s2 are not found
    */
   public static int firstBefore(String text, String s1, String s2, int endIndex) {
      int index = -1;
      int i1 = text.indexOf(s1, 0);
      int i2 = -1;
      if (!s2.isEmpty()) {
         i2 = text.indexOf(s2, 0);
      }
      if (i1 != -1 && i1 < endIndex && (i2 == -1 || i1 < i2)) {
         index = i1;
      }
      else if (i2 != -1 && i2 < endIndex && (i1 == -1 || i2 < i1)) {
         index = i2;
      }
      return index;
   }

   /**
    * Returns the last position where either <code>s1</code> or <code>s2</code>
    * is found behind <code>beginIndex</code>
    *
    * @param text  the text
    * @param s1  the one alternative string to search
    * @param s2  the other alternative string to search
    * @param beginIndex  the position behind which either s1 or s2 must be found
    * @return  the position, -1 if s1 and s2 are not found
    */
   public static int lastBehind(String text, String s1, String s2, int beginIndex) {
      int index = -1;
      int i1 = text.lastIndexOf(s1, text.length());
      int i2 = -1;
      if (!s2.isEmpty()) {
         i2 = text.lastIndexOf(s2, text.length());
      }
      if (i1 != -1 && i1 > beginIndex && i1 > i2) {
         index = i1;
      }
      if (i2 != -1 && i2 > beginIndex && i2 > i1) {
         index = i2;
      }
      return index;
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
    public static int nextNonEscaped(String text, char toSearch, int pos) {
      int index = text.indexOf(toSearch, pos);
      while (SyntaxUtils.isEscaped(text, index)) {
         index = text.indexOf(toSearch, index + 1);
      }
      return index;
   }

   /**
    * Returns the position of the next non space character
    *
    * @param text  the text
    * @param pos  the position where the search starts
    * @return  the position of the next space or the text length if none
    * is found
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

   /**
    * Returns if the specified position is found inside a text block
    *
    * @param text  the text
    * @param del  the delimiter that surrounds the text block
    * @param pos  the position
    * @param lineCmntStart  the mark for line comments
    * @return  true if inside a text block, false otherwise
    */  
   public static boolean isInTextBlock(String text, String del, int pos,
         String lineCmntStart) {

      int before = SyntaxUtils.countBefore(text, del, pos, lineCmntStart);
      int after = SyntaxUtils.countAfter(text, del, pos, lineCmntStart);
      return before % 2 != 0 & after % 2 != 0;
   }

   //
   //--private--/
   //

   private SyntaxUtils() {}

   private static int countBefore (String text, String toSearch, int pos,
         String lineCmntStart) {

      int count = 0;
      int i = 0;
      while (i != -1) {
         i = text.indexOf(toSearch, i);
         if (i != -1) {
            if (lineCmntStart != null && isLineCommented(text, lineCmntStart, i)) {
               i+= toSearch.length();
               continue;
            }
            if (i > pos) {
               break;
            }
            count++;
            i+= toSearch.length();
         }
      }
      return count;
   }

   private static int countAfter(String text, String toSearch, int pos,
         String lineCmntStart) {

      int count = 0;
      int i = pos;
      while (i != -1) {
         i = text.indexOf(toSearch, i);
         if (i != -1) {
            if (lineCmntStart != null && isLineCommented(text, lineCmntStart, i)) {
               i+= toSearch.length();
               continue;
            }
            count++;
            i+= toSearch.length();
         }
      }
      return count;
   }

   private static boolean isLineCommented(String text, String lineCmntStart, int pos) {
      int posInLine = pos - LinesFinder.lastNewline(text, pos);
      String line = LinesFinder.lineAtPos(text, pos);
      return line.lastIndexOf(lineCmntStart, posInLine) != -1;
   }

   private static int nextUnquoted(String text, int pos, String toSearch,
         int quoteOpt) {

      if (quoteOpt == BLOCK_QUOTED) {
         return nextUnquoted(text, toSearch, pos);
      }
      else if (quoteOpt == LINE_QUOTED) {
         return nextUnquotedInLine(text, toSearch, pos);
      }
      else {
         throw new IllegalArgumentException(
               "The value of 'quoteOpt' is not allowed");
      }
   }

   private static int lastUnquoted(String text, int pos, String toSearch,
         int quoteOpt) {

      if (quoteOpt == BLOCK_QUOTED) {
         return lastUnquoted(text, pos, toSearch);
      }
      else if (quoteOpt == LINE_QUOTED) {
         return lastUnquotedInLine(text, pos, toSearch);
      }
      else {
         throw new IllegalArgumentException(
               "The value of 'quoteOpt' is not allowed");
      }
   }

   private static int nextUnquotedInLine(String text, String toSearch, int pos) {
      int index = text.indexOf(toSearch, pos);
      while (index != -1 && isQuotedInLine(text, index)) {
         index = text.indexOf(toSearch, index + 1);
      }
      return index;
   }

   private static int lastUnquoted(String text, int pos,  String toSearch) {
      int index = text.lastIndexOf(toSearch, pos);
      while (index != -1
            && (isQuoted(text, index, SyntaxConstants.DOUBLE_QUOTE)
            || isQuoted(text, index, SyntaxConstants.SINGLE_QUOTE))) {

         index = text.lastIndexOf(toSearch, index - 1);
      }
      return index;
   }

   private static int lastUnquotedInLine(String text, int pos, String toSearch) {
      int index = text.lastIndexOf(toSearch, pos);
      while (index != -1 && isQuotedInLine(text, index)) {
         index = text.lastIndexOf(toSearch, index - 1);
      }
      return index;
   }

   private static boolean isQuotedInLine(String text, int pos) {
      String line;
      int relStart;
      line = LinesFinder.lineAtPos(text, pos);
      relStart = pos - LinesFinder.lastNewline(text, pos);
      return SyntaxUtils.isQuoted(line, relStart, SyntaxConstants.DOUBLE_QUOTE)
            || SyntaxUtils.isQuoted(line, relStart, SyntaxConstants.SINGLE_QUOTE);
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
