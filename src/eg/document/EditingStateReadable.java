package eg.document;

/**
 * The interface to read parameters related to the state of the document
 * while it is edited
 */
public interface EditingStateReadable {

   /**
    * Updates the state which indicates that the document is being changed
    *
    * @param b  true if in change, false otherwise
    */
   public void updateInChangeState(boolean b);
   
   /**
    * Updates the state which indicates if edits can be redone and/or
    * undone
    *
    * @param canUndo  true if edits can be undone, false otherwise
    * @param canRedo  true if edits can be redone, false otherwise
    */
   public void updateUndoableState(boolean canUndo, boolean canRedo);
   
   /**
    * Updates the state which indicates if text is selected
    *
    * @param b  true if text is selected, false otherwise 
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
