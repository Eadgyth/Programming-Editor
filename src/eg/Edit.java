package eg;

import java.awt.Toolkit;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import java.io.IOException;

//--Eadgyth--/
import eg.ui.IndentSettingWin;
import eg.utils.FileUtils;
import eg.utils.LinesFinder;
import eg.utils.SystemParams;
import eg.document.EditableDocument;

/**
 * The editing of text by commands in an edit menu or toolbar
 */
public class Edit {

   private static final char TAB_CH = '\t';
   private static final char SPACE_CH = ' ';
   private static final String TAB_STR = "\t";

   private static final Clipboard CLIPBOARD
         = Toolkit.getDefaultToolkit().getSystemClipboard();

   private static final Prefs PREFS = new Prefs();

   private final IndentSettingWin indentSetWin;

   private final FunctionalAction pasteAction
         = new FunctionalAction("Paste", null, e -> pasteText());
   private final FunctionalAction undoAction
         = new FunctionalAction("Undo", null, e -> undo());
   private final FunctionalAction redoAction
         = new FunctionalAction("Redo", null, e -> redo());
   private final FunctionalAction indentAction
         = new FunctionalAction("Increase indentation", null, e -> indent());
   private final FunctionalAction outdentAction
         = new FunctionalAction("Decrease indentation", null, e -> outdent());

   private EditableDocument edtDoc;
   private JTextPane textArea;
   private String indentUnit;
   private int indentLength;
   private boolean useTabs;
   private String indentUnitPref;
   private boolean indentTabPref;

   /**
    * @param useIndentSettingWin  true to use {@link IndentSettingWin}
    * to change the indentation mode, false to use the method
    * {@link #changeIndentationMode(String,boolean)}
    */
   public Edit(boolean useIndentSettingWin) {
      indentUnitPref = PREFS.property("IndentUnit");
      if (indentUnitPref.isEmpty() || !indentUnitPref.trim().isEmpty()
            || indentUnitPref.length() > IndentSettingWin.N_SPACES.length) {

         indentUnitPref = "   ";
      }
      indentTabPref = PREFS.yesNoProperty("IndentTab");
      if (useIndentSettingWin) {
         indentSetWin = new IndentSettingWin(indentUnitPref.length(), indentTabPref);
         indentSetWin.okAct(e -> indentSettingInput());
      }
      else {
         indentSetWin = null;
      }
   }

   /**
    * Sets the <code>EditableDocument</code> that is edited
    *
    * @param edtDoc  the EditableDocument
    */
   public void setDocument(EditableDocument edtDoc) {
      this.edtDoc  = edtDoc;
      this.textArea = edtDoc.textArea();
      setKeyBindings();
      if (edtDoc.indentUnit().isEmpty()) {
         changeIndentationModeImpl(indentUnitPref, indentTabPref);
      }
      else {
         indentUnit = edtDoc.indentUnit();
         indentLength = indentUnit.length();
         useTabs = edtDoc.useTabs();
         if (indentSetWin != null) {
            indentSetWin.update(indentLength, useTabs);
         }
      }
   }

   /**
    * Undoes edits
    */
   public void undo() {
      edtDoc.undo();
   }

   /**
    * Returns the <code>FunctionalAction</code> named "Undo" that is
    * associated with the shortcut 'modifier mask' + Z and added to
    * the text area of an <code>EditableDocument</code>
    *
    * @return  the FunctionalAction that undoes edits
    * @see #undo
    */
   public FunctionalAction undoAction() {
      return undoAction;
   }

   /**
    * Redoes edits
    */
   public void redo() {
      edtDoc.redo();
   }

   /**
    * Returns the <code>FunctionalAction</code> named "Redo" that is
    * associated with the shortcut 'modifier mask' + Y and added to
    * the text area of an <code>EditableDocument</code>
    *
    * @return  the FunctionalAction that redos edits
    * @see #redo
    */
   public FunctionalAction redoAction() {
      return redoAction;
   }

   /**
    * Cuts selected text and stores it in the system clipboard
    */
   public void cut() {
      int start = textArea.getSelectionStart();
      int end = textArea.getSelectionEnd();
      setClipboard();
      edtDoc.remove(start, end - start);
   }

