package eg.document;

/**
 * The interface for detecting that the state, in which text is selected,
 * has changed
 */
@FunctionalInterface
public interface SelectionListener {
   
   /**
    * Called when the state, in which text is selected, has changed
    *
    * @param e  a {@link SelectionEvent} object
    */
   public void selectionUpdate(SelectionEvent e);
   
}
