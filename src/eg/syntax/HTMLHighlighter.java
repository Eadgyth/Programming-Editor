package eg.syntax;

/**
 * Syntax highlighting for Html
 */
public class HTMLHighlighter implements Highlighter {

   private final JavascriptHighlighter js = new JavascriptHighlighter();
   private final CSSHighlighter css = new CSSHighlighter();

   private SyntaxHighlighter.SyntaxSearcher s;

   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      s = searcher;
   }

   @Override
   public void highlight() {
      if (!s.isInBlock(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END)
            
         && !s.isInBlock(SyntaxConstants.CDATA_BLOCK_START,
               SyntaxConstants.CDATA_BLOCK_END)) {

         s.setMarkupSection();
         s.resetAttributes();
         s.markup(true);
      }
      s.block(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END);
               
      s.block(SyntaxConstants.CDATA_BLOCK_START,
            SyntaxConstants.CDATA_BLOCK_END);
            
      s.embeddedHtmlSections("<script", "</script>", js);
      s.embeddedHtmlSections("<style", "</style>", css);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
