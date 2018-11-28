package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for xml
 */
public class XMLHighlighter implements Highlighter {

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      if (!s.isInBlock(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END, false)

            && !s.isInBlock(SyntaxConstants.CDATA_BLOCK_START,
                  SyntaxConstants.CDATA_BLOCK_END, false)) {

         s.setMarkupSection();
         s.resetAttributes();
         s.markup(false);
      }
      s.block(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END, false);

      s.block(SyntaxConstants.CDATA_BLOCK_START,
            SyntaxConstants.CDATA_BLOCK_END, false);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
