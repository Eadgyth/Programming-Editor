package eg.document;

import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

import java.awt.EventQueue;
import java.awt.Color;

import java.util.Vector;

//--Eadgyth--//
import eg.ui.EditArea;

/** 
 * Responsible for the edits the display in the {@code EditArea} that shall
 * happen during typing. <p>
 * The changes include the line numbering, the syntax / keyword coloring;
 * auto-indentation and undo/redo editing
 */
class TypingEdit {

   private final DocUndoManager undomanager = new DocUndoManager();
   private final StyledDocument doc;  
   private final Element el;
   private final SimpleAttributeSet normalSet = new SimpleAttributeSet(); 

   private final Coloring col;
   private final AutoIndent autoInd;
   private final RowNumbers rowNum;

   /*
    * true to call modifying methods from this update methods */
   private boolean isDocListen = true; 
   /*
    * true to call text coloring and indentation from this
    * update methods */
   private boolean isTextModify = false;
   /*
    * Controls separately if auto-indentation is used */
   private boolean isIndent = false;

   TypingEdit(EditArea editArea) {
      doc = editArea.textArea().getStyledDocument();
      el = doc.getParagraphElement(0);
      setStyles();

      doc.addDocumentListener(docListen);
      doc.addUndoableEditListener(new DocUndoManager());
      undomanager.setLimit(1000);

      col = new Coloring(doc, normalSet);
      rowNum = new RowNumbers(editArea.lineArea(), editArea.scrolledArea());
      autoInd = new AutoIndent(editArea.textArea(), doc, normalSet);
   }
   
   void enableDocListen(boolean isDocListen) {
      this.isDocListen = isDocListen;
   }

   void enableTextModify(boolean isTextModify) {
      this.isTextModify = isTextModify;
      col.enableSingleLines(isTextModify);
   }
   
   void enableIndent(boolean isEnabled) {
      isIndent = isEnabled;
      if (!isEnabled) {
         autoInd.resetIndent();
      }
   }
   
   void configColoring(String[] keywords, String lineCmnt, String blockCmntStart,
         String blockCmntEnd, boolean isStringLit, boolean isBrackets,
         boolean constrainWord) {
      col.configColoring(keywords, lineCmnt, blockCmntStart,
            blockCmntEnd, isStringLit, isBrackets, constrainWord);
   }

   StyledDocument doc() {
      return doc;
   }

   SimpleAttributeSet normalSet() {
      return normalSet;
   }
   
   String getDocText() {
      String in = null;
      try {
         in = doc.getText(0, doc.getLength());
      }
      catch (BadLocationException e) {
         e.printStackTrace();
      }
      return in;
   }

   String getIndentUnit() {
      return autoInd.getIndentUnit();
   }
   
   void changeIndentUnit(String indentUnit) {
      autoInd.changeIndentUnit(indentUnit);
   }

   void updateRowNumber(String in) {
      rowNum.updateRowNumber(in);
   }

   void colorAll(boolean enableTextModify) {
      enableTextModify(false);
      String all = getDocText();
      doc.setCharacterAttributes(0, all.length(),
            normalSet(), false);
      col.color(all, 0);
      enableTextModify(enableTextModify);
   }

    public void undo() {
       undomanager.undo();
   }

   void redo() {
      undomanager.redo();
      if (isTextModify) {
          colorAll(true);
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
   
   private DocumentListener docListen = new DocumentListener() {
      public void changedUpdate(DocumentEvent documentEvent) {
      }

      public void insertUpdate(DocumentEvent de) {
         if (isDocListen) {
            String in = getDocText();
            updateRowNumber(in);
            if (isTextModify) {
               int pos = de.getOffset();
               insertTextModify(de, in, pos);
            }
         }
      }

      public void removeUpdate(DocumentEvent de) {
         if (isDocListen) {
            String in = getDocText();
            updateRowNumber(in);
            if (isTextModify) {
               int pos = de.getOffset();
               removeTextModify(de, in, pos);
            }
         }
      }
   };
   
   private void insertTextModify(DocumentEvent de, String in, int pos) {
      if (pos > 0 && isIndent) {
         autoInd.openBracketIndent(in, pos);
      }
      EventQueue.invokeLater(() -> {
         col.color(in, pos);
         if (isIndent) {
            autoInd.closeBracketIndent(in, pos); // must be invoked later
         }
      });
   }

   private void removeTextModify(DocumentEvent de, String in, int pos) {
      EventQueue.invokeLater( () -> {
         if (col.isBlockCmnt()) {
            col.uncommentBlock(in, pos);
         }
         col.color(in, pos);
      });
    }
   
   class DocUndoManager extends UndoManager implements UndoableEditListener {
      
      @Override
      public void undoableEditHappened (UndoableEditEvent e) {
         if (!isDocListen) {
            return;
         }
         /*
          * Exclude changes of the style */
         AbstractDocument.DefaultDocumentEvent event =
               (AbstractDocument.DefaultDocumentEvent) e.getEdit();
         if (event.getType().equals(DocumentEvent.EventType.CHANGE)) {
            return;
         }
         addEdit(e.getEdit());
         undomanager.addEdit(lastEdit());
      }
   
      public void undo() {
         System.out.println("undo");
         try {
            if (super.canUndo()) {
               super.undo();
            }
         }
         catch (CannotUndoException cue) {
            cue.printStackTrace();
         }
      }
   
      public void redo() {
         try {
            if (super.canRedo()) {
               super.redo();
            }
         }
         catch (CannotRedoException cre) {
            cre.printStackTrace();
         }
      }
   }
}