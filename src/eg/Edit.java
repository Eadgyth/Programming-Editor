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
import eg.document.FileDocument;

/**
 * The editing of the document in the selected tab
 */
public class Edit {

   private static final String[] SPACE_NUMBER = { "1", "2", "3", "4", "5", "6" };
   
   private final Preferences prefs = new Preferences();

   private FileDocument fDoc;
   private JTextPane textArea;
   private String indentUnit;
   private int indentLength;

   /**
    * Sets the {@code FileDocument} that is edited and its current
    * indentation unit
    *
    * @param fDoc  the {@link FileDocument}
    */
   public void setFileDocument(FileDocument fDoc) {
      this.fDoc  = fDoc;
      this.textArea = fDoc.docTextArea();
      indentUnit = fDoc.getIndentUnit();
      indentLength = indentUnit.length();
   }

   /**
    * Performs undo action
    */
   public void undo() {
      fDoc.undo();
   }

   /**
    * Performs redo action
    */
   public void redo() {
      fDoc.redo();
   }

   /**
    * Cuts selected text and stores it to the system's clipboard
    */
   public void cut() {
      int start = textArea.getSelectionStart();
      int end = textArea.getSelectionEnd();
      setClipboard();
      fDoc.remove(start, end - start);
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
      String clipboard = getClipboard();
      if (clipboard.length() == 0) {
         return;
      }
      String sel = textArea.getSelectedText();
      int pos = textArea.getSelectionStart();
      fDoc.enableCodeEditing(false);
      if (sel != null) {
         fDoc.remove(pos, sel.length());
      }
      EventQueue.invokeLater(() -> {
         fDoc.insert(pos, clipboard);
         fDoc.colorSection(clipboard, pos);
         fDoc.enableCodeEditing(true);
      });
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
         fDoc.setIndentUnit(indentUnit);
         prefs.storePrefs("indentUnit", indentUnit);
      }
   }

   /**
    * Increases the indentation by one indentation unit
    */
   public void indent()  {
      String sel = textArea.getSelectedText();
      int start = textArea.getSelectionStart();
      fDoc.enableCodeEditing(false);
      if (sel == null) {
         fDoc.insert(start, indentUnit);
      }
      else {
         String[] selArr = sel.split("\n");
         int sum = 0;
         for (String s : selArr) {
            int lineLength = s.length() + indentLength;
            fDoc.insert(start + sum, indentUnit);
            sum += lineLength + 1;
         }
      }
      fDoc.enableCodeEditing(true);
   }

   /**
    * Reduces the indentation by one indentation unit
    */
   public void outdent() {
      String sel = textArea.getSelectedText();
      int start = textArea.getSelectionStart();
      String text = fDoc.getDocText();
      fDoc.enableCodeEditing(false);
      if (sel == null) {
         boolean isAtLineStart
               = LinesFinder.lastNewline(text, start) > start - indentLength;
         if (!isAtLineStart && start >= indentLength) {
            if (indentUnit.equals(text.substring(start - indentLength, start))) {
               fDoc.remove(start - indentLength, indentLength);
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
            int sum = 0;
            for (String s : selArr) {
               if (s.startsWith(indentUnit)) {
                  fDoc.remove(start + sum, indentLength);
                  sum += (s.length() - indentLength) + 1;
               } else {
                  sum += s.length() + 1;
               }
            }
         }
      }
      fDoc.enableCodeEditing(true);
   }

   /**
    * Clears trailing spaces
    */
   public void clearTrailingSpaces() {
      fDoc.enableCodeEditing(false);
      String text = fDoc.getDocText();
      String[] textArr = text.split("\n");
      int sum = 0;
      for (String s : textArr) {
         int startOfSpaces = startOfTrailingSpaces(s);
         int spacesLength = s.length() - startOfSpaces;
         fDoc.remove(startOfSpaces + sum, spacesLength);
         sum += startOfSpaces + 1;
      }
      fDoc.enableCodeEditing(true);
   }

   //
   //--private--//
   //

   private String getClipboard() {
      String inClipboard = "";
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Clipboard clipboard = toolkit.getSystemClipboard();
      try {
         DataFlavor flavor = DataFlavor.stringFlavor;
         inClipboard = (String) clipboard.getData(flavor);
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }
      catch (UnsupportedFlavorException e) {
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
