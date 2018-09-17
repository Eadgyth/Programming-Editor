package eg.syntax;

/**
 * The interface to highlight text elements
 */
public interface Highlighter {
   
   /**
    * Sets the reference to <code>SyntaxHighlighter.SyntaxSearcher</code>
    *
    * @param searcher  the reference to
    * {@link SyntaxHighlighter.SyntaxSearcher}
    */
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher);

   /**
    * Highlights text elements
    */
   public void highlight();
   
   /**
    * Returns if highlighting the text element at the specified position
    * is enabled. An implementation may define conditions that add to
    * the conditions in the search methods in
    * {@link SyntaxHighlighter.SyntaxSearcher}
    *
    * @param text  the entire text
    * @param pos  the position where a text element is found
    * @param condition  a switch for conditions which an implementation
    * may ignore
    * @return  the boolean value; true if enabled
    */
    public boolean isEnabled(String text, int pos, int condition);
}
