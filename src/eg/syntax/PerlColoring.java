package eg.syntax;

public class PerlColoring implements Colorable {
   
   final static String[] PERL_KEYWORDS = {
      "cmp", "continue", "CORE",
      "do",
      "else", "elsif", "eq", "exp",
      "for", "foreach",
      "if",
      "lock",
      "no",
      "package",
      "sub",
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
         lex.signedVariable(s);
      }
      for (String s : PERL_KEYWORDS) {
         lex.keywordRed(s, true);
      }
      for (String s : PERL_OP) {
         lex.keywordRed(s, false);
      }
      for (String s : SyntaxUtils.BRACKETS) {
         lex.bracketBlue(s);
      }
      for (String s : SyntaxUtils.CURLY_BRACKETS) {
         lex.bracket(s);
      }
      lex.quotedLineWise("\"", "\\");
      lex.lineComments(LINE_CMNT);
   }
}
