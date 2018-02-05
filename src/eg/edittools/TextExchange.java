package eg.edittools;

import java.io.File;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.FileChooser;
import eg.Preferences;
import eg.Languages;
import eg.document.EditableDocument;
import eg.utils.Dialogs;
import eg.utils.FileUtils;

/**
 * The exchange of text between one <code>EditableDocument</code> set in
 * the constructor and a settable object of <code>EditableDocument</code>.
 * The first is named 'exchange document' and the second 'source document'.
 * The source document is the document currently viewed in the main editor
 * area
 */
public class TextExchange {

   private final static File BACK_UP = new File(System.getProperty("user.dir")
         + "/" + "exchangeContent.txt");

   private final EditableDocument exchangeDoc;
   private final Preferences prefs = new Preferences();
   private final FileChooser fc;

   private EditableDocument sourceDoc;

   /**
    * @param exchangeDoc  the <code>EditableDocument</code> that
    * represents the exchange document
    */
   public TextExchange(EditableDocument exchangeDoc) {
      this.exchangeDoc = exchangeDoc;
      prefs.readPrefs();
      String recentDir = prefs.getProperty("recentPath");
      fc = new FileChooser(recentDir);
      exchangeDoc.setIndentUnit(prefs.getProperty("indentUnit"));
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
      String text = sourceDoc.docTextArea().getSelectedText();
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
      String text = exchangeDoc.docTextArea().getSelectedText();
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
      }
      int res = 0;
      if (exchangeDoc.docLength() > 0) {
         res = Dialogs.confirmYesNo(
               "The current text will be replaced.\n"
               + " Continue?");
      }
      if (res == 0) {
         clear();
         loadFileContent(f);
      }
   }

   /**
    * Changes the language and switches on or off auto-indention depending
    * on whether or not the language is a coding language
    *
    * @param lang  the language which is a constant in {@link Languages}
    */
   public void changeCodeEditing(Languages lang) {
      exchangeDoc.changeLanguage(lang);
   }

   /**
    * Clears the exchange document
    */
   public void clear() {
      exchangeDoc.remove(0, exchangeDoc.docLength(), false);
   }

   /**
    * Saves the content contained in the exchange document to the file
    * 'exchangeContent.txt' which is saved in the program's directory.
    * It is asked before in a dialog to confirm that the content shall
    * be saved
    */
   public void save() {
      if (exchangeDoc.docLength() > 0) {
         int res = Dialogs.confirmYesNo("Keep content in exchange editor?");
         if (0 != res) {
            clear();
         }
      }
      exchangeDoc.saveCopy(BACK_UP);
   }
   
   //
   //--private--//
   //
   
   private void copy(EditableDocument destination, String text) {
      destination.docTextArea().requestFocusInWindow();
      String toReplace = destination.docTextArea().getSelectedText();
      int pos = destination.docTextArea().getSelectionStart();
      int end = destination.docTextArea().getSelectionEnd();
      int length = end - pos;
      destination.replace(pos, length, text);
   }
   
   private void loadFileContent(File f) {
      try (BufferedReader br = new BufferedReader(new FileReader(f))) {
         String line = br.readLine();
         String nextLine = br.readLine();
         while (null != line) {
            if (null == nextLine) {
               exchangeDoc.insert(exchangeDoc.docLength(), line);
            }
            else {
               exchangeDoc.insert(exchangeDoc.docLength(), line + "\n");
            }
            line = nextLine;
            nextLine = br.readLine();
         }
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }
   }
}
