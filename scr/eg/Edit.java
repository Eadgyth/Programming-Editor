package eg;

import java.awt.Toolkit;
import java.awt.EventQueue;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import java.io.IOException;

//--Eadgyth--//
import eg.utils.*;
import eg.document.TextDocument;

/**
 * Represents the editing of text in a selected tab.
 */ 
public class Edit {

   /* Numbers of white spaces in indentation unit */
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
      int pos = txtDoc.caretPosition();

      if (selection == null) {
         txtDoc.insertStr(pos, clipboard);
         txtDoc.setCaret(pos + clipboard.length());
      }
      else {
         txtDoc.removeStr(pos - selection.length(), selection.length());
         txtDoc.insertStr(pos - selection.length(), clipboard);
         txtDoc.setCaret(pos - selection.length() + clipboard.length());
      }

      if (txtDoc.isComputerLanguage()) {
         EventQueue.invokeLater(() -> {
            txtDoc.colorAll(true);
         });
      }
   }

   /**
    * Sets a new indentation length
    */
   public void setNewIndentUnit() {
      String selectedNumber = ShowJOption.comboBoxRes("Number of spaces:",
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

      txtDoc.enableTextModify(false);

      String sel = txtDoc.selectedText();
      String[] selection = sel.split("\n");
      int start = txtDoc.selectionStart();

      /* remove the selection of a first empty line */
      if (selection[0].length() == 0) {
         txtDoc.select(start + 1, txtDoc.selectionEnd());
         sel = txtDoc.selectedText();
         selection = sel.split("\n");
         start = txtDoc.selectionStart();
      }

      int startOfFirstLine = txtDoc.getDocText().lastIndexOf("\n", start);

      /* count spaces at the beginning of selection */
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

      for (int i = 0; i < selection.length; i++) {         
         if (selection[i].length() == 0 ) {
            txtDoc.insertStr(start + startOfLines[i] , indentUnit);
            for (int j = i + 1; j < selection.length; j++) {
               startOfLines[j] += indentLength;
            }
         }
      }

      int startUpdate = start - indentLength + countSpaces;

      if (countSpaces < indentLength && startUpdate > startOfFirstLine) {     
         txtDoc.select(startUpdate, txtDoc.selectionEnd());
         sel = txtDoc.selectedText();
         selection = sel.split("\\n");
         start = txtDoc.selectionStart();
      }        

      if (sel.substring(0, indentLength).equals(indentUnit)) {
         if (isIndentUnit(selection)) {
            startOfLines = Finder.startOfLines(selection);
            for (int i = 0; i < selection.length; i++) {
               txtDoc.removeStr(start + startOfLines[i] - i * indentLength,
                     indentLength);
            }
         }
         else {
            ShowJOption.warnMessage
                  ("Operation blocked because indentation of lines is not consistent");
         }
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
    */
   public void changeLanguage(String newLanguage) {
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
      catch (UnsupportedFlavorException ufe) {
         ufe.printStackTrace();
      }
      catch (IOException ioe) {
         ioe.printStackTrace();
      }
      return inClipboard;
   }

   /*
    * @return  if all lines except the first begin with an
    * indentation unit
    */
   private boolean isIndentUnit(String[] in) {
      boolean isIndentUnit = true;

      for (int i = 1; i < in.length; i++) {
         if (!in[i].startsWith(indentUnit)) {
            isIndentUnit = false;
         }   
      }
      return isIndentUnit;
   }
}