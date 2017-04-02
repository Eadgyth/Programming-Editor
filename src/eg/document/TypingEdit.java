/**
 * This inner class {@code DocUndoManager} is based on
 * CompoundUndoManager class from JSyntaxPane found at
 * https://github.com/aymanhs/jsyntaxpane
 * Copyright 2008 Ayman Al-Sairafi
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

   private final static char[] UNDO_SEP = {' ', '(', ')', '{', '}', '\0', '\n'};

   private final EditArea editArea;
   private final UndoManager undomanager = new DocUndoManager();
   private final Lexer lex;
   private final Coloring col;
   private final AutoIndent autoInd;
   private final RowNumbers rowNum;

   private boolean isDocListen = true;
   private boolean isTypeEdit = false;
   private char typed = '\0';
   private int pos;
   private int caret;
   private boolean isChangeEvent;

   TypingEdit(EditArea editArea) {
      this.editArea = editArea;
      editArea.getDoc().addDocumentListener(docListen);
      editArea.getDoc().addUndoableEditListener(undomanager);
      undomanager.setLimit(1000);
      editArea.textArea().addCaretListener(new DocCaretListener());
      lex = new Lexer(editArea.getDoc(), editArea.getNormalSet());
      col = new Coloring(lex);
      rowNum = new RowNumbers(editArea);
      autoInd = new AutoIndent(editArea);
   }

   void enableDocListen(boolean isDocListen) {
      this.isDocListen = isDocListen;
   }

   void enableTypeEdit(boolean isTypeEdit) {
      this.isTypeEdit = isTypeEdit;
      lex.enableTypeMode(isTypeEdit);
   }

   void setUpEditing(Languages lang) {
      restartUndo();
      if (lang == Languages.PLAIN_TEXT) {
         editArea.allTextToBlack();
         enableTypeEdit(false);
         lex.enableTypeMode(false);
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

   void addAllRowNumbers(String in) {
      rowNum.addAllRowNumbers(in);
   }

   void updateRowNumber(String content) {
      rowNum.updateRowNumber(content);
   }

   void colorSection(String allText, String section, int posStart) {
      int length = 0;
      if (section != null) {
         length = section.length();
         //
         // include full lines
         String[] sectionArr = section.split("\n");
         String startingLine = Finder.currLine(allText, posStart);  
         sectionArr[0] = startingLine;
         if (sectionArr.length > 1) {
            String endingLine = Finder.currLine(allText, posStart + length);
            sectionArr[sectionArr.length - 1] = endingLine;
         }
         StringBuffer sb = new StringBuffer();
         for (String s : sectionArr) {
            sb.append(s);
            sb.append("\n");
         }
         section = sb.toString();

         posStart = Finder.lastReturn(allText, posStart) + 1;
         lex.enableTypeMode(true);
      }
      else {
         length = allText.length();
      }
      enableTypeEdit(false);
      editArea.textToBlack(length, posStart);
      col.colorSection(allText, section, posStart);
      enableTypeEdit(true);
   }

   synchronized void undo() {
      try {
         int prevLineNr = rowNum.getCurrLineNr();
         enableDocListen(false);
         if (undomanager.canUndo()) {
            undomanager.undo();
         }
         updateAfterUndoRedo(prevLineNr);
      }
      catch (CannotUndoException e) {
         FileUtils.logStack(e);
      }
   }

   synchronized void redo() {
      try {
         int prevLineNr = rowNum.getCurrLineNr();
         enableDocListen(false);
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
   //--private--//
   //

   private synchronized void restartUndo() {
      undomanager.discardAllEdits();
   }

   private synchronized void updateAfterUndoRedo(int prevLineNr) {
      String allText = editArea.getDocText();
      updateRowNumber(allText);
      if (!isTypeEdit) {
         return;
      }
      int newLineNr = rowNum.getCurrLineNr();
      if (newLineNr > prevLineNr) {
         colorSection(allText, null, 0);
      }
      //
      // switch off because redo multiline breaks document (no solutiuon)
      else if (newLineNr < prevLineNr) {
         restartUndo();
      }
      else {
         if (pos > 0) {
            color(allText, pos);
         }
      }
      enableDocListen(true);
   }

   private synchronized void color(String allText, int pos) {
      EventQueue.invokeLater(() -> {
         col.colorLine(allText, pos);
      });
   }

   private final DocumentListener docListen = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         pos = de.getOffset();
         if (isDocListen) {
            isChangeEvent = false;
            String in = editArea.getDocText();
            typed = in.charAt(pos);
            updateRowNumber(in);
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
         if (isDocListen) {
            isChangeEvent = false;
            String in = editArea.getDocText();
            typed = '\0';
            updateRowNumber(in);
            if (isTypeEdit) {
               color(in, pos);
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         if (isDocListen) {
            isChangeEvent = true;
         }
      }
   };

   private class DocCaretListener implements CaretListener {

      int lastCaret;
      int lastPos;

      @Override
      public void caretUpdate(CaretEvent ce) {
         caret = ce.getDot();
         if (isDocListen && pos > 0 && pos == lastPos) {
            //
            // cursor was moved by mouse or arrow keys
            if (caret != lastCaret) {
               restartUndo();
            }
         }
         lastCaret = caret;
         lastPos = pos;
      }
   }

   private class DocUndoManager extends UndoManager implements UndoableEditListener {

      CompoundEdit comp = null;

      @Override
      public synchronized void undoableEditHappened(UndoableEditEvent e) {
         if (!isDocListen) {
            return;
         }
         UndoableEdit ed = e.getEdit();
         if (!isChangeEvent) {
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
         if (isEditSeparator()) {
            commitCompound();
            super.addEdit(anEdit);
         }
         else {
            comp.addEdit(anEdit);
         }
      }

      private void commitCompound() {
         if (comp != null) {
            comp.end();
            super.addEdit(comp);
            comp = null;
         }
      }

      private boolean isEditSeparator() {
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
