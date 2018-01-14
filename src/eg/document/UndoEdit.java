package eg.document;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * The undo and redo editing.
 * <p>
 * A change to the text is named edit. This can be an insertion or a
 * deletion and it can be a single character or a larger chunk of text.
 * Calling undo undoes edits in reverse order until the last breakpoint
 * is reached. More precisely, undoing edits stops before an edit whose
 * index is the value of the lastly added breakpoint. A subsequent
 * undo action then continues to undo edits up to the second last
 * breakpoint etc.. Redo actions redo undone edits and stop before the
 * same breakpoints just in reverse order.<br>
 * Edits are marked as breakpoints in the following cases:
 * <ul>
 * <li> The edit is is newline.
 * <li> The edit type, insertion and removal, changes. However, if an
 *      insertion follows a removal that was not triggerd by pressing
 *      the delete or backspace keys, that is selected text was replaced,
 *      the removal is not marked.
 * <li> The edit type has not changed but the edit is longer than one
 *      character.
 * <li> A mark was added from outside by calling {@link #markBreakpoint()}.
 * </ul>
 * <p>
 * Undone edits are always removed if a new edit is added.
 * <p>
 * Created in {@link TypingEdit} which adds edits and also marks
 * breakpoints when the cursor is moved with the mouse or cursor keys.
 */
public class UndoEdit {

   private final TextDocument textDoc;

   private final List<String> edits = new ArrayList<>(500);
   private final List<Integer> positions = new ArrayList<>(500);
   private final List<Boolean> eventTypes = new ArrayList<>(500);
   private final List<Integer> breakpoints = new ArrayList<>(100);

   private int iEd = -1;
   private int iBr = -1;
   private boolean isBreak = false;
   private boolean isDeleteTyped = false;

   /**
    * @param textDoc  the reference to {@link TextDocument}
    */
   public UndoEdit(TextDocument textDoc) {
      this.textDoc = textDoc;
      textDoc.textArea().addKeyListener(keyListener);
   }

   /**
    * Adds an edit
    *
    * @param change  the text content of the edit
    * @param pos  the position of the edit in the document
    * @param isInsert  specifies if the edit is an insertion or
    * a deletion
    */
   void addEdit(String change, int pos, boolean isInsert) {
      trim();
      edits.add(change);
      positions.add(pos);
      eventTypes.add(isInsert);
      iEd = edits.size() - 1;
      if (isBreak) {
         addBreakpoint();
         isBreak = false;
      }
      if ("\n".equals(change)) {
         isBreak = true;
      }
      else {
         if (iEd > 0) {
            if (isInsert != isInsert(iEd - 1)) {
               boolean isReplace = isInsert && !isDeleteTyped;
               if (!isReplace) {
                  addBreakpoint();
               }
            }
            else if (change.length() > 1) {
                addBreakpoint();
            }
         }
      }
      if (isInsert) {
         isDeleteTyped = false;
      }
      iBr = breakpoints.size() - 1;
   }

   /**
    * Returns the boolean that indicates if edits can be undone
    *
    * @return  the boolean value
    */
   public boolean canUndo() {
      return iEd > -1;
   }

   /**
    * Returns the boolean that indicates if edits can be redone
    *
    * @return  the boolean value
    */
   public boolean canRedo() {
      return iEd < edits.size() - 1;
   }

   /**
    * Undoes edits up to the next breakpoint that is located before the
    * edits that are not yet undone
    */
   public void undo() {
      int nextPos = 0;
      while (iEd > -1) {
         if (isInsert(iEd)) {
            nextPos = pos(iEd);
            textDoc.remove(nextPos, edit(iEd).length());
         }
         else {
            nextPos = pos(iEd) + edit(iEd).length();
            textDoc.insert(pos(iEd), edit(iEd));
         }
         iEd--;
         if (iBr > -1) {
            if (iEd == breakPt(iBr)) {
               iBr--;
               break;
            }
         }
      }
      if (iEd == -1) {
         iBr--;
      }
      textDoc.textArea().setCaretPosition(nextPos);
   }

   /**
    * Redoes edits up to the next breakpoint that is located behind the
    * edits that are undone and not yet redone
    */
   public void redo() {
      int nextPos = 0;
      while (iEd < edits.size() - 1) {
         int iNext = iEd + 1;
         if (isInsert(iNext)) {
            nextPos = pos(iNext) + edit(iNext).length();
            textDoc.insert(pos(iNext), edit(iNext));
         }
         else {
            nextPos = pos(iNext);
            textDoc.remove(nextPos, edit(iNext).length());
         }
         iEd++;
         int iBrAhead = iBr + 2;
         if (iBrAhead < breakpoints.size()) {
            if (iNext == breakPt(iBrAhead)) {
               iBr++;
               break;
            }
         }
      }
      if (iEd == edits.size() - 1) {
         iBr++;
      }
      textDoc.textArea().setCaretPosition(nextPos);
   }

   /**
    * Marks that the recently added edit will be a breakpoint as soon
    * as another edit is added
    */
   public void markBreakpoint() {
      if (edits.size() > 0) {
         isBreak = true;
      }
   }

   //
   //--private--//
   //

   private void addBreakpoint() {
      int iLastBreak = breakpoints.size() - 1;
      if (iLastBreak > -1 && iEd - 1 == breakPt(iLastBreak)) {
         return;
      }
      breakpoints.add(iEd - 1);
      iBr = breakpoints.size() - 1;
   }

   private void trim() {
      if (iEd == edits.size() - 1) {
         return; // no edits are undone (or all undone edits are redone)
      }
      for (int i = edits.size() - 1; i > iEd; i--) {
         edits.remove(i);
         positions.remove(i);
         eventTypes.remove(i);
         int iLastBreak = breakpoints.size() - 1;
         if (iLastBreak > -1 && i == breakPt(iLastBreak)) {
            breakpoints.remove(iLastBreak);
         }
      }
   }

   private int pos(int i) {
      return positions.get(i);
   }

   private String edit(int i) {
      return edits.get(i);
   }

   private boolean isInsert(int i) {
      return eventTypes.get(i);
   }

   private int breakPt(int i) {
      return breakpoints.get(i);
   }

   private final KeyListener keyListener = new KeyAdapter() {

      @Override
      public void keyPressed(KeyEvent e) {
         int key = e.getKeyCode();
         if (key == KeyEvent.VK_DELETE || key == KeyEvent.VK_BACK_SPACE) {
            isDeleteTyped = true;
         }
      }
   };
}
