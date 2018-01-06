package eg.syntax;

/**
 * Syntax highlighting for Perl
 */
public class PerlHighlighter implements Highlighter {
   
   private final static char[] START_OF_VAR = {
      '$', '@', '%'
   };
   
   private final static char[] END_OF_VAR = {
      ' ', '\\', '(', ')', ';', '='
   };
   
   final static String[] PERL_KEYWORDS = {
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
      "while",     
   };
   
   final static String[] PERL_OP = {
      " and ",
      " eq ",
      " ge ", " gt ",
      " le ", " lt ",
      " ne ",
      " or ",
      " q ", " qq ", " qr ", " qw ", " qx ",
      " s ",
      " tr ",
      " xor ",
      " y "
   };
   
   private final static String LINE_CMNT = "#";
   
   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
      searcher.setCharAttrBlack();
      searcher.signedVariables(START_OF_VAR, END_OF_VAR, Attributes.PURPLE_PLAIN);
      searcher.keywords(PERL_KEYWORDS, true, Attributes.RED_BOLD);
      searcher.keywords(PERL_OP, false, Attributes.RED_BOLD);
      searcher.bracesGray();
      searcher.quotedText();
      searcher.lineComments(LINE_CMNT, START_OF_VAR);
   }
}
