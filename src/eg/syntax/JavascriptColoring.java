package eg.syntax;

/**
 * Syntax coloring for Javascript
 */
public class JavascriptColoring implements Colorable {
   
   // incomplete
   public final static String[] JS_KEYWORDS = {
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
   public void color(SyntaxSearch search) {
      if (!search.isInBlock(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END)) {
         search.setCharAttrBlack();
         search.keywordsRed(JS_KEYWORDS, true);
         search.bracketsBlue();
         search.bracesGray();
         search.quotedText();
         search.lineComments(SyntaxUtils.LINE_CMNT, null);
      }
      search.blockComments(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END);
   }
}
