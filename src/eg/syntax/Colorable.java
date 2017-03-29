package eg.syntax;

/**
 * The interface to color text
 */
public interface Colorable {
   
   /**
    * Colors text. Called by {@link Lexer #color(String,int)}
    *
    * @param allText  the entire text of the document
    * @param toColor  the portion of '{@code allText}' to be colored. This is the
    * line where a change happened if 'type mode' is enabled in {@link Coloring}
    * and equals '{@code allText}' otherwise
    * @param pos  the pos within the entire text where a change happened
    * @param posStart  the position within the entire text where '{@code toColor}'
    * starts
    * @param lex  the reference to {@link Lexer}
    */
   public void color(String allText, String toColor, int pos,
         int posStart, Lexer lex);
}
