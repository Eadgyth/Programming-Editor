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
      if (!s.isInBlockCmnt(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END)) {

         s.setMarkupSection();
         s.resetAttributes();
         s.markupElements(null, null);
      }
      s.blockComments(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END);
   }
   
   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
