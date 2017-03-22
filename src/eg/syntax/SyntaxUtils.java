package eg.syntax;

public class SyntaxUtils {

   private SyntaxUtils() {}

   public final static String[] BRACKETS = {
      "(",")"
   };

   /**
    * Returns if the specified word is a word.
    * <p>
    * A word is considered as such if it does not adjoin to a letter
    * or a digit at the left and/or right end.
    * @param text  the text which the word is part of
    * @param word  the word that may be a word
    * @param pos  the position which the word starts at within the
    * text
    * @return  if the word does not adjoin to a letter or a digit
    */
   public static boolean isWord(String text, String word, int pos) {
      boolean startMatches = isWordStart(text, pos);
      boolean endMatches   = isWordEnd(text, word, pos);
      return startMatches && endMatches;
   }

   public static boolean isWordStart(String in, int pos) {
      if (pos > 0) {
         char c = in.charAt(pos - 1);
         return !isLetterOrDigit(c);
      }
      else {
         return true;
      }
   }

  public static boolean isWordEnd(String in, String word, int pos) {
      int length = word.length();
      int endPos = pos + length;
      String end = "";   
      if (in.length() > endPos) {
         char c = in.charAt(endPos);
         return !isLetterOrDigit(c);
      }
      else {
         return true;
      }
   }

   /**
    * Returns the length of a word which is defined as the first
    * position where the character is not a digit or letter
    * @param in  the String in which a word end is searched
    * @return  the length of a word which is defined as the first
    * position where the character is not a digit or letter
    */
   public static int wordLength(String in) {      
      char[] c = in.toCharArray();
      int i = 1;
      if (c.length > 1) {
         for (i = 1; i < c.length; i++ ) {
            if (!isLetterOrDigit(c[i])) {
               break;
            }
         }
      }
      return i;
   }

   /**
    * Returns the position of the last block start where block is a
    * portion of text that is bordered by a block start and a block
    * end signal.
    * <p>
    * @param in  the text
    * @param pos  the position relative to which the last block start
    * is searched
    * @param blockStart  the String that signals the start of a block
    * @param blockEnd  the String that signals the end of a block
    * @return the position of the last block start or -1 if no block
    * start is found or the block end is closer than block start
    */
   public static int indLastBlockStart(String in, int pos, String blockStart,
         String blockEnd) {

      int index = in.lastIndexOf(blockStart, pos);
      int indLastEnd = in.lastIndexOf(blockEnd, pos - 1);
      while (index != -1 && isInQuotes(in, index, blockStart.length())) {
         index = in.lastIndexOf(blockStart, index - 1);
      }
      while (indLastEnd != -1 && isInQuotes(in, indLastEnd, blockEnd.length())) {
         indLastEnd = in.lastIndexOf(blockEnd, indLastEnd - 1);
      }
      if (index < indLastEnd) {
         index = -1;
      }
      return index;
   }

   /**
    * Returns the position of the next block end where block is a
    * portion of text that is bordered by a block start and a block
    * end signal.
    * <p>
    * @param in  the text
    * @param pos  the position relative to which the next block end
    * is searched
    * @param blockStart  the String that signals the start of a block
    * @param blockEnd  the String that signals the end of a block
    * @return the position of the next block end or -1 if the next
    * block start is closer than the block end
    */
   public static int indNextBlockEnd(String in, int pos, String blockStart,
         String blockEnd) {
      int index = in.indexOf(blockEnd, pos);
      int indNextStart = in.indexOf(blockStart, pos);

      while (index != -1 && isInQuotes(in, index, blockEnd.length())) {
         index = in.indexOf(blockEnd, index + 1);
      }
      while (indNextStart != -1 && isInQuotes(in, indNextStart, blockStart.length())) {
         indNextStart = in.indexOf(blockStart, indNextStart + 1);
      }
      if (index > indNextStart & indNextStart != -1) {
         index = -1;
      }
      return index;
   }

   /**
    * Returns if a portion of text is in double quotes
    * @param in  the text
    * @param pos  the start position of the portion that may be in quotes
    * @param length  the length of the portion that may be in quotes
    * @return  if the portion of text starting at {@code pos} and given
    * {@code length} is in double quotes
    */
   public static boolean isInQuotes(String in, int pos, int length) {
      boolean isInQuotes = false;
      int endPos = pos + length;
      if (pos > 0 & in.length() > endPos) {
         isInQuotes = in.substring(pos - 1, pos).equals("\"")
                    & in.substring(endPos, endPos + 1).equals("\"");
      }
      return isInQuotes;
   }

   //
   //--private
   //

   private static boolean isLetterOrDigit(char c) {
      return Character.isLetter(c) || Character.isDigit(c);
   }
}
