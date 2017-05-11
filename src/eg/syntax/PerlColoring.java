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
   
   private final static String[] PERL_FLAGS = {
      "$", "@", "%"
   };
   
   private final static String LINE_CMNT = "#";
   
   @Override
   public void color(String allText, String toColor, int pos,
         int posStart, Lexer lex) {

      lex.setCharAttrBlack(posStart, toColor.length());
      for (String s : PERL_FLAGS) {
         variable(toColor, s, posStart, lex);
      }
      for (String s : PERL_KEYWORDS) {
         lex.keywordRed(toColor, s, posStart, true);
      }
      for (String s : PERL_OP) {
         lex.keywordRed(toColor, s, posStart, false);
      }
      for (String b : SyntaxUtils.BRACKETS) {
         lex.bracketBlue(toColor, b, posStart);
      }
      for (String s : SyntaxUtils.CURLY_BRACKETS) {
         lex.bracket(toColor, s, posStart);
      }
      lex.quotedLineWise(toColor, posStart, "\"", null, null, "\\");
      lex.lineComments(toColor, posStart, LINE_CMNT);
   }
   
   private void variable(String in, String flag, int pos, Lexer lex) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(flag, start + jump);
         if (start != -1 && SyntaxUtils.isWordStart(in, start)) {
            int length = variableLength(in.substring(start));
            lex.setCharAttrKeyBlue(start + pos, length);
         }  
         jump = 1; 
      }
   }
   
   private int variableLength(String text) {      
      char[] c = text.toCharArray();
      int i = 1;
      for (i = 1; i < c.length; i++) {                     
         if (c[i] == ' ') {
            break;
         }
      }
      return i;
   }
}
