package eg.document;

import java.awt.EventQueue;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

//--Eadgyth--/
import eg.Languages;
import eg.utils.LinesFinder;
import eg.syntax.*;
import eg.document.styledtext.EditableText;

/**
 * Mediates between the editing of the document and the actions that
 * happen in response
 */
public class TypingEdit {

   private final EditableText txt;
   private final LineNumbers lineNum;
   private final SyntaxHighlighter syntax;
   private final Indentation indent;
   private final UndoEdit undo;

   private boolean isDocUpdate = true;
   private boolean isCodeUpdate = false;
   private boolean isAddToUndo = true;

   private boolean isInsert;
   private int chgPos = 0;
   private String change = "";

   private EditingStateReadable esr;
   private boolean inChangeState = false;
   private boolean selectionState = false;
   private int lineNr = 1;
   private int colNr = 1;

   /**
    * @param txt  the {@link EditableText}
    * @param lineNum  the {@link LineNumbers}
    */
   public TypingEdit(EditableText txt, LineNumbers lineNum) {
      this.txt = txt;
      this.lineNum = lineNum;
      syntax = new SyntaxHighlighter(txt);
      indent = new Indentation(txt);
      undo = new UndoEdit(txt);
      txt.addDocumentListener(docListener);
      txt.textArea().addCaretListener(caretListener);
   }

   /**
    * Sets the <code>EditingStateReadable</code>
    *
    * @param esr  the EditingStateReadable
    */
   public void setEditingStateReadable(EditingStateReadable esr) {
      if (this.esr != null) {
         throw new IllegalStateException(
               "An EditingStateReadable is already set");
      }
      this.esr = esr;
   }

   /**
    * Enables or disables the update methods in this
    * <code>DocumentListener</code>
    *
    * @param b  true to enable, false to disable
    */
   public void enableDocUpdate(boolean b) {
      isDocUpdate = b;
      if (b) {
         updateText();
      }
   }

   /**
    * Sets the editing mode
    *
    * @param cl  the CurrentLanguege
    */
   public void setEditingMode(CurrentLanguage cl) {
      syntax.setHighlighter(cl.createHighlighter());
      indent.setCurlyBracketMode(cl.curlyBracketMode());
      if (cl.lang() == Languages.NORMAL_TEXT) {
         isCodeUpdate = false;
      }
      else {
         if (!txt.text().isEmpty()) {
            syntax.highlight();
         }
         isCodeUpdate = true;
      }
   }

   /**
    * Makes a text change
    *
    * @param tc  the TextChange
    */
   public void editText(TextChange tc) {
      boolean isCodeUpdateHelper = isCodeUpdate;
      isCodeUpdate = false;
      tc.edit(isCodeUpdateHelper);
      isCodeUpdate = isCodeUpdateHelper;
   }

   /**
    * Highlights text in a section around the position where a change
    * happened. The exact range is defined by the {@link Highlighter}
    * for a given language.
    */
   public void highlightAtPos() {
      syntax.highlight(chgPos);
      if (isInsert && change.equals("\n")) {
         syntax.highlight(chgPos + 1);
      }
   }

   /**
    * Highlights text in a section that may be multiline. The exact
    * range is defined by the {@link Highlighter} for a given language.
    */
   public void highlightInsertion() {
      if (change.length() > 0) {
         syntax.highlight(change, chgPos);
      }
   }

   /**
    * Resets the state which indicates that the text has been changed
    */
   public void resetInChangeState() {
      inChangeState = false;
      esr.updateInChangeState(inChangeState);
   }

   /**
    * Sets the indent unit which consists of spaces
    *
    * @param indentUnit  the indend unit
    */
   public void setIndentUnit(String indentUnit) {
      indent.setIndentUnit(indentUnit);
   }

   /**
    * Gets the current indent unit
    *
    * @return  the indent unit
    */
   public String indentUnit() {
      return indent.indentUnit();
   }

   /**
    * Disables or re-enables adding breakpoint to define undoable units
    *
    * @param b  true to disable, false to re-enable
    * @see UndoEdit#disableBreakpointAdding
    */
   public void disableBreakpointAdding(boolean b) {
      undo.disableBreakpointAdding(b);
   }

   /**
    * Reads the current editing state by calling the methods defined in
    * <code>EditingStateReadable</code>
    *
    * @see EditingStateReadable
    */
   public void readEditingState() {
      if (esr != null) {
         esr.updateInChangeState(inChangeState);
         esr.updateUndoableState(undo.canUndo(), undo.canRedo());
         esr.updateSelectionState(selectionState);
         esr.updateCursorState(lineNr, colNr);
      }
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
   //--private--/
   //

   private void updateAfterUndoRedo() {
      updateUndoableState();
      if (isCodeUpdate) {
         if (isInsert) {
            syntax.highlight();
         }
         else {
            highlightAtPos();
         }
      }
      isAddToUndo = true;
   }

   private void updateText() {
      txt.updateTextCopy();
      lineNum.updateLineNumber(txt.text());
      updateInChangeState();
   }

   private void updateInChangeState() {
      if (esr == null) {
         return;
      }
      if (!inChangeState) {
         inChangeState = true;
         esr.updateInChangeState(inChangeState);
      }
   }

   private void updateUndoableState() {
      if (esr == null) {
         return;
      }
      esr.updateUndoableState(undo.canUndo(), undo.canRedo());
   }

   private void updateSelectionState(boolean isSelection) {
      if (esr == null) {
         return;
      }
      if (selectionState != isSelection) {
         selectionState = isSelection;
         esr.updateSelectionState(selectionState);
      }
   }

   private void updateCursorState(int caret) {
      if (esr == null) {
         return;
      }
      int lastNewLine = LinesFinder.lastNewline(txt.text(), caret);
      lineNr = LinesFinder.lineNrAtPos(txt.text(), caret);
      if (lastNewLine == -1) {
         colNr = caret + 1;
      }
      else {
         colNr = caret - lastNewLine;
      }
      esr.updateCursorState(lineNr, colNr);
   }

   private void markUndoBreakpoint(int caret) {
      if (!isAddToUndo) {
         return;
      }
      boolean isBreakpoint;
      if (isInsert) {
         isBreakpoint = caret - chgPos != 1;
      }
      else {
         isBreakpoint = caret - chgPos != 0;
      }
      if (isBreakpoint) {
         undo.markBreakpoint();
      }
   }

   private final DocumentListener docListener = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (!isDocUpdate) {
            return;
         }
         isInsert = true;
         chgPos = de.getOffset();
         updateText();
         change = txt.text().substring(chgPos, chgPos + de.getLength());
         if (isAddToUndo) {
            undo.addEdit(change, chgPos, isInsert);
            updateUndoableState();
            if (isCodeUpdate) {
               EventQueue.invokeLater(() -> highlightAtPos());
               EventQueue.invokeLater(() -> indent.adjustIndent(chgPos));
            }
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         if (!isDocUpdate) {
            return;
         }
         isInsert = false;
         chgPos = de.getOffset();
         change = txt.text().substring(chgPos, chgPos + de.getLength());
         updateText();
         if (isAddToUndo) {
            undo.addEdit(change, chgPos, isInsert);
            updateUndoableState();
            if (isCodeUpdate) {
               EventQueue.invokeLater(() -> highlightAtPos());
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {}
   };

   private final CaretListener caretListener = (CaretEvent ce) -> {
      updateSelectionState(ce.getDot() != ce.getMark());
      updateCursorState(ce.getDot());
      markUndoBreakpoint(ce.getDot());
   };
}
