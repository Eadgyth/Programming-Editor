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
   
   private SyntaxHighlighter.SyntaxSearcher searcher;
   
   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      this.searcher = searcher;
      searcher.blkCmntMarksQuoted(false);
   }

   @Override
   public void highlight() {
     if (!searcher.isInBlockCmnt(SyntaxConstants.SLASH_STAR,
            SyntaxConstants.STAR_SLASH)) {

         searcher.setSectionBlack();
         searcher.keywords(JAVA_ANNOTATIONS, true, null, Attributes.BLUE_PLAIN);
         searcher.keywords(JAVA_KEYWORDS, true, null, Attributes.RED_PLAIN);
         searcher.brackets();
         searcher.braces();
         searcher.quotedLinewise(Attributes.ORANGE_PLAIN);
         searcher.lineComments(SyntaxConstants.DOUBLE_SLASH);
     }
     searcher.blockComments(SyntaxConstants.SLASH_STAR,
           SyntaxConstants.STAR_SLASH);
   }
   
   @Override
   public boolean isEnabled(String text, int pos, int condition) {
      return true;
   }
}
