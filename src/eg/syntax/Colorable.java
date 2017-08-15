package eg.syntax;

/**
 * The interface to color text
 */
public interface Colorable {
   
   /**
    * Colors text
    *
    * @param lex  the reference to {@link Lexer}
    */
   public void color(Lexer lex);
}
