package eg.document;

/**
 * Static String arrays containing keywords and other syntax features
 * and static methods (only one public) to search for syntax features
 */
public class Syntax {
   
   private Syntax() {}
   
   /**
    * Returns if the specified word is a word.
    * <p>
    * A word is considered as such if it does not adjoin to
    * a letter or a digit at the left or right or both ends.
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
   
   final static String[] JAVA_KEYWORDS = {
     "abstract", "assert", 
     "break", "boolean", "Boolean", "byte",
     "catch", "case","const","continue", "class", "char",
     "default", "do", "double",
     "else", "enum", "extends",
     "false", "finally", "final", "float", "for",
     "if", "implements", "import", "instanceof", "int", "interface",
     "long",
     "native", "new", "null",
     "package", "private", "protected", "public",
     "return",
     "strictfp", "switch", "synchronized", "short", "static", "super",
     "String",
     "this", "throw", "throws", "transient", "true", "try",
     "void", "volatile",
     "while"
   };
   
   final static String[] JAVA_ANNOTATIONS = {
      "@Override", "@Deprecated", "@SuppressWarnings", "@SafeVarargs"
   };
   
   final static String[] HTML_KEYWORDS = {
      "<a>", "<a", "</a>",
      "<b>", "</b>", "<br>", "</br>",
      "<body>", "</body>",
      "<div>", "</div>",
      "<em>", "</em>",
      "<head>", "</head>", "<html>",  "</html>", "<hr>",
      "<h1>", "</h1>", "<h2>", "</h2>", "<h3>", "</h3>",
      "<h4>", "</h4>", "<h5>", "</h5>", "<h6>", "</h6>",
      "<h1 ", "<h2 ", "<h3 ", "<h4 ", "<h5 ", "<h6 ",
      "<i>", "</i>", "<img>",
      "<li>", "</li>",
      "<meta>",
      "<ol>", "</ol>",
      "<p>", "</p>",
      "<style>", "</style>",
      "<table>", "</table>", "<title>", "</title>",
      "<ul>", "</ul>",
      "bu%20",
      "&lsquo;", "&rsquo;"
   };
   
   final static String[] PERL_KEYWORDS = {
      "cmp", "continue", "CORE",
      "do",
      "else", "elsif", "eq", "exp",
      "for", "foreach",
      "if",
      "lock",
      "no",
      "package",
      "sub",
      "unless", "until",
      "while",      
   };
   
   final static String[] PERL_OP = {
      " and ",
      " eq ",
      " ge ", " gt ",
      " le ", " lt ",
      " ne ",
      " or ",
      " q ", " qq ", " qr ", " qw ", " qx ",
      " s ",
      " tr ",
      " xor ",
      " y "
   };
   
   final static String[] PERL_FLAGS = {
      "$", "@"
   };
   
   final static String[] BRACKETS = {
      "(",")"
   };
   
   static boolean isWordStart(String in, int pos) {
      if (pos > 0) {
         char c = in.charAt(pos - 1);
         return !isLetterOrDigit(c);
      }
      else {
         return true;
      }
   }
   
   static boolean isWordEnd(String in, String word, int pos) {
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

   static int wordLength(String in) {      
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

   static int indLastBlockStart(String in, int pos, String blockStart,
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

   static int indNextBlockEnd(String in, int pos, String blockStart,
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

   static boolean isInQuotes(String in, int pos, int length) {
      boolean isInQuotes = false;
      int endPos = pos + length;
      if (pos > 0 & in.length() > endPos) {
         isInQuotes = in.substring(pos - 1, pos).equals("\"")
                    & in.substring(endPos, endPos + 1).equals("\"");
      }
      return isInQuotes;
   }
   
   private static boolean isLetterOrDigit(char c) {
      return Character.isLetter(c) || Character.isDigit(c);
   }
}
