package eg.document;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;

import java.util.ArrayList;
import java.util.List;

//--Eadgyth--/
import eg.document.styledtext.EditableText;

/**
 * The undo and redo editing.
 * <p>
 * An undoable edit is defined by the text content, the position
 * and the type (insertion or removal). The sequence of edits is
 * divided into undoable units by means of breakpoints. An undo
 * action undoes the edits behind a breakpoint (or all if there
 * is none) in reverse order. Accordingly, a redo action redoes
 * the edits in front of a breakpoint (or all if there is none)
 * in forward direction. The starting point of an action is
 * always the edit were undoing or redoing has stopped before.
 * <p>
 * When a new edit is added a breakpoint is set if:
 * <ul>
 * <li> The content of the previous edit is just a newline
 *      character.
 * <li> The type of the new edit differs from the type of the
 *      previous edit, if a selection is replaced the breakpoint
 *      is the index of the next edit.
 * <li> The edit is a removal and the content length is longer
 *      than one character.
 * <li> A mark was set by {@link #markBreakpoint()} during adding
 *      the previous edit.
 * </ul>
 * <p>
 * Adding breakpoints may be disabled to form a larger undoable
 * unit (see {@link #disableBreakpointAdding(boolean)}).
 * <p>
 * Any undone edits are removed when a new edit is added.
 */
public class UndoEditing {

   private final EditableText txt;

   private final List<String> contents = new ArrayList<>(1000);
   private final List<Integer> positions = new ArrayList<>(1000);
   private final List<Boolean> types = new ArrayList<>(1000);
   private final List<Integer> breakpoints = new ArrayList<>(500);

   private int iEd = -1;
   private int iBr = -1;
   private boolean isMark = false;
   private boolean isMerging = false;
   private boolean isDeleteTyped = false;

   /**
    * @param txt  the {@link EditableText}
    */
   public UndoEditing(EditableText txt) {
      this.txt = txt;
      txt.textArea().addKeyListener(keyListener);
   }

   /**
    * Adds an edit
    *
    * @param content  the text content of the edit
    * @param pos  the position of the edit
    * @param isInsert  true for an insert, false for a removal
    */
   public void addEdit(String content, int pos, boolean isInsert) {
      trim();
      contents.add(content);
      positions.add(pos);
      types.add(isInsert);
      iEd = contents.size() - 1;
      if (isMark) {
         addBreakpoint();
         isMark = false;
      }
      if ("\n".equals(content)) {
         isMark = true;
      }
      else if (iEd > 0) {
         if (isInsert != isInsert(iEd - 1)) {
            boolean isReplace = isInsert && !isDeleteTyped;
            if (!isReplace) {
               addBreakpoint();
            }
            else {
               isMark = true;
            }
         }
         else if (!isInsert && content.length() > 1) {
            addBreakpoint();
         }
      }
      if (isInsert) {
         isDeleteTyped = false;
      }
      iBr = breakpoints.size() - 1;
   }

   /**
    * Returns if contents that can be undone are present
    *
    * @return  true if contents can be undone
    */
   public boolean canUndo() {
      return iEd > -1;
   }

   /**
    * Returns if contents that can be redone are present
    *
    * @return  true if contents can be redone
    */
   public boolean canRedo() {
      return iEd < contents.size() - 1;
   }

   /**
    * Undoes contents up to the next breakpoint that is located
    * before the contents that are not yet undone
    */
   public void undo() {
      int nextPos = 0;
      while (iEd > -1) {
         if (isInsert(iEd)) {
            nextPos = pos(iEd);
            txt.remove(nextPos, content(iEd).length());
         }
         else {
            nextPos = pos(iEd) + content(iEd).length();
            txt.insert(pos(iEd), content(iEd));
         }
         iEd--;
         if (iBr > -1 && iEd == breakPt(iBr)) {
            iBr--;
            break;
         }
      }
      if (iEd == -1) {
         iBr--;
      }
      setCaretPosition(nextPos);
   }

   /**
    * Redoes contents up to the next breakpoint that is located
    * behind the contents that are undone and not yet redone
    */
   public void redo() {
      int nextPos = 0;
      while (iEd < contents.size() - 1) {
         int iNext = iEd + 1;
         if (isInsert(iNext)) {
            nextPos = pos(iNext) + content(iNext).length();
            txt.insert(pos(iNext), content(iNext));
         }
         else {
            nextPos = pos(iNext);
            txt.remove(nextPos, content(iNext).length());
         }
         iEd++;
         int iBrAhead = iBr + 2;
         if (iBrAhead < breakpoints.size() && iNext == breakPt(iBrAhead)) {
            iBr++;
            break;
         }
      }
      if (iEd == contents.size() - 1) {
         iBr++;
      }
      setCaretPosition(nextPos);
   }

   /**
    * Marks that a breakpoint will be added as soon as another edit is
    * added
    */
   public void markBreakpoint() {
      if (!contents.isEmpty()) {
         isMark = true;
      }
   }

   /**
    * Disables or re-enables the usual adding of breakpoints.
    * Edits added after disabling until re-enabling are "framed"
    * by breakpoints
    *
    * @param b  true to disable, false to re-enable
    */
   public void disableBreakpointAdding(boolean b) {
      if (b && !txt.text().isEmpty()) {
         addBreakpoint(iEd);
      }
      else {
         markBreakpoint();
      }
      isMerging = b;
   }

   //
   //--private--/
   //

   private void addBreakpoint() {
      if (!isMerging) {
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
      if (iEd == contents.size() - 1) {
         return; // no contents are undone or all undone contents are redone
      }
      for (int i = contents.size() - 1; i > iEd; i--) {
         contents.remove(i);
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

   private String content(int i) {
      return contents.get(i);
   }

   private boolean isInsert(int i) {
      return types.get(i);
   }

   private int breakPt(int i) {
      return breakpoints.get(i);
   }

   private void setCaretPosition(int pos) {
      txt.textArea().setCaretPosition(pos);
      if (!txt.textArea().hasFocus()) {
         txt.textArea().requestFocusInWindow();
      }
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
