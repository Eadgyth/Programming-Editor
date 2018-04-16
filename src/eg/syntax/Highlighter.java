package eg.syntax;

/**
 * The interface to highlight text
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
    * Returns the boolean that indicates if highlighting the text element
    * at the specified position is enabled
    *
    * @param text  the entire text
    * @param pos  the position where a text element is found
    * @param option  a switch which an implementation may ignore
    * @return  the boolean value
    */
    public boolean isEnabled(String text, int pos, int option);
}
