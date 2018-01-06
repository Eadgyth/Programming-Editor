package eg.document;

/**
 * The interface to read if edits can or cannot be undone and/or redone.
 */
@FunctionalInterface
public interface UndoableStateReadable {
   
   /**
    * Sets the booleans that, if true, indicate that edits can be redone
    * undone
    *
    * @param canUndo  the boolean value for undoable edits
    * @param canRedo  the boolean value for redobale edits
    */
   public void setUndoableState(boolean canUndo, boolean canRedo);
}
