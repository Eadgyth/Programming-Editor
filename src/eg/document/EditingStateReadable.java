package eg.document;

/**
 * The interface to read parameters related to the state of the document
 * while it is edited
 */
public interface EditingStateReadable {

   /**
    * Updates the boolean that, if true, indicates that the document
    * is in change. False indicates that the "in change state" is
    * reupdate which should be the case when the document text is saved.
    *
    * @param b  the boolean value
    */
   public void updateInChangeState(boolean b);
   
   /**
    * Updates the booleans that indicate if edits can be redone and/or
    * undone
    *
    * @param canUndo  the boolean value for undoable edits
    * @param canRedo  the boolean value for redobale edits
    */
   public void updateUndoableState(boolean canUndo, boolean canRedo);
   
   /**
    * Updates the boolean that indicates if text is currenty selected
    *
    * @param b  the boolean value 
    */
   public void updateSelectionState(boolean b);
   
   /**
    * Updates the number of the line and the column where the cursor is
    * located
    *
    * @param line  the line number
    * @param col  the column number
    */
   public void updateCursorState(int line, int col);
}
