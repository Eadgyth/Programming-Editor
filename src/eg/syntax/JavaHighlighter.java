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

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
     if (!searcher.isInBlockCmnt(SyntaxUtils.BLOCK_CMNT_START,
            SyntaxUtils.BLOCK_CMNT_END)) {

         searcher.setSectionBlack();
         searcher.keywords(JAVA_ANNOTATIONS, true, null, Attributes.BLUE_PLAIN);
         searcher.keywords(JAVA_KEYWORDS, true, null, Attributes.RED_PLAIN);
         searcher.brackets();
         searcher.braces();
         searcher.quotedTextInLines(Attributes.ORANGE_PLAIN);
         searcher.lineComments(SyntaxUtils.LINE_CMNT);
     }
     searcher.blockComments(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END);
   }
   
   @Override
   public boolean isEnabled(String text, int pos, int option) {
      return true;
   }
}
