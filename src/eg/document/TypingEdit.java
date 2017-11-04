package eg.document;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.Languages;
import eg.utils.LinesFinder;
import eg.syntax.Coloring;

/**
 * Mediates between the editing of the text document and the actions that
 * happen in response.
 * Actions include syntax coloring, indentation, line numbering, reading the
 * current line and columns and adding edits to the undoable edits.<br>
 * <p> Created in {@link FileDocument}
 */
public class TypingEdit {

   private final TextDocument textDoc;
   private final Coloring col;
   private final AutoIndent autoInd;
   private final LineNumbers lineNum;
   private final UndoEdit undo;

   private UndoableChangeEvent uce;
   private UndoableChangeListener ucl;
   private TextSelectionEvent tse;
   private TextSelectionListener tsl;
   private LineAndColumnReadable lcr;
   private boolean isDocListen = true;
   private boolean isAddToUndo = true;
   private boolean isCodeEditing = false;
   private DocumentEvent.EventType event;
   private int pos = 0;
   private String text = "";
   private String change = "";
   private boolean isSelectionTmp = false;
   private boolean canUndoTmp = false;
   private boolean canRedoTmp = false;
   private int lineNrAtCursor = 1;
   private int colNrAtCursor = 1;

   /**
    * @param textDoc  the reference to {@link TextDocument}
    * @param lineNrDoc  the reference to {@link LineNumberDocument}
    */
   public TypingEdit(TextDocument textDoc, LineNumberDocument lineNrDoc) {
      this.textDoc = textDoc;
      col = new Coloring(textDoc.doc(), textDoc.attrSet());
      lineNum = new LineNumbers(lineNrDoc);
      autoInd = new AutoIndent(textDoc);
      undo = new UndoEdit(textDoc);
      textDoc.doc().addDocumentListener(docListen);
      textDoc.docTextArea().addCaretListener(caretListen);
   }

   /**
    * Sets an <code>UndoableChangeListener</code> if none was set before
    *
    * @param ucl  an {@link UndoableChangeListener}
    */
   public void setUndoableChangeListener(UndoableChangeListener ucl) {
      if (this.ucl != null) {
         throw new IllegalStateException(
               "An UndoableChangeListener is already set");
      }
      this.ucl = ucl;
   }

  /**
    * Sets a <code>TextSelectionLister</code> if none was set before
    *
    * @param tsl  a {@link TextSelectionListener}
    */
   public void setTextSelectionListener(TextSelectionListener tsl) {
      if (this.tsl != null) {
         throw new IllegalStateException(
               "A TextSelectionListener is already set");
      }
      this.tsl = tsl;
   }
   
   /**
    * Sets a <code>CursorPositionReadable</code> if none was set before
    *
    * @param lcr  a {@link LineAndColumnReadable}
    */
   public void setLineAndColumnReadable(LineAndColumnReadable lcr) {
      if (this.lcr != null) {
         throw new IllegalStateException(
               "A PositionReadable is already set");
      }
      this.lcr = lcr;
   }

   /**
    * Enabled or disables the update methods in this
    * <code>DocumentListener</code>
    *
    * @param isEnabled  true to enable, false to disable
    */
   public void enableDocListen(boolean isEnabled) {
      isDocListen = isEnabled;
      if (isEnabled) {
         textUpdate();
         textDoc.docTextArea().setCaretPosition(0);
      }
   }

   /**
    * Enables or disables actions in responce to the editing of source code
    *
    * @param isEnabled  true to enable, false to disable
    */
   public void enableCodeEditing(boolean isEnabled) {
      isCodeEditing = isEnabled;
   }

   /**
    * Set the editing mode that depends on the language
    *
    * @param lang  the language which is a constant in {@link Languages}
    */
   public void setEditingMode(Languages lang) {
      col.setColorable(lang);
      if (lang == Languages.NORMAL_TEXT) {
         enableCodeEditing(false);
      }
      else {
         colorMultipleLines(null, 0);
         enableCodeEditing(true);
      }
   }
   
