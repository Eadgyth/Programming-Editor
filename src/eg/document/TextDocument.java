package eg.document;

import java.awt.EventQueue;

import javax.swing.JTextPane;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//--Eadgyth--//
import eg.Preferences;
import eg.Languages;

import eg.utils.FileUtils;
import eg.ui.EditArea;

/**
 * Represents a text document with a language or a file that
 * defines the language.<br>
 * 
 */
public final class TextDocument {

   private final static String LINE_SEP = System.lineSeparator();
   private final static Preferences PREFS = new Preferences();

   private final JTextPane textArea;
   private final EditArea editArea;
   private final TypingEdit type;

   private File docFile = null;
   private Languages lang;
   private String filename = "";
   private String filepath = "";
   private String dir = "";
   private String content = "";
   
   /**
    * Creates a <code>TextDocument</code> with the specified file. The file
    * content is displayed.<br>
    * The file extension defines the language. The language cannot be
    * changed afterwards but a new file may be set which also may define
    * another language
    *
    * @param editArea  a new {@link EditArea}
    * @param f  the file
    */
   public TextDocument(EditArea editArea, File f) {
      this(editArea);
      assignFile(f);
      displayFileContent(f);
      setLanguageBySuffix();
      setContent();
   }

   /**
    * Creates a <code>TextDocument</code> with the specified
    * language
    *
    * @param editArea  a new {@link EditArea}
    * @param lang  the language that is one of the constants in
    * {@link Languages}
    */
   public TextDocument(EditArea editArea, Languages lang) {
      this(editArea);
      type.setUpEditing(lang);
      this.lang = lang;
   }
   
   /**
    * Returns the text area of this <code>EditArea</code>
    *
    * @return  the text area of this {@link EditArea}
    */
    public JTextPane textArea() {
       return textArea;
    }
    
    /**
    * Gets the name of this file
    *
    * @return  the name of this file.The empty string
    * of no file has been assinged
    */
   public String filename() {
      return filename;
   }
   
   /**
    * Gets the parent directory of this file.
    *
    * @return  the parent directory of this file. The empty string
    * of no file has been assinged
    */
   public String dir() {
      return dir;
   }

   /**
    * Gets the path of this file
    *
    * @return  the full path of this file. The empty string of
    * no file has been assinged
    */
   public String filepath() {
      return filepath;
   }
   
   /**
    * Returns if a file has been assigned
    *
    * @return  if a file has been assigned
    */
   public boolean hasFile() {
      return docFile != null;
   }
   
   /**
    * Gets this file if a file has been assigned
    *
    * @return  this file
    */
   public File docFile() {
      if (docFile == null) {
         throw new IllegalStateException("No file has"
               + " been assigned");
      }
      return docFile;
   }
   
   /**
    * Sets a <code>TextSelectionLister</code>
    *
    * @param sl  a {@link TextSelectionListener}
    */
   public void setTextSelectionListener(TextSelectionListener sl) {
      type.setTextSelectionListener(sl);
   }
   
   /**
    * Sets an <code>UndoableChangeListener</code>
    *
    * @param ul  an {@link UndoableChangeListener}
    */
   public void setUndoableChangeListener(UndoableChangeListener ul) {
      type.setUndoableChangeListener(ul);
   }

   /**
    * Saves the current text content to this file
    *
    * @return  if the content was saved
    */
   public boolean saveFile() {
      if (docFile == null) {
         throw new IllegalStateException("No file has"
               + " been assigned");
      }
      setContent();
      return writeToFile(docFile);
   }

   /**
    * Assigns the specified file and saves the current text content to
    * the file. If a file that has been assigned before is replaced.
    *
    * @param f  the file
    * @return  if the content was saved
    */
   public boolean setFile(File f) {
      assignFile(f);
      setLanguageBySuffix();
      setContent();
      return writeToFile(f);
   }
   
   /**
    * Saves the current content to the specified file but does not
    * assign the file
    *
    * @param f  the file which the current content is saved to
    */
   public void saveCopy(File f) {
      writeToFile(f);
   }
   
   /**
    * Returns if the text equals the content since the last
    * saving point
    *
    * @return  if the current text is saved
    */
   public boolean isContentSaved() {
      return content.equals(type.getText());
   }
   
   /**
    * Returns the text in the document
    *
    * @return  the text in the document
    */
   public String getText() {
      return type.getText();
   }
   
   /**
    * Returns this language
    *
    * @return  this language which has a constant value in {@link Languages}
    */
   public Languages language() {
      return lang;
   }
   
   /**
    * If the set Language is a coding language, i.e. not plain text
    *
    * @return  the set Language is any coding language, i.e. not plain text
    */
   public boolean isCodingLanguage() {
      return Languages.PLAIN_TEXT != lang;
   }
   
