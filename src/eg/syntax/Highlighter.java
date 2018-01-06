package eg.syntax;

/**
 * The interface to highlight text
 */
public interface Highlighter {

   /**
    * Highlights text elements
    *
    * @param searcher  the reference to {@link SyntaxHighlighter.SyntaxSearcher}
    */
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher);
}
