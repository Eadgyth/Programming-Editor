package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for Perl
 *
 */
public class PerlHighlighter implements Highlighter, QuoteOperatorSearch,
      HeredocSearch {

   private static final char[] START_OF_VAR = {
      '$', '@', '%'
   };

   private static final char[] START_OF_SCALAR = {
      '$'
   };

   private static final char[] START_OF_ARR_HASH = {
      '@', '%'
   };

   private static final char[] SECOND_POS_SCALAR = {
      '\\', '(', ')', ';', '[', ']', '}', '.', ':', ',', '?', '#',
      '=', '/', '+', '-', '*', '|', '&', '!', '%', '^', '<', '>', '~'
   };

   private static final char[] END_OF_VAR = {
      ' ', '\\', '(', ')', ';', '[', ']', '{', '}', '.', ':', ',', '?', '\n',
      '=', '/', '+', '-', '*', '|', '&', '!', '%', '^', '<', '>', '~', '"', '\''
   };

   private static final char[] PERL_NON_OP_START = {
      '§', '%', '&', '@', '$', '/', '#'
   };

   private static final char[] PERL_Q_KEYWORD_SEC = {
      'q', 'r', 'w', 'x'
   };

   private static final char[] NON_KEY_WORD_START = {
      '§', '$', '%', '_', '°'
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

   private static final String[] LINE_CMNT_MARKS = {
      SyntaxConstants.HASH
   };

   private static final String POD_SIGNAL = "\n=";
   private static final String POD_END = "=cut";
   private static final String HEREDOC_SIGNAL = "<<";

   private static final int IGNORE_COND = 0;
   private static final int LINE_CMNT_COND = 1;

   private int firstPodSignal = -1;
   private boolean prescannedForPod = false;

   @Override
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.resetAttributes();
      s.mapHeredocs(this);
      s.mapQuoteOperators(this, false);
      s.quote(false);
      s.setCondition(LINE_CMNT_COND);
      s.lineComments(LINE_CMNT_MARKS);
      s.setCondition(IGNORE_COND);
      s.keywords(SYNTAX_KEYWORDS, NON_KEY_WORD_START, attr.redPlain);
      s.signedVariables(START_OF_SCALAR, END_OF_VAR, SECOND_POS_SCALAR,
            attr.bluePlain);

      s.signedVariables(START_OF_ARR_HASH, END_OF_VAR, null,
            attr.purplePlain);

      s.brackets();
      s.braces();
      firstPodSignal = -1;
      prescannedForPod = false;
   }

   @Override
   public boolean isValid(String text, int pos, int condition) {
      if (condition == LINE_CMNT_COND) {
         return isLineCmntStart(text, pos);
      }
      return true;
   }

   @Override
   public int behindLineCmntMark(String text, int pos) {
      int i = SyntaxUtils.behindMark(text, SyntaxConstants.HASH, pos);
      if (i != -1 && !isLineCmntStart(text, i)) {
         i = -1;
      }
      return i;
   }

   @Override
   public int inBlockCmntMarks(String text, int pos) {
      if (!prescannedForPod) {
         prescanForPod(text);
      }
      if (firstPodSignal != -1 && pos > firstPodSignal) {
         int lastStart = text.lastIndexOf(POD_SIGNAL, pos);
         while (lastStart != -1 && text.length() > lastStart + 1
               && !Character.isLetter(text.charAt(lastStart + 2))) {

            lastStart = text.lastIndexOf(POD_SIGNAL, lastStart - 1);
         }
         if (lastStart != -1 || firstPodSignal == -2) {
            int start = lastStart == -1 && firstPodSignal == -2 ? 0 : lastStart + 1;
            if (text.length() >= start + 4
                  && !POD_END.equals(text.substring(start, start + 4))) {

               return start;
            }
         }
      }
      return -1;
   }

   @Override
   public int nextQuoteOperator(String text, int pos) {
      return text.indexOf('q', pos);
   }

   @Override
   public int quoteIdentifierLength(String text, int pos) {
      int length = 1;
      if (text.length() - 1 > pos
            && SyntaxUtils.isCharEqualTo(text, pos + 1, PERL_Q_KEYWORD_SEC)) {

         length = 2;
      }
      if (!SyntaxUtils.isWord(text, pos, length, PERL_NON_OP_START)
            || inLineCmnt(text, pos)) {

         length = 0;
      }
      return length;
   }

   @Override
   public int quoteLength(String text, int pos) {
      int length = 0;
      int d = SyntaxUtils.nextNonSpace(text, pos, false);
      if (d < text.length()) {
         char c = text.charAt(d);
         if (!Character.isLetter(c)) {
            int iDel = -1;
            for (iDel = 0; iDel < SyntaxConstants.OPEN_BRACKETS.length; iDel++) {
               if (SyntaxConstants.OPEN_BRACKETS[iDel] == c) {
                  break;
               }
            }
            int offset = 0;
            if (iDel != SyntaxConstants.OPEN_BRACKETS.length) {
               offset = SyntaxUtils.sectionLengthSkipEscaped(text, d,
                     SyntaxConstants.CLOSE_BRACKETS[iDel]);
            }
            else {
               offset = SyntaxUtils.sectionLengthSkipEscaped(text, d, c);
            }
            if (d + offset < text.length()) {
               length = offset + (d - pos) + 1;
            }
         }
      }
      return length;
   }

   @Override
   public int nextHeredoc(String text, int start) {
      return text.indexOf(HEREDOC_SIGNAL, start);
   }

   @Override
   public String heredocTag(String text, int pos, int lineEnd) {
      int start = pos + HEREDOC_SIGNAL.length();
      if (start == lineEnd) {
         return "";
      }
      int length = 0;
      boolean quoted = false;
      char s = text.charAt(start);
      if (s == '\'' || s == '\"') {
         start++;
         quoted = true;
      }
      for (int i = start; i < lineEnd; i++) {
         char c = text.charAt(i);
         if (i == start) {
            if (Character.isLetter(c) || c == '_' || (quoted && c == ' ')) {
               length++;
            }
         }
         else {
            if (SyntaxUtils.isLetterOrDigit(c) || c == '_' || (quoted && c == ' ')) {
               length++;
            }
            else if (quoted && c == s) {
               break;
            }
            else {
               length = 0;
               break;
            }
         }
      }
      return text.substring(start, start + length);
   }

   @Override
   public boolean validHeredocEnd(String text, int end, int tagLength) {
      if (text.charAt(end - 1) != '\n') {
         return false;
      }
      int idEnd = end + tagLength;
      boolean b = text.length() == idEnd;
      if (text.length() - 1 >= idEnd) {
         b = text.charAt(idEnd) == '\n';
      }
      return b;
   }

   //
   //--private--/
   //

   private void prescanForPod(String text) {
      if (text.length() > 1 && text.charAt(0) == '='
            && Character.isLetter(text.charAt(1))) {
         //
         // -2: artificial value to indicate that the checked
         // =[letter] but not \n= is found at pos 0 
         firstPodSignal = -2;
      }
      else {
         //
         // the first occurence of \n=
         firstPodSignal = text.indexOf(POD_SIGNAL, 0);
      }
      prescannedForPod = true;
   }

   private boolean isLineCmntStart(String text, int pos) {
      if (pos > 0) {
         char c = text.charAt(pos - 1);
         for (char non : START_OF_VAR) {
            if (c == non) {
               return false;
            }
         }
      }
      return true;
   }

   private boolean inLineCmnt(String text, int pos) {
      int i = -1;
      i = behindLineCmntMark(text, pos);
      if (i != -1) {
         i = SyntaxUtils.isQuoted(text, i)
               || !isLineCmntStart(text, i) ? -1 : i;
      }
      return i != -1;
   }
}
