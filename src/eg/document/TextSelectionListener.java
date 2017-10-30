package eg.document;

/**
 * The interface for detecting that the state, in which text is selected,
 * has changed
 */
@FunctionalInterface
public interface TextSelectionListener {
   
   /**
    * Called when the state, in which text is selected, has changed
    *
    * @param e  a {@link TextSelectionEvent} object
    */
   public void selectionUpdate(TextSelectionEvent e);
   
}
