package eg.edittools;

import java.io.File;


//--Eadgyth--/
import eg.FileChooser;
import eg.document.EditableDocument;
import eg.utils.Dialogs;

/**
 * The exchange of text between an <code>EditableDocument</code> set in
 * the constructor and a variable <code>EditableDocument</code>.
 * The first is named 'exchange document' and the second 'source document'.
 * The source document is the document currently viewed in the main editor
 * area
 */
public class TextExchange {

   private final static File BACK_UP
         = new File(System.getProperty("user.dir")
         + "/exchangeContent.txt");

   private final EditableDocument exchangeDoc;
   private final FileChooser fc;
   
   private EditableDocument sourceDoc;

   /**
    * @param exchangeDoc  the <code>EditableDocument</code> that
    * represents the exchange document
    * @param recentDir  the directory where a file was opened or saved
    * the last time
    */
   public TextExchange(EditableDocument exchangeDoc, String recentDir) {
      this.exchangeDoc = exchangeDoc;
      fc = new eg.FileChooser(recentDir);
      if (BACK_UP.exists()) {
         loadFileContent(BACK_UP);
      }
   }

   /**
    * Sets the <code>EditableDocument</code> that represents the source
    * document
    *
    * @param sourceDoc  the <code>EditableDocument</code>
    */
   public void setSourceDocument(EditableDocument sourceDoc) {
      this.sourceDoc  = sourceDoc;
   }

   /**
    * Copies text selected in the source document and inserts the text
    * at the caret position in the exchange document. The text to copy
    * replaces any selected text
    */
   public void copyTextFromSource() {
      String text = sourceDoc.textArea().getSelectedText();
      if (text == null) {
         String filename = sourceDoc.filename();
         if (filename.length() == 0) {
            filename = "unnamed";
         }
         Dialogs.warnMessage("No text is selected in \"" + filename + "\"");
         return;
      }
      copy(exchangeDoc, text);
   }

   /**
    * Copies text selected in the exchange document and inserts the text
    * at the caret position in the source document. The text to copy
    * replaces any selected text
    */
   public void copyTextToSource() {
      String text = exchangeDoc.textArea().getSelectedText();
      if (text == null) {
         Dialogs.warnMessage("No text is selected in the exchange editor");
         return;
      }
      copy(sourceDoc, text);
   }
   
   /**
    * Loads the content of a file that is selected in the file chooser
    */
   public void loadFile() {
      File f = fc.fileToOpen();
      if (f == null) {
         return;
      }
      if (!f.exists()) {
         Dialogs.warnMessage(f.getName() + " was not found.");
         return;
      }
      int res = 0;
      if (exchangeDoc.docLength() > 0) {
         res = Dialogs.confirmYesNo(
               "The current text content will be replaced.\n"
               + " Continue?");
      }
      if (res == 0) {
         clear();
         loadFileContent(f);
      }
   }

   /**
    * Sets in this exchange document the language of the source document
    */
   public void adoptLanguage() {
      exchangeDoc.changeLanguage(sourceDoc.language());
   }
   
   /**
    * Sets in this exchange document the indent unit of the source
    * document
    */
   public void adoptIndentUnit() {
      exchangeDoc.setIndentUnit(sourceDoc.currIndentUnit());
   }

   /**
    * Clears the exchange document
    */
   public void clear() {
      exchangeDoc.remove(0, exchangeDoc.docLength(), false);
   }

   /**
    * Saves the content in the exchange document to the file
    * 'exchangeContent.txt' in the program folder
    */
   public void save() {
      exchangeDoc.saveCopy(BACK_UP);
   }
   
   //
   //--private--/
   //
   
   private void copy(EditableDocument destination, String text) {
      destination.textArea().requestFocusInWindow();
      String toReplace = destination.textArea().getSelectedText();
      int pos = destination.textArea().getSelectionStart();
      int end = destination.textArea().getSelectionEnd();
      int length = end - pos;
      destination.replace(pos, length, text);
   }
   
   private void loadFileContent(File f) {
      exchangeDoc.enableMerging(true);
      exchangeDoc.displayFileContent(f);
      exchangeDoc.enableMerging(false);
   }
}
