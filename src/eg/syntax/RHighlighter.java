package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for R
 */
public class RHighlighter implements Highlighter {

   private static final String[] KEYWORDS = {
      "break",
      "else",
      "FALSE", "for", "function",
      "if", "in", "inf",
      "NA", "NA_integer", "NA_real", "Na_complex", "NaN", "Na_character",
      "next", "NULL",
      "repeat",
      "TRUE",
      "while"
   };

   private static final String[] LINE_CMNT_MARKS = {
      SyntaxConstants.HASH
   };

   @Override
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.resetAttributes();
      s.quote(false);
      s.lineComments(LINE_CMNT_MARKS);
      s.keywords(KEYWORDS, null, attr.redPlain);
      s.braces();
      s.brackets();
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
