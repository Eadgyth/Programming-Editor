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

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--//
import eg.Languages;
import eg.syntax.Lexer;
import eg.syntax.Coloring;
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
   private final Lexer lex;
   private final Coloring col;
   private final AutoIndent autoInd;
   private final LineNumbers lineNum;
   private final UndoRedo undo = new UndoRedo();

   private boolean isDocListen = true;
   private boolean isUndoable = true;
   private boolean isTypeEdit = false;
   private String text = "";
   private int pos = 0;
   private String change = "";
   private DocumentEvent.EventType event;

   TypingEdit(EditArea editArea) {
      this.editArea = editArea;
      lex = new Lexer(editArea.getDoc(), editArea.getAttrSet());
      col = new Coloring(lex);
      lineNum = new LineNumbers(editArea);
      autoInd = new AutoIndent(editArea);
      
      editArea.getDoc().addDocumentListener(docListen);
      editArea.textArea().addCaretListener(new CaretListener() {
         @Override
         public void caretUpdate(CaretEvent ce) {
            if (text.length() > 0) {
               if (event.equals(DocumentEvent.EventType.CHANGE)) {
                  undo.markBreak();
               }
            }
         }
      });
   }
   
   String getText() {
      return text;
   }

   void enableDocListen(boolean isEnabled) {
      isDocListen = isEnabled;
      if (isEnabled) {
         text = editArea.getDocText();
         lineNum.addAllLineNumbers(text);
      }
   }

   void enableTypeEdit(boolean isEnabled) {
      isTypeEdit = isEnabled;
   }

   void setUpEditing(Languages lang) {
      if (lang == Languages.PLAIN_TEXT) {
         lex.setAllCharAttrBlack();
         enableTypeEdit(false);
         autoInd.enableIndent(false);
      }
      else {
         col.selectColorable(lang);
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
      lex.enableTypeMode(section != null);
      col.colorMultipleLines(text, section, pos);
      lex.enableTypeMode(true);
   }

   void undo() {
      isUndoable = false;
      if (undo.canUndo()) {
         undo.undo();
         updateAfterUndoRedo();
      }
      isUndoable = true;
   }

   void redo() {
      isUndoable = false;
      if (undo.canRedo()) {
         undo.redo();
         updateAfterUndoRedo();
      }
      isUndoable = true;
   }

   //
   //--private methods/classes--//
   //

   private void updateAfterUndoRedo() {
      if (isTypeEdit) {
         colorMultipleLines(change, pos);
      }
   }

   private final DocumentListener docListen = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (!isDocListen) {
            return;
         }
         pos = de.getOffset();
         assignChange(de);
         change = text.substring(pos, pos + de.getLength());
         if (isUndoable) {
            undo.addEdit();
            if (isTypeEdit) {
               autoInd.setText(text);
               color();
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
         pos = de.getOffset();
         change = text.substring(pos, pos + de.getLength());
         assignChange(de);
         if (isUndoable) {
            undo.addEdit();
            if (isTypeEdit) {
               color();
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         event = de.getType();
      }
      
      private void assignChange(DocumentEvent de) {
         event = de.getType();
         text = editArea.getDocText();
         lineNum.updateLineNumber(text);
      }
      
      private void color() {
         EventQueue.invokeLater(() -> col.colorLine(text, pos));
      }
   };

   private final class UndoRedo {

      List<String> edits = new ArrayList<>();
      List<Integer> positions = new ArrayList<>();
      List<Boolean> isInsert = new ArrayList<>();
      List<Integer> breaks = new ArrayList<>();
      boolean isBreak = false;
      int iEdits = -1;
      int iBreaks = -1;

      void addEdit() {
         trim();
         edits.add(change);
         positions.add(pos);
         isInsert.add(event.equals(DocumentEvent.EventType.INSERT));
         iEdits = edits.size() - 1;
         if (isBreak) {
            addBreakpoint();
         }
         if ("\n".equals(change)) {
            isBreak = true;
         }
         iBreaks = breaks.size() - 1;
      }

      boolean canUndo() {
         return edits.size() > 0 && iEdits > -1;
      }

      boolean canRedo() {
         return edits.size() > 0 && iEdits < edits.size() - 1;
      }

      void undo() {
         int nextPos = 0;
         while (iEdits > -1) {
            if (isInsert.get(iEdits)) {
               nextPos = positions.get(iEdits);
               editArea.removeStr(nextPos, edits.get(iEdits).length());
            }
            else {
               nextPos = positions.get(iEdits) + edits.get(iEdits).length();
               editArea.insertStr(positions.get(iEdits), edits.get(iEdits));
            }
            iEdits--;
            if (iBreaks > -1) {
               if (iEdits == breaks.get(iBreaks)) {
                  iBreaks--;
                  break;
               }
            }
         }
         if (iEdits == -1) {
            iBreaks--;
         }
         editArea.textArea().setCaretPosition(nextPos);
      }

      void redo() {
         int nextPos = 0;
         while (iEdits < edits.size() - 1) {
            int next = iEdits + 1;
            if (isInsert.get(next)) {
               nextPos = positions.get(next) + edits.get(next).length();
               editArea.insertStr(positions.get(next), edits.get(next));
            }
            else {
               nextPos = positions.get(next);
               editArea.removeStr(nextPos, edits.get(next).length());
            }
            iEdits++;
            if (iBreaks + 2 < breaks.size()) {
               if (next == breaks.get(iBreaks + 2)) {
                  iBreaks++;
                  break;
               }
            }
         }
         if (iEdits == edits.size() - 1) {
            iBreaks++;
         }
         editArea.textArea().setCaretPosition(nextPos);
      }
      
      void markBreak() {
         isBreak = true;
      }

      private void addBreakpoint() {
         breaks.add(iEdits - 1);
         iBreaks = breaks.size() - 1;
         isBreak = false;
      }

      private void trim() {
         if (iEdits < edits.size() - 1) {
            for (int i = edits.size() - 1; i > iEdits; i--) {
               edits.remove(i);
               positions.remove(i);
               isInsert.remove(i);
            }
            for (int i = breaks.size() - 1; i > iBreaks + 1; i--) {
               breaks.remove(i);
            }
         }
      }
   }
}