   /**
    * Gets the text that is updated in the insert- and remove
    * methods of this <code>DocumentListener</code>
    *
    * @return  the text in the document
    */
   public String getText() {
      return text;
   }

   /**
    * Sets the indent unit which consists of spaces
    *
    * @param indentUnit  the indend unit
    */
   public void setIndentUnit(String indentUnit) {
      autoInd.setIndentUnit(indentUnit);
   }

   /**
    * Returns the currently set indent unit
    *
    * @return  the indent unit
    */
   public String getIndentUnit() {
      return autoInd.getIndentUnit();
   }

   /**
    * Colors multiple lines of the document text
    *
    * @param section  a section of the document text. If null the entire
    * text is used.
    * @param pos  the pos where <code>section</code> starts
    */
   public void colorMultipleLines(String section, int pos) {
      int posStart = 0;
      if (section == null) {
         section = text;
      }
      else {
         section = LinesFinder.allLinesAtPos(text, section, pos);
         posStart = LinesFinder.lastNewline(text, pos) + 1;
      }
      col.color(text, section, pos, posStart);
   }
   
   /**
    * Gets the number of the line where the cursor is positioned
    *
    * @return  the number
    */
   public int lineNrAtCursor() {
      return lineNrAtCursor;
   }
   
   /**
    * Gets the number of the column within the line where the cursor is
    * positioned
    *
    * @return  the number
    */
   public int columnNrAtCursor() {
      return colNrAtCursor;
   }

   /**
    * Returns the boolean that indicates if edits can be undone
    * 
    * @return  the boolean
    */
   public boolean canUndo() {
      return undo.canUndo();
   }

   /**
    * Returns the boolean that indicates if edits can be redone
    * 
    * @return  the boolean
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
      if (isCodeEditing) {
         if (event.equals(DocumentEvent.EventType.INSERT)) {
            colorMultipleLines(change, pos);
         }
         else if (event.equals(DocumentEvent.EventType.REMOVE)) {
            colorLine();
         }
      }
      isAddToUndo = true;
   }
   
   private void textUpdate() {
      text = textDoc.getText();
      lineNum.updateLineNumber(text);
   }
   
   private void colorLine() {
      String toColor = LinesFinder.lineAtPos(text, pos);
      int posStart = LinesFinder.lastNewline(text, pos) + 1;
      EventQueue.invokeLater(() ->
         col.color(text, toColor, pos, posStart));
   }

   private void notifyUndoableChangeEvent() {
      if (ucl == null) {
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
      if (isUndoableChange || isRedoableChange) {
         uce = new UndoableChangeEvent(canUndoTmp, canRedoTmp);
         ucl.undoableStateChanged(uce);
      }
   }

   private void notifyTextSelectionEvent(boolean isSelection) {
      if (tsl == null) {
         return;
      }
      if (isSelectionTmp != isSelection) {
         isSelectionTmp = isSelection;
         tse = new TextSelectionEvent(isSelection);
         tsl.selectionUpdate(tse);
      }
   }
   
   private void readLineAndColumnNr(int caret) {
      int lastNewLine = LinesFinder.lastNewline(text, caret);
      lineNrAtCursor = LinesFinder.lineNrAtPos(text, caret);
      if (lastNewLine == -1) {
         colNrAtCursor = caret + 1;
      }
      else {
         colNrAtCursor = caret - lastNewLine;
      }
      if (lcr != null) {
         lcr.setCurrLineAndColumn(lineNrAtCursor, colNrAtCursor);
      }
   }

   private void markUndoBreakpoint(int caret) {
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
            undo.markBreakpoint();
         }
      }
   }

   private final DocumentListener docListen = new DocumentListener() {

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
            if (isCodeEditing) {
               colorLine();
               EventQueue.invokeLater(() -> {
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
            if (isCodeEditing) {
               colorLine();
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         // nothing to do
      }
   };
   
   private final CaretListener caretListen = (CaretEvent ce) -> {
       notifyTextSelectionEvent(ce.getDot() != ce.getMark());
       readLineAndColumnNr(ce.getDot());
       markUndoBreakpoint(ce.getDot());
   };
}
