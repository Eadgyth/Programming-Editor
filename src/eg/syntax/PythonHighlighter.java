package eg.syntax;

import eg.document.styledtext.Attributes;

import eg.utils.LinesFinder;
/**
 * Syntax highlighting for Python
 */
public class PythonHighlighter implements Highlighter {

   private final static String[] KEYWORDS = {
      "and", "as", "assert",
      "break",
      "class", "continue",
      "def", "del",
      "elif", "else", "except",
      "False", "finally", "for", "from",
      "global",
      "if", "import", "in", "is",
      "lambda",
      "None", "nonlocal", "not",
      "or",
      "pass",
      "raise", "return",
      "True", "try",
      "while", "with",
      "yield"
   };

   private final static int IGNORE_COND = 0;
   private final static int TEXT_BLOCK_COND = 1;
   private final static int SINGLE_QUOTE_TEXT_BLOCK_COND = 2;
   private final static int DOUBLE_QUOTE_TEXT_BLOCK_COND = 3;

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      s.setCondition(IGNORE_COND);
      s.setExtendedBlockSection(
            SyntaxConstants.SINGLE_QUOTE_STR, SyntaxConstants.DOUBLE_QUOTE_STR);

      s.resetAttributes();
      s.keywords(KEYWORDS, true, null, attr.redPlain);
      s.brackets();
      s.braces();
      s.setCondition(TEXT_BLOCK_COND);
      s.lineComments(SyntaxConstants.HASH, SyntaxUtils.BLOCK_QUOTED);
      s.quote();
      s.setCondition(DOUBLE_QUOTE_TEXT_BLOCK_COND);
      s.textBlock(SyntaxConstants.TRI_DOUBLE_QUOTE);
      s.setCondition(SINGLE_QUOTE_TEXT_BLOCK_COND);
      s.textBlock(SyntaxConstants.TRI_SINGLE_QUOTE);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      if (condition == IGNORE_COND) {
         return true;
      }
      else if (condition == TEXT_BLOCK_COND) {
         return !SyntaxUtils.isInTextBlock(
               text, SyntaxConstants.TRI_SINGLE_QUOTE, pos, SyntaxConstants.HASH)
               && !SyntaxUtils.isInTextBlock(
               text, SyntaxConstants.TRI_DOUBLE_QUOTE, pos, SyntaxConstants.HASH);
      }
      else {
         String altDel = condition == DOUBLE_QUOTE_TEXT_BLOCK_COND ?
               SyntaxConstants.TRI_SINGLE_QUOTE : SyntaxConstants.TRI_DOUBLE_QUOTE;

         return !SyntaxUtils.isInTextBlock(text, altDel, pos, SyntaxConstants.HASH);
      }
   }
}
