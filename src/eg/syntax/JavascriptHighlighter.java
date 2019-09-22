package eg.syntax;

import eg.document.styledtext.Attributes;

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

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      if (!s.isInBlock(SyntaxConstants.SLASH_STAR,
            SyntaxConstants.STAR_SLASH, SyntaxUtils.LINE_QUOTED)) {

         s.resetAttributes();
         s.keywords(JS_KEYWORDS, true, null, attr.redPlain);
         s.brackets();
         s.braces();
         s.quoteInLine();
         s.lineComments(SyntaxConstants.DOUBLE_SLASH, SyntaxUtils.LINE_QUOTED);
         s.lineComments(SyntaxConstants.HTML_BLOCK_CMNT_START, SyntaxUtils.LINE_QUOTED);
         s.lineComments(SyntaxConstants.HTML_BLOCK_CMNT_END, SyntaxUtils.LINE_QUOTED);
      }
      s.block(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH,
            SyntaxUtils.LINE_QUOTED);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
