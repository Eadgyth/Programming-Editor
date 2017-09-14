package eg.document;

/**
 * Created to give notice of a change of the state, in which edits can be undone
 * or redone
 */
public class UndoableChangeEvent {

   private boolean canUndo;
   private boolean canRedo;

   /**
    * @param canUndo  if edits can be undone
    * @param canRedo  if edits can be redone
    */
   public UndoableChangeEvent(boolean canUndo, boolean canRedo) {
      this.canUndo = canUndo;
      this.canRedo = canRedo;
   }

   /**
    * @return  if edits can be undone
    */
   public boolean canUndo() {
      return canUndo;
   }

   /**
    * @return  if edits can be redone
    */
   public boolean canRedo() {
      return canRedo;
   }
}
