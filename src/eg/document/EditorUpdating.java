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
 * The updating of the editor during changing the text
 */
public class EditorUpdating {

   /**
    * Value for syntax highlighting of the entire text */
   public static final int ALL_TEXT = 0;
   /**
    * Value for syntax highlighting after a possibly
    * multiline insertion or replacement */
   public static final int INSERT = 1;
   /**
    * Value for omitting syntax highlighting */
   public static final int OMIT = 3;

   private final EditableText txt;
   private final LineNumbers lineNum;
   private final SyntaxHighlighter syntax;
   private final Indentation indent;
   private final UndoEditing undo;

   private boolean isUpdate = true;
   private boolean isCodeEditing = false;
   private boolean isUndoRedo = false;

   private boolean isInsert;
   private int chgPos = 0;
   private String change = "";

   private EditingStateReadable esr;
   private boolean changedState = false;
   private boolean selectionState = false;
   private int lineNr = 1;
   private int colNr = 1;

   /**
    * @param txt  the reference to EditableText
    * @param undo  the reference to UndoEditing
    * @param lineNum  the reference to LineNumbers
    * @param indent  the reference to Indentation
    */
   public EditorUpdating(EditableText txt, UndoEditing undo,
         LineNumbers lineNum, Indentation indent) {

      this.txt = txt;
      this.undo = undo;
      this.lineNum = lineNum;
      this.indent = indent;
      syntax = new SyntaxHighlighter(txt);
      txt.addDocumentListener(docListener);
      txt.textArea().addCaretListener(caretListener);
   }

   /**
    * Sets an <code>EditingStateReadable</code>
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
    * Disables or re-enables the updating provided that no
    * editing has happened before. Disabled updating is used
    * for loading a file which isn't undoable.
    *
    * @param b  true to enable, false to disable
    */
   public void disableUpdating(boolean b) {
      if (undo.canUndo() || undo.canRedo() || !txt.text().isEmpty()) {
         throw new IllegalStateException(
               "Cannot disable updating because the document is being"
               + " edited already.");
      }
      isUpdate = !b;
      if (!b) {
         updateText();
      }
   }

   /**
    * Sets the editing mode which enables source code editing
    * (syntax highlighting, auto indent) if the language in the
    * specified <code>CurrentLanguage</code> is not 'normal text'.
    *
    * @param cl  the CurrentLanguage
    */
   public void setEditingMode(CurrentLanguage cl) {
      syntax.setHighlighter(cl.createHighlighter());
      indent.enableCurlyBracketMode(cl.curlyBracketMode());
      isCodeEditing = cl.lang() != Languages.NORMAL_TEXT;
   }

   /**
    * Makes the specified <code>TextChange</code> and updates
    * (or omits) syntax highlighting if source code editing is
    * enabled
    *
    * @param tc  the TextChange
    * @param editValue  the value that indicates the type of
    * syntax highlighting required: {@link #ALL_TEXT},
    * {@link #INSERT} or {@link #OMIT}.
    */
   public void editText(TextChange tc, int editValue) {
      boolean isCodeEditingHelper = isCodeEditing;
      isCodeEditing = false;
      tc.edit();
      if (isCodeEditingHelper && editValue != EditorUpdating.OMIT) {
         if (editValue == EditorUpdating.ALL_TEXT && !txt.text().isEmpty()) {
            syntax.highlight();
         }
         else if (editValue == EditorUpdating.INSERT && isInsert) {
            syntax.highlight(change, chgPos);
         }
      }
      isCodeEditing = isCodeEditingHelper;
   }

   /**
    * Makes the specified <code>TextChange</code> that undoes
    * or redoes edits and updates the undoable/redoable state
    * as well as syntax highlighting if source code editing
    * is enabled
    *
    * @param undoRedo  the TextChange
    */
   public void updateUndoRedo(TextChange undoRedo) {
      isUndoRedo = true;
      undoRedo.edit();
      updateUndoableState();
      if (isCodeEditing) {
         if (isInsert) {
            //
            // compromise; defined quite arbitrarily
            if (txt.text().length() > 90000) {
               EventQueue.invokeLater(syntax::highlight);
            }
            else {
               syntax.highlight();
            }
         }
         else {
            syntax.highlight(chgPos, false);
         }
      }
      isUndoRedo = false;
   }

   /**
    * Resets the state which indicates that the text has been
    * changed
    */
   public void resetChangedState() {
      changedState = false;
      esr.updateChangedState(changedState);
   }

   /**
    * Reads the current editing state by calling the methods
    * defined in <code>EditingStateReadable</code>
    *
    * @see EditingStateReadable
    */
   public void readEditingState() {
      if (esr != null) {
         esr.updateChangedState(changedState);
         esr.updateUndoableState(undo.canUndo(), undo.canRedo());
         esr.updateSelectionState(selectionState);
         esr.updateCursorState(lineNr, colNr);
      }
   }

   //
   //--private--/
   //

   private void updateText() {
      txt.updateTextCopy();
      lineNum.updateLineNumber(txt.text());
      updateChangedState();
   }

   private void updateChangedState() {
      if (esr == null) {
         return;
      }
      if (!changedState) {
         changedState = true;
         esr.updateChangedState(changedState);
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

   private void markUndoBreakpoint(int dot) {
      if (isUndoRedo) {
         return;
      }
      boolean isBreakpoint;
      if (isInsert) {
         isBreakpoint = dot - chgPos != 1;
      }
      else {
         isBreakpoint = dot - chgPos != 0;
      }
      if (isBreakpoint) {
         undo.markBreakpoint();
      }
   }

   private final DocumentListener docListener = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (!isUpdate) {
            return;
         }
         isInsert = true;
         chgPos = de.getOffset();
         updateText();
         change = txt.text().substring(chgPos, chgPos + de.getLength());
         if (!isUndoRedo) {
            undo.addEdit(change, chgPos, isInsert);
            updateUndoableState();
            if (isCodeEditing) {
               boolean isNewline = change.equals("\n");
               EventQueue.invokeLater(() -> syntax.highlight(chgPos, isNewline));
               EventQueue.invokeLater(() -> indent.adjustIndent(chgPos));
            }
         }
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         if (!isUpdate) {
            return;
         }
         isInsert = false;
         chgPos = de.getOffset();
         change = txt.text().substring(chgPos, chgPos + de.getLength());
         updateText();
         if (!isUndoRedo) {
            undo.addEdit(change, chgPos, isInsert);
            updateUndoableState();
            if (isCodeEditing) {
               EventQueue.invokeLater(() -> syntax.highlight(chgPos, false));
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         // not used
      }
   };

   private final CaretListener caretListener = (CaretEvent ce) -> {
      updateSelectionState(ce.getDot() != ce.getMark());
      updateCursorState(ce.getDot());
      markUndoBreakpoint(ce.getDot());
   };
}
