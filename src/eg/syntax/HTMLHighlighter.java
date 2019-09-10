package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for Html (or PHP)
 */
public class HTMLHighlighter implements Highlighter {

   private final JavascriptHighlighter js = new JavascriptHighlighter();
   private final CSSHighlighter css = new CSSHighlighter();
   private final PHPHighlighter php = new PHPHighlighter(true);

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      if (!s.isInBlock(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END, SyntaxUtils.IGNORE_QUOTED)

            && !s.isInBlock(SyntaxConstants.CDATA_BLOCK_START,
                  SyntaxConstants.CDATA_BLOCK_END, SyntaxUtils.IGNORE_QUOTED)) {

         s.setMarkupSection();
         s.resetAttributes();
         s.markup(true);
      }
      s.block(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END, SyntaxUtils.IGNORE_QUOTED);

      s.block(SyntaxConstants.CDATA_BLOCK_START,
            SyntaxConstants.CDATA_BLOCK_END, SyntaxUtils.IGNORE_QUOTED);

      s.innerHtmlSections("<style", "</style>", true, css);
      s.innerHtmlSections("<?", "?>", false, php);
      s.innerHtmlSections("<script", "</script>", true, js);      
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
