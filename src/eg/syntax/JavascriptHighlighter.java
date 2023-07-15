package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for Javascript
 */
public class JavascriptHighlighter implements Highlighter {

   private static final String[] LINE_CMNT_MARKS = {
      SyntaxConstants.HTML_BLOCK_CMNT_START, SyntaxConstants.HTML_BLOCK_CMNT_END,
               SyntaxConstants.DOUBLE_SLASH
   };

   // incomplete
   private static final String[] JS_KEYWORDS = {
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
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.resetAttributes();
      s.quote(true);
      s.lineComments(LINE_CMNT_MARKS);
      s.keywords(JS_KEYWORDS, null, attr.redPlain);
      s.brackets();
      s.braces();
      s.blockComments(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH, false);
   }

   @Override
   public boolean isValid(String text, int pos, int condition) {
      return true;
   }

   @Override
   public int behindLineCmntMark(String text, int pos) {
      int res;
      int slash = SyntaxUtils.behindMark(text, SyntaxConstants.DOUBLE_SLASH, pos);
      int htmlStartMark = SyntaxUtils.behindMark(text,
            SyntaxConstants.HTML_BLOCK_CMNT_START, pos);

      res = htmlStartMark > slash ? htmlStartMark : slash;
      int htmlEndMark = SyntaxUtils.behindMark(text,
            SyntaxConstants.HTML_BLOCK_CMNT_END, pos);

      res = htmlEndMark > res ? htmlEndMark : res;
      return res;
   }

   @Override
   public int inBlockCmntMarks(String text, int pos) {
      return SyntaxUtils.inBlock(text, SyntaxConstants.SLASH_STAR,
            SyntaxConstants.STAR_SLASH, pos);
   }
}
