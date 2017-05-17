package eg.syntax;

import javax.swing.text.StyledDocument;

/**
 * The interface to color text
 */
public interface Colorable {
   
   /**
    * Colors text.
    * 
    * @param allText  the text of the document
    * @param toColor  the section of '{@code allText}' to be colored. May
    * be equal to '{@code allText}' to scan the entire text
    * @param pos  the pos within the entire text where a change happened
    * @param posStart  the position within the entire text where
    * '{@code toColor}' starts
    * @param lex  the reference to {@link Lexer}
    */
   public void color(String allText, String toColor, int pos,
         int posStart, Lexer lex);
}
