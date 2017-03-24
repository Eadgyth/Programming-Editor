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
   public void color(String allText, String toColor, int pos,
         int posStart, Coloring col) {

      col.setCharAttrBlack(posStart, toColor.length());
      for (String s : PERL_FLAGS) {
         withFlag(toColor, s, posStart, col);
      }
      for (String s : PERL_KEYWORDS) {
         col.keysRed(toColor, s, posStart, true);
      }
      for (String s : PERL_OP) {
         col.keysRed(toColor, s, posStart, false);
      }
      for (String b : SyntaxUtils.BRACKETS) {
         col.brackets(toColor, b, posStart);
      }
      col.stringLiterals(toColor, posStart, null, null);
      col.lineComments(toColor, posStart, lineCmnt);
   }
   
   private void withFlag(String in, String flag, int pos, Coloring col) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(flag, start + jump);
         if (start != -1 && SyntaxUtils.isWordStart(in, start)) {
            int length = SyntaxUtils.wordLength(in.substring(start));
            col.setCharAttrKeyBlue(start + pos, length);
         }  
         jump = 1; 
      }
   }
}