   /**
    * Copies selected text to the system clipboard
    */
   public void setClipboard() {
      String str = textArea.getSelectedText();
      if (str != null) {
         StringSelection strSel = new StringSelection(str);
         CLIPBOARD.setContents(strSel, null);
      }
   }

   /**
    * Pastes text from the clipboard
    */
   public void pasteText() {
      String clipboard = getClipboard();
      if (clipboard.length() == 0) {
         return;
      }
      int pos = textArea.getSelectionStart();
      int end = textArea.getSelectionEnd();
      int length = end - pos;
      edtDoc.replace(pos, length, clipboard, true);
   }

   /**
    * Returns the <code>FunctionalAction</code> named "Paste" that is
    * associated with the shortcut 'modifier mask' + V and added to
    * the text area of an <code>EditableDocument</code>
    *
    * @return  the FunctionalAction that pastes text
    * @see #pasteText
    */
   public FunctionalAction pasteAction() {
      return pasteAction;
   }

   /**
    * Selects the entire text
    */
   public void selectAll() {
      textArea.selectAll();
   }

   /**
    * Selects the current line
    */
   public void selectLine() {
      int pos = textArea.getSelectionStart();
      int lineStart = LinesFinder.lastNewline(edtDoc.text(), pos) + 1;
      int lineEnd = LinesFinder.nextNewline(edtDoc.text(), pos);
      textArea.select(lineStart, lineEnd);
   }

   /**
    * Selects the current line from the beginning of text
    */
   public void selectLineText() {
      int pos = textArea.getSelectionStart();
      int lineStart = LinesFinder.lastNewline(edtDoc.text(), pos) + 1;
      String line = LinesFinder.line(edtDoc.text(), lineStart - 1);
      int lineEnd = LinesFinder.nextNewline(edtDoc.text(), pos);
      int indentEnd = indentEnd(line);
      textArea.select(lineStart + indentEnd, lineEnd);
   }

   /**
    * Selects the current line from the current cursor position
    */
   public void selectLineFromCursor() {
      int pos = textArea.getSelectionStart();
      int lineEnd = LinesFinder.nextNewline(edtDoc.text(), pos);
      textArea.select(pos, lineEnd);
   }

   /**
    * Opens the window to set the indentation mode
    */
   public void openIndentSettingWin() {
      checkIndentSetWinForNull();
      indentSetWin.setVisible(true);
   }

   /**
    * Stores the values for the indentation mode that were lastly
    * selected in <code>IndentSettingWin</code> in <code>Prefs</code>
    */
   public void storeIndentProperties() {
       checkIndentSetWinForNull();
       PREFS.setProperty(Prefs.INDENT_UNIT_KEY, indentUnitPref);
       PREFS.setYesNoProperty(Prefs.INDENT_TAB_KEY, indentTabPref);
    }

   /**
    * Changes the indentation mode
    *
    * @param indentUnit  the indent unit which consists of empty
    * spaces
    * @param indentTab  true to use tabs for indentation, false
    * to use spaces
    */
   public void changeIndentationMode(String indentUnit, boolean indentTab) {
      if (indentSetWin != null) {
         throw new IllegalStateException(
               "Edit uses IndentSettingWin to change the Indentation");
      }
      changeIndentationModeImpl(indentUnit, indentTab);
   }

   /**
    * Increases the indentation
    */
   public void indent()  {
      String sel = textArea.getSelectedText();
      int start = textArea.getSelectionStart();
      if (sel == null) {
         insertIndent(start);
      }
      else {
         edtDoc.enableUndoMerging(true);
         String[] selArr = sel.split("\n");
         if (selArr.length == 1) {
            edtDoc.remove(start, sel.length());
            insertIndent(start);
         }
         else {
            int lineStart = LinesFinder.lastNewline(edtDoc.text(), start) + 1;
            String line = LinesFinder.line(edtDoc.text(), lineStart - 1);
            selArr[0] = line;
            int sum = 0;
            for (String s : selArr) {
               int indentEnd = indentEnd(s) + lineStart;
               if (useTabs) {
                  edtDoc.insertIgnoreSyntax(indentEnd + sum, TAB_STR);
                  sum += s.length() + 2; // 2 for newline + tab
               }
               else {
                  edtDoc.insertIgnoreSyntax(indentEnd + sum, indentUnit);
                  sum += s.length() + indentLength + 1;
               }
            }
         }
         edtDoc.enableUndoMerging(false);
      }
   }

