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
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.setMarkupSection();
      s.resetAttributes();
      s.markup(true);
      s.innerSections("<style", "</style>", true, css);
      s.innerSections("<?", "?>", false, php);
      s.innerSections("<script", "</script>", true, js);
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
