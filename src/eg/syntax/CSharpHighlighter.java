package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for C#
 */
public class CSharpHighlighter implements Highlighter, QuoteOperatorSearch {

   private static final String[] KEYWORDS = {
      "abstract", "as", "base", "bool", "break",
      "byte", "case", "catch", "char", "checked",
      "class", "const", "continue", "decimal", "default",
      "delegate", "do", "double", "else", "enum",
      "event", "explicit", "extern", "false", "finally",
      "fixed", "float", "for", "foreach", "goto",
      "if", "implicit", "in", "int", "interface",
      "internal", "is", "lock", "long", "namespace",
      "new", "null", "object", "operator", "out",
      "override", "params", "private", "protected", "public",
      "readonly", "ref", "return", "sbyte", "sealed",
      "short", "sizeof", "stackalloc", "static", "string",
      "struct", "switch", "this", "throw", "true",
      "try", "typeof", "uint", "ulong", "unchecked",
      "unsafe", "ushort", "using", "var", "virtual",
      "void", "volatile", "while"
   };

   private static final String[] LINE_CMNT_MARK = {
      SyntaxConstants.DOUBLE_SLASH
   };

   private static final char VERBATIM_START = '@';

   private static final char[] KEY_NON_START = {
      VERBATIM_START, '§', '\'', '$', '°', '@'
   };

   @Override
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.resetAttributes();
      s.mapQuoteOperators(this, true);
      s.quote(true);
      s.lineComments(LINE_CMNT_MARK);
      s.brackets();
      s.braces();
      s.keywords(KEYWORDS, KEY_NON_START, attr.redPlain);
      s.blockComments(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH, false);
   }

   @Override
   public boolean isValid(String text, int pos, int condition) {
      return true;
   }

   @Override
   public int behindLineCmntMark(String text, int pos) {
      return SyntaxUtils.behindMark(text, SyntaxConstants.DOUBLE_SLASH, pos);
   }

   @Override
   public int inBlockCmntMarks(String text, int pos) {
      return SyntaxUtils.inBlock(text, SyntaxConstants.SLASH_STAR,
            SyntaxConstants.STAR_SLASH, pos);
   }

   @Override
   public int nextQuoteOperator(String text, int start) {
      return text.indexOf(VERBATIM_START, start);
   }

   @Override
   public int quoteIdentifierLength(String text, int pos) {
      int p1 = pos + 1;
      boolean valid = text.length() > p1
            && (text.charAt(p1) == SyntaxConstants.DOUBLE_QUOTE
            && !inCmnt(text, pos)
            && !SyntaxUtils.isClosingQuoteMarkInLine(text, p1));

      return valid ? 1 : 0;
   }

   @Override
   public int quoteLength(String text, int pos) {
      int length = 0;
      int end = -1;
      int p1 = pos + 1;
      if (text.length() > p1) {
         end = text.indexOf(SyntaxConstants.DOUBLE_QUOTE, p1);
         if (end != -1) {
            length = (end - pos) + 1;
            while (end != -1
                  && text.length() > end + 2
                  && text.charAt(end + 1) == SyntaxConstants.DOUBLE_QUOTE) {

               end = text.indexOf(SyntaxConstants.DOUBLE_QUOTE, end + 2);
               if (end != -1) {
                  length = (end - pos) + 1;
               }
            }
         }
      }
      return length;
   }

   //
   //--private--//
   //

   private boolean inCmnt(String text, int pos) {
      int i = -1;
      i = inBlockCmntMarks(text, pos);
      if (i == -1) {
         i = behindLineCmntMark(text, pos);
      }
      if (i != -1) {
         i = SyntaxUtils.isQuotedInLine(text, i) ? -1 : i;
      }
      return i != -1;
   }
}
