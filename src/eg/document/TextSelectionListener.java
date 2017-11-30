package eg.document;

/**
 * The interface for detecting that the state, in which text is selected,
 * has changed
 */
@FunctionalInterface
public interface TextSelectionListener {
   
   /**
    * Called when text is selected or unselected
    *
    * @param e  a new {@link TextSelectionEvent} object
    */
   public void selectionUpdate(TextSelectionEvent e);
   
}
