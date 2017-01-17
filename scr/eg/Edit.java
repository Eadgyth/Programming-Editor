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
import eg.document.TextDocument;

/**
 * The editing of the document in the selected tab by actions
 * that are invoked in the edit menu / toolbar
 */ 
public class Edit {

   /* Options for the numbers of white spaces in indentation unit */
   private static final String[] SPACE_NUMBER = { "1", "2", "3", "4", "5", "6" };

   private TextDocument txtDoc;
   private String indentUnit;
   private int indentLength;

   /**
    * Assigns to this the TextDocument object that is edited and
    * its current indentation unit
    * @param txtDoc  the {@link TextDocument} object
    */
   public void setTextObject(TextDocument txtDoc) {
      this.txtDoc  = txtDoc;
      indentUnit   = txtDoc.getIndentUnit();
      indentLength = indentUnit.length();
   }

   public void selectAll() {
      txtDoc.selectAll();
   }
   
   /**
    * Cuts selected text
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
      txtDoc.enableTextModify(false);

      String clipboard = getClipboard();
      String selection = txtDoc.selectedText();
      int pos = txtDoc.getCaretPos();

      if (selection == null) {
         txtDoc.insertStr(pos, clipboard);
         txtDoc.setCaretPos(pos + clipboard.length());
      }
      else {
         txtDoc.removeStr(pos - selection.length(), selection.length());
         txtDoc.insertStr(pos - selection.length(), clipboard);
         txtDoc.setCaretPos(pos - selection.length() + clipboard.length());
      }

      if (txtDoc.isComputerLanguage()) {
         txtDoc.colorAll();
      }
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
      if (txtDoc.selectedText() == null) {
         return;
      }

      txtDoc.enableTextModify(false);
      String sel = txtDoc.selectedText();
      String[] selection = sel.replaceAll("\\r", "").split("\\n");
      int start = txtDoc.selectionStart();
      int[] startOfLines = Finder.startOfLines(selection);

      for (int i = 0; i < selection.length; i++) {
         txtDoc.insertStr(start + startOfLines[i]
               + i * (indentLength), indentUnit);
      }
      txtDoc.select(start, (sel.length()
            + selection.length * indentLength) + start);
      if (txtDoc.isComputerLanguage()) {
         txtDoc.enableTextModify(true);
      }
   }

   /**
    * Reduces the indentation of selected text by one indentation unit
    */
   public void outdentSelection() {
      if (txtDoc.selectedText() == null) {
         return;
      }
      
      String sel = txtDoc.selectedText();
      int start = txtDoc.selectionStart();
      String startingLine = Finder.currLine(txtDoc.getDocText(), start);
      if (!startingLine.startsWith(indentUnit)) {
         return;
      }
      txtDoc.enableTextModify(false);
      String[] selection = sel.split("\n");
      /*
       * count spaces at the beginning of selection */
      int countSpaces = 0;       
      for (int i = 0; i < selection[0].length(); i++) {
         if (selection[0].substring(i, i + 1).equals(" ")) {
            countSpaces++;
         }
         else {
            break;
         }
      }
      int[] startOfLines = Finder.startOfLines(selection);
      /*
       * add an indent unit to empty lines or lines with too few spaces */
      for (int i = 0; i < selection.length; i++) {         
         if (selection[i].length() == 0) {
            txtDoc.insertStr(start + startOfLines[i], indentUnit);
            for (int j = i + 1; j < selection.length; j++) {
               startOfLines[j] += indentLength;
            }
         }
         if (selection[i].matches("[\\s]+")) {
            int length = selection[i].length();
            if (length < indentLength) {
               txtDoc.insertStr(start + startOfLines[i], indentUnit);
               for (int j = i + 1; j < selection.length; j++) {
                  startOfLines[j] += indentLength;
               }
            }
         } 
      }
      /*
       * renew selection */
      int startOfFirstLine = txtDoc.getDocText().lastIndexOf("\n", start);
      int startUpdate = start - indentLength + countSpaces;
      if (countSpaces != indentLength && startUpdate > startOfFirstLine) {
         txtDoc.select(startUpdate, txtDoc.selectionEnd());
         sel = txtDoc.selectedText();
         selection = sel.split("\\n");
         start = txtDoc.selectionStart();
      }           
      startOfLines = Finder.startOfLines(selection);
      for (int i = 0; i < selection.length; i++) {
         txtDoc.removeStr(start + startOfLines[i] - i * indentLength,
               indentLength);
      }
      if (txtDoc.isComputerLanguage()) {
         txtDoc.enableTextModify(true);
      }
   }

   /**
    * Clears residual spaces in otherwise empty lines
    */
   public void clearSpaces() {
      txtDoc.enableTextModify(false);
      String allTxt = txtDoc.getDocText();
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
      if (txtDoc.isComputerLanguage()) {
         txtDoc.enableTextModify(true);
      }
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

   /**
    * Changes the language
    * @param newLanguage  the language that is used
    * for the editing of text during typing
    */
   public void changeLanguage(Languages newLanguage) {
      txtDoc.changeLanguage(newLanguage);
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
         FileUtils.logMessage(e);
      }
      return inClipboard;
   }
}
