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
      List<Boolean> eventTypes = new ArrayList<>();
      List<Integer> breakpoints = new ArrayList<>();
      boolean isBreak = false;
      int iEd = -1;
      int iBr = -1;

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
            int nextEd = iEd + 1;
            if (isInsert(nextEd)) {
               nextPos = pos(nextEd) + edit(nextEd).length();
               editArea.insertStr(pos(nextEd), edit(nextEd));
            }
            else {
               nextPos = pos(nextEd);
               editArea.removeStr(nextPos, edit(nextEd).length());
            }
            iEd++;
            if (iBr + 2 < breakpoints.size()) {
               if (nextEd == breakPt(iBr + 2)) {
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
