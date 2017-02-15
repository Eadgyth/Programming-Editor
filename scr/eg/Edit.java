package eg;

import java.awt.Toolkit;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;

//--Eadgyth--//
import eg.utils.JOptions;
import eg.utils.Finder;
import eg.utils.FileUtils;
import eg.ui.EditArea;
import eg.document.TextDocument;

/**
 * The editing of the document in the selected tab by actions
 * that are invoked in the edit menu/toolbar except the language
 */ 
public class Edit {

   /* Options for the numbers of white spaces in indentation unit */
   private static final String[] SPACE_NUMBER = { "1", "2", "3", "4", "5", "6" };

   private EditArea editArea;
   private TextDocument txtDoc;
   private String indentUnit;
   private int indentLength;

   /**
    * @param txtDoc  the {@link TextDocument} object
    * @param editArea  the {@link EditArea} object
    */
   public void setTextDocument(TextDocument txtDoc, EditArea editArea) {
      this.txtDoc  = txtDoc;
      this.editArea = editArea;
      indentUnit = txtDoc.getIndentUnit();
      indentLength = indentUnit.length();
   }

   public void selectAll() {
      txtDoc.selectAll();
   }
   
   /**
    * Cuts selected text and stores it to the system's clipboard
    */
   public void cut() {
      int start = txtDoc.selectionStart();
      int end = txtDoc.selectionEnd();
      setClipboard();
      txtDoc.removeStr(start, end - start);
   }

   /**
    * Copies selected text to the system's clipboard
    */
   public void setClipboard() {
      String str = txtDoc.selectedText();
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
      String selection = txtDoc.selectedText();
      int pos = txtDoc.caretPos();

      if (selection == null) {
         txtDoc.insertStr(pos, clipboard);
         txtDoc.setCaretPos(pos + clipboard.length());
      }
      else {
         txtDoc.removeStr(pos - selection.length(), selection.length());
         txtDoc.insertStr(pos - selection.length(), clipboard);
         txtDoc.setCaretPos(pos - selection.length() + clipboard.length());
      }

      txtDoc.colorAll();
   }

  /**
   * Sets a new indentation length
   */
   public void setNewIndentUnit() {
      String selectedNumber = JOptions.comboBoxRes("Number of spaces:",
            "Indentation length", SPACE_NUMBER, String.valueOf(indentLength));
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
      String sel = txtDoc.selectedText();
      if (sel == null) {
         return;
      }
      txtDoc.enableTypeEdit(false);
      String[] selSplit = sel.replaceAll("\\r", "").split("\\n");
      int start = txtDoc.selectionStart();
      int[] startOfLines = Finder.startOfLines(selSplit);

      for (int i = 0; i < selSplit.length; i++) {
         txtDoc.insertStr(start + startOfLines[i]
               + i * (indentLength), indentUnit);
      }
      txtDoc.select(start, (sel.length()
            + selSplit.length * indentLength) + start);
      txtDoc.enableTypeEdit(true);
   }

   /**
    * Reduces the indentation of selected text by one indentation unit
    */
   public void outdentSelection() {
      String sel = txtDoc.selectedText();
      if (sel == null) {
         return;
      }      
      int start = txtDoc.selectionStart();
      String startingLine = Finder.currLine(txtDoc.getText(), start);
      if (!startingLine.startsWith(indentUnit)) {
         return;
      }
      txtDoc.enableTypeEdit(false);
      String[] selSplit = sel.split("\n");
      /*
       * count spaces at the beginning of selection */
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
      /*
       * add an indent unit to empty lines or lines with too few spaces */
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
      /*
       * renew selection */
      int startOfFirstLine = editArea.getDocText().lastIndexOf("\n", start);
      int startUpdate = start - indentLength + countSpaces;
      if (countSpaces != indentLength && startUpdate > startOfFirstLine) {
         txtDoc.select(startUpdate, txtDoc.selectionEnd());
         sel = txtDoc.selectedText();
         selSplit = sel.split("\\n");
         start = txtDoc.selectionStart();
      }           
      startOfLines = Finder.startOfLines(selSplit);
      for (int i = 0; i < selSplit.length; i++) {
         txtDoc.removeStr(start + startOfLines[i] - i * indentLength,
               indentLength);
      }
      txtDoc.enableTypeEdit(true);
   }

   /**
    * Clears residual spaces in otherwise empty lines
    */
   public void clearSpaces() {
      txtDoc.enableTypeEdit(false);
      String allTxt = editArea.getDocText();
      String[] allTxtArr = allTxt.split("\n");
      int[] startOfLines = Finder.startOfLines(allTxtArr);
      for (int i = 0; i < allTxtArr.length; i++) {
         if (allTxtArr[i].matches("[\\s]+")) {
            txtDoc.removeStr(startOfLines[i], allTxtArr[i].length());
            for (int j = i + 1; j < startOfLines.length; j++) {
               startOfLines[j] -= allTxtArr[i].length();
            }
         }
      }
      txtDoc.enableTypeEdit(true);
   }

   /**
    * Performs undo action
    */
   public void undo() {
      txtDoc.undo();
   }

   /**
    * performes redo action
    */
   public void redo() {
      txtDoc.redo();
   }

   private String getClipboard() {
      String inClipboard = "";
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Clipboard clipboard = toolkit.getSystemClipboard();
      Transferable content = clipboard.getContents(null);
      try {
         inClipboard = (String) content.getTransferData(DataFlavor.stringFlavor);
      }
      catch (UnsupportedFlavorException | IOException e) {
         FileUtils.logStack(e);
      }
      return inClipboard;
   }
}
