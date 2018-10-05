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

/**
 * The mediation between the editing of the document by typing in,
 * removing, pasting or replacing text and the actions that happen
 * in response.
 * <p>
 * Created in {@link EditableDocument}
 */
public class TypingEdit {

   private final StyledText txt;
   private final LineNumbers lineNum;
   private final SyntaxHighlighter syntax;
   private final Indentation indent;
   private final UndoEdit undo;

   private boolean isDocUpdate = true;
   private boolean isCodeEditing = false;
   private boolean isInsert;
   private int chgPos = 0;
   private String change = "";
   private boolean isAddToUndo = true;
   private EditingStateReadable esr;
   private boolean inChangeState = false;
   private boolean selectionState = false;
   private boolean canUndoState = false;
   private boolean canRedoState = false;
   private int lineNr = 1;
   private int colNr = 1;

   /**
    * @param txt  the reference to {@link StyledText}
    * @param lineNum  the reference to {@link LineNumbers}
    */
   public TypingEdit(StyledText txt, LineNumbers lineNum) {
      this.txt = txt;
      this.lineNum = lineNum;
      syntax = new SyntaxHighlighter(txt);
      indent = new Indentation(txt);
      undo = new UndoEdit(txt);
      txt.addDocumentListener(docListener);
      txt.textArea().addCaretListener(caretListener);
   }

   /**
    * Sets an <code>EditingStateReadable</code>
    *
    * @param esr  an {@link EditingStateReadable}
    */
   public void setEditingStateReadable(EditingStateReadable esr) {
      if (this.esr != null) {
         throw new IllegalStateException(
               "An EditingStateReadable is already set");
      }
      this.esr = esr;
   }

   /**
    * Sets the boolean that controls if the update methods in this
    * <code>DocumentListener</code> are enabled or disabled
    *
    * @param b  the boolean value. True to enable, false to disable
    */
   public void enableDocUpdate(boolean b) {
      isDocUpdate = b;
      if (b) {
         updateText();
         txt.textArea().setCaretPosition(0);
      }
   }

   /**
    * Sets the editing mode that depends on the specified language
    *
    * @param lang  the language which is a constant in
    * {@link Languages}
    */
   public void setEditingMode(Languages lang) {
      if (lang == Languages.NORMAL_TEXT) {
         txt.resetAttributes();
         isCodeEditing = false;
      }
      else {
         Highlighter hl = HighlighterSelector.createHighlighter(lang);
         syntax.setHighlighter(hl);
         syntax.highlight();
         isCodeEditing = true;
      }
   }

   /**
    * Resets the state which indicates that document text has been
    * changed
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
   public String getIndentUnit() {
      return indent.getIndentUnit();
   }

   /**
    * Sets the boolean that disables/re-enables the addition of
    * breakpoint that define undoable units.
    *
    * @param b  the boolean value; true to disable, false to re-enable
    * @see UndoEdit #disableBreakpointAdding(boolean)
    */
   public void disableBreakpointAdding(boolean b) {
      undo.disableBreakpointAdding(b);
   }

   /**
    * Inserts the specified string at the specified position
    *
    * @param pos  the position
    * @param toInsert  the string
    */
   public void insert(int pos, String toInsert) {
      boolean isCodeEditingHelper = isCodeEditing;
      isCodeEditing = false;
      txt.insert(pos, toInsert);
      if (isCodeEditingHelper) {
         highlightInsertion();
      }
      isCodeEditing = isCodeEditingHelper;
   }

   /**
    * Replaces a section of the document with the specified string
    *
    * @param pos  the position where the section to be replaced starts
    * @param length  the length of the section
    * @param toInsert  the String to insert
    */
   public void replace(int pos, int length, String toInsert) {
      boolean isCodeEditingHelper = isCodeEditing;
      isCodeEditing = false;  
      if (length != 0) {
         txt.remove(pos, length);
         if (isCodeEditingHelper) {
            highlightAtPos();
         }
      }
      txt.insert(pos, toInsert);
      if (isCodeEditingHelper) {
         highlightInsertion();
      }
      isCodeEditing = isCodeEditingHelper;
   }

   /**
    * Removes a section from the document
    *
    * @param pos  the position where the section starts
    * @param length  the length of the section
    * @param useHighlighting  if syntax highlighting of the line that
    * contains the position is done
    */
   public void remove(int pos, int length, boolean useHighlighting) {
      boolean isCodeEditingHelper = isCodeEditing;
      isCodeEditing = useHighlighting;
      txt.remove(pos, length);
      if (isCodeEditingHelper) {
         highlightAtPos();
      }
      isCodeEditing = isCodeEditingHelper;
   }

   /**
    * Reads the current editing state by calling the methods defined in
    * {@link EditingStateReadable}
    */
   public void readEditingState() {
      if (esr != null) {
         esr.updateInChangeState(inChangeState);
         esr.updateUndoableState(canUndoState, canRedoState);
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
      if (isCodeEditing) {
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
      txt.updateText();
      lineNum.updateLineNumber(txt.text());
      updateInChangeState();
   }
   
   private void highlightAtPos() {
      syntax.highlight(chgPos);
      if (isInsert && change.equals("\n")) {
         syntax.highlight(chgPos + 1);
      }
   }

   private void highlightInsertion() {
      if (change.length() > 0) {
         syntax.highlight(change, chgPos);
      }
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
      boolean isUndoableChange = canUndoState != undo.canUndo();
      boolean isRedoableChange = canRedoState != undo.canRedo();
      if (isUndoableChange) {
         canUndoState = undo.canUndo();
      }
      if (isRedoableChange) {
         canRedoState = undo.canRedo();
      }
      if (isUndoableChange || isRedoableChange) {
         esr.updateUndoableState(canUndoState, canRedoState);
      }
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
            if (isCodeEditing) {
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
            if (isCodeEditing) {
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
