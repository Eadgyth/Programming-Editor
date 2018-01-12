package eg.document;

import java.awt.EventQueue;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

//--Eadgyth--//
import eg.Languages;
import eg.utils.LinesFinder;
import eg.syntax.*;

/**
 * Mediates between the editing of the text document and the actions that
 * happen in response.
 * <p> Created in {@link FileDocument}
 */
public class TypingEdit {

   private final TextDocument textDoc;
   private final LineNumberDocument lineNrDoc;
   private final SyntaxHighlighter syntax;
   private final AutoIndent autoInd;
   private final UndoEdit undo;

   private EditingStateReadable esr;
   private boolean isDocListen = true;
   private boolean isAddToUndo = true;
   private boolean isCodeEditing = false;
   private DocumentEvent.EventType event;
   private int chgPos = 0;
   private String text = "";
   private String change = "";
   private boolean isInChange = false;
   private boolean isSelectionTmp = false;
   private boolean canUndoTmp = false;
   private boolean canRedoTmp = false;
   private int lineNr = 1;
   private int colNr = 1;

   /**
    * @param textDoc  the reference to {@link TextDocument}
    * @param lineNrDoc  the reference to {@link LineNumberDocument}
    */
   public TypingEdit(TextDocument textDoc, LineNumberDocument lineNrDoc) {
      this.textDoc = textDoc;
      this.lineNrDoc = lineNrDoc;
      syntax = new SyntaxHighlighter(textDoc);
      autoInd = new AutoIndent(textDoc);
      undo = new UndoEdit(textDoc);
      textDoc.addDocumentListener(docListener);
      textDoc.textArea().addCaretListener(caretListener);
   }
   
   /**
    * Sets an <code>EditingStateReadable</code>
    *
    * @param esr  a {@link EditingStateReadable}
    */
   public void setEditingStateReadable(EditingStateReadable esr) {
       if (this.esr != null) {
         throw new IllegalStateException(
               "A SavedStateReadable is already set");
      }
      this.esr = esr;
   }

   /**
    * Sets the boolean that specifies if the update methods in this
    * <code>DocumentListener</code> are enabled or disabled
    *
    * @param b  the boolean value which is true to enable
    */
   public void enableDocListen(boolean b) {
      isDocListen = b;
      if (b) {
         updateText();
         textDoc.textArea().setCaretPosition(0);
      }
   }

   /**
    * Sets the boolean that specified if actions in responce to the
    * editing of source code are enabled or disabled
    *
    * @param b  the boolean value which is true to enable
    */
   public void enableCodeEditing(boolean b) {
      isCodeEditing = b;
   }

   /**
    * Sets the editing mode that depends on the specified language
    *
    * @param lang  a language in {@link Languages}
    */
   public void setEditingMode(Languages lang) {
      if (lang == Languages.NORMAL_TEXT) {
         enableCodeEditing(false);
         textDoc.setAllCharAttrBlack();
      }
      else {
         Highlighter hl = null;
         switch(lang) {
            case JAVA:
               hl = new JavaHighlighter();
               break;
            case HTML:
               hl = new HTMLHighlighter();
               break;
            case JAVASCRIPT:
               hl = new JavascriptHighlighter();
               break;
            case CSS:
               hl = new CSSHighlighter();
               break;
            case PERL:
               hl = new PerlHighlighter();
               break;
         }
         syntax.setHighlighter(hl);
         highlightSection(null);
         enableCodeEditing(true);
      }
   }
   
   /**
    * Gets the text in the document which is updated in the insert- and
    * remove methods of this <code>DocumentListener</code>
    *
    * @return  the text
    */
   public String getText() {
      return text;
   }
   
