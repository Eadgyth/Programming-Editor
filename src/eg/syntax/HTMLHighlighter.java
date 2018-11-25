package eg.syntax;

import eg.document.Attributes;

/**
 * Syntax highlighting for Html
 */
public class HTMLHighlighter implements Highlighter {

   private final JavascriptHighlighter js = new JavascriptHighlighter();
   private final CSSHighlighter css = new CSSHighlighter();

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
            
      s.embeddedHtmlSections("<script", "</script>", js);
      s.embeddedHtmlSections("<style", "</style>", css);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
