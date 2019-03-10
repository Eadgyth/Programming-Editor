package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for Html (or PHP)
 */
public class HTMLHighlighter implements Highlighter {

   private final JavascriptHighlighter js = new JavascriptHighlighter();
   private final CSSHighlighter css = new CSSHighlighter();
   private final PHPHighlighter php = new PHPHighlighter();

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      if (!s.isInBlock(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END, false)

            && !s.isInBlock(SyntaxConstants.CDATA_BLOCK_START,
                  SyntaxConstants.CDATA_BLOCK_END, false)) {

         s.setMarkupSection();
         s.resetAttributes();
         s.markup(true);
      }
      s.block(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END, false);

      s.block(SyntaxConstants.CDATA_BLOCK_START,
            SyntaxConstants.CDATA_BLOCK_END, false);

      s.innerHtmlSections("<?", "?>", false, php);
      s.innerHtmlSections("<script", "</script>", true, js);
      s.innerHtmlSections("<style", "</style>", true, css);      
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }

   private static class PHPHighlighter implements Highlighter {

      private final static String[] PHP_KEYWORDS = {
         "abstract", "and", "as",
         "break",
         "callable", "case", "catch", "class", "clone", "const", "continue",
         "declare", "default", "do",
         "echo", "else", "elseif", "empty", "enddeclare", "endfor", "endforeach",
         "endif", "endswith", "endwhile", "eval", "exit", "extends",
         "final", "finally", "for", "foreach", "function",
         "global", "goto",
         "if", "implements", "include", "include__once", "instanceof", "insteadof",
         "interface",
         "namespace", "new",
         "or",
         "print", "private", "protected", "public",
         "require", "require_once", "return",
         "static", "switch",
         "throw", "trait", "try",
         "use",
         "var",
         "while",
         "xor",
         "yield",
      };

      private final static char[] END_OF_VAR = {
         ' ', '\n', '='
      };

      @Override
      public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
         if (!s.isInBlock(SyntaxConstants.SLASH_STAR,
               SyntaxConstants.STAR_SLASH, true)) {

            s.resetAttributes();
            s.signedVariable('$', END_OF_VAR, null, attr.bluePlain);
            s.keywordsIgnoreCase(PHP_KEYWORDS, true, null, attr.redPlain);
            s.quote();
            s.lineComments(SyntaxConstants.DOUBLE_SLASH);
            s.lineComments(SyntaxConstants.HASH);
         }
         s.block(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH, true);
      }

      @Override
      public boolean isValid(String text, int pos, int length, int condition) {
         return true;
      }
   }
}
