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
import eg.utils.*;
import eg.document.EditableDocument;

/**
 * The editing of the document in the selected tab
 */
public class Edit {

   private final static Clipboard CLIPBOARD
         = Toolkit.getDefaultToolkit().getSystemClipboard();

   private static final String[] SPACE_NUMBER
         = { "0", "1", "2", "3", "4", "5", "6" };

   private final Preferences prefs = new Preferences();

   private EditableDocument edtDoc;
   private JTextPane textArea;
   private String indentUnit;
   private int indentLength;

   /**
    * Sets the <code>EditableDocument</code> that is edited and its
    * current indentation unit
    *
    * @param edtDoc  the {@link EditableDocument}
    */
   public void setDocument(EditableDocument edtDoc) {
      this.edtDoc  = edtDoc;
      this.textArea = edtDoc.docTextArea();
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
      edtDoc.insert(pos, clipboard, sel);
   }

   /**
    * Selects the entire text
    */
   public void selectAll() {
      textArea.selectAll();
   }

   /**
    * Sets a new indentation length
    */
   public void setNewIndentUnit() {
      String selectedNumber = Dialogs.comboBoxOpt(
            "Select the number of spaces:",
            "Indentation length",
            SPACE_NUMBER,
            String.valueOf(indentLength), false);

      if (selectedNumber != null) {
         indentLength = Integer.parseInt(selectedNumber);
         indentUnit = "";
         for (int i = 0; i < indentLength; i++) {
            indentUnit += " ";
         }
         edtDoc.setIndentUnit(indentUnit);
         prefs.storePrefs("indentUnit", indentUnit);
      }
   }

   /**
    * Increases the indentation by one indentation unit
    */
   public void indent()  {
      String sel = textArea.getSelectedText();
      int start = textArea.getSelectionStart();
      if (sel == null) {
         edtDoc.insert(start, indentUnit, null);
      }
      else {
         edtDoc.enableMergedUndo(true);
         String[] selArr = sel.split("\n");
         int sum = 0;
         for (String s : selArr) {
            int lineLength = s.length() + indentLength;
            edtDoc.insert(start + sum, indentUnit, null);
            sum += lineLength + 1;
         }
         edtDoc.enableMergedUndo(false);
      }
   }

   /**
    * Reduces the indentation by one indentation unit
    */
   public void outdent() {
      String sel = textArea.getSelectedText();
      int start = textArea.getSelectionStart();
      String text = edtDoc.docText();
      if (sel == null) {
         boolean isAtLineStart
               = LinesFinder.lastNewline(text, start) > start - indentLength;

         if (!isAtLineStart && start >= indentLength) {
            if (indentUnit.equals(text.substring(start - indentLength, start))) {
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
               selArr[0] = text.substring(start, start + selArr[0].length() + diff);
            }
         }
         if (selArr[0].startsWith(" ") && isIndentConsistent(selArr)) {
            edtDoc.enableMergedUndo(true);
            int sum = 0;
            for (String s : selArr) {
               if (s.startsWith(indentUnit)) {
                  edtDoc.remove(start + sum, indentLength, true);
                  sum += (s.length() - indentLength) + 1;
               } else {
                  sum += s.length() + 1;
               }
            }
            edtDoc.enableMergedUndo(false);
         }
      }
   }

   /**
    * Clears trailing spaces
    */
   public void clearTrailingSpaces() {
      String text = edtDoc.docText();
      String[] textArr = text.split("\n");
      int sum = 0;
      for (String s : textArr) {
         int startOfSpaces = startOfTrailingSpaces(s);
         int spacesLength = s.length() - startOfSpaces;
         edtDoc.remove(startOfSpaces + sum, spacesLength, false);
         sum += startOfSpaces + 1;
      }
   }

   //
   //--private--//
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
         FileUtils.logStack(e);
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
         Dialogs.warnMessage("The selected text is not consistently"
               + " indented by at least one indentation length");
      }
      return isConsistent;
   }
}
