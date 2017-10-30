package eg.document;

/**
 * The interface for detecting that the state, in which edits can or
 * cannot be undone and/or redone, has changed
 */
@FunctionalInterface
public interface UndoableChangeListener {
   
   /**
    * Called when the state, in which edits can or cannot be undone
    * or redone, has changed
    *
    * @param e  an {@link UndoableChangeEvent} object
    */
   public void undoableStateChanged(UndoableChangeEvent e);
}
