/**
 * This inner class {@code DocUndoManager} is based on
 * CompoundUndoManager class from JSyntaxPane found at 
 * https://github.com/aymanhs/jsyntaxpane
 * Copyright 2008 Ayman Al-Sairafi
 * In the present class merged edits are separated by "edit separators".
 * (white space and brackets). A newline is treated as an edit on its own.
 */
package eg.document;

import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import javax.swing.text.AbstractDocument;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import javax.swing.undo.UndoManager;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

import java.awt.EventQueue;
import java.awt.Color;

//--Eadgyth--//
import eg.Languages;
import eg.ui.EditArea;
import eg.utils.FileUtils;

/**
 * The editing in the {@code EditArea} that shall happen during typing.
 * <p>
 * The changes include the line numbering, the syntax coloring,
 * auto-indentation and undo/redo editing.
 */
class TypingEdit {

   private final static char[] UNDO_SEP = {' ', '(', ')', '{', '}'};

   private final StyledDocument doc;  
   private final Element el;
   private final SimpleAttributeSet normalSet = new SimpleAttributeSet(); 

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
      doc = editArea.textArea().getStyledDocument();
      el = doc.getParagraphElement(0);
      setStyles();

      doc.addDocumentListener(docListen);
      doc.addUndoableEditListener(undomanager);
      undomanager.setLimit(1000);

      col = new Coloring(doc, normalSet);
      rowNum = new RowNumbers(editArea.lineArea(), editArea.textPanel());
      autoInd = new AutoIndent(editArea.textArea(), doc, normalSet);
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
         doc.setCharacterAttributes(0, getDocText().length(), normalSet, false);
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

   StyledDocument getDoc() {
      return doc;
   }

   SimpleAttributeSet getNormalSet() {
      return normalSet;
   }

   String getDocText() {
      String in = null;
      try {
         in = doc.getText(0, doc.getLength());
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
      return in;
   }

   void changeIndentUnit(String indentUnit) {
      autoInd.changeIndentUnit(indentUnit);
   }

   public String getIndentUnit() {
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
      String all = getDocText();
      doc.setCharacterAttributes(0, all.length(), normalSet, false);
      col.color(all, 0);
      enableTypeEdit(true);
   }

   void undo() {
      try {
         if (undomanager.canUndo()) {
            undomanager.undo();
         }
      }
      catch (CannotUndoException e) {
         FileUtils.logStack(e);
      }
   }

   void redo() {
      try {
         if (undomanager.canRedo()) {
            undomanager.redo();
         }
      }
      catch (CannotRedoException e) {
         FileUtils.logStack(e);
      }
   }

   //
   //--private--
   //

   private void setStyles() {
      StyleConstants.setForeground(normalSet, Color.BLACK);
      StyleConstants.setLineSpacing(normalSet, 0.2f);
      StyleConstants.setBold(normalSet, false);
      doc.setParagraphAttributes(0, el.getEndOffset(), normalSet, false);
   }

   private final DocumentListener docListen = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (isDocListen) {
            isChangeEvent = false;
            String in = getDocText();
            int pos = de.getOffset();
            typed = in.charAt(pos);
            updateRowNumber(in);
            if (isTypeEdit) {
               insertTextModify(de, in, pos);
            }
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         if (isDocListen) {
            isChangeEvent = false;
            String in = getDocText();
            typed = '\0';
            updateRowNumber(in);
            if (isTypeEdit) {
               int pos = de.getOffset();
               removeTextModify(de, in, pos);
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

   private void insertTextModify(DocumentEvent de, String in, int pos) {
      if (pos > 0 && isIndent) {
         autoInd.openBracketIndent(in, pos);
      }
      EventQueue.invokeLater(() -> {
         if (isIndent) {
            autoInd.closeBracketIndent(in, pos);
         }
         if (typed != '\n') {
            col.color(in, pos);
         }
      });
   }

   private void removeTextModify(DocumentEvent de, String in, int pos) {
      EventQueue.invokeLater(() -> {
         col.color(in, pos);
      });
   }

   class DocUndoManager extends UndoManager implements UndoableEditListener {

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
         if (typed != '\0' & typed != '\n') {
            comp.addEdit(anEdit);
         }
         else {
            comp.end();
            super.addEdit(comp);
            comp = null;
            super.addEdit(anEdit);
         }
         if (isEditSeparator()) {
            comp.end();
            super.addEdit(comp);
            comp = null;
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
