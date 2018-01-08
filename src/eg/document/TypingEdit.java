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

   private UndoableStateReadable usr;
   private SelectionStateReadable ssr;
   private CursorPositionReadable cpr;

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
    * Sets an <code>UndoableStateReadable</code> if none was set before
    *
    * @param usr  an {@link UndoableStateReadable}
    */
   public void setUndoableStateReadable(UndoableStateReadable usr) {
      if (this.usr != null) {
         throw new IllegalStateException(
               "An UndoableStateReadable is already set");
      }
      this.usr = usr;
   }

  /**
    * Sets a <code>SelectionStateReadable</code> if none was set before
    *
    * @param ssr  a {@link SelectionStateReadable}
    */
   public void setSelectionStateReadable(SelectionStateReadable ssr) {
      if (this.ssr != null) {
         throw new IllegalStateException(
               "A SelectionStateReadable is already set");
      }
      this.ssr = ssr;
   }
   
   /**
    * Sets a <code>CursorPositionReadable</code> if none was set before
    *
    * @param cpr  a {@link CursorPositionReadable}
    */
   public void setCursorPositionReadable(CursorPositionReadable cpr) {
      if (this.cpr != null) {
         throw new IllegalStateException(
               "A CursorPositionReadable is already set");
      }
      this.cpr = cpr;
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
         highlightMultipleLines(null, 0);
         enableCodeEditing(true);
      }
   }
   
   /**
    * Gets the text in the document which is updated in the insert- and remove
    * methods of this <code>DocumentListener</code>
    *
    * @return  the text
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
    * Gets the current indent unit
    *
    * @return  the indent unit
    */
   public String getIndentUnit() {
      return autoInd.getIndentUnit();
   }

   /**
    * Highlights the specified <code>section</code> of the document
    * text.<br>
    * If the section does not encompass full lines, its first and last lines
    * are completed for highlighting. If it is only a part of a single line
    * this line is as well completed.
    *
    * @param section  a section of the document text. If null the entire
    * text is used.
    * @param pos  the pos where <code>section</code> starts
    */
   public void highlightMultipleLines(String section, int pos) {
      int posStart = 0;
      if (section != null) {
         section = LinesFinder.allLinesAtPos(text, section, pos);
         posStart = LinesFinder.lastNewline(text, pos) + 1;
      }
      syntax.highlight(text, section, pos, posStart);
   }
   
   /**
    * Reads the current parameters that are set in {@link SelectionStateReadable},
    * {@link UndoableStateReadable} and {@link CursorPositionReadable}
    */
   public void readEditingState() {
      if (cpr != null) {
         cpr.setPosition(lineNr, colNr);
      }
      if (usr != null) {
         usr.setUndoableState(canUndoTmp, canRedoTmp);
      }
      if (ssr != null) {
         ssr.setSelectionState(isSelectionTmp);
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
      notifyUndoableState();
      if (isCodeEditing) {
         if (event.equals(DocumentEvent.EventType.INSERT)) {
            highlightMultipleLines(change, pos);
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
   }
   
   private void highlightLine() {
      int lineStart = LinesFinder.lastNewline(text, pos);
      int lineEnd = LinesFinder.nextNewline(text, pos);
      String toColor = LinesFinder.line(text, lineStart, lineEnd);
      EventQueue.invokeLater(() ->
         syntax.highlight(text, toColor, pos, lineStart + 1));
   }

   private void notifyUndoableState() {
      if (usr == null) {
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
         usr.setUndoableState(canUndoTmp, canRedoTmp);
      }
   }

   private void notifySelectionState(boolean isSelection) {
      if (ssr == null) {
         return;
      }
      if (isSelectionTmp != isSelection) {
         isSelectionTmp = isSelection;
         ssr.setSelectionState(isSelectionTmp);
      }
   }
   
   private void notifyCursorPosition(int caret) {
      int lastNewLine = LinesFinder.lastNewline(text, caret);
      lineNr = LinesFinder.lineNrAtPos(text, caret);
      if (lastNewLine == -1) {
         colNr = caret + 1;
      }
      else {
         colNr = caret - lastNewLine;
      }
      if (cpr != null) {
         cpr.setPosition(lineNr, colNr);
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

   private final DocumentListener docListener = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         if (!isDocListen) {
            return;
         }
         event = de.getType();
         pos = de.getOffset();
         updateText();
         change = text.substring(pos, pos + de.getLength());
         if (isAddToUndo) {
            undo.addEdit(change, pos, true);
            notifyUndoableState();
            if (isCodeEditing) {
               highlightLine();
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
         updateText();
         if (isAddToUndo) {
            undo.addEdit(change, pos, false);
            notifyUndoableState();
            if (isCodeEditing) {
               highlightLine();
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         // nothing to do
      }
   };
   
   private final CaretListener caretListener = (CaretEvent ce) -> {
      notifySelectionState(ce.getDot() != ce.getMark());
      notifyCursorPosition(ce.getDot());
      markUndoBreakpoint(ce.getDot());
   };
}
