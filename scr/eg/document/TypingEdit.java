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
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

import java.awt.EventQueue;
import java.awt.Color;


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

   private boolean isDocListen = true; 
   private boolean isTextModify = false;
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
         System.out.println(e.getMessage());
      }
      return in;
   }

   String getIndentUnit() {
      return autoInd.getIndentUnit();
   }
   
   void changeIndentUnit(String indentUnit) {
      autoInd.changeIndentUnit(indentUnit);
   }

   void updateRowNumber(String content) {
      rowNum.updateRowNumber(content);
   }
   
   void colorAll() {
      col.color(getDocText(), 0);
      enableTextModify(true);
   }

   void recolorAll() {
      enableTextModify(false);
      String all = getDocText();
      doc.setCharacterAttributes(0, all.length(),
            normalSet(), false);
      col.color(all, 0);
      enableTextModify(true);
   }

   void undo() {
       undomanager.undo();
   }

   void redo() {
      undomanager.redo();
      if (isTextModify) {
          recolorAll();
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
      public void changedUpdate(DocumentEvent documentEvent) {
      }

      @Override
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

      @Override
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
         if (isIndent) {
            autoInd.closeBracketIndent(in, pos); // must be invoked later
         }
         col.color(in, pos);        
      });
   }

   private void removeTextModify(DocumentEvent de, String in, int pos) {
      EventQueue.invokeLater( () -> {
         col.color(in, pos);
         if (col.isBlockCmnt()) {
            col.uncommentBlock(in, pos);
         }
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
         undomanager.addEdit(e.getEdit());
      }
   
      @Override
      public void undo() {
         try {
            if (super.canUndo()) {
               super.undo();
            }
         }
         catch (CannotUndoException cue) {
            System.out.println(cue.getMessage());
         }
      }
   
      @Override
      public void redo() {
         try {
            if (super.canRedo()) {
               super.redo();
            }
         }
         catch (CannotRedoException cre) {
            System.out.println(cre.getMessage());
         }
      }
   }
}