   /**
    * Returns the <code>FunctionalAction</code> named "Increase
    * indentation" that is associated with the shortcut 'Tab' and
    * added to the text area of an <code>EditableDocument</code>
    *
    * @return  the FunctionalAction that increases the indentation
    * @see #indent
    */
   public FunctionalAction indentAction() {
      return indentAction;
   }

   /**
    * Decreases the indentation
    */
   public void outdent() {
      if (edtDoc.textLength() == 0) {
         return;
      }
      String sel = textArea.getSelectedText();
      int pos = textArea.getSelectionStart();
      int lineStart = LinesFinder.lastNewline(edtDoc.text(), pos) + 1;
      String line = LinesFinder.line(edtDoc.text(), lineStart - 1);
      String[] lines = null;
      if (sel != null) {
         lines = sel.split("\n");
      }
      if (lines == null || lines.length == 1) {
         int indentEnd = indentEnd(line) + lineStart;
         if (pos > indentEnd) {
            textArea.setCaretPosition(indentEnd);
         }
         else {
            outdent(lineStart, line);
         }
      }
      else {
         lines[0] = line;
         edtDoc.enableUndoMerging(true);
         outdent(lineStart, lines);
         edtDoc.enableUndoMerging(false);
      }
   }

   /**
    * Returns the <code>FunctionalAction</code> named "Decrease
    * indentation" that is associated with the shortcut
    * 'Shift + Tab' and added to the text area of an
    * <code>EditableDocument</code>
    *
    * @return  the FunctionalAction
    * @see #indent
    */
   public FunctionalAction outdentAction() {
      return outdentAction;
   }

   /**
    * Clears trailing spaces (white spaces and tab characters)
    *
    * @param total  true for the entire text, false for selected
    * text or the current line
    */
   public void clearTrailingSpaces(boolean total) {
      String[] textArr;
      int lineStart = 0;
      if (total) {
         textArr = edtDoc.text().split("\n");
      }
      else {
         String sel = textArea.getSelectedText();
         int pos = textArea.getSelectionStart();
         lineStart = LinesFinder.lastNewline(edtDoc.text(), pos) + 1;
         String lines;
         if (sel == null) {
            lines = LinesFinder.line(edtDoc.text(), lineStart - 1);
         }
         else {
            lines = LinesFinder.lines(edtDoc.text(), lineStart - 1, sel.length());
         }
         textArr = lines.split("\n");
      }
      edtDoc.enableUndoMerging(true);
      for (String s : textArr) {
         int startOfSpaces = startOfTrailingSpaces(s);
         int spacesLength = s.length() - startOfSpaces;
         edtDoc.removeIgnoreSyntax(startOfSpaces + lineStart, spacesLength);
         lineStart += startOfSpaces + 1;
      }
      edtDoc.enableUndoMerging(false);
   }

   //
   //--private--/
   //

   private String getClipboard() {
      String inClipboard = "";
      Transferable transf = CLIPBOARD.getContents(null);
      try {
         if (transf != null
               && transf.isDataFlavorSupported(DataFlavor.stringFlavor)) {

            inClipboard = (String) transf.getTransferData(DataFlavor.stringFlavor);
         }
      }
      catch (IOException | UnsupportedFlavorException e) {
         FileUtils.log(e);
      }
      return inClipboard;
   }

   private void insertIndent(int pos) {
      if (useTabs) {
         edtDoc.insert(pos, TAB_STR);
      }
      else {
         edtDoc.insert(pos, indentUnit);
      }
   }

