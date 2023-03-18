package eg.syntax;

//--Eadgyth--/
import eg.utils.LinesFinder;
import eg.document.styledtext.StyledText;
import eg.document.styledtext.Attributes;

/**
 * The syntax highlighting
 */
public class SyntaxHighlighter {

   private final SyntaxSearcher searcher;

   private final StyledText txt;
   private final Attributes attr;
   private Highlighter hl;

   /**
    * @param txt  the StyledText
    */
   public SyntaxHighlighter(StyledText txt) {
      this.txt = txt;
      attr = txt.attributes();
      searcher = new SyntaxSearcher(txt);
   }

    /**
    * Sets a <code>Highlighter</code>.
    *
    * @param hl  the Highlighter; null means that this
    * SyntaxHighligher is not used (is not checked)
    */
   public void setHighlighter(Highlighter hl) {
      searcher.setHighlighter(hl);
      this.hl = hl;
   }

   /**
    * Highlights text elements in the entire text
    */
   public void highlight() {
      searcher.setTextParams(txt.text(), 0, 0);
      hl.highlight(searcher, attr);
   }

   /**
    * Highlights text elements in a section around the position where
    * a change happened. The section to upate is initially the line
    * that contains the <code>chgPos</code> or two lines if
    * <code>isNewline</code> is true.
    *
    * @param chgPos  the position where a change happened
    * @param isNewline  if the change is a newline character
    */
   public void highlight(int chgPos, boolean isNewline) {
      int lineStart = LinesFinder.lastNewline(txt.text(), chgPos);
      String scn;
      if (!isNewline) {
         scn = LinesFinder.line(txt.text(), lineStart);
      }
      else {
         int lineEnd = LinesFinder.nextNewline(txt.text(), chgPos + 1);
         scn = txt.text().substring(lineStart + 1, lineEnd);
      }
      searcher.setTextParams(scn, chgPos, lineStart + 1);
      hl.highlight(searcher, attr);
   }

   /**
    * Highlights text elements in a section that may be multiline.
    * The section to update initially consists of the completed
    * lines that contain <code>change</code> and begins with the line
    * that contains <code>chgPos</code>.
    *
    * @param change  the change to the text
    * @param chgPos  the position where the change starts
    */
   public void highlight(String change, int chgPos) {
      int linesStart = LinesFinder.lastNewline(txt.text(), chgPos);
      int length = chgPos - linesStart + change.length();
      String scn = LinesFinder.lines(txt.text(), linesStart, length);
      searcher.setTextParams(scn, chgPos, linesStart + 1);
      hl.highlight(searcher, attr);
   }
}
