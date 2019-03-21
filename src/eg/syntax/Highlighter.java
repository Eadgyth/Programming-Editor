package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * The interface to highlight text elements
 */
public interface Highlighter {

   /**
    * Highlights text elements
    *
    * @param s  the SyntaxHighlighter.SyntaxSearcher
    * @param attr  the Attributes
    */
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr);

   /**
    * Returns if text elements found by search methods in
    * <code>SyntaxSearcher</code> are valid. An implementation may define
    * additional conditions that are not specified in the search methods or
    * simply return true.
    *
    * @param text  the text
    * @param pos  the position where a text element is found
    * @param length  the length of the text element
    * @param condition  a switch for conditions which an implementation
    * may ignore
    * @return  true if valid, false otherwise
    */
    public boolean isValid(String text, int pos, int length, int condition);
}
