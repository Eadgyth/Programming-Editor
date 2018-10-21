package eg.syntax;

/**
 * Syntax highlighting for Javascript
 */
public class JavascriptHighlighter implements Highlighter {

   // incomplete
   private final static String[] JS_KEYWORDS = {
      "abstract",
      "boolean", "break", "byte",
      "case", "catch", "char", "class", "const", "continue",
      "debugger", "default", "delete", "do", "double",
      "else", "enum", "export", "extends",
      "false", "final", "finally", "float", "for", "function",
      "goto",
      "if", "implements", "import", "in", "instanceof", "int", "interface",
      "let", "long",
      "native", "new", "null",
      "package", "private", "protected", "public",
      "return",
      "short", "static", "super", "switch", "synchronized",
      "this", "throw", "throws", "transient", "true", "try", "typeof",
      "var", "void", "volatile",
      "while", "with"
   };
   
   private SyntaxHighlighter.SyntaxSearcher s;
   
   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      s = searcher;
      s.setSkipQuotedBlockMarks();
   }

   @Override
   public void highlight() {
      if (!s.isInBlock(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH)) {
         s.resetAttributes();
         s.keywords(JS_KEYWORDS, true, null, Attributes.RED_PLAIN);
         s.brackets();
         s.braces();
         s.quoteInLine();
         s.lineComments(SyntaxConstants.DOUBLE_SLASH);
         s.lineComments(SyntaxConstants.HTML_BLOCK_CMNT_START);
         s.lineComments(SyntaxConstants.HTML_BLOCK_CMNT_END);
      }
      s.block(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int option) {
      return true;
   }
}
