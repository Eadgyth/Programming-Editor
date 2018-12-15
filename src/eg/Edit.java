package eg;

import java.awt.Toolkit;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JTextPane;

import java.io.IOException;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.utils.LinesFinder;
import eg.document.EditableDocument;

/**
 * The editing of text in an <code>EditableDocument</code>
 */
public class Edit {

   private final static Clipboard CLIPBOARD
         = Toolkit.getDefaultToolkit().getSystemClipboard();

   private static final String[] SPACE_NUMBER
         = { "0", "1", "2", "3", "4", "5", "6" };

   private EditableDocument edtDoc;
   private JTextPane textArea;
   private String indentUnit;
   private int indentLength;
   private String indentUnitSelection;

   public Edit(){}

   /**
    * @param indentUnitSelection  the initial value for the selection
    * of the indent unit. The value can only be changed in
    * {@link #setIndentUnit}
    */
   public Edit(String indentUnitSelection) {
      this.indentUnitSelection = indentUnitSelection;
   }

   /**
    * Sets the <code>EditableDocument</code> that is edited and assigns
    * its indent unit
    *
    * @param edtDoc  the EditableDocument
    */
   public void setDocument(EditableDocument edtDoc) {
      this.edtDoc  = edtDoc;
      this.textArea = edtDoc.textArea();
      indentUnit = edtDoc.currIndentUnit();
      indentLength = indentUnit.length();
   }

   /**
    * Performs an undo action
    */
   public void undo() {
      edtDoc.undo();
   }

   /**
    * Performs a redo action
    */
   public void redo() {
      edtDoc.redo();
   }

   /**
    * Cuts selected text and stores it in the system's clipboard
    */
   public void cut() {
      int start = textArea.getSelectionStart();
      int end = textArea.getSelectionEnd();
      setClipboard();
      edtDoc.remove(start, end - start, true);
   }

   /**
    * Copies selected text to the system's clipboard
    */
   public void setClipboard() {
      String str = textArea.getSelectedText();
      if (str != null) {
         StringSelection strSel = new StringSelection(str);
         CLIPBOARD.setContents(strSel, null);
      }
   }

   /**
    * Pastes text stored in the clipboard and replaces selected text
    */
   public void pasteText() {
      String clipboard = getClipboard();
      if (clipboard.length() == 0) {
         return;
      }
      String sel = textArea.getSelectedText();
      int pos = textArea.getSelectionStart();
      int end = textArea.getSelectionEnd();
      int length = end - pos;
      edtDoc.replace(pos, length, clipboard);
   }

   /**
    * Selects the entire text
    */
   public void selectAll() {
      textArea.selectAll();
   }

   /**
    * Sets the indent length using a selection dialog
    */
   public void setIndentUnit() {
      String number = Dialogs.comboBoxOpt(
            "Select the number of spaces:",
            "Indent length",
            SPACE_NUMBER,
            String.valueOf(indentLength),
            false);

      if (number != null) {
         indentLength = Integer.parseInt(number);
         indentUnit = "";
         for (int i = 0; i < indentLength; i++) {
            indentUnit += " ";
         }
         edtDoc.setIndentUnit(indentUnit);
         indentUnitSelection = indentUnit;
      }
   }

   /**
    * Gets the lastly selected indent unit
    *
    * @see #setIndentUnit
    * @return  the indent unit
    */
   public String indentUnitSelection() {
      return indentUnitSelection;
   }

   /**
    * Increases the indentation by one indent unit
    */
   public void indent()  {
      String sel = textArea.getSelectedText();
      int start = textArea.getSelectionStart();
      if (sel == null) {
         edtDoc.insert(start, indentUnit);
      }
      else {
         edtDoc.enableUndoMerging(true);
         String[] selArr = sel.split("\n");
         int sum = 0;
         for (String s : selArr) {
            int lineLength = s.length() + indentLength;
            edtDoc.insert(start + sum, indentUnit);
            sum += lineLength + 1;
         }
         edtDoc.enableUndoMerging(false);
      }
   }

   /**
    * Reduces the indentation by one indent unit
    */
   public void outdent() {
      String sel = textArea.getSelectedText();
      int start = textArea.getSelectionStart();
      if (sel == null) {
         boolean isAtLineStart
               = LinesFinder.lastNewline(
                     edtDoc.text(), start) > start - indentLength;

         if (!isAtLineStart && start >= indentLength) {
            if (indentUnit.equals(edtDoc.text().substring(
                  start - indentLength, start))) {

               edtDoc.remove(start - indentLength, indentLength, true);
            }
            else {
               textArea.setCaretPosition(start - indentLength);
            }
         }
      }
      else {
         String[] selArr = sel.split("\n");
         if (!selArr[0].startsWith(indentUnit)) {
            int countSpaces = 0;
            while (selArr[0].charAt(countSpaces) == ' ') {
               countSpaces++;
            }
            int diff = indentLength - countSpaces;
            start -= diff;
            if (start >= 0) {
               selArr[0] = edtDoc.text().substring(
                     start, start + selArr[0].length() + diff);
            }
         }
         if (selArr[0].startsWith(" ") && isIndentConsistent(selArr)) {
            edtDoc.enableUndoMerging(true);
            int sum = 0;
            for (String s : selArr) {
               if (s.startsWith(indentUnit)) {
                  edtDoc.remove(start + sum, indentLength, true);
                  sum += (s.length() - indentLength) + 1;
               } else {
                  sum += s.length() + 1;
               }
            }
            edtDoc.enableUndoMerging(false);
         }
      }
   }

   /**
    * Clears trailing spaces
    */
   public void clearTrailingSpaces() {
      String[] textArr = edtDoc.text().split("\n");
      int sum = 0;
      for (String s : textArr) {
         int startOfSpaces = startOfTrailingSpaces(s);
         int spacesLength = s.length() - startOfSpaces;
         edtDoc.remove(startOfSpaces + sum, spacesLength, false);
         sum += startOfSpaces + 1;
      }
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

   private int startOfTrailingSpaces(String line) {
      char[] c = line.toCharArray();
      int i;
      for (i = c.length - 1; i >= 0; i--) {
         if (c[i] != ' ') {
            break;
         }
      }
      return i + 1;
   }

   private boolean isIndentConsistent(String[] textArr) {
      boolean isConsistent = true;
      for (String s : textArr) {
         if (!s.startsWith(indentUnit)
                && s.length() > 0 && !s.matches("[\\s]+")) {

            isConsistent = false;
            break;
         }
      }
      if (!isConsistent) {
         Dialogs.warnMessage(
               "The selected text is not consistently"
               + " indented by at least one indentation length");
      }
      return isConsistent;
   }
}
