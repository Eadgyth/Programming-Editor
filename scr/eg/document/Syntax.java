package eg.document;

/**
 * Static String arrays containing keywords
 */
class Syntax {
   
   final static String[] JAVA_KEYWORDS = {
     "abstract", "assert", "break", "boolean", "Boolean", "byte",
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
      "<a>", "</a>",
      "<b>", "</b>", "<br>", "</br>",
      "<body>", "</body>",
      "<em>", "</em>",
      "<head>", "</head>", "<html>",  "</html>",
      "<h1>", "</h1>", "<h2>", "</h2>", "<h3>", "</h3>",
      "<h4>", "</h4>", "<h5>", "</h5>", "<h6>", "</h6>",
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

   static boolean isWord(String in, String word, int pos) {
      boolean startMatches = isWordStart(in, pos);
      boolean endMatches   = isWordEnd(in, word, pos);
      return startMatches && endMatches;
   }
   
   public static boolean isWordStart(String in, int pos) {
      String start = "";
      if (pos > 0) {
         start = in.substring(pos - 1, pos);
         char c = start.charAt(0);
         return !Character.isLetter(c) && !Character.isDigit(c);
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
         end = in.substring(endPos, endPos + 1);
         char c = end.charAt(0);
         return !Character.isLetter(c) && !Character.isDigit(c);
      }
      else {
         return true;
      }
   }

   static int wordLength(String in) {      
      char[] chars = in.toCharArray();
      int i = 1;
      if (chars.length > 1) {
         for (i = 1; i < chars.length; i++ ) {
            if (!Character.isLetter(chars[i]) && !Character.isDigit(chars[i])) {
               break;
            }
         }
      }
      return i;
   }

   static int indLastBlockStart(String in, int pos, String blockStart,
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

   static int indNextBlockEnd(String in, int pos,  String blockStart,
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

   static boolean isInQuotes(String in, int pos) {
      boolean isInQuotes = false; 
      if (pos > 0) {
         isInQuotes = in.substring(pos - 1, pos).equals("\"");
      }
      return isInQuotes;
   }
}