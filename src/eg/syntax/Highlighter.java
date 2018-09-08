package eg.syntax;

/**
 * The interface to highlight text elements
 */
public interface Highlighter {

   /**
    * Highlights text elements
    *
    * @param searcher  the reference to
    * {@link SyntaxHighlighter.SyntaxSearcher}
    */
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher);
   
   /**
    * Returns if highlighting the text element at the specified position
    * is enabled. This method may define conditions for highlightnig a
    * found text element or simply return true.
    *
    * @param text  the entire text
    * @param pos  the position where a text element is found
    * @param option  a switch for conditions which an implementation may ignore
    * @return  the boolean value, true if enabled
    */
    public boolean isEnabled(String text, int pos, int option);
}
