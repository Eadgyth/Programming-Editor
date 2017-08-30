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
 * Uses methods from other classes that show line numbering, do syntax
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

   private boolean isUndoable = true;
   private boolean isTypeEdit = false;
   private String text = "";
   private int pos;
   private int changeLength = 0; // reset to 0 in caretUpdate()
   private DocumentEvent.EventType event;

   TypingEdit(EditArea editArea) {
      this.editArea = editArea;
      editArea.getDoc().addDocumentListener(docListen);
      editArea.textArea().addCaretListener(new UndoStopper());
      lex = new Lexer(editArea.getDoc(), editArea.getAttrSet());
      col = new Coloring(lex);
      lineNum = new LineNumbers(editArea);
      autoInd = new AutoIndent(editArea);
   }

   void enableUndoableEdit(boolean isEnabled) {
      isUndoable = isEnabled;
      text = editArea.getDocText();
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

   void addAllLineNumbers(String allText) {
      lineNum.addAllLineNumbers(allText);
   }

   void updateLineNumber(String allText) {
      lineNum.updateLineNumber(allText);
   }

   void colorMultipleLines(String section, int pos) {
      lex.enableTypeMode(section != null);
      col.colorMultipleLines(text, section, pos);
      lex.enableTypeMode(true);
   }

   void undo() {
      int prevLineNr = lineNum.getCurrLineNr();
      enableUndoableEdit(false);
      if (undo.canUndo()) {
         undo.undo();
         updateAfterUndoRedo(prevLineNr);
      }
      enableUndoableEdit(true);
   }

   void redo() {
      int prevLineNr = lineNum.getCurrLineNr();
      enableUndoableEdit(false);
      if (undo.canRedo()) {
         undo.redo();
         updateAfterUndoRedo(prevLineNr);
      }
      enableUndoableEdit(true);
   }

   //
   //--private methods/classes--//
   //

   private void updateAfterUndoRedo(int prevLineNr) {
      text = editArea.getDocText();
      updateLineNumber(text);
      if (isTypeEdit) {
         int lineNr = lineNum.getCurrLineNr();
         if (lineNr != prevLineNr) {
            colorMultipleLines(null, 0);
         }
         else {
            color();
         }
      }
   }

   void color() {
      EventQueue.invokeLater(() ->
         col.colorLine(text, pos)
      );
   }

   private final DocumentListener docListen = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         event = de.getType();
         pos = de.getOffset();
         changeLength = de.getLength();
         if (isUndoable) {
            text = editArea.getDocText();
            undo.addAnEdit(text.substring(pos, pos + de.getLength()));
            updateLineNumber(text);
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
         event = de.getType();
         pos = de.getOffset();
         changeLength = -de.getLength();
         if (isUndoable) {
            undo.addAnEdit(text.substring(pos, pos + de.getLength()));
            text = editArea.getDocText();
            updateLineNumber(text);
            if (isTypeEdit) {
               color();
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         event = de.getType();
      }
   };

   private class UndoStopper implements CaretListener {

      int caret;

      @Override
      public void caretUpdate(CaretEvent ce) {
         caret = editArea.textArea().getSelectionStart();
         if (caret > 0) {
            boolean isStop = caret - pos != changeLength;
            if (event.equals(DocumentEvent.EventType.INSERT)) {
               isStop = isStop && caret - pos != 1;
            }
            else if (event.equals(DocumentEvent.EventType.REMOVE)) {
               isStop = isStop && caret - pos != 0;
            }
            if (isStop) {
               undo.discardEdits();
            }
         }
         changeLength = 0;
      }
   }

   private final class UndoRedo {

      List<String> edits = new ArrayList<>();
      List<Integer> positions = new ArrayList<>();
      List<Boolean> types = new ArrayList<>();
      int index = -1;
      
      void addAnEdit(String changedText) {
         if (index == -1) {
            discardEdits();
         }
         edits.add(changedText);
         positions.add(pos);
         types.add(event.equals(DocumentEvent.EventType.INSERT));
         index = edits.size() - 1;
      }         

      boolean canUndo() {           
         return edits.size() > 0; 
      }

      boolean canRedo() {
         return edits.size() > 0 && index < edits.size() - 1;
      }

      void undo() {
         while (index > -1) {
            if (types.get(index)) {
               editArea.removeStr(positions.get(index),
                     edits.get(index).length());
            }
            else {
               editArea.insertStr(positions.get(index),
                     edits.get(index));
            }
            index--;
         }
      }

      void redo() {
         while (index < edits.size() - 1) {
            if (!types.get(index + 1)) {
               editArea.removeStr(positions.get(index + 1),
                     edits.get(index + 1).length());
            }
            else {
               editArea.insertStr(positions.get(index + 1),
                     edits.get(index + 1));
            }
            index++;
         }
         discardEdits();
      }

      void discardEdits() {
         edits.clear();
         positions.clear();
         types.clear();
         index = -1;
      }
   }
}
