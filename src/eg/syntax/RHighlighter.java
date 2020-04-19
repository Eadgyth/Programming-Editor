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

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      s.resetAttributes();
      s.keywords(KEYWORDS, true, null, attr.redPlain);
      s.braces();
      s.brackets();
      s.lineComments(SyntaxConstants.HASH, SyntaxUtils.BLOCK_QUOTED);
      s.quote();
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
