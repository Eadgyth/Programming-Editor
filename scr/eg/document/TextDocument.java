package eg.document;

import java.awt.EventQueue;

import javax.swing.JTextPane;
import javax.swing.JPanel;

import javax.swing.text.Document;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.BadLocationException;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//--Eadgyth--//
import eg.Constants;
import eg.Preferences;
import eg.Languages;

import eg.utils.JOptions;
import eg.ui.EditArea;

/**
 * Class represents the text document
 */
public class TextDocument {

   private final static Preferences PREFS = new Preferences();

   private final JTextPane textArea;
   private final EditArea editArea;
   private final TypingEdit type;

   private String filename = "";
   private String filepath = "";
   private String dir = "";
   private String language;
   private String content;

   public TextDocument(EditArea editArea) {
      this.editArea = editArea;
      this.textArea = editArea.textArea();
      type = new TypingEdit(editArea);
      PREFS.readPrefs();
      language = PREFS.prop.getProperty("language");
      openSettings();
   }

   /**
    * Returns the name of this file
    * @return  the String that represents the name of this file
    */
   public String filename() {
      return filename;
   }

   /**
    * Returns this full filepath
    * @return  the String that represents the filepath of this file
    */
   public String filepath() {
      return filepath;
   }

   /**
    * Returns the directory of this file
    * @return  the String that represents the directory of this file
    */
   public String dir() {
      return dir;
   }

   /**
    * @param filepath  the existing file whose content is shown in
    * this text area
    */
   public void openFile(File filepath) {
      if (this.filepath().length() != 0) {
         throw new IllegalStateException("Illegal attempt to assign a file"
               + " to a TextDocument which a file was assigned to before");
      }
      assignFileStrings(filepath);
      EventQueue.invokeLater(() -> {
         displayFileContent();
         openSettings();
      });
   }

