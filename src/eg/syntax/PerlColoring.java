package eg.syntax;

/**
 * Syntax coloring for Perl
 */
public class PerlColoring implements Colorable {
   
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
   public void color(SyntaxSearch search) {
      search.setCharAttrBlack();
      search.signedVariables(START_OF_VAR, END_OF_VAR);
      search.keywordsRedBold(PERL_KEYWORDS, true);
      search.keywordsRedBold(PERL_OP, false);
      search.bracesGray();
      search.quotedText();
      search.lineComments(LINE_CMNT, START_OF_VAR);
   }
}
