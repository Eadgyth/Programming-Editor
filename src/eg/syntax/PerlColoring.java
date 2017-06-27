package eg.syntax;

public class PerlColoring implements Colorable {
   
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
   
   private final static String[] PERL_SIGNS = {
      "$", "@", "%"
   };
   
   private final static String LINE_CMNT = "#";
   
   @Override
   public void color(Lexer lex) {
      lex.setCharAttrBlack();
      for (String s : PERL_SIGNS) {
         lex.signedVariable(s, END_OF_VAR);
      }
      for (String s : PERL_KEYWORDS) {
         lex.keywordRed(s, true);
      }
      for (String s : PERL_OP) {
         lex.keywordRed(s, false);
      }
      for (String s : SyntaxUtils.BRACKETS) {
         lex.bracket(s);
      }
      for (String s : SyntaxUtils.BRACES) {
         lex.bracket(s);
      }
      lex.quotedLineWise("\'", true);
      lex.quotedLineWise("\"", true);
      lex.lineComments(LINE_CMNT, '$');
   }
}
