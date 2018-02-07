package eg.syntax;

public class RHighlighter implements Highlighter {
   
   private final static String LINE_CMNT = "#";
   
   final static String[] KEYWORDS = {
      "break",
      "else",
      "FALSE","for", "function",
      "if", "in", "inf",
      "NA", "NA_integer", "NA_real", "Na_complex", "NaN", "Na_character",
      "next", "NULL",
      "repeat",
      "TRUE",
      "while"
   };
      
   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
      searcher.setCharAttrBlack();
      searcher.keywords(KEYWORDS, true, Attributes.RED_PLAIN);
      searcher.braces();
      searcher.brackets();
      searcher.quotedText();
      searcher.lineComments(LINE_CMNT, null);
   }
}
