package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for xml
 */
public class XMLHighlighter implements Highlighter {

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      if (!s.isInBlock(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END, SyntaxUtils.INCLUDE_QUOTED)

            && !s.isInBlock(SyntaxConstants.CDATA_BLOCK_START,
                  SyntaxConstants.CDATA_BLOCK_END, SyntaxUtils.INCLUDE_QUOTED)) {

         s.setMarkupSection();
         s.resetAttributes();
         s.markup(false);
      }
      s.block(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END, SyntaxUtils.INCLUDE_QUOTED);

      s.block(SyntaxConstants.CDATA_BLOCK_START,
            SyntaxConstants.CDATA_BLOCK_END, SyntaxUtils.INCLUDE_QUOTED);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
