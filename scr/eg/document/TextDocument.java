package eg.document;

import java.awt.EventQueue;

import javax.swing.JTextPane;

import javax.swing.text.Document;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.BadLocationException;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//--Eadgyth--//
import eg.Preferences;
import eg.Languages;
import eg.Constants;

import eg.utils.JOptions;
import eg.utils.FileUtils;
import eg.ui.EditArea;

/**
 * Class represents the text document
 */
public class TextDocument {
   private final static Preferences PREFS = new Preferences();

   private final JTextPane textArea;
   private final TypingEdit type;

   private String filename = "";
   private String filepath = "";
   private String dir = "";
   private String language;
   private String content;

   /**
    * Creates a TextDocument
    * @param editArea  the reference to the {@link EditArea}
    */
   public TextDocument(EditArea editArea) {
      this.textArea = editArea.textArea();
      type = new TypingEdit(editArea);
      PREFS.readPrefs();
      language = PREFS.getProperty("language");
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
    * Returns the filepath of this file
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
    * Assigns to this the specified file and displays the file
    * content
    * @param file  the file whose content is displayed in this text
    * text area
    */
   public void openFile(File file) {
      if (this.filepath().length() != 0) {
         throw new IllegalStateException(
                 "Illegal attempt to assign a file to a "
               + " TextDocument which a file was assigned to before");
      }
      assignFileStrings(file);
      EventQueue.invokeLater(() -> {
         displayFileContent();
         openSettings();
      });
   }

   /**
    * Saves the current content to this file
    */
   public void saveToFile() {
      setContent();
      String[] lines = content.split("\n"); 
      try (FileWriter writer = new FileWriter(filepath)) {
         for (String s : lines) {
            writer.write(s + Constants.LINE_SEP);
         }
      }
      catch(IOException e) {
         FileUtils.logStack(e);
      }
   }

   /**
    * Assigns to this the specified file and saves the content of
    * this text area to the specified file.
    * @param file  the file which the current content is saved to
    */
   public void saveFileAs(File file) {        
      assignFileStrings(file);
      saveToFile();
      openSettings();
   }

   /**
    * @return  if the content of this text area equals the content 
    * since the last saving point
    */ 
   public boolean isContentSaved() {
      return content.equals(type.getDocText());
   }
   
   /**
    * If this language is a computer language
    * @return  if this language is a computer language, i.e. not
    * set to plain text
    */
   public boolean isComputerLanguage() {
      return !"Plain text".equals(language);
   }

   /**
    * Returns the content of this document
    * @return  the content of this document
    */
   public String getDocText() {
      return type.getDocText();
   }
   
   /**
    * Returns the current length of the text of this document
    * @return  the current length of text of this document
    */
   public int textLength() {
      return type.getDocText().length();
   }

   /**
    * Selects the entire text of this text area
    */
   public void selectAll() {
      textArea.selectAll();
   }

   /**
    * Selects text between the specified start end end positions
    * in this text area
    * @param start  the start of the selection
    * @param end  the end position of the selection
    */
   public void select(int start, int end) {
      textArea.select(start, end);
   }

   /**
    * Returns the selected text in this text area
    * @return  the selected text in this text area. Null if no text
    * is selected
    */
   public String selectedText() {
      return textArea.getSelectedText();
   }

   /**
    * Returns the selection start
    * @return the start position of selected text
    */
   public int selectionStart() {
      return textArea.getSelectionStart();
   }

   /**
    * Returns the selection end
    * @return the end position of selected text
    */
   public int selectionEnd() {
      return textArea.getSelectionEnd();
   }

   /**
    * @return the caret position of this text area
    */
   public int getCaretPos() {
      return textArea.getCaretPosition();
   }

   /**
    * Sets the caret at the sepecified position of
    * this text document
    * @param pos  the position where the caret is set
    */
   public void setCaretPos(int pos) {
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
      if (indentUnit == null || !indentUnit.matches("[\\s]+")) {
         throw new IllegalArgumentException(
               "Argument 'indentUnit' is incorrect");
      }  
      type.changeIndentUnit(indentUnit);
   }

   /**
    * Enables/Disables syntax coloring and auto-indentation
    * @param isEnabled  true to enable syntax coloring and
    * auto-indentation
    */
   public void enableTextModify(boolean isEnabled) {
      type.enableTextModify(isEnabled);
   }

   /**
    * (Re-)colors the text starting at the specified position
    * and spanning the specified length in the default color
    * @param length  the length of text that is colored in the
    * default color
    * @param pos  the position where the text to color starts
    */
   public void backInBlack(int length, int pos) {
      type.doc().setCharacterAttributes(pos, length,
            type.normalSet(), false);
   }

   /**
    * Colors keyword/syntax of the entire text in this document.
    */
   public void colorAll() {
      type.colorAll();
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
    * Inserts text in this document
    * @param pos  the position where new text is inserted
    * @param toInsert  the String that contains the text to insert
    */
   public void insertStr(int pos, String toInsert) {
      try {
         type.doc().insertString(pos, toInsert, type.normalSet());
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }

   /**
    * Removes text from this document
    * @param start  the position where text to be removed starts
    * @param length  the length of the text to be removed
    */   
   public void removeStr(int start, int length) {
      try {
         type.doc().remove(start, length);
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);;
      }
   }

   /**
    * Changes this language if no file has been assigned to this
    * @param language  the language which has one of the constant values
    * in {@link eg.Languages}
    */
   public void changeLanguage(Languages language) {
      PREFS.storePrefs("language", language.toString()); 
      if (filename.length() > 0) {
         return;
      }
      switch (language) {
         case JAVA:
            changeToJava();
            break;
         case PERL:
            changeToPerl();
            break;
         case HTML:
            changeToHtml();
            break;
         case PLAIN_TEXT:
            changeToPlain();
            break;
      }
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
         throw new IllegalArgumentException(
               "Argument 'searchTerms' is null");
      }
      for (String searchTerm : searchTerms) {
         if (searchTerm.length() == 0) {
            throw new IllegalArgumentException(
                  "'searchTerms' contains an"
                  + " empty element");
          }
      }
      if (isComputerLanguage()) {
         JOptions.infoMessage(
               "The coloring of text requires"
               + " that the language is plain txt");
         return;
      }
      type.setKeywords(searchTerms, constrainWord);
      type.enableIndent(false);
      colorAll();
   }
   
   //
   //----private methods----//
   //

   private void displayFileContent() {
      type.enableDocListen(false);
      type.enableTextModify(false);
      /*
       * set text attributes later to speed up placing larger pieces of text */
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
      textArea.setDocument(type.doc()); // doc is the StyledDocument
      if (textArea.getText().endsWith("\n")) {
         removeStr(type.doc().getLength() - 1, 1);
      }
      type.enableDocListen(true);
   }
   
   private void setContent() {
      content = type.getDocText();
   }

   private void assignFileStrings(File filepath) {
      filename = filepath.getName();
      this.filepath = filepath.toString();
      dir = filepath.getParent();
   }

   private void openSettings() {
      setContent();
      type.addAllRowNumbers(content);
      if (filename.endsWith(Constants.JAVA_EXT)
            || (filename.length() == 0
            & Languages.JAVA.toString().equals(language))) {
         changeToJava();
      }
      else if (filename.endsWith(Constants.HTML_EXT)
            || (filename.length() == 0
            & Languages.HTML.toString().equals(language))) {
         changeToHtml();
      }
      else if (filename.endsWith(Constants.PERL_PL_EXT)
            || filename.endsWith(Constants.PERL_PM_EXT)
            || (filename.length() == 0
            & Languages.PERL.toString().equals(language))) {
         changeToPerl();
      }
      else {
         changeToPlain();
      }
   }

   private void changeToJava() {
      configColoring(Languages.JAVA);
      type.colorAll();
      language = Languages.JAVA.toString();
   }
   
   private void changeToHtml() {
      configColoring(Languages.HTML);
      type.colorAll();
      language = Languages.HTML.toString();
   }
   
   private void changeToPerl() {
      configColoring(Languages.PERL);
      type.colorAll();
      language = Languages.PERL.toString();
   }

   private void changeToPlain() {
      configColoring(Languages.PLAIN_TEXT);
      backInBlack(getDocText().length(), 0);
      enableTextModify(false);
      language = Languages.PLAIN_TEXT.toString();
   }
   
    /**
    * Controls which text modification are done upon typing depending on
    * the language.
    * This method has to be modified if other languages are implemented in
    * Eadgyth.
    */
   private void configColoring(Languages language) {
      switch(language) {
         case JAVA:
            type.configColoring(language);
            type.enableIndent(true);
            break;
         case HTML:
            type.configColoring(language);
            type.enableIndent(true);
            break;
         case PERL:
            type.configColoring(language);
            type.enableIndent(true);
            break;
         case PLAIN_TEXT:
            type.enableIndent(false);
            break;
      }
   }
}
