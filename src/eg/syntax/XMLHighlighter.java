package eg.syntax;

/**
 * Syntax highlighting for xml
 */
public class XMLHighlighter implements Highlighter {
   
   private SyntaxHighlighter.SyntaxSearcher searcher;
   
   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      this.searcher = searcher;
   }

   @Override
   public void highlight() {
      if (!searcher.isInBlockCmnt(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END)) {

         searcher.setMarkupSection();
         searcher.resetAttributes();
         searcher.xmlElements();
      }
      searcher.blockComments(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END);
   }
   
   @Override
   public boolean isValid(String text, int pos, int condition) {
      return true;
   }
}
