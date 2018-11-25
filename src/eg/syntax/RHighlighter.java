package eg.syntax;

import eg.document.Attributes;

/**
 * Syntax highlighting for R
 */
public class RHighlighter implements Highlighter {

   final static String[] KEYWORDS = {
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
      s.setEntireText();
      s.resetAttributes();
      s.keywords(KEYWORDS, true, null, attr.redPlain);
      s.braces();
      s.brackets();
      s.quote();
      s.lineComments(SyntaxConstants.HASH);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
