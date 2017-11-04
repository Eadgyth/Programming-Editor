package eg.document;

/**
 * Created to give notice that the state, in which text is or isn't
 * selected, has changed
 */
public class TextSelectionEvent {
   
   private final boolean isSelection;
   
   /**
    * Creates a <code>TextSelectionEvent</code> with the boolean that
    * indicates if text is or isn't selected
    *
    * @param isSelection  the boolean
    */
   public TextSelectionEvent(boolean isSelection) {
      this.isSelection = isSelection;
   }
   
   /**
    * Returns if text is selected
    *
    * @return  true if text is selected, false otherwise
    */
   public boolean isSelection() {
      return isSelection;
   }
}
