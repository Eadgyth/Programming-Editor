package eg.document;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * The undo and redo editing.
 * <p>
 * An undoable change to the text (edit) is characterized by its text
 * content, type (insertion or removal) and position.
 * <p>
 * The sequence of edits is divided into larger undoable units by means
 * of breakpoints. A breakpoint may be added when a new edit is added
 * and its value then is the index of the edit that was added previously.
 * An undo action undoes the edits behind a breakpoint (or all if there
 * is none) in reverse order. Accordingly, a redo action redoes the edits
 * in front of a breakpoint (or all if there is none) in forward direction.
 * The starting point of an action is always the edit were undoing or
 * redoing has stopped before.
 * <p>
 * When a new edit is added also a breakpoint is added if
 * <ul>
 * <li> The content of the previous edit is just a newline character.
 * <li> The type of the new edit differs from the type of the
 *      previous edit. However, no breakpoint is added if an insertion
 *      follows a removal that was not triggerd by pressing the delete
 *      or backspace keys, since then a replace action is assumed.
 * <li> The content is longer than one character.
 * <li> A mark was set by {@link #markBreakpoint()}.
 * </ul>
 * <p>
 * An undoable unit may as well be formed by disabling breakpoint adding.
 * For this {@link #disableBreakpointAdding(boolean)} is invoked before
 * and after adding the edits to be included in the undoable unit.
 * <p>
 * Any undone edits are always removed when a new edit is added.
 * <p>
 * Created in {@link TypingEdit}.
 */
public class UndoEdit {

   private final StyledText txt;

   private final List<String> edits = new ArrayList<>(1000);
   private final List<Integer> positions = new ArrayList<>(1000);
   private final List<Boolean> types = new ArrayList<>(1000);
   private final List<Integer> breakpoints = new ArrayList<>(500);

   private int iEd = -1;
   private int iBr = -1;
   private boolean isMark = false;
   private boolean isMerge = false;
   private boolean isDeleteTyped = false;

   /**
    * @param txt  the reference to {@link StyledText}
    */
   public UndoEdit(StyledText txt) {
      this.txt = txt;
      txt.textArea().addKeyListener(keyListener);
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
      types.add(isInsert);
      iEd = edits.size() - 1;
      if (isMark) {
         addBreakpoint();
         isMark = false;
      }
      if ("\n".equals(change)) {
         isMark = true;
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
    * Returns if edits that can be undone are present
    *
    * @return  true if edits can be undone
    */
   public boolean canUndo() {
      return iEd > -1;
   }

   /**
    * Returns if edits that can be redone are present
    *
    * @return  true if edits can be redone
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
            txt.remove(nextPos, edit(iEd).length());
         }
         else {
            nextPos = pos(iEd) + edit(iEd).length();
            txt.insert(pos(iEd), edit(iEd));
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
      txt.textArea().setCaretPosition(nextPos);
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
            txt.insert(pos(iNext), edit(iNext));
         }
         else {
            nextPos = pos(iNext);
            txt.remove(nextPos, edit(iNext).length());
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
      txt.textArea().setCaretPosition(nextPos);
   }

   /**
    * Marks that a breakpoint will be added as soon as another edit is
    * added
    */
   public void markBreakpoint() {
      if (edits.size() > 0) {
         isMark = true;
      }
   }
   
   /**
    * Disables or re-enables adding breakpoints. Edits added
    * after disabling until re-enabling are "framed" by breakpoints
    *
    * @param b  true to disable, false to re-enable
    */
   public void disableBreakpointAdding(boolean b) {
      if (b) {
         addBreakpoint(iEd);
      }
      else {
         markBreakpoint();
      }
      isMerge = b;
   }

   //
   //--private--/
   //

   private void addBreakpoint() {
      if (!isMerge) {
         addBreakpoint(iEd - 1);
      }
   }
   
   private void addBreakpoint(int index) {
      int iLastBreak = breakpoints.size() - 1;
      if (iLastBreak == -1 || index != breakPt(iLastBreak)) {
         breakpoints.add(index);
         iBr = breakpoints.size() - 1;
      }
   }

   private void trim() {
      if (iEd == edits.size() - 1) {
         return; // no edits are undone or all undone edits are redone
      }
      for (int i = edits.size() - 1; i > iEd; i--) {
         edits.remove(i);
         positions.remove(i);
         types.remove(i);
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
      return types.get(i);
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
