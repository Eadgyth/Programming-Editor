package eg.syntax;

import eg.utils.Finder;

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
      "$", "@"
   };
   
   private final static String lineCmnt = "#";
   
   @Override
   public void color(String in, String chunk, int posStart, Coloring col) {
      col.setCharAttrBlack(posStart, chunk.length());
      for (String s : PERL_FLAGS) {
         col.withFlag(chunk, s, posStart);
      }
      for (String s : PERL_KEYWORDS) {
         col.keysRed(chunk, s, posStart, true);
      }
      for (String s : PERL_OP) {
         col.keysRed(chunk, s, posStart, false);
      }
      for (String b : SyntaxUtils.BRACKETS) {
         col.brackets(chunk, b, posStart);
      }
      col.stringLiterals(chunk, posStart, null, null);
      col.lineComments(chunk, posStart, lineCmnt);
   }
}
