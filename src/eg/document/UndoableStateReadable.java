package eg.document;

/**
 * The interface read the state, in which edits can or
 * cannot be undone and/or redone.
 */
@FunctionalInterface
public interface UndoableStateReadable {
   
   /**
    *
    * @param canUndo  the boolean
    * @param canRedo  the boolean
    */
   public void setUndoableState(boolean canUndo, boolean canRedo);
}
