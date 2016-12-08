package eg.document;

/**
 * Static String arrays containing keywords
 */
class Keywords {
   
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
   
   final static String[] HTML_KEYWORDS = {
      ">",
      "<a", "</a",
      "<b", "<br",
      "<body", "</body",
      "<head", "</head", "<html",  "</html",
      "<h1", "</h1", "<h2", "</h2", "<h3", "</h3",
      "<h4", "</h4", "<h5", "</h5", "<h6", "</h6",
      "<img",
      "<li", "</li",
      "<meta ",
      "<ol", "</ol",
      "<p", "</p",
      "<style", "</style",
      "<table", "</table", "<title", "</title",
      "<ul", "</ul",
      "bu%20",
      "&lsquo;", "&rsquo"
   };
   
   final static String[] BRACKETS = {
      "(",")"
   };
}