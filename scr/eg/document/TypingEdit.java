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

import javax.swing.undo.UndoManager;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

import javax.swing.JTextPane;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.Languages;
import eg.ui.EditArea;
import eg.utils.FileUtils;
import eg.utils.Finder;

/**
 * Mediates the editing in the {@code EditArea} that shall happen during
 * typing.
 * <p>
 * Methods are used in the other classes that show line numbering, syntax
 * coloring, auto-indentation and undo/redo editing (the latter an inner class).
 */
class TypingEdit {

   private final static char[] UNDO_SEP = {' ', '(', ')', '{', '}', '\n', '\0'};

   private final EditArea editArea;
   private final UndoManager undomanager = new DocUndoManager();
   private final Coloring col;
   private final AutoIndent autoInd;
   private final RowNumbers rowNum;

   private boolean isDocListen = true; 
   private boolean isTypeEdit = false;
   private boolean isIndent = false;
   private char typed = '\0';
   private boolean isChangeEvent;

   TypingEdit(EditArea editArea) {
      this.editArea = editArea;

      editArea.getDoc().addDocumentListener(docListen);
      editArea.getDoc().addUndoableEditListener(undomanager);
      undomanager.setLimit(1000);

      col = new Coloring(editArea.getDoc(), editArea.getNormalSet());
      rowNum = new RowNumbers(editArea);
      autoInd = new AutoIndent(editArea);
   }

   void enableDocListen(boolean isDocListen) {
      this.isDocListen = isDocListen;
   }

   void enableTypeEdit(boolean isTypeEdit) {
      this.isTypeEdit = isTypeEdit;
      col.enableSingleLines(isTypeEdit);
   }

   void setUpEditing(Languages language) {
      if (Languages.PLAIN_TEXT == language) {
         editArea.allTextToBlack();
         enableTypeEdit(false);
         isIndent = false;
         autoInd.resetIndent();
      }
      else {
         col.setUpColoring(language);
         colorAll();
         isIndent = true;
      }
   }

   void setKeywords(String[] keywords, boolean constrainWord) {
      col.setKeywords(keywords, constrainWord);
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

   void colorAll() {
      enableTypeEdit(false);
      String all = editArea.getDocText();
      editArea.allTextToBlack();
      col.color(all, 0);
      enableTypeEdit(true);
   }

   void undo() {
      try {
         int previousLineNr = rowNum.getCurrLineNr();
         enableDocListen(false);
         if (undomanager.canUndo()) {
            undomanager.undo();
            updateAfterUndoRedo(previousLineNr);
         }
      }
      catch (CannotUndoException e) {
         FileUtils.logStack(e);
      }
   }

   void redo() {
      try {
         int previousLineNr = rowNum.getCurrLineNr();
         enableDocListen(false);
         if (undomanager.canRedo()) {
            undomanager.redo();
            updateAfterUndoRedo(previousLineNr);
         }
      }
      catch (CannotRedoException e) {
         FileUtils.logStack(e);
      }
   }

   //
   //--private--//
   //

   private final DocumentListener docListen = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (isDocListen) {
            isChangeEvent = false;
            String in = editArea.getDocText();
            int pos = de.getOffset();
            typed = in.charAt(pos);
            updateRowNumber(in);
            if (isTypeEdit) {
               insertTextModify(in, pos);
            }
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         if (isDocListen) {
            isChangeEvent = false;
            String in = editArea.getDocText();
            typed = '\0';
            updateRowNumber(in);
            if (isTypeEdit) {
               int pos = de.getOffset();
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

   private void insertTextModify(String in, int pos) {
      if (pos > 0 && isIndent) {
         autoInd.openBracketIndent(in, pos);
      }
      EventQueue.invokeLater(() -> {
         if (isIndent) {
            autoInd.closeBracketIndent(in, pos);
         }
         if (typed != '\n') {
            color(in, pos);
         }
      });
   }
   
   private void updateAfterUndoRedo(int previousLineNr) {
      String in = editArea.getDocText();
      updateRowNumber(in);
      int newLineNr = rowNum.getCurrLineNr();
      if (previousLineNr - newLineNr > 1) {
         colorAll();
      }
      else {
         int pos = editArea.textArea().getCaretPosition();
         if (pos > 0) {
            color(in, pos);
         }
      }
      enableDocListen(true);
   }

   private void color(String in, int pos) {
      EventQueue.invokeLater(() -> {
         col.color(in, pos);
      });
   }

   private class DocUndoManager extends UndoManager implements UndoableEditListener {

      CompoundEdit comp = null;

      @Override
      public synchronized void undoableEditHappened(UndoableEditEvent e) {
         if (!isDocListen) {
            return;
         }    
         if (!isChangeEvent) {
            addAnEdit(e.getEdit());
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
         //commitCompound();
         super.undo();
      }

      @Override
      public synchronized void redo() {
         //commitCompound();
         super.redo();
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
