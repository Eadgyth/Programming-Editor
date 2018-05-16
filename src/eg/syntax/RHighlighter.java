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
      
   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
      searcher.setSectionBlack();
      searcher.keywords(KEYWORDS, true, null, Attributes.RED_PLAIN);
      searcher.braces();
      searcher.brackets();
      searcher.quotedTextInLines(Attributes.ORANGE_PLAIN);
      searcher.lineComments(SyntaxConstants.HASH);
   }
   
   @Override
   public boolean isEnabled(String text, int pos, int option) {
      return true;
   }
}
