package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for xml
 */
public class XMLHighlighter implements Highlighter {

   @Override
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.setMarkupSection();
      s.resetAttributes();
      s.markup(false);
   }

   @Override
   public boolean isValid(String text, int pos, int condition) {
      return true;
   }
   
   @Override
   public int behindLineCmntMark(String text, int pos) {
      return -1;
   }
   
   @Override
   public int inBlockCmntMarks(String text, int pos) {
      return -1;
   }
}
