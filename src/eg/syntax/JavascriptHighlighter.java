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
      "var", "void", "volatile", "while", "with"
   };

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
      if (!searcher.isInBlockCmnt(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END)) {
         searcher.setCharAttrBlack();
         searcher.keywords(JS_KEYWORDS, true, Attributes.RED_PLAIN);
         searcher.brackets();
         searcher.braces();
         searcher.quotedText();
         searcher.lineComments(SyntaxUtils.LINE_CMNT, null);
      }
      searcher.blockComments(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END);
   }
}
