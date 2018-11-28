package eg.document;

/**
 * The interface to edit text (remove, paste, replace) programmatically
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
