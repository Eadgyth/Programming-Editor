package eg.document;

/**
 * The interface for detecting that the state, in which edits can be
 * undone and redone, has changed.
 */
public interface UndoableChangeListener {
   
   /**
    * Called when the state, in which edits can be undone
    * or redone, has changed
    *
    * @param e  an {@link UndoableChangeEvent} object
    */
   public void undoableStateChanged(UndoableChangeEvent e);
}
