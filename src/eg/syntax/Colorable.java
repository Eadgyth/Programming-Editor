package eg.syntax;

/**
 * The interface to color text
 */
public interface Colorable {
   
   /**
    * Colors text using methods in {@code Coloring} and/or own methods
    * @param in  the text of the document
    * @param chunk  a chunk of text to be colored (a line)
    * @param posStart  the start position of the chunk within the
    * entire text (0 if chunk equals in)
    * @param col  the reference to {@link Coloring}
    */
   public void color(String in, String chunk, int posStart, Coloring col);
}