   /**
    * Saves the current text content to the file specified by this
    * filepath
    */
   public void saveToFile() {
      content();
      try (FileWriter writer = new FileWriter(filepath)) {
         writer.write(content);        
      }
      catch(IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * @param filepath  the non-existing file that is saved
    */
   public void saveFileAs(File filepath) {        
      assignFileStrings(filepath);
      content();
      saveToFile();
      openSettings();
   }

   /**
    * @return  if the content of the text area equals the content 
    * since the last saving point
    */ 
   public boolean isContentSaved() {
      return content.equals(textArea.getText());
   }
   
   /**
    * @return  if this language is a computer language, i.e. not
    * set to plain text
    */
   public boolean isComputerLanguage() {
      return !Languages.PLAIN_TEXT.toString().equals(language);
   }

   /**
    * @return  the content of this document
    */
   public String getDocText() {
      return type.getDocText();
   }
   
   /**
    * @return  the length of text shown in this text area
    */
   public int textLength() {
      return type.getDocText().length();
   }

   /**
    * Selects the entire text
    */
   public void selectAll() {
      textArea.selectAll();
   }

   /**
    * Selects text
    */
   public void select(int start, int end) {
      textArea.select(start, end);
   }

   /**
    * @return  the selected text of this text area
    */
   public String selectedText() {
      return textArea.getSelectedText();
   }

   /**
    * @return the start position of selected text
    */
   public int selectionStart() {
      return textArea.getSelectionStart();
   }

   /**
    * @return the end position of selected text
    */
   public int selectionEnd() {
      return textArea.getSelectionEnd();
   }

   /**
    * @return the caret position of this text area
    */
   public int caretPosition() {
      return textArea.getCaretPosition();
   }

   /**
    * Sets the caret in this text area at the sepecified
    * position
    */
   public void setCaret(int pos) {
      textArea.setCaretPosition(pos);
   }

   /**
    * Gains focus in this text area
    */
   public void requestFocus() {
      textArea.requestFocusInWindow();
   }

   /**
    * @return  the current indentation unit
    */
   public String getIndentUnit() {
      return type.getIndentUnit();
   }

   /**
    * @param indentUnit  the String that consists of a certain number of
    * white spaces
    */
   public void changeIndentUnit(String indentUnit) {
      type.changeIndentUnit(indentUnit);
   }

   /**
    * @param isEnabled  true to enable syntax coloring and
    * auto-indentation
    */
   public void enableTextModify(boolean isEnabled) {
      type.enableTextModify(isEnabled);
   }

   /**
    * (Re-)colors the text starting at the specified position
    * and spanning the specified length in black
    */
   public void backInBlack(int length, int pos) {
      type.doc().setCharacterAttributes(pos, length,
            type.normalSet(), false);
   }

   /**
    * Colors keyword/syntax of the entire text in this text area
    * @param enableTextModify   true to enable the coloring
    * during typing after the entire text has been scanned
    */
   public void colorAll(boolean enableTextModify) {
      type.colorAll(enableTextModify);
   }

   /**
    * Performs an undo action
    */
   public void undo() {
      type.undo();
   }

   /**
    * Performs a redo action
    */
   public void redo() {
      type.redo();
   }

   /**
    * Inserts a String at the specified position with normal attribute set
    */
   public void insertStr(int pos, String toInsert) {
      try {
         type.doc().insertString(pos, toInsert, type.normalSet());
      }
      catch (BadLocationException ble) {
         ble.printStackTrace();
      }
   }

   /**
    * Removes text of the specified length starting at the specified position 
    */   
   public void removeStr(int start, int length) {
      try {
         type.doc().remove(start, length);
      }
      catch (BadLocationException ble) {
         ble.printStackTrace();
      }
   }

   /**
    * Changes this language if no file has been assigned
    * and saves language to 'prefs'
    */
   public void changeLanguage(Languages language) {
      if (language == Languages.JAVA & filename.length() == 0) {
         changeToJava();
      }
      if (language == Languages.HTML & filename.length() == 0) {
         changeToHtml();
      }
      else if (language == Languages.PLAIN_TEXT & filename.length() == 0) {
         changeToPlain();
      }
      PREFS.storePrefs("language", language.toString());
   }
   
   /**
    * Colors text elements specified by an array of search terms in the
    * keyword color if this language is plain text.
    * @param searchTerms  the array of Strings that contain search terms
    * to be colored
    * @param constrainWord  true to color only words
    * @throws IllegalArgumentException  if searchTerms is null or contains
    * empty Strings
    */
   public void colorSearchedText(String[] searchTerms, boolean constrainWord) {
      if (searchTerms == null) {
         throw new IllegalArgumentException("Argument 'searchTerms' is null");
      }
      for (String searchTerm : searchTerms) {
         if (searchTerm.length() == 0) {
            throw new IllegalArgumentException("'searchTerms' contains an"
                  + " empty element");
          }
      }
      if (isComputerLanguage()) {
         JOptions.infoMessage("The coloring of text requires"
            + " that the language is plain txt");
         return;
      }
      type.configColoring(searchTerms, "", "", "", false, false, constrainWord);
      type.enableIndent(false);
      colorAll(true);
   }         
   
   //
   //----private methods-----//
   //
   
   /**
    * Controls which text modification are done upon typing depending on
    * the language.
    * This method has to be modified if other languages are implemented in
    * Eadgyth.
    */
   private void configColoring(Languages language) {
      switch(language) {
         case JAVA:
            type.configColoring(Keywords.JAVA_KEYWORDS, "//",
                  "/*", "*/", true, true, true);
            type.enableIndent(true);
            break;
         case HTML:
            type.configColoring(Keywords.HTML_KEYWORDS, "",
                  "<!--", "-->", true, true, false);
            type.enableIndent(true);
            break;
         /*
          * just to reset the indentation, textModify is switched off anyway */
         case PLAIN_TEXT:
            type.configColoring(null, "", "", "", false, false, false);
            type.enableIndent(false);
            break;
      }
   }

   private void content() {
      content = textArea.getText();
   }

   private void displayFileContent() {
      type.enableDocListen(false);
      type.enableTextModify(false);

      // set text attributes later to speed up placing larger pieces of text
      Document blank = new DefaultStyledDocument();
      textArea.setDocument(blank);

      try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
         String line;
         while ((line = br.readLine()) != null) {
            insertStr(type.doc().getLength(), line + "\n");
         }
      }
      catch (IOException e) {
         e.printStackTrace();
      }   
      editArea.textArea().setDocument(type.doc()); // doc is the StyledDocument
      if (textArea.getText().endsWith("\n")) {
         removeStr(type.doc().getLength() - 1, 1);
      }
      type.enableDocListen(true);
   }

   private void assignFileStrings(File filepath) {
      filename = filepath.getName();
      this.filepath = filepath.toString();
      dir = filepath.getParent().toString();
   }

   private void openSettings() {
      updateRowNumber();
      if (filename.endsWith(".java")
            || (filename.length() == 0
            & Languages.JAVA.toString().equals(language))) {
         changeToJava();
      }
      else if (filename.endsWith(".html")
            || (filename.length() == 0
            & Languages.HTML.toString().equals(language))) {
         changeToHtml();
      }
      else {
         changeToPlain();
      }
   }

   private void changeToJava() {
      configColoring(Languages.JAVA);
      colorAll(true);
      language = Languages.JAVA.toString();
   }
   
   private void changeToHtml() {
      configColoring(Languages.HTML);
      colorAll(true);
      language = Languages.HTML.toString();
   }

   private void changeToPlain() {
      configColoring(Languages.PLAIN_TEXT);
      backInBlack(getDocText().length(), 0);
      enableTextModify(false);
      language = Languages.PLAIN_TEXT.toString();
   }

   private void updateRowNumber() {
      content();
      type.updateRowNumber(content);
   }
}