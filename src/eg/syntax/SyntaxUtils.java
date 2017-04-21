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
    * <p>
    * A word is considered as such if it does not adjoin to a letter
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
    * Returns if the specified pos is found in a block of text that is
    * delimited by given start and end signals
    *
    * @param text  the text
    * @param pos  the position that may be found in a block of text
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

      int index = text.lastIndexOf(blockStart, pos);
      int indLastEnd = text.lastIndexOf(blockEnd, pos - 1);
      while (index != -1 && isInQuotes(text, index, blockStart)) {
         index = text.lastIndexOf(blockStart, index - 1);
      }
      while (indLastEnd != -1 && isInQuotes(text, indLastEnd, blockEnd)) {
         indLastEnd = text.lastIndexOf(blockEnd, indLastEnd - 1);
      }
      if (index < indLastEnd) {
         index = -1;
      }
      return index;
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

      int index = text.indexOf(blockEnd, pos);
      int indNextStart = text.indexOf(blockStart, pos);

      while (index != -1 && isInQuotes(text, index, blockEnd)) {
         index = text.indexOf(blockEnd, index + 1);
      }
      while (indNextStart != -1 && isInQuotes(text, indNextStart, blockStart)) {
         indNextStart = text.indexOf(blockStart, indNextStart + 1);
      }
      if (index > indNextStart & indNextStart != -1) {
         index = -1;
      }
      return index;
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

   public static boolean isLetterOrDigit(char c) {
      return Character.isLetter(c) || Character.isDigit(c);
   }
}
