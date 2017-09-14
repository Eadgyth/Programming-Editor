package eg.document;

/**
 * Created to give notice of a change of the state, in which text is selected
 */
public class SelectionEvent {
   
   private boolean isSelection;
   
   /**
    * @param isSelection  if text is selected
    */
   public SelectionEvent(boolean isSelection) {
      this.isSelection = isSelection;
   }
   
   /**
    * Returns if text is selected
    *
    * @return  if text is selected
    */
   public boolean isSelection() {
      return isSelection;
   }
}
