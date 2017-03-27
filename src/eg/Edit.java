package eg;

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
    * performs redo action
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
    * Pastes text stored in the clipboard and replaces selected
    * text
    */
   public void pasteText() {
      txtDoc.enableTypeEdit(false);

      String clipboard = getClipboard();
      String selection = textArea.getSelectedText();
      int pos = textArea.getCaretPosition();

      if (selection == null) {
         txtDoc.insertStr(pos, clipboard);
         textArea.setCaretPosition(pos + clipboard.length());
      }
      else {
         txtDoc.removeStr(pos - selection.length(), selection.length());
         txtDoc.insertStr(pos - selection.length(), clipboard);
         textArea.setCaretPosition(pos - selection.length() + clipboard.length());
      }
      txtDoc.colorAll();
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
      if (selectedNumber != null) {    // if not cancelled
         indentLength = Integer.parseInt(selectedNumber);
         indentUnit = "";
         for (int i = 0; i < indentLength; i++) {
            indentUnit += " ";
         }
      }
      txtDoc.changeIndentUnit(indentUnit);
   }

   /**
    * Indents all lines of selected text by one indentation unit
    */
   public void indentSelection()  {
      String sel = textArea.getSelectedText();
      if (sel == null) {
         return;
      }
      txtDoc.enableTypeEdit(false);
      String[] selSplit = sel.replaceAll("\\r", "").split("\\n");
      int start = textArea.getSelectionStart();
      int[] startOfLines = Finder.startOfLines(selSplit);

      for (int i = 0; i < selSplit.length; i++) {
         txtDoc.insertStr(start + startOfLines[i]
               + i * (indentLength), indentUnit);
      }
      textArea.select(start, (sel.length()
            + selSplit.length * indentLength) + start);
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
      int start = textArea.getSelectionStart();
      String startingLine = Finder.currLine(txtDoc.getText(), start);
      if (!startingLine.startsWith(indentUnit)) {
         return;
      }
      txtDoc.enableTypeEdit(false);
      String[] selSplit = sel.split("\n");
      //
      // count spaces at the beginning of selection
      int countSpaces = 0;
      for (int i = 0; i < selSplit[0].length(); i++) {
         if (selSplit[0].substring(i, i + 1).equals(" ")) {
            countSpaces++;
         }
         else {
            break;
         }
      }
      int[] startOfLines = Finder.startOfLines(selSplit);
      //
      // add an indent unit to empty lines or lines with too few spaces */
      for (int i = 0; i < selSplit.length; i++) {
         if (selSplit[i].length() == 0) {
            txtDoc.insertStr(start + startOfLines[i], indentUnit);
            for (int j = i + 1; j < selSplit.length; j++) {
               startOfLines[j] += indentLength;
            }
         }
         if (selSplit[i].matches("[\\s]+")) {
            int length = selSplit[i].length();
            if (length < indentLength) {
               txtDoc.insertStr(start + startOfLines[i], indentUnit);
               for (int j = i + 1; j < selSplit.length; j++) {
                  startOfLines[j] += indentLength;
               }
            }
         }
      }
      //
      // renew selection
      int startOfFirstLine = txtDoc.getText().lastIndexOf("\n", start);
      int startUpdate = start - indentLength + countSpaces;
      if (countSpaces != indentLength && startUpdate > startOfFirstLine) {
         textArea.select(startUpdate, textArea.getSelectionEnd());
         sel = textArea.getSelectedText();
         selSplit = sel.split("\\n");
         start = textArea.getSelectionStart();
      }
      startOfLines = Finder.startOfLines(selSplit);
      for (int i = 0; i < selSplit.length; i++) {
         txtDoc.removeStr(start + startOfLines[i] - i * indentLength,
               indentLength);
      }
      txtDoc.enableTypeEdit(true);
   }

   /**
    * Clears trailing spaces
    */
   public void clearTrailingSpaces() {
      txtDoc.enableTypeEdit(false);
      String allTxt = txtDoc.getText();
      String[] allTextArr = allTxt.split("\n");
      int sum = 0;
      for (int i = 0; i < allTextArr.length; i++) {
         int startOfSpaces = startOfTrailingSpaces(allTextArr[i]);
         int spacesLength = allTextArr[i].length() - startOfSpaces;
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
      for (i = c.length -1; i >= 0; i--) {
         if (c[i] != ' ') {
            break;
         }
      }
      return i + 1;
   }
}
