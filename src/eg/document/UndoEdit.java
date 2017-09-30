package eg.document;

import javax.swing.event.DocumentEvent.EventType;

import java.util.ArrayList;
import java.util.List;

//--Eadgyth--//
import eg.ui.EditArea;

class UndoEdit {
   
   private final EditArea editArea;

   private final List<String> edits = new ArrayList<>(500);
   private final List<Integer> positions = new ArrayList<>(500);
   private final List<Boolean> eventTypes = new ArrayList<>(500);
   private final List<Integer> breakpoints = new ArrayList<>(100);

   private int iEd = -1;
   private int iBr = -1;
   private boolean isBreak = false;

   UndoEdit(EditArea editArea) {
      this.editArea = editArea;
   }

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

   boolean canUndo() {
      return edits.size() > 0 && iEd > -1;
   }

   boolean canRedo() {
      return edits.size() > 0 && iEd < edits.size() - 1;
   }

  void undo() {
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

   void redo() {
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

   void markBreak() {
      if (edits.size() > 0) {
         isBreak = true;
      }
   }

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
