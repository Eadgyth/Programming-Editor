package eg.document;

/**
 * The interface make changes to the text programmatically
 */
@FunctionalInterface
public interface TextChange {
   
   /**
    * Performs the text change
    *
    * @param highlight  true to do syntax highlighting after the
    * text change
    */
   public void edit(boolean highlight);
}
