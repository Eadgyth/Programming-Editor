package eg.utils;

import java.util.ArrayList;

/**
 * Static methods to search for elements in text
 */
public class Finder {

   private final static String NUM_REGEX    = "[0-9]+";
   private final static String LETTER_REGEX = "[a-zA-Z]+";

   /**
    * @param in  the array of lines of a text
    * @return  an array of start positions of the lines of a passed in
    * text whose lines are splitted into an array 
    */
   public static int[] startOfLines(String[] in) {
      int[] startOfLines = new int[in.length];
      int startOfLine = 0;
      startOfLines[0] = 0;
      for (int i = 1; i < startOfLines.length; i++) {
         startOfLine += in[i - 1].length();
         startOfLines[i] = startOfLine + i; // +i to add the missing new lines
      }   
      return startOfLines;
   }

   /**
    * @param in  the String in which a line is searched
    * @param pos  the position that is found in the searched line
    * @return  the line that includes the specified position
    */
   public static String currLine(String in, int pos) {
      int indLastReturn = in.lastIndexOf("\n", pos);
      int indNextReturn = in.indexOf("\n", pos);

      if (indLastReturn != -1 && indNextReturn != -1) {
         return in.substring(indLastReturn + 1, indNextReturn + 1);
      }
      else if (indLastReturn != -1 && indNextReturn == -1) {
         return in.substring(indLastReturn + 1 );
      }
      else if (indLastReturn == -1 && indNextReturn != -1) {
         return in.substring(0, indNextReturn + 1);
      }
      else {
         return in;
      }
   }

   /**
    * @param in  the text that a word is searched in
    * @param query  the String that may be a word
    * @param pos  the position where the query is located
    * @return  if the character before and/or after the 'query' String
    * is not a letter or a number
    */
   public static boolean isWord(String in, String query, int pos) {
      int length = query.length();
      int indQueryEnd = pos + length;

      String start = "";
      String end = "";
      if (pos > 0) {
         start = in.substring(pos - 1, pos);
      }
      if (in.length() > indQueryEnd) {
         end = in.substring(indQueryEnd, indQueryEnd + 1);
      }
      boolean startMatches = start.matches(NUM_REGEX) || start.matches(LETTER_REGEX);
      boolean endMatches   = end.matches(NUM_REGEX) || end.matches(LETTER_REGEX);
      return !startMatches && !endMatches;
   }

   /**
    * @return  the index of the block comment start before the specifies position.
    * returns -1 if a block end is found behind a block start
    */
   public static int indLastBlockStart(String in, int pos, String blockStart,
         String blockEnd) {
      int index = in.lastIndexOf(blockStart, pos);
      int indLastEnd = in.lastIndexOf(blockEnd, pos);

      while (index != -1 && isInQuotes(in, index)) {
         index = in.lastIndexOf(blockStart, index - 1);
      }
      while (indLastEnd != -1 && isInQuotes(in, indLastEnd)) {
         indLastEnd = in.lastIndexOf(blockEnd, indLastEnd - 1);
      } 
      if (index < indLastEnd) {
         index = -1;
      } 
      return index;
   }

   /**
    * @return  the index of the block comment end. -1 if there is no block
    * end or if the block end is located before a block start
    */   
   public static int indNextBlockEnd(String in, int pos,  String blockStart,
         String blockEnd) {
      int index = in.indexOf(blockEnd, pos);
      int indNextStart = in.indexOf(blockStart, pos);

      while (index != -1 && isInQuotes(in, index)) {
         index = in.indexOf(blockEnd, index + 1);
      }
      while (indNextStart != -1 && isInQuotes(in, indNextStart)) {
         indNextStart = in.indexOf(blockStart, indNextStart + 1);
      }
      if (index > indNextStart & indNextStart != -1) {
         index = -1;
      }
      return index;
   }

   /**
    * @return  true if the symbol right before 'pos' is a quotes symbol
    */   
   public static boolean isInQuotes(String in, int pos) {
      boolean isInQuotes = false; 
      if (pos > 0) {
         isInQuotes = in.substring(pos - 1, pos).equals("\"");
      }
      return isInQuotes;
   }

   public static int countMotive(String in, String query) {
      int count = 0;
      int index = 0;

      while (index != -1) {
         index = in.indexOf(query, index);
         if (index != -1) {
            count++;
            index++;
         }
      }
      return count;
   }
}