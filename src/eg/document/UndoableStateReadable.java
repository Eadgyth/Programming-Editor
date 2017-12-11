package eg.document;

/**
 * The interface read if edits can or cannot be undone and/or redone.
 */
@FunctionalInterface
public interface UndoableStateReadable {
   
   /**
    * Sets the boolean values that indicate if edits can be redone
    * undone
    *
    * @param canUndo  the boolean for undoable edits
    * @param canRedo  the boolean for redobale edits
    */
   public void setUndoableState(boolean canUndo, boolean canRedo);
}
