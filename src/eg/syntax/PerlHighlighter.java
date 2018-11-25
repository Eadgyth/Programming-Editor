package eg.syntax;

import eg.document.Attributes;

/**
 * Syntax highlighting for Perl
 */
public class PerlHighlighter implements Highlighter {

   private final static char[] START_OF_VAR = {
      '$', '@', '%'
   };

   private final static char[] START_OF_ARR_HASH = {
      '@', '%'
   };

   private final static char[] START_OF_SCALAR = {
      '$'
   };

   private final char[] SECOND_POS_SCALAR = {
      '\\', '(', ')', ';', '[', ']', '}', '.', ':', ',', '?',
      '=', '/', '+', '-', '*', '|', '&', '!', '%', '^', '<', '>', '~'
   };

   private final static char[] END_OF_VAR = {
      ' ', '\\', '(', ')', ';', '[', ']', '{', '}', '.', ':', ',', '?', '\n',
      '=', '/', '+', '-', '*', '|', '&', '!', '%', '^', '<', '>', '~',
   };

   private final static char[] OPEN_QW_DEL = {
      '(', '{', '<', '/', '\'', '!', '@'
   };

   private final static char[] CLOSE_QW_DEL = {
      ')', '}', '>', '/', '\'', '!', '@'
   };
   
   private final static String[] SYNTAX_KEYWORDS = {
      "and",
      "cmp", "continue", "CORE", "do",
      "else", "elsif", "eq", "exp",
      "for", "foreach",
      "ge", "gt",
      "if",
      "le", "lock", "lt",
      "m",
      "ne", "no",
      "or",
      "package",
      "q", "qq", "qr", "qw",
      "s", "sub",
      "tr",
      "unless", "until",
      "while",
      "xor",
      "y"
   };

   private final static int QW_COND = 0;
   private final static int LINE_CMNT_COND = 1;

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      s.setCondition(QW_COND);
      s.setStatementSection();
      s.resetAttributes();
      s.signedVariables(START_OF_ARR_HASH, END_OF_VAR, null,
            attr.purplePlain);

      s.signedVariable('$', END_OF_VAR, SECOND_POS_SCALAR,
            attr.bluePlain);

      s.keywords(SYNTAX_KEYWORDS, true, START_OF_VAR, attr.redPlain);
      s.brackets();
      s.braces();
      s.quote();
      s.setCondition(LINE_CMNT_COND);
      s.lineComments(SyntaxConstants.HASH);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      boolean ok = true;
      if (condition == QW_COND) {
         ok = isNotQwFunction(text, pos);
      }
      else if (condition == LINE_CMNT_COND) {
         ok = isLineCmnt(text, pos) && isNotQwFunction(text, pos);
      }
      return ok;
   }

   //
   //--private--/
   //

   private boolean isNotQwFunction(String text, int pos) {
      boolean ok = true;
      int qwPos = text.lastIndexOf("qw", pos);
      if (qwPos != -1) {
         if (SyntaxUtils.isWordStart(text, qwPos, null)) {
            int delStart = SyntaxUtils.nextNonSpace(text, qwPos + 2);
            int ithDel = -1;
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
               int length = SyntaxUtils.sectionLength(text, delStart, close, null);
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
