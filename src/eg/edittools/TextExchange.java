package eg.edittools;

import javax.swing.JFrame;

import java.io.File;

//--Eadgyth--/
import eg.BusyFunction;
import eg.Edit;
import eg.FileChooser;
import eg.Languages;
import eg.document.EditableDocument;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.utils.SystemParams;

/**
 * The exchange of text between an <code>EditableDocument</code> set
 * in the constructor and a variable <code>EditableDocument</code>.
 * The first is named 'exchange document' and the second 'source
 * document'. The source document is the document currently viewed in
 * the main editor.
 */
public class TextExchange {

   private final EditableDocument exchangeDoc;
   private final FileChooser fc;

   private EditableDocument sourceDoc;
   private File exchFile;

   /**
    * @param exchangeDoc  the <code>EditableDocument</code> that
    * represents the exchange document
    * @param recentDir  the directory where a file was opened or saved
    * the last time
    */
   public TextExchange(EditableDocument exchangeDoc, String recentDir) {
      this.exchangeDoc = exchangeDoc;
      fc = new FileChooser(recentDir);
      exchFile = new File(SystemParams.EADGYTH_DATA_DIR + "/exchgContent.txt");
      if (exchFile.exists()) {
          exchangeDoc.displayFileContentIgnoreLang(exchFile);
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
         if (filename.isEmpty()) {
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
      if (exchangeDoc.textLength() > 0) {
         res = Dialogs.confirmYesNo(
               "The current text content will be replaced.\n"
               + " Continue?");
      }
      if (res == 0) {
         clear();
         loadFile(f);
      }
   }

   /**
    * Changes the language in the exchange editor
    *
    * @param lang  the language to change to
    */
   public void changeLanguage(Languages lang) {
      exchangeDoc.changeLanguage(lang);
   }

   /**
    * Sets in this exchange document the language of the source
    * document
    *
    * @return  the adopted language
    */
   public Languages adoptedLanguage() {
      Languages lang = sourceDoc.language();
      exchangeDoc.changeLanguage(lang);
      return lang;
   }

   /**
    * Returns the language currently set in the exchange editor
    *
    * @return  the language
    */
   public Languages language() {
      return exchangeDoc.language();
   }

   /**
    * Gets the indent unit of the source document
    *
    * @return  the indent unit
    */
   public String sourceDocIndentUnit() {
      return sourceDoc.indentUnit();
   }

   /**
    * Clears the exchange document
    */
   public void clear() {
      exchangeDoc.remove(0, exchangeDoc.textLength(), false);
   }

   /**
    * Saves the content in the exchange document to the file
    * 'exchangeContent.txt' in the program folder
    */
   public void save() {
      if (exchFile.getParentFile().exists()) {
         exchangeDoc.saveCopy(exchFile);
      }
   }

   //
   //--private--/
   //
   private void loadFile(File f) {
      JFrame frame = ((JFrame) exchangeDoc.textArea()
            .getTopLevelAncestor());

      new BusyFunction(frame).executeLater(
           () -> exchangeDoc.displayFileContent(f));
   }

   private void copy(EditableDocument destination, String text) {
      destination.textArea().requestFocusInWindow();
      String toReplace = destination.textArea().getSelectedText();
      int pos = destination.textArea().getSelectionStart();
      int end = destination.textArea().getSelectionEnd();
      int length = end - pos;
      destination.replace(pos, length, text);
   }
}
