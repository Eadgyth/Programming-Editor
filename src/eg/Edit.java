package eg;

import java.awt.EventQueue;
import java.awt.Toolkit;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JTextPane;

import java.io.IOException;

//--Eadgyth--//
import eg.utils.*;
import eg.document.TextDocument;

/**
 * The editing of the document in the selected tab by actions
 * that are invoked in the edit menu/toolbar except the language
 */
public class Edit {

   /* Options for the numbers of white spaces in indentation unit */
   private static final String[] SPACE_NUMBER = { "1", "2", "3", "4", "5", "6" };

   private TextDocument txtDoc;
   private JTextPane textArea;
   private String indentUnit;
   private int indentLength;

   /**
    * Sets the {@code TextDocument} that is edited and its current
    * indentation unit
    *
    * @param txtDoc  the {@link TextDocument} that is edited
    */
   public void setTextDocument(TextDocument txtDoc) {
      this.txtDoc  = txtDoc;
      this.textArea = txtDoc.getTextArea();
      indentUnit = txtDoc.getIndentUnit();
      indentLength = indentUnit.length();
   }

   /**
    * Performs undo action
    */
   public void undo() {
      txtDoc.undo();
   }

   /**
    * Performs redo action
    */
   public void redo() {
      txtDoc.redo();
   }

   /**
    * Cuts selected text and stores it to the system's clipboard
    */
   public void cut() {
      int start = textArea.getSelectionStart();
      int end = textArea.getSelectionEnd();
      setClipboard();
      txtDoc.removeStr(start, end - start);
   }

   /**
    * Copies selected text to the system's clipboard
    */
   public void setClipboard() {
      String str = textArea.getSelectedText();
      if (str != null) {
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringSelection strSel = new StringSelection(str);
         clipboard.setContents(strSel, null);
      }
   }

   /**
    * Pastes text stored in the clipboard and replaces selected text
    */
   public void pasteText() {
      txtDoc.enableTypeEdit(false); 
      String clipboard = getClipboard();
      String sel = textArea.getSelectedText();
      int pos = textArea.getCaretPosition();
      if (sel != null) {
         pos -= sel.length();
         txtDoc.removeStr(pos, sel.length());
      }
      txtDoc.insertStr(pos, clipboard);
      txtDoc.colorSection(clipboard, pos);
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
      String selectedNumber = JOptions.comboBoxRes("Number of spaces:",
            "Indentation length", SPACE_NUMBER,
            String.valueOf(indentLength));
      if (selectedNumber != null) {
         indentLength = Integer.parseInt(selectedNumber);
         indentUnit = "";
         for (int i = 0; i < indentLength; i++) {
            indentUnit += " ";
         }
      }
      txtDoc.changeIndentUnit(indentUnit);
   }

   /**
    * Indents selected text by one indentation unit
    */
   public void indentSelection()  {
      String sel = textArea.getSelectedText();
      if (sel == null) {
         return;
      }

      txtDoc.enableTypeEdit(false);
      String[] selArr = sel.split("\n");
      int start = textArea.getSelectionStart();
      int sum = 0;
      for (int i = 0; i < selArr.length; i++) {
         int lineLength = selArr[i].length() + indentLength;
         txtDoc.insertStr(start + sum, indentUnit);
         sum += lineLength + 1;
      }
      txtDoc.enableTypeEdit(true);
   }

   /**
    * Reduces the indentation of selected text by one indentation unit
    */
   public void outdentSelection() {
      String sel = textArea.getSelectedText();
      if (sel == null) {
         return;
      }

      txtDoc.enableTypeEdit(false);
      int start = textArea.getSelectionStart();
      String firstLine = Finder.lineAtPos(txtDoc.getText(), start);
      String[] selArr = sel.split("\n");
      start -= firstLine.length() - selArr[0].length();
      selArr[0] = firstLine;
      if (selArr[0].startsWith(" ") && isIndentConsistent(selArr)) {
         int sum = 0;
         for (int i = 0; i < selArr.length; i++) {
            if (selArr[i].startsWith(indentUnit)) {
               txtDoc.removeStr(start + sum, indentLength);
               sum += (selArr[i].length() - indentLength) + 1;
            }
            else {
               sum += selArr[i].length() + 1;
            }
         }
      }
      txtDoc.enableTypeEdit(true);
   }

   /**
    * Clears trailing spaces
    */
   public void clearTrailingSpaces() {
      txtDoc.enableTypeEdit(false);
      String text = txtDoc.getText();
      String[] textArr = text.split("\n");
      int sum = 0;
      for (int i = 0; i < textArr.length; i++) {
         int startOfSpaces = startOfTrailingSpaces(textArr[i]);
         int spacesLength = textArr[i].length() - startOfSpaces;
         txtDoc.removeStr(startOfSpaces + sum, spacesLength);
         sum += startOfSpaces + 1;
      }
      txtDoc.enableTypeEdit(true);
   }

   //
   //--private
   //

   private String getClipboard() {
      String inClipboard = "";
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Clipboard clipboard = toolkit.getSystemClipboard();
      DataFlavor flavor = DataFlavor.stringFlavor;
      try {
         inClipboard = (String) clipboard.getData(flavor);
      }
      catch (UnsupportedFlavorException | IOException e) {
         FileUtils.logStack(e);
      }
      return inClipboard;
   }

   private int startOfTrailingSpaces(String line) {
      char[] c = line.toCharArray();
      int i = 0;
      for (i = c.length - 1; i >= 0; i--) {
         if (c[i] != ' ') {
            break;
         }
      }
      return i + 1;
   }

   private boolean isIndentConsistent(String[] textArr) {
      boolean isConsistent = true;
      for (int i = 0; i < textArr.length; i++) {
         if (!textArr[i].matches("[\\s]+") && !textArr[i].startsWith(indentUnit)) {
            isConsistent = false;
            break;
         }
      }
      if (!isConsistent) {
         JOptions.warnMessage("The indentation is not consistent.");
      }
      return isConsistent;
   }
}
