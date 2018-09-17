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

   private final static char[] OPEN_QW_DEL = {
      '(', '{', '<', '/', '\'', '!', '@'
   };

   private final static char[] CLOSE_QW_DEL = {
      ')', '}', '>', '/', '\'', '!', '@'
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
   private final static int DEF_COND = 0;
   private final static int LINE_CMNT_COND = 1;

   private SyntaxHighlighter.SyntaxSearcher searcher;
   
   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      this.searcher = searcher;
   }

   @Override
   public void highlight() {
      searcher.setSectionBlack();
      searcher.setCondition(DEF_COND);
      searcher.signedVariables(START_OF_VAR, END_OF_VAR, true,
            Attributes.PURPLE_PLAIN);

      searcher.keywords(KEYWORDS, true, null, Attributes.RED_BOLD);
      searcher.keywords(STRING_OP, false, null, Attributes.RED_BOLD);
      searcher.braces();
      searcher.quotedLinewise(Attributes.ORANGE_PLAIN);
      searcher.setCondition(LINE_CMNT_COND);
      searcher.lineComments(SyntaxConstants.HASH);
   }

   @Override
   public boolean isEnabled(String text, int pos, int condition) {
      boolean ok = true;
      if (condition == DEF_COND) {
         ok = isNotQwFunction(text, pos);
      }
      else if (condition == LINE_CMNT_COND) {
         ok = isNotQwFunction(text, pos) && isLineCmnt(text, pos);
      }
      return ok;
   }

   private boolean isNotQwFunction(String text, int pos) {
      boolean ok = true;
      int qwPos = SyntaxUtils.lastBlockStart(text, pos, "qw", ";", true);
      if (qwPos != -1) {
         int delStart = SyntaxUtils.nextNonSpace(text, qwPos + 2);
         int ithDel = -1;
         if (SyntaxUtils.isWordStart(text, qwPos, null)) {
            if (delStart < text.length()) {
               for (ithDel = 0; ithDel < OPEN_QW_DEL.length; ithDel++) {
                  if (OPEN_QW_DEL[ithDel] == text.charAt(delStart)) {
                     break;
                  }
               }
            }
            if (ithDel != -1 && ithDel != OPEN_QW_DEL.length) {
               char[] close = {
                  CLOSE_QW_DEL[ithDel]
               };
               int length = SyntaxUtils.wordLength(text, delStart, close);
               ok = pos <= qwPos || pos > delStart + length;
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