   /**
    * Resets this flag that indicates that text is being changed and
    * calls {@link EditingStateReadable#setInChangeState(boolean)}
    */
   public void resetInChangeState() {
      isInChange = false;
      esr.setInChangeState(isInChange);
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
    * Gets the current indent unit
    *
    * @return  the indent unit
    */
   public String getIndentUnit() {
      return autoInd.getIndentUnit();
   }

   /**
    * Highlights the specified <code>section</code> of the document text
    * <p>
    * If the <code>section</code> does not encompass full lines, its first
    * and last lines are completed for highlighting. If it is only a part
    * of a single line this line is as well completed.
    *
    * @param section  the section. Null to highlight the entire text
    */
   public void highlightSection(String section) {
      int sectionStart = 0;
      if (section != null) {
         section = LinesFinder.allLinesAtPos(text, section, chgPos);
         sectionStart = LinesFinder.lastNewline(text, chgPos) + 1;
      }
      else {
         section = text;
      }
      syntax.highlight(text, section, chgPos, sectionStart);
   }
   
   /**
    * Reads the current editing state by calling the methods defined in
    * {@link EditingStateReadable}
    */
   public void readEditingState() {
      if (esr != null) {
         esr.setInChangeState(isInChange);
         esr.setCursorPosition(lineNr, colNr);
         esr.setUndoableState(canUndoTmp, canRedoTmp);
         esr.setSelectionState(isSelectionTmp);
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
   //--private--
   //

   private void updateAfterUndoRedo() {
      outputUndoableState();
      if (isCodeEditing) {
         if (event.equals(DocumentEvent.EventType.INSERT)) {
            highlightSection(change);
         }
         else if (event.equals(DocumentEvent.EventType.REMOVE)) {
            highlightLine();
         }
      }
      isAddToUndo = true;
   }
   
   private void updateText() {
      text = textDoc.docText();
      lineNrDoc.updateLineNumber(text);
      outputInChangeState();
   }
   
   private void highlightLine() {
      int lineStart = LinesFinder.lastNewline(text, chgPos);
      int lineEnd = LinesFinder.nextNewline(text, chgPos);
      String toColor = LinesFinder.line(text, lineStart, lineEnd);
      EventQueue.invokeLater(() ->
         syntax.highlight(text, toColor, chgPos, lineStart + 1));
   }
   
   private void outputInChangeState() {
      if (esr == null) {
         return;
      }
      if (!isInChange) {
         isInChange = true;
         esr.setInChangeState(isInChange);
      }
   }

   private void outputUndoableState() {
      if (esr == null) {
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
         esr.setUndoableState(canUndoTmp, canRedoTmp);
      }
   }

   private void outputSelectionState(boolean isSelection) {
      if (esr == null) {
         return;
      }
      if (isSelectionTmp != isSelection) {
         isSelectionTmp = isSelection;
         esr.setSelectionState(isSelectionTmp);
      }
   }
   
   private void outputCursorPosition(int caret) {
      if (esr == null) {
         return;
      }
      int lastNewLine = LinesFinder.lastNewline(text, caret);
      lineNr = LinesFinder.lineNrAtPos(text, caret);
      if (lastNewLine == -1) {
         colNr = caret + 1;
      }
      else {
         colNr = caret - lastNewLine;
      }
      esr.setCursorPosition(lineNr, colNr);
   }

   private void markUndoBreakpoint(int caret) {
      if (!isAddToUndo || event == null) {
         return;
      }
      if (caret > 0) {
         boolean isBreakpoint = false;
         if (event.equals(DocumentEvent.EventType.INSERT)) {
            isBreakpoint = caret - chgPos != 1;
         }
         else if (event.equals(DocumentEvent.EventType.REMOVE)) {
            isBreakpoint = caret - chgPos != 0;
         }
         if (isBreakpoint) {
            undo.markBreakpoint();
         }
      }
   }

   private final DocumentListener docListener = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (!isDocListen) {
            return;
         }
         event = de.getType();
         chgPos = de.getOffset();
         updateText();
         change = text.substring(chgPos, chgPos + de.getLength());
         if (isAddToUndo) {
            undo.addEdit(change, chgPos, true);
            outputUndoableState();
            if (isCodeEditing) {
               highlightLine();
               EventQueue.invokeLater(() -> {
                  autoInd.indent(text, chgPos);
                  autoInd.closedBracketIndent(text, chgPos);
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
         chgPos = de.getOffset();
         change = text.substring(chgPos, chgPos + de.getLength());
         updateText();
         if (isAddToUndo) {
            undo.addEdit(change, chgPos, false);
            outputUndoableState();
            if (isCodeEditing) {
               highlightLine();
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {}
   };
   
   private final CaretListener caretListener = (CaretEvent ce) -> {
      outputSelectionState(ce.getDot() != ce.getMark());
      outputCursorPosition(ce.getDot());
      markUndoBreakpoint(ce.getDot());
   };
}
