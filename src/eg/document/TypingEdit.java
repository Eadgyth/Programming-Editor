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
 * <p> Uses methods from classes that show line numbering, do syntax
 * coloring, auto-indentation and undo/redo editing.
 * <p> Is created in {@link TextDocument}
 */
public class TypingEdit {

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

   /**
    * @param editArea  the reference to {@link EditArea}
    */
   public TypingEdit(EditArea editArea) {
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

   /**
    * Sets an <code>UndoableChangeListener</code> if none was set before
    *
    * @param ul  an {@link UndoableChangeListener}
    */
   public void setUndoableChangeListener(UndoableChangeListener ul) {
      if (ul == null) {
         throw new IllegalStateException(
               "An UndoableChangeListener is already set");
      }
      this.ul = ul;
   }

  /**
    * Sets a <code>TextSelectionLister</code> if none was set before
    *
    * @param sl  a {@link TextSelectionListener}
    */
   public void setTextSelectionListener(TextSelectionListener sl) {
      if (sl == null) {
         throw new IllegalStateException(
               "A TextSelectionListener is already set");
      }
      this.sl = sl;
   }

   /**
    * Enabled/disables the update methods in this
    * <code>DocumentListener</code>
    *
    * @param isEnabled  true/false to enable/disabled the update
    * methods in this <code>DocumentListener</code>
    */
   public void enableDocListen(boolean isEnabled) {
      isDocListen = isEnabled;
      if (isEnabled) {
         text = editArea.getDocText();
         lineNum.updateLineNumber(text);
         editArea.textArea().setCaretPosition(0);
      }
      else {
         undo.discardEdits();
         notifyUndoableChangeEvent();
      }
   }

   /**
    * Gets the text that is set in the update methods in this
    * <code>DocumentListener</code>
    *
    * @return  the text in the document
    */
   public String getText() {
      return text;
   }

   /**
    * Enables/disables syntax coloring and auto-indentation
    *
    * @param isEnabled  true/false to enable/disable editing during
    * typing
    */
   public void enableTypeEdit(boolean isEnabled) {
      isTypeEdit = isEnabled;
   }

   /**
    * Sets the editing during typing depending on the language
    *
    * @param lang  the language which is one of the constants in
    * {@link Languages}
    */
   public void setUpEditing(Languages lang) {
      if (lang == Languages.PLAIN_TEXT) {
         col.setAllCharAttrBlack();
         enableTypeEdit(false);
      }
      else {
         langSet.setColorable(lang);
         colorSection(null, 0);
         enableTypeEdit(true);
      }
   }

   /**
    * Sets the indentation unit which consists in any number of spaces
    *
    * @param indentUnit  the String that consists of a certain number of
    * white spaces
    */
   public void setIndentUnit(String indentUnit) {
      autoInd.setIndentUnit(indentUnit);
   }

   /**
    * Returns the current indentation unit
    *
    * @return the current indentation unit
    */
   public String getIndentUnit() {
      return autoInd.getIndentUnit();
   }

   /**
    * Colors a section of the document
    *
    * @param section  a section of the document which also may be the
    * entire text. If null the entire text is assumed. The complete lines
    * are colored even if it does not start a line start or end at line end
    * @param pos  the pos within the entire text where the section to
    * be colored starts
    */
   public void colorSection(String section, int pos) {
      col.colorMultipleLines(text, section, pos);
   }

   /**
    * Returns if edits can be undone
    * 
    * @return  if edits can be undone
    */
   public boolean canUndo() {
      return undo.canUndo();
   }

   /**
    * Returns if edits can be redone
    * 
    * @return  if edits can be redone
    */
   public boolean canRedo() {
      return undo.canRedo();
   }

   /**
    * Performs an undo action
    */
   public void undo() {
      isAddToUndo = false;
      undo.undo();
      updateAfterUndoRedo();
   }

   /**
    * Performs a redo action
    */
   public void redo() {
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
            colorSection(change, pos);
         }
         else if (event.equals(DocumentEvent.EventType.REMOVE)) {
            EventQueue.invokeLater(() ->
                     col.colorLine(text, pos));
         }
      }
      isAddToUndo = true;
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
               EventQueue.invokeLater(() -> {
                  col.colorLine(text, pos);
                  autoInd.indent(text, pos);
                  autoInd.closedBracketIndent(text, pos);
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
               EventQueue.invokeLater(() ->
                     col.colorLine(text, pos));
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         // nothing to do
      }

      private void textUpdate() {
         text = editArea.getDocText();
         lineNum.updateLineNumber(text);
      }
   };
}
