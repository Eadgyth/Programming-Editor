package eg.syntax;

/**
 * Static methods to search for text elements
 */
public class SyntaxUtils {

   private SyntaxUtils() {}

   public final static String[] BRACKETS = {
      "(", ")",
   };
   
   public final static String[] CURLY_BRACKETS = {
      "{", "}",
   };

   /**
    * Returns if the specified word is a word.
    * A word is considered as one if it does not adjoin to a letter
    * or a digit at the left and/or right end.
    *
    * @param text  the text which the word is part of
    * @param word  the word that may be a word
    * @param pos  the position where the word starts
    * @return  if the word does not adjoin to a letter or a digit
    */
   public static boolean isWord(String text, String word, int pos) {
      boolean startMatches = isWordStart(text, pos);
      boolean endMatches   = isWordEnd(text, word, pos);
      return startMatches && endMatches;
   }

   public static boolean isWordStart(String text, int pos) {
      if (pos > 0) {
         char c = text.charAt(pos - 1);
         return !isLetterOrDigit(c);
      }
      else {
         return true;
      }
   }

   public static boolean isWordEnd(String text, String word, int pos) {
      int length = word.length();
      int endPos = pos + length;
      String end = "";   
      if (text.length() > endPos) {
         char c = text.charAt(endPos);
         return !isLetterOrDigit(c);
      }
      else {
         return true;
      }
   }
   
   /**
    * Returns if this pos is found in a block of text that is delimited
    * by given start and end signals
    *
    * @param text  the text
    * @param pos  the position that maybe found in a block
    * @param blockStart  the String that defines the block start
    * @param blockEnd  the String that defines the block end
    * @return  if the specified pos is found in a certain block of text 
    */
   public static boolean isInBlock(String text, int pos, String blockStart,
         String blockEnd) {

      int lastStart = SyntaxUtils.lastBlockStart(text, pos, blockStart,
            blockEnd);
      int nextEnd = SyntaxUtils.nextBlockEnd(text, pos, blockStart,
            blockEnd);
      return lastStart != -1 & nextEnd != -1;
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
      while (lastStart != -1 && isInQuotes(text, lastStart, blockStart)) {
         lastStart = text.lastIndexOf(blockStart, lastStart - 1);
      }
      while (lastEnd != -1 && isInQuotes(text, lastEnd, blockEnd)) {
         lastEnd = text.lastIndexOf(blockEnd, lastEnd - 1);
      }
      if (lastStart < lastEnd) {
         lastStart = -1;
      }
      return lastStart;
   }

   /**
    * Returns the position of the next block end where block is a portion of
    * text that is bordered by a block start and a block end signal.
    *
    * @param text  the text
    * @param pos  the position relative to which the next block end is
    * searched
    * @param blockStart  the String that signals the start of a block
    * @param blockEnd  the String that signals the end of a block
    * @return the position of the next block end after '{@code pos}'. -1 if no
    * end is found or the next block start is closer to '{@code pos}' than the
    * block end
    */
   public static int nextBlockEnd(String text, int pos, String blockStart,
         String blockEnd) {

      int nextEnd = text.indexOf(blockEnd, pos);
      int nextStart = text.indexOf(blockStart, pos);
      while (nextEnd != -1 && isInQuotes(text, nextEnd, blockEnd)) {
         nextEnd = text.indexOf(blockEnd, nextEnd + 1);
      }
      while (nextStart != -1 && isInQuotes(text, nextStart, blockStart)) {
         nextStart = text.indexOf(blockStart, nextStart + 1);
      }
      if (nextEnd > nextStart & nextStart != -1) {
         nextEnd = -1;
      }
      return nextEnd;
   }

   /**
    * Returns if a given string is in double quotes
    *
    * @param text  the text
    * @param pos  the start position of the portion that may be in quotes
    * @param str  the string that may be in quotes
    * @return  if the specified string is in double quotes
    */
   public static boolean isInQuotes(String text, int pos, String str) {
      boolean isInQuotes = false;
      int endPos = pos + str.length();
      if (pos > 0 & text.length() > endPos) {
         isInQuotes = text.substring(pos - 1, pos).equals("\"")
                    & text.substring(endPos, endPos + 1).equals("\"");
      }
      return isInQuotes;
   }

   public static boolean isEscaped(String text, int pos) {
      if (pos > 0) {
         return text.substring(pos - 1, pos).equals("\\")
               && !isEscaped(text, pos - 1);
      }
      else {
         return false;
      }
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
   
   public static boolean isOutsideQuote(String text, int pos) {
      char[] c = text.toCharArray();
      int count = 0;
      for (int i = 0; i < pos; i++) {
         if (c[i] == '\"') {
            count++;
         }
      }
      return count % 2 == 0;
   }

   public static int endOfWord(String text) {
      char[] c = text.toCharArray();
      int i = 1;
      for (i = 1; i < c.length; i++) {
         if (c[i] == ' ') {
            break;
         }
      }
      return i;
   }

   private static boolean isLetterOrDigit(char c) {
      return Character.isLetter(c) || Character.isDigit(c);
   }
}
