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
   
   private SyntaxHighlighter.SyntaxSearcher searcher;
   
   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      this.searcher = searcher;
   }
      
   @Override
   public void highlight() {
      searcher.setEntireText();
      searcher.setSectionBlack();
      searcher.keywords(KEYWORDS, true, null, Attributes.RED_PLAIN);
      searcher.braces();
      searcher.brackets();
      searcher.quoted(Attributes.ORANGE_PLAIN);
      searcher.lineComments(SyntaxConstants.HASH);
   }
   
   @Override
   public boolean isValid(String text, int pos, int condition) {
      return true;
   }
}
