package eg.syntax;

/**
 * The interface to highlight text elements
 */
public interface Highlighter {

   /**
    * Returns if the marks for the start and end of block comments may be
    * surrounded by quotation marks
    *
    * @return  the boolean value that is true if allowed
    */
   public boolean allowBlkCmntMarksQuoted();

   /**
    * Highlights text elements
    *
    * @param searcher  the reference to
    * {@link SyntaxHighlighter.SyntaxSearcher}
    */
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher);
   
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
