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
   
   private final static char[] OPEN_DEL = {
      '(', '{', '<', '/'
   };
   
   private final static char[] CLOSE_DEL = {
      ')', '}', '>', '/'
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
   
   private final static String[] STRING_OP = {
      " and ",
      " cmp ",
      " eq ",
      " ge ", " gt ",
      " le ", " lt ",
      " ne ",
      " or ",
      " xor "
   };
   private final static int DEF_OPT = 0;
   private final static int LINE_CMNT_OPT = 1;
   
   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
      searcher.setSectionBlack();
      searcher.setOption(DEF_OPT);
      searcher.signedVariables(START_OF_VAR, END_OF_VAR, true,
            Attributes.PURPLE_PLAIN);
      searcher.keywords(KEYWORDS, true, null, Attributes.RED_BOLD);
      searcher.keywords(STRING_OP, false, null, Attributes.RED_BOLD);
      searcher.braces();
      searcher.quotedTextInLines(Attributes.ORANGE_PLAIN);
      searcher.setOption(LINE_CMNT_OPT);
      searcher.lineComments(SyntaxConstants.HASH);
   }

   @Override
   public boolean isEnabled(String text, int pos, int option) {
      boolean ok = true;
      if (option == DEF_OPT) {
         ok = isNotQwFunction(text, pos);
      }
      else if (option == LINE_CMNT_OPT) {
         ok = isNotQwFunction(text, pos) && isLineCmnt(text, pos);
      }
      return ok;
   }
   
   private boolean isNotQwFunction(String text, int pos) {
      boolean ok = true;
      int qwPos = SyntaxUtils.lastBlockStart(text, pos, "qw", ";");
      if (qwPos != -1) {
         int bracketStart
               = SyntaxUtils.nextNonSpace(text, qwPos + "qw".length());

         int ithDel = -1;
         if (SyntaxUtils.isWordStart(text, qwPos, null)
               && bracketStart >= qwPos + "qw".length()) {

            if (bracketStart < text.length()) {
               for (ithDel = 0; ithDel < OPEN_DEL.length; ithDel++) { 
                  if (OPEN_DEL[ithDel] == text.charAt(bracketStart)) {
                     break;
                  }
               }
            }
            if (ithDel != -1 && ithDel != OPEN_DEL.length) {  
               char[] close = { CLOSE_DEL[ithDel] };
               int length = SyntaxUtils.wordLength(text, bracketStart, close);
               ok = pos <= qwPos || pos > bracketStart + length;
            }
         }
      }
      return ok;
   }
   
   private boolean isLineCmnt(String text, int pos) {
      boolean ok = true;
      if (pos > 0) {
         char c = text.charAt(pos - 1);
         for (char non : START_OF_VAR) {
            ok = c != non;
            if (!ok) {
               break;
            }
         }
      }
      return ok;
   }
}
