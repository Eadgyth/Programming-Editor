package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for Python
 */
public class PythonHighlighter implements Highlighter {

   private static final String[] KEYWORDS = {
      "and", "as", "assert", "async", "await",
      "break",
      "class", "continue",
      "def", "del",
      "elif", "else", "except",
      "False", "finally", "for", "from",
      "global",
      "if", "import", "in", "is",
      "lambda",
      "None", "nonlocal", "not",
      "or",
      "pass",
      "raise", "return",
      "True", "try",
      "while", "with",
      "yield"
   };

   private static final String[] LINE_CMNT_MARKS = {
      SyntaxConstants.HASH
   };

   @Override
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.resetAttributes();
      s.tripleQuoteTextBlocks(true);
      s.quote(true);
      s.lineComments(LINE_CMNT_MARKS);
      s.keywords(KEYWORDS, null, attr.redPlain);
      s.brackets();
      s.braces();
   }

   @Override
   public boolean isValid(String text, int pos, int condition) {
      return true;
   }

   @Override
   public int behindLineCmntMark(String text, int pos) {
      return SyntaxUtils.behindMark(text, SyntaxConstants.HASH, pos);
   }

   @Override
   public int inBlockCmntMarks(String text, int pos) {
      return -1;
   }
}
