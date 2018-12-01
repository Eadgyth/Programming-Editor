package eg.document;

/**
 * The interface to edit text programmatically
 */
@FunctionalInterface
public interface TextChange {
   
   /**
    * Makes the text change and applies syntax highlighting if the
    * specified boolean is true
    *
    * @param highlight  true for syntax highlighting
    */
   public void edit(boolean highlight);
}
