/**
 * This inner class {@code DocUndoManager} is based on
 * CompoundUndoManager class from JSyntaxPane found at
 * https://github.com/aymanhs/jsyntaxpane
 * (Copyright 2008 Ayman Al-Sairafi).
 * The separation of merged undo edits by time is replaced by
 * "undo-separators"
 */
package eg.document;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

import javax.swing.undo.UndoManager;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

import javax.swing.JTextPane;
import javax.swing.SwingWorker;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.Languages;
import eg.syntax.Lexer;
import eg.syntax.Coloring;
import eg.ui.EditArea;
import eg.utils.FileUtils;
import eg.utils.Finder;

/*
 * Mediates the editing in the {@code EditArea} that shall happen during
 * typing.
 * <p>
 * Methods are used in other classes that show line numbering, syntax
 * coloring, auto-indentation and undo/redo editing (the latter an inner
 * class).
 */
class TypingEdit {
   
   private final static int CHANGE_EVENT = 0;
   private final static int INSERT_EVENT = 1;
   private final static int REMOVE_EVENT = 2;
   private final static char[] UNDO_SEP = {' ', '(', ')', '{', '}', '\n'};

   private final EditArea editArea;
   private final Lexer lex;
   private final Coloring col;
   private final AutoIndent autoInd;
   private final LineNumbers lineNum;
    private final UndoManager undomanager = new DocUndoManager();

   private boolean evaluateText = true;
   private boolean isTypeEdit = false;
   private char typed;
   private int eventType;
   private int pos;
   private int changeLength = 1;

   TypingEdit(EditArea editArea) {
      this.editArea = editArea;
      editArea.getDoc().addDocumentListener(docListen);
      editArea.getDoc().addUndoableEditListener(undomanager);
      editArea.textArea().addCaretListener(new UndoStopper());
      undomanager.setLimit(1000);
      lex = new Lexer(editArea.getDoc(), editArea.getNormalSet());
      col = new Coloring(lex);
      lineNum = new LineNumbers(editArea);
      autoInd = new AutoIndent(editArea);
   }

   void enableEvaluateText(boolean isEnabled) {
      evaluateText = isEnabled;
   }
   
   void enableTypeEdit(boolean isEnabled) {
      isTypeEdit = isEnabled;
   }

   void setUpEditing(Languages lang) {
      undomanager.discardAllEdits();
      if (lang == Languages.PLAIN_TEXT) {
         lex.setCharAttrBlack(0, editArea.getDocText().length());
         enableTypeEdit(false);
         autoInd.enableIndent(false);
      }
      else {
         col.selectColorable(lang);
         colorSection(editArea.getDocText(), null, 0);
         autoInd.enableIndent(true);
      }
   }

   void changeIndentUnit(String indentUnit) {
      autoInd.changeIndentUnit(indentUnit);
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

   void colorSection(String allText, String section, int pos) {
      enableTypeEdit(false);
      lex.enableTypeMode(section != null);
      col.colorSection(allText, section, pos);
      enableTypeEdit(true);
      lex.enableTypeMode(true);
   }

   void undo() {
      try {
         int prevLineNr = lineNum.getCurrLineNr();
         enableEvaluateText(false);
         if (undomanager.canUndo()) {
            undomanager.undo();
         }
         updateAfterUndoRedo(prevLineNr);
      }
      catch (CannotUndoException e) {
         FileUtils.logStack(e);
      }
   }

   void redo() {
      try {
         int prevLineNr = lineNum.getCurrLineNr();
         enableEvaluateText(false);
         if (undomanager.canRedo()) {
            undomanager.redo();
         }
         updateAfterUndoRedo(prevLineNr);
      }
      catch (CannotRedoException e) {
         FileUtils.logStack(e);
      }
   }

   //
   //--private methods/classes--//
   //

   private void updateAfterUndoRedo(int prevLineNr) {
      String allText = editArea.getDocText();
      updateLineNumber(allText);
      if (isTypeEdit) {
         int newLineNr = lineNum.getCurrLineNr();
         if (newLineNr > prevLineNr) {
            colorSection(allText, null, 0);
         }
         else if (newLineNr < prevLineNr) {
            undomanager.discardAllEdits();
         }
         else {
            if (pos > 0 & pos < allText.length()) {
               color(allText, pos);
            }
         }
      }
      enableEvaluateText(true);
   }

   private void color(String allText, int pos) {
      EventQueue.invokeLater(() -> {
         col.colorLine(allText, pos);
      });
   }

   private final DocumentListener docListen = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         pos = de.getOffset();
         eventType = INSERT_EVENT;
         if (evaluateText) {
            changeLength = de.getLength();
            String in = editArea.getDocText();
            typed = in.charAt(pos);
            updateLineNumber(in);
            if (isTypeEdit) {
               autoInd.setText(in);
               if (typed != '\n') {
                  color(in, pos);
               }
               EventQueue.invokeLater(() -> {
                  autoInd.closeBracketIndent(pos);
               });
            }
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         pos = de.getOffset();
         eventType = REMOVE_EVENT;
         if (evaluateText) {
            eventType = REMOVE_EVENT;
            typed = '\0';
            String in = editArea.getDocText();
            updateLineNumber(in);
            if (isTypeEdit) {
               color(in, pos);
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         if (evaluateText) {
            eventType = CHANGE_EVENT;
         }
      }
   };

   private class UndoStopper implements CaretListener {

      int caret;

      @Override
      public void caretUpdate(CaretEvent ce) {
         caret = editArea.shiftToSelectionStart(ce.getDot());
         if (caret > 0) {
            boolean isStop = true;
            if (eventType == 1) {
               isStop = caret - pos != 1 && caret - pos != changeLength;
            }
            else if (eventType == 2) {
               isStop = caret - pos != 0 && caret - pos != changeLength;
            }
            if (isStop) {
               undomanager.discardAllEdits();
            }
         }
         changeLength = 0;
      }
   }

   private final class DocUndoManager extends UndoManager
         implements UndoableEditListener {

      CompoundEdit comp = null;

      @Override
      public synchronized void undoableEditHappened(UndoableEditEvent e) {
         if (!evaluateText) {
            return;
         }
         UndoableEdit ed = e.getEdit();
         if (eventType != CHANGE_EVENT) {
            addAnEdit(ed);
         }
      }

      @Override
      public synchronized boolean canUndo() {
         commitCompound();
         return super.canUndo();
      }

      @Override
      public synchronized boolean canRedo() {
         commitCompound();
         return super.canRedo();
      }

      @Override
      public synchronized void undo() {
         super.undo();
      }

      @Override
      public synchronized void redo() {
         super.redo();
      }

      @Override
      public synchronized void discardAllEdits() {
         if (comp != null) {
            comp = null;
         }
         super.discardAllEdits();
      }

      private synchronized void addAnEdit(UndoableEdit anEdit) {
         if (comp == null) {
            comp = new CompoundEdit();
         }
         if ((typed != '\0' & isEditSeparator()) || typed == '\0') {
            commitCompound();
            super.addEdit(anEdit);
         }
         else {
            comp.addEdit(anEdit);
         }
      }

      private synchronized void commitCompound() {
         if (comp != null) {
            comp.end();
            super.addEdit(comp);
            comp = null;
         }
      }

      private synchronized boolean isEditSeparator() {
         int i = 0;
         for (i = 0; i < UNDO_SEP.length; i++) {
            if (UNDO_SEP[i] == typed) {
               break;
            }
         }
         return i != UNDO_SEP.length;
      }
   }
}
