package eg.document;

/** 
 * Created to give notice that the state, in which edits can or
 * cannot be undone and/or redone, has changed
 */
public class UndoableChangeEvent {

   private final boolean canUndo;
   private final boolean canRedo;

   /**
    * Creates an <code>UndoableChangeEvent</code> with the booleans
    * that indicate if edits can or cannot be undone and redone
    *
    * @param canUndo  the boolean
    * @param canRedo  the boolean
    */
   public UndoableChangeEvent(boolean canUndo, boolean canRedo) {
      this.canUndo = canUndo;
      this.canRedo = canRedo;
   }

   /**
    * Returns if edits can be undone
    *
    * @return  true if undo is possible, false otherwise
    */
   public boolean canUndo() {
      return canUndo;
   }

   /**
    * Returns if edits can be redone
    *
    * @return  true if redo is possible, false otherwise
    */
   public boolean canRedo() {
      return canRedo;
   }
}
