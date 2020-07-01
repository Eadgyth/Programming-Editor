package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * The interface to highlight text elements
 */
public interface Highlighter {

   /**
    * Highlights text elements
    *
    * @param s  the SyntaxSearcher
    * @param attr  the Attributes
    */
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr);

   /**
    * Returns if a text element found by search methods in
    * <code>SyntaxSearcher</code> is valid
    *
    * @param text  the text
    * @param pos  the position where a text element is found
    * @param length  the length of the text element
    * @param condition  a condition for validating a text element
    * @return  true if valid, false otherwise
    */
    public boolean isValid(String text, int pos, int length, int condition);
}
