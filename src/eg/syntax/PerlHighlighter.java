package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for Perl
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
      '\\', '(', ')', ';', '[', ']', '}', '.', ':', ',', '?',
      '=', '/', '+', '-', '*', '|', '&', '!', '%', '^', '<', '>', '~'
   };

   private static final char[] END_OF_VAR = {
      ' ', '\\', '(', ')', ';', '[', ']', '{', '}', '.', ':', ',', '?', '\n',
      '=', '/', '+', '-', '*', '|', '&', '!', '%', '^', '<', '>', '~', '"', '\''
   };

   private static final char[] PERL_NON_OP_START = {
      '§', '%', '&', '@', '$', '/'
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

   private static final String POD_START = "\n=";
   private static final String POD_END = "\n=cut";
   private static final String HEREDOC_SYMBOL = "<<";

   private static final int IGNORE_COND = 0;
   private static final int LINE_CMNT_COND = 1;

   @Override
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.resetAttributes();
      s.mapHeredocs(this);
      s.mapQuoteOperators(this);
      s.quote(false);
      s.setCondition(LINE_CMNT_COND);
      s.lineComments(LINE_CMNT_MARKS);
      s.setCondition(IGNORE_COND);
      s.keywords(SYNTAX_KEYWORDS, NON_KEY_WORD_START, attr.redPlain);
      s.signedVariables(START_OF_ARR_HASH, END_OF_VAR, null,
            attr.purplePlain);

      s.signedVariables(START_OF_SCALAR, END_OF_VAR, SECOND_POS_SCALAR,
            attr.bluePlain);

      s.brackets();
      s.braces();
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
      int start = -1;
      int end = text.indexOf(POD_END, pos);
      if (end != -1) {
         int lastEnd = text.lastIndexOf(POD_END, end - 1);
         lastEnd = lastEnd == -1 ? 0 : lastEnd + POD_END.length();
         int startTest = text.indexOf(POD_START, lastEnd);
         if (startTest < pos && pos < end
               && startTest != text.indexOf(POD_END, startTest)) {

            start = startTest;
         }
      }
      return start;
   }

   @Override
   public int nextQuoteOperator(String text, int start) {
      return text.indexOf('q', start);
   }

   @Override
   public int quoteIdentifierLength(String text, int pos) {
      int length = 1;
      if (text.length() - 1 > pos
            && SyntaxUtils.isCharEqualTo(text, pos + 1, PERL_Q_KEYWORD_SEC)) {

         length = 2;
      }
      return SyntaxUtils.isWord(text, pos, length, PERL_NON_OP_START) ? length : 0;
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
      return text.indexOf(HEREDOC_SYMBOL, start);
   }

   @Override
   public String heredocTag(String text, int pos, int lineEnd) {
      int start = pos + HEREDOC_SYMBOL.length();
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
}
