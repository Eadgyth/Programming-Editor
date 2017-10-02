package eg.document;

import javax.swing.event.DocumentEvent.EventType;

import java.util.ArrayList;
import java.util.List;

//--Eadgyth--//
import eg.ui.EditArea;

/**
 * An undo/redo editing for the document in <code>EditArea</code>.<br>
 *
 * <p>Undoing/redoing stops and breakpoints. These are included when the
 * text change is newline, when the direction, i.e. insertion and removal,
 * changes and when the change is longer than one character. Additional
 * causes for breakpoints are added through {link #markBreak()} (used
 * to mark breaks when the cursor was moved with the mouse or cursor keys).
 * Already undone (and not redone) edits are removed when a new edit is added.
 * <p> Is created in {@link TypingEdit}
 */
public class UndoEdit {
   
   private final EditArea editArea;

   private final List<String> edits = new ArrayList<>(500);
   private final List<Integer> positions = new ArrayList<>(500);
   private final List<Boolean> eventTypes = new ArrayList<>(500);
   private final List<Integer> breakpoints = new ArrayList<>(100);

   private int iEd = -1;
   private int iBr = -1;
   private boolean isBreak = false;

   /**
    * @param editArea  the reference to {@link EditArea}
    */
   public UndoEdit(EditArea editArea) {
      this.editArea = editArea;
   }

   /**
    * Adds an edit
    *
    * @param change  the text change
    * @param pos  the position where the change happened
    * @param isInsert  true if the change is an insertion, false if it
    * was a removal
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
               addBreakpoint();
            }
            else if (change.length() > 1) {
               addBreakpoint();
            }
         }
      }
      iBr = breakpoints.size() - 1;
   }

   /**
    * Returns if edits can be undone
    *
    * @return if edits can be undone
    */
   public boolean canUndo() {
      return edits.size() > 0 && iEd > -1;
   }

   /**
    * Returns if edits can be redone
    *
    * @return if edits can be redone
    */
   public boolean canRedo() {
      return edits.size() > 0 && iEd < edits.size() - 1;
   }

   /**
    * Undoes edits up to the next breakpoint that is located before the
    * undoable edits
    */
   public void undo() {
      int nextPos = 0;
      while (iEd > -1) {
         if (isInsert(iEd)) {
            nextPos = pos(iEd);
            editArea.removeStr(nextPos, edit(iEd).length());
         }
         else {
            nextPos = pos(iEd) + edit(iEd).length();
            editArea.insertStr(pos(iEd), edit(iEd));
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
      editArea.textArea().setCaretPosition(nextPos);
   }

   /**
    * Redoes edits up to the next breakpoint that is located behind the
    * redoable edits
    */
   public void redo() {
      int nextPos = 0;
      while (iEd < edits.size() - 1) {
         int iNext = iEd + 1;
         if (isInsert(iNext)) {
            nextPos = pos(iNext) + edit(iNext).length();
            editArea.insertStr(pos(iNext), edit(iNext));
         }
         else {
            nextPos = pos(iNext);
            editArea.removeStr(nextPos, edit(iNext).length());
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
      editArea.textArea().setCaretPosition(nextPos);
   }

   /**
    * Marks that the edit before the next edit that will be added is
    * a breakpoint
    */
   public void markBreak() {
      if (edits.size() > 0) {
         isBreak = true;
      }
   }

   /**
    * Discards all edits
    */
   void discardEdits() {
      edits.clear();
      positions.clear();
      eventTypes.clear();
      breakpoints.clear();
      iEd = -1;
      iBr = -1;
      isBreak = false;
   }

   //
   //--private methods--//
   //

   private void addBreakpoint() {
      int iLastBreak = breakpoints.size() - 1;
      if (iLastBreak > -1
            && iEd - 1 == breakPt(iLastBreak)) {
         return;
      }
      breakpoints.add(iEd - 1);
      iBr = breakpoints.size() - 1;    
   }

   private void trim() {
      if (iEd < edits.size() - 1) {
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
}
