package eg.document;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent.EventType;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.AttributeSet;

import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.List;

//--Eadgyth--//
import eg.Languages;
import eg.syntax.Coloring;
import eg.syntax.LanguageSetter;
import eg.ui.EditArea;
import eg.utils.FileUtils;

/**
 * Mediates the editing in the {@code EditArea} that shall happen during
 * typing.
 * <p>
 * Uses methods from classes that show line numbering, do syntax
 * coloring, auto-indentation and undo/redo editing (the latter in this inner
 * class).
 */
class TypingEdit {

   private final EditArea editArea;
   private final Coloring col;
   private final LanguageSetter langSet;
   private final AutoIndent autoInd;
   private final LineNumbers lineNum;
   private final UndoRedo undo = new UndoRedo();

   private UndoableChangeEvent cue;
   private UndoableChangeListener ul;
   private SelectionEvent se;
   private SelectionListener sl;
   private boolean isDocListen = true;
   private boolean isAddToUndo = true;
   private boolean isTypeEdit = false;
   private String text = "";
   private int pos = 0;
   private String change = "";
   private DocumentEvent.EventType event;
   private boolean isSelection;

   TypingEdit(EditArea editArea) {
      this.editArea = editArea;
      col = new Coloring(editArea.getDoc(), editArea.getAttrSet());
      langSet = new LanguageSetter(col);
      lineNum = new LineNumbers(editArea);
      autoInd = new AutoIndent(editArea);

      editArea.getDoc().addDocumentListener(docListen);

      editArea.textArea().addCaretListener(new CaretListener() {
         @Override
         public void caretUpdate(CaretEvent ce) {
            notifySelectionEvent(ce.getDot() != ce.getMark());
            if (text.length() > 0) {
               if (isAddToUndo && event.equals(DocumentEvent.EventType.CHANGE)) {
                  undo.markBreak();
               }
            }
         }
      });
   }

   String getText() {
      return text;
   }

   void setUndoableChangeListener(UndoableChangeListener ul) {
      if (ul != null) {
         this.ul = ul;
      }
   }
   
   void setSelectionListener(SelectionListener sl) {
      if (sl != null) {
         this.sl = sl;
      }
   }

   void enableDocListen(boolean isEnabled) {
      isDocListen = isEnabled;
      if (isEnabled) {
         text = editArea.getDocText();
         lineNum.addAllLineNumbers(text);
      }
      else {
         undo.discardEdits();
      }
   }

   void enableTypeEdit(boolean isEnabled) {
      isTypeEdit = isEnabled;
   }

   void setUpEditing(Languages lang) {
      if (lang == Languages.PLAIN_TEXT) {
         col.setAllCharAttrBlack();
         enableTypeEdit(false);
         autoInd.enableIndent(false);
      }
      else {
         langSet.setColorable(lang);
         colorMultipleLines(null, 0);
         enableTypeEdit(true);
         autoInd.enableIndent(true);
      }
   }

   void setIndentUnit(String indentUnit) {
      autoInd.setIndentUnit(indentUnit);
   }

   String getIndentUnit() {
      return autoInd.getIndentUnit();
   }

   void colorMultipleLines(String section, int pos) {
      col.colorMultipleLines(text, section, pos);
   }

   boolean canUndo() {
      return undo.canUndo();
   }

   boolean canRedo() {
      return undo.canRedo();
   }

   void undo() {
      isAddToUndo = false;
      if (canUndo()) {
         undo.undo();
         updateAfterUndoRedo();
      }
      isAddToUndo = true;
   }

   void redo() {
      isAddToUndo = false;
      if (canRedo()) {
         undo.redo();
         updateAfterUndoRedo();
      }
      isAddToUndo = true;
   }

   //
   //--private methods/classes--//
   //

   private void updateAfterUndoRedo() {
      undo.notifyUndoableChangeEvent();
      if (isTypeEdit) {
         if (event.equals(DocumentEvent.EventType.INSERT)) {
            colorMultipleLines(change, pos);
         }
         else if (event.equals(DocumentEvent.EventType.REMOVE)) {
            colorLine();
         }
      }
   }
   
   private void colorLine() {
      EventQueue.invokeLater(() -> col.colorLine(text, pos));
   }
   
   private void notifySelectionEvent(boolean isSelectionUpdate) {
      if (sl == null) {
         return;
      }
      if (isSelectionUpdate != isSelection) {
         isSelection = isSelectionUpdate;
         se = new SelectionEvent(isSelection);
         sl.selectionUpdate(se);
      }
   }

   private final DocumentListener docListen = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (!isDocListen) {
            return;
         }
         event = de.getType();
         pos = de.getOffset();
         textUpdate();
         change = text.substring(pos, pos + de.getLength());
         if (isAddToUndo) {
            undo.addEdit();
            if (isTypeEdit) {
               autoInd.setText(text);
               colorLine();
               EventQueue.invokeLater(() -> {
                  autoInd.closeBracketIndent(pos);
               });
            }
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         if (!isDocListen) {
            return;
         }
         event = de.getType();
         pos = de.getOffset();
         change = text.substring(pos, pos + de.getLength());
         textUpdate();
         if (isAddToUndo) {
            undo.addEdit();
            if (isTypeEdit) {
               colorLine();
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         event = de.getType();
      }

      private void textUpdate() {
         text = editArea.getDocText();
         lineNum.updateLineNumber(text);
      }
   };

   private final class UndoRedo {

      private final List<String> edits = new ArrayList<>(500);
      private final List<Integer> positions = new ArrayList<>(500);
      private final List<Boolean> eventTypes = new ArrayList<>(500);
      private final List<Integer> breakpoints = new ArrayList<>();
      private boolean isBreak = false;
      private int iEd = -1;
      private int iBr = -1;
      private boolean canUndo;
      private boolean canRedo;

      void addEdit() {
         trim();
         edits.add(change);
         positions.add(pos);
         eventTypes.add(event.equals(DocumentEvent.EventType.INSERT));
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
               if (isInsert(iEd) != isInsert(iEd - 1)) {
                  addBreakpoint();
               }
            }
         }
         iBr = breakpoints.size() - 1;
         notifyUndoableChangeEvent();
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
         notifyUndoableChangeEvent();
      }
      
      void notifyUndoableChangeEvent() {
         if (ul == null) {
            return;
         }
         if (canUndo != canUndo()) {
            canUndo = canUndo();
            cue = new UndoableChangeEvent(canUndo, canRedo);
            ul.undoableStateChanged(cue);
         }
         if (canRedo != canRedo()) {
            canRedo = canRedo();
            cue = new UndoableChangeEvent(canUndo, canRedo);
            ul.undoableStateChanged(cue);
         }
      }
      
      //--private methods--//

      private void addBreakpoint() {
         breakpoints.add(iEd - 1);
         iBr = breakpoints.size() - 1;
      }

      private void trim() {
         if (iEd < edits.size() - 1) {
            for (int i = edits.size() - 1; i > iEd; i--) {
               edits.remove(i);
               positions.remove(i);
               eventTypes.remove(i);
            }
            for (int i = breakpoints.size() - 1; i > iBr + 1; i--) {
               breakpoints.remove(i);
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
}
