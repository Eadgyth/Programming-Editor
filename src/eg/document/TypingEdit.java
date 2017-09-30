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
   private final UndoEdit undo;

   private UndoableChangeEvent cue;
   private UndoableChangeListener ul;
   private TextSelectionEvent se;
   private TextSelectionListener sl;
   private boolean isDocListen = true;
   private boolean isAddToUndo = true;
   private boolean isTypeEdit = false;
   private String text = "";
   private int pos = 0;
   private String change = "";
   private DocumentEvent.EventType event;
   private boolean isSelectionTmp = false;
   private boolean canUndoTmp = false;
   private boolean canRedoTmp = false;

   TypingEdit(EditArea editArea) {
      this.editArea = editArea;
      col = new Coloring(editArea.getDoc(), editArea.getAttrSet());
      langSet = new LanguageSetter(col);
      lineNum = new LineNumbers(editArea);
      autoInd = new AutoIndent(editArea);
      undo = new UndoEdit(editArea);

      editArea.getDoc().addDocumentListener(docListen);

      editArea.textArea().addCaretListener(new CaretListener() {
         @Override
         public void caretUpdate(CaretEvent ce) {
            int caret = ce.getDot();
            notifyTextSelectionEvent(caret != ce.getMark());
            stopUndo(caret);
         }
      });
   }

   void setUndoableChangeListener(UndoableChangeListener ul) {
      if (ul != null) {
         this.ul = ul;
      }
   }

   void setTextSelectionListener(TextSelectionListener sl) {
      if (sl != null) {
         this.sl = sl;
      }
   }

   void enableDocListen(boolean isEnabled) {
      isDocListen = isEnabled;
      if (isEnabled) {
         text = editArea.getDocText();
         lineNum.addAllLineNumbers(text);
         editArea.textArea().setCaretPosition(0);
      }
      else {
         undo.discardEdits();
         notifyUndoableChangeEvent();
      }
   }

   String getText() {
      return text;
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
      undo.undo();
      updateAfterUndoRedo();
   }

   void redo() {
      isAddToUndo = false;
      undo.redo();
      updateAfterUndoRedo();
   }

   //
   //--private methods/classes--//
   //

   private void updateAfterUndoRedo() {
      notifyUndoableChangeEvent();
      if (isTypeEdit) {
         if (event.equals(DocumentEvent.EventType.INSERT)) {
            colorMultipleLines(change, pos);
         }
         else if (event.equals(DocumentEvent.EventType.REMOVE)) {
            colorLine();
         }
      }
      isAddToUndo = true;
   }

   private void colorLine() {
      EventQueue.invokeLater(() -> col.colorLine(text, pos));
   }

   private void notifyUndoableChangeEvent() {
      if (ul == null) {
         return;
      }
      boolean isUndoableChange = canUndoTmp != undo.canUndo();
      boolean isRedoableChange = canRedoTmp != undo.canRedo();
      if (isUndoableChange) {
         canUndoTmp = undo.canUndo();
      }
      if (isRedoableChange) {
         canRedoTmp = undo.canRedo();
      }
      if (isUndoableChange | isRedoableChange) {
         cue = new UndoableChangeEvent(canUndoTmp, canRedoTmp);
         ul.undoableStateChanged(cue);
      }
   }

   private void notifyTextSelectionEvent(boolean isSelection) {
      if (sl == null) {
         return;
      }
      if (isSelection != isSelectionTmp) {
         isSelectionTmp = isSelection;
         se = new TextSelectionEvent(isSelection);
         sl.selectionUpdate(se);
      }
   }

   private void stopUndo(int caret) {
      if (!isAddToUndo || event == null) {
         return;
      }
      if (caret > 0) {
         boolean isStop = false;
         if (event.equals(DocumentEvent.EventType.INSERT)) {
            isStop = caret - pos != 1;
         }
         else if (event.equals(DocumentEvent.EventType.REMOVE)) {
            isStop = caret - pos != 0;
         }
         if (isStop) {
            undo.markBreak();
         }
      }
   }

   private DocumentListener docListen = new DocumentListener() {

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
            undo.addEdit(change, pos, true);
            notifyUndoableChangeEvent();
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
            undo.addEdit(change, pos, false);
            notifyUndoableChangeEvent();
            if (isTypeEdit) {
               colorLine();
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         //nothing
      }

      private void textUpdate() {
         text = editArea.getDocText();
         lineNum.updateLineNumber(text);
      }
   };
}
