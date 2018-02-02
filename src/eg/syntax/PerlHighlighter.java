package eg.syntax;

/**
 * Syntax highlighting for Perl
 */
public class PerlHighlighter implements Highlighter {
   
   private final static String LINE_CMNT = "#";

   private final static char[] START_OF_VAR = {
      '$', '@', '%'
   };
   
   private final static char[] END_OF_VAR = {
      ' ', '\\', '(', ')', ';', '='
   };
   
   private final static char[] END_OF_QW = {
      ')'
   };
   
   final static String[] KEYWORDS = {
      "cmp", "chomp", "continue", "CORE", "cos",
      "do",
      "else", "elsif", "eq", "exp",
      "for", "foreach",
      "int", "if",
      "lock",
      "my",
      "no",
      "package", "print",
      "rand",
      "sin", "sqrt", "sub", "substr",
      "unless", "until",
      "while"   
   };
   
   final static String[] STRING_OP = {
      " and ",
      " cmp ",
      " eq ",
      " ge ", " gt ",
      " le ", " lt ",
      " ne ",
      " or ",
      " xor "
   };
   
   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
      searcher.setCharAttrBlack();
      searcher.signedVariables(START_OF_VAR, END_OF_VAR, Attributes.PURPLE_PLAIN);
      searcher.keywords(KEYWORDS, true, Attributes.RED_BOLD);
      searcher.keywords(STRING_OP, false, Attributes.RED_BOLD);
      searcher.braces();
      searcher.quotedText();
      searcher.unHighlight("qw(", END_OF_QW);
      searcher.lineComments(LINE_CMNT, START_OF_VAR);
   }
}
