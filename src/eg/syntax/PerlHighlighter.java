package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for Perl
 */
public class PerlHighlighter implements Highlighter {

   private static final char[] START_OF_VAR = {
      '$', '@', '%'
   };

   private static final char[] START_OF_ARR_HASH = {
      '@', '%'
   };

   private static final char[] SECOND_POS_SCALAR = {
      '\\', '(', ')', ';', '[', ']', '}', '.', ':', ',', '?',
      '=', '/', '+', '-', '*', '|', '&', '!', '%', '^', '<', '>', '~'
   };

   private static final char[] END_OF_VAR = {
      ' ', '\\', '(', ')', ';', '[', ']', '{', '}', '.', ':', ',', '?', '\n',
      '=', '/', '+', '-', '*', '|', '&', '!', '%', '^', '<', '>', '~',
   };

   private static final char[] OPEN_QW_DEL = {
      '(', '{', '<'
   };

   private static final char[] CLOSE_QW_DEL = {
      ')', '}', '>'
   };

   private static final String[] SYNTAX_KEYWORDS = {
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
      "q", "qq", "qr", "qw", "qx",
      "s", "sub",
      "tr",
      "unless", "until",
      "while",
      "xor",
      "y"
   };

   private static final int IGNORE_COND = 0;
   private static final int QW_START_COND = 1;
   private static final int QW_COND = 2;
   private static final int LINE_CMNT_COND = 3;

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      s.setCondition(QW_START_COND);
      s.setStatementSection();
      s.resetAttributes();
      s.setCondition(QW_COND);
      s.signedVariables(START_OF_ARR_HASH, END_OF_VAR, null,
            attr.purplePlain);

      s.signedVariable('$', END_OF_VAR, SECOND_POS_SCALAR,
            attr.bluePlain);

      s.keywords(SYNTAX_KEYWORDS, true, START_OF_VAR, attr.redPlain);
      s.setCondition(IGNORE_COND);
      s.brackets();
      s.braces();
      s.setCondition(LINE_CMNT_COND);
      s.lineComments(SyntaxConstants.HASH, SyntaxUtils.BLOCK_QUOTED);
      s.setCondition(QW_COND);
      s.quote();
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      boolean ok = true;
      if (condition == QW_START_COND) {
         ok = !isQBefore(text, pos);
      }
      if (condition == QW_COND) {
         ok = isNotQFunction(text, pos);
      }
      else if (condition == LINE_CMNT_COND) {
         ok = isLineCmnt(text, pos) && isNotQFunction(text, pos);
      }
      return ok;
   }

   //
   //--private--/
   //

   private boolean isQBefore(String text, int pos) {
      return text.length() > pos && SyntaxUtils.lastUnquoted(text, "q", pos) > -1;
   }

   private boolean isNotQFunction(String text, int pos) {
      boolean ok = true;
      int delStart = qDelStart(text, pos);
      if (delStart != -1) {
         int ithDel = -1;
         for (ithDel = 0; ithDel < OPEN_QW_DEL.length; ithDel++) {
            if (OPEN_QW_DEL[ithDel] == text.charAt(delStart)) {
               break;
            }
         }
         char[] close;
         if (ithDel != -1) {
            if (ithDel != OPEN_QW_DEL.length) {
               close = new char[] { CLOSE_QW_DEL[ithDel] };
            }
            else {
               char c = text.charAt(delStart);
               close = new char[] {c};
            }
            int length = SyntaxUtils.sectionLength(text, delStart, close, null);
            ok = (pos <= delStart
                  || (delStart + length == text.length() || pos > delStart + length));
         }
      }
      return ok;
   }

   private int qDelStart(String text, int pos) {
      int qPos = SyntaxUtils.lastUnquoted(text, "q", pos);

      boolean valid = true;
      int del = -1;
      if (qPos != -1) {
         int searchStart = 0;
         if (qPos > 0 && text.charAt(qPos - 1) == 'q') {
            qPos--;
            searchStart = qPos + 2;
         }
         valid = !SyntaxUtils.isLineCommented(text, SyntaxConstants.HASH, qPos,
               SyntaxUtils.BLOCK_QUOTED);

         if (valid) {
            if (searchStart == 0 && text.length() > qPos + 2) {
               char c = text.charAt(qPos + 1);
               searchStart = (c == 'x' || c == 'w') ? qPos + 2 : qPos + 1;
            }
            valid = SyntaxUtils.isWordEnd(text, searchStart);
            int d = -1;
            if (valid) {
               d = SyntaxUtils.nextNonSpace(text, searchStart);
               while (text.charAt(d) == '\n') {
                  d = SyntaxUtils.nextNonSpace(text, d + 1);
               }
               valid = SyntaxUtils.isWordStart(text, d + 1, CLOSE_QW_DEL);
            }
            if (valid) {
               del = d;
            }
         }
         if (!valid) {
            return qDelStart(text, qPos - 1);
         }
      }
      return del;
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