   /**
    * Changes the language if no file has been assigned
    *
    * @param lang  the language which has one of the constant values
    * in {@link eg.Languages}
    */
   public void changeLanguage(Languages lang) {
      if (hasFile()) {
         throw new IllegalStateException("The language cannot be changed.");
      }
      this.lang = lang;
      type.setUpEditing(lang);
   }

   /**
    * Sets the indentation unit
    *
    * @param indentUnit  the String that consists of a certain number of
    * white spaces
    */
   public void setIndentUnit(String indentUnit) {
      if (indentUnit == null || !indentUnit.matches("[\\s]+")) {
         throw new IllegalArgumentException(
               "Argument indentUnit is incorrect");
      }
      type.setIndentUnit(indentUnit);
      PREFS.storePrefs("indentUnit", type.getIndentUnit());
   }

   /**
    * Returns the current indentation unit
    *
    * @return the current indentation unit
    */
   public String getIndentUnit() {
      return type.getIndentUnit();
   }

   /**
    * Enables/disables editing during typing. Affects syntax coloring and
    * auto-indentation.
    *
    * @param isEnabled  true/false to enable/disable editing during
    * typing. Has no effect if this language is plain text
    */
   public void enableTypeEdit(boolean isEnabled) {
      if (!isCodingLanguage()) {
         type.enableTypeEdit(isEnabled);
      }
   }

   /**
    * Colors a section of text
    *
    * @param section  a section of the document which also may be the
    * entire text. If null the entire text is assumed. The complete lines
    * are colored even if it does not start a line start or end at line end
    * @param pos  the pos within the entire text where the section to
    * be colored starts
    */
   public void colorSection(String section, int pos) {
      if (Languages.PLAIN_TEXT != lang) {
         type.colorMultipleLines(section, pos);
      }
   }
   
   /**
    * Returns if edits can be redone
    * 
    * @return  if edits can be redone
    */
   public boolean canUndo() {
      return type.canUndo();
   }
   
   /**
    * Returns if edits can be undone
    * 
    * @return  if edits can be redone
    */
   public boolean canRedo() {
      return type.canRedo();
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
    * Inserts text in this text area
    *
    * @param pos  the position where new text is inserted
    * @param toInsert  the String that contains the text to insert
    */
   public void insertStr(int pos, String toInsert) {
      editArea.insertStr(pos, toInsert);
   }

   /**
    * Removes text from this document
    *
    * @param start  the position where text to be removed starts
    * @param length  the length of the text to be removed
    */
   public void removeStr(int start, int length) {
      editArea.removeStr(start, length);
   }

   /**
    * Asks this text area to gain the focus
    */
   public void requestFocus() {
      textArea.requestFocusInWindow();
   }

   //
   //----private methods----//
   //
   
   private TextDocument(EditArea editArea) {
      this.editArea = editArea;
      this.textArea = editArea.textArea();
      type = new TypingEdit(editArea);
      PREFS.readPrefs();
      String indentUnit = PREFS.getProperty("indentUnit");
      setIndentUnit(indentUnit); 
   }

   private void displayFileContent(File f) {
      type.enableDocListen(false);
      try (BufferedReader br = new BufferedReader(new FileReader(f))) {
         String line = br.readLine();
         String nextLine = br.readLine();
         while (null != line) {            
            if (null == nextLine) {
               insertStr(editArea.getDocText().length(), line);
            }
            else {
               insertStr(editArea.getDocText().length(), line + "\n");
            }
            line = nextLine;
            nextLine = br.readLine();
         }
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }
      type.enableDocListen(true);
   }

   /**
    * Saves the current content to this file
    */
   private boolean writeToFile(File f) {
      String[] lines = type.getText().split("\n");
      try (FileWriter writer = new FileWriter(f)) {
         for (String s : lines) {
            writer.write(s + LINE_SEP);
         }
         return true;
      }
      catch(IOException e) {
         FileUtils.logStack(e);
      }
      return false;
   }

   private void setContent() {
      content = type.getText();
   }

   private void assignFile(File f) {
      docFile = f;
      filename = f.getName();
      filepath = f.toString();
      dir = f.getParent();
   }

   private void setLanguageBySuffix() {
      String suffix = FileUtils.fileSuffix(filename);
      switch (suffix) {
         case "java":
           lang = Languages.JAVA;
           break;
         case "html": case "htm": case "xml":
            lang = Languages.HTML;
            break;
         case "js":
            lang = Languages.JAVASCRIPT;
            break;
         case "pl": case "pm":
            lang = Languages.PERL;
            break;
         default:
            lang = Languages.PLAIN_TEXT;
      }
      type.setUpEditing(lang);
   }
}