   private void outdent(int lineStart, String ... lines) {
      int sum = 0;
      for (String s : lines) {
         int indentEnd = indentEnd(s);
         if (s.startsWith(indentUnit, indentEnd - indentLength)) {
            int removePos = sum + lineStart + indentEnd - indentLength;
            edtDoc.remove(removePos, indentLength);
            sum += (s.length() - indentLength) + 1;
         }
         else if (indentEnd > 0) {
            int removePos = 0;
            int lastTab = s.lastIndexOf(TAB_CH, indentEnd - 1);
            if (lastTab != -1) {
               removePos = lastTab;
               indentEnd = lastTab + 1;
               if (lastTab > 0) {
                  removePos -= spacesBeforeNextStop(s, lastTab);
               }
            }
            int removeLength = indentEnd - removePos;
            removePos += (sum + lineStart);
            edtDoc.remove(removePos, removeLength);
            sum += (s.length() - removeLength) + 1;
         }
         else {
            sum += s.length() + 1;
         }
      }
   }

   private int indentEnd(String line) {
      char[] c = line.toCharArray();
      int i;
      for (i = 0; i < c.length; i++) {
         if (c[i] != SPACE_CH && c[i] != TAB_CH) {
            break;
         }
      }
      return i;
   }

   private int spacesBeforeNextStop(String line, int pos) {
      int count = 0;
      for (int i = pos - 1; i >= 0 && line.charAt(i) != TAB_CH; i--) {
         count++;
         if (count == indentLength) {
            count = 0;
            break;
         }
      }
      return count;
   }

   private int startOfTrailingSpaces(String line) {
      char[] c = line.toCharArray();
      int i;
      for (i = c.length - 1; i >= 0; i--) {
         if (c[i] != SPACE_CH && c[i] != TAB_CH) {
            break;
         }
      }
      return i + 1;
   }

   private void indentSettingInput() {
      int n = indentSetWin.indentLength();
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < n; i++) {
         sb.append(" ");
      }
      boolean b = indentSetWin.useTabs();
      indentUnitPref = sb.toString();
      indentTabPref = b;
      changeIndentationModeImpl(indentUnitPref, b);
      indentSetWin.setVisible(false);
   }

   private void changeIndentationModeImpl(String indentUnit, boolean useTabs) {
      this.indentUnit = indentUnit;
      this.useTabs = useTabs;
      indentLength = indentUnit.length();
      edtDoc.setIndentationMode(indentUnit, useTabs);
   }

   private void checkIndentSetWinForNull() {
      if (indentSetWin == null) {
         throw new IllegalStateException("This Edit does not use IndentSettingWin");
      }
   }

   private void setKeyBindings() {
      if (textArea == null) {
        throw new IllegalStateException("No document has been set");
      }
      InputMap im = textArea.getInputMap(JComponent.WHEN_FOCUSED);
      if (im.size() != 0) {
         return;
      }

      ActionMap am = textArea.getActionMap();

      String undoKey = "Z_pressed";
      KeyStroke ksUndo = KeyStroke.getKeyStroke(
            KeyEvent.VK_Z, SystemParams.MODIFIER_MASK);
      im.put(ksUndo, undoKey);
      am.put(undoKey, undoAction);

      String redoKey = "Y_pressed";
      KeyStroke ksRedo = KeyStroke.getKeyStroke(
            KeyEvent.VK_Y, SystemParams.MODIFIER_MASK);
      im.put(ksRedo, redoKey);
      am.put(redoKey, redoAction);

      String pasteKey = "V_pressed";
      KeyStroke ksPaste = KeyStroke.getKeyStroke(
            KeyEvent.VK_V, SystemParams.MODIFIER_MASK);
      im.put(ksPaste, pasteKey);
      am.put(pasteKey, pasteAction);

      String tabKey = "Tab_pressed";
      KeyStroke ksTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
      im.put(ksTab, tabKey);
      am.put(tabKey, indentAction);

      String shiftTabKey = "Shift_Tab_pressed";
      KeyStroke ksShiftTab = KeyStroke.getKeyStroke(
            KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK);
      im.put(ksShiftTab, shiftTabKey);
      am.put(shiftTabKey, outdentAction);
   }
}
