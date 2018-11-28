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
    * Returns if a found text element is valid. An Implementation may
    * define conditions that are tested in addition to those specified
    * in the search methods in {@link SyntaxHighlighter.SyntaxSearcher}
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
