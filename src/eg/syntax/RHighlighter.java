package eg.syntax;

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
   
   private SyntaxHighlighter.SyntaxSearcher s;
   
   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      s = searcher;
   }
      
   @Override
   public void highlight() {
      s.setEntireText();
      s.resetAttributes();
      s.keywords(KEYWORDS, true, null, Attributes.RED_PLAIN);
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
