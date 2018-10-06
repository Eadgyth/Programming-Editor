package eg.syntax;

/**
 * Syntax highlighting for xml
 */
public class XMLHighlighter implements Highlighter {
   
   private SyntaxHighlighter.SyntaxSearcher s;
   
   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      s = searcher;
   }

   @Override
   public void highlight() {
      if (!s.isInBlock(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END)
            
         && !s.isInBlock(SyntaxConstants.HTML_CDATA_BLOCK_START,
               SyntaxConstants.HTML_CDATA_BLOCK_END)) {

         s.setMarkupSection();
         s.resetAttributes();
         s.markupElements(null, null);
      }
      s.block(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END);
            
      s.block(SyntaxConstants.HTML_CDATA_BLOCK_START,
            SyntaxConstants.HTML_CDATA_BLOCK_END);
   }
   
   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
