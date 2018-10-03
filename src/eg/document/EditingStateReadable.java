package eg.document;

/**
 * The interface to read parameters related to the state of the document
 * while it is edited
 */
public interface EditingStateReadable {

   /**
    * Sets the boolean that, if true, indicates that the document
    * is in change. False indicates that the "in change state" is
    * reset which should be the case when the document text is saved.
    *
    * @param b  the boolean value
    */
   public void setInChangeState(boolean b);
   
   /**
    * Sets the booleans that indicate if edits can be redone and/or
    * undone
    *
    * @param canUndo  the boolean value for undoable edits
    * @param canRedo  the boolean value for redobale edits
    */
   public void setUndoableState(boolean canUndo, boolean canRedo);
   
   /**
    * Sets the boolean that indicates if text is currenty selected
    *
    * @param b  the boolean value 
    */
   public void setSelectionState(boolean b);
   
   /**
    * Sets the number of the line and the column where the cursor is
    * located
    *
    * @param line  the line number
    * @param col  the column number
    */
   public void setCursorPosition(int line, int col);
}
