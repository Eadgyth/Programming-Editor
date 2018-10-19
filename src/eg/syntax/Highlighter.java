package eg.syntax;

/**
 * The interface to highlight text elements
 */
public interface Highlighter {
   
   /**
    * Sets the reference to <code>SyntaxHighlighter.SyntaxSearcher</code>
    *
    * @param searcher  the {@link SyntaxHighlighter.SyntaxSearcher}
    */
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher);

   /**
    * Highlights text elements
    */
   public void highlight();
   
   /**
    * Returns if a found text element is valid
    *
    * @param text  the text
    * @param pos  the position where a text element is found
    * @param length  the length of the text element
    * @param condition  a switch for conditions which an implementation
    * may ignore
    * @return  true if valid
    */
    public boolean isValid(String text, int pos, int length, int condition);
}
