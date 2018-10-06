package eg.syntax;

/**
 * Syntax highlighting for Java
 */
public class JavaHighlighter implements Highlighter {

   private final static String[] JAVA_KEYWORDS = {
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

   private final static String[] JAVA_ANNOTATIONS = {
      "@Override", "@Deprecated", "@SuppressWarnings", "@SafeVarargs",
      "@FunctionalInterface"
   };
   
   private SyntaxHighlighter.SyntaxSearcher s;
   
   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      s = searcher;
      s.setSkipQuotedBlkCmntMarks();
   }

   @Override
   public void highlight() {
      if (!s.isInBlock(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH)) {
         s.resetAttributes();
         s.keywords(JAVA_ANNOTATIONS, true, null, Attributes.BLUE_PLAIN);
         s.keywords(JAVA_KEYWORDS, true, null, Attributes.RED_PLAIN);
         s.brackets();
         s.braces();
         s.quoteInLine();
         s.lineComments(SyntaxConstants.DOUBLE_SLASH);
     }
     else {
        System.out.println("hallo");
     }
     s.block(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH);
   }
   
   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
