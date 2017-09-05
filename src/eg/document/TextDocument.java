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
 * Class represents the text document which a file and language
 * can be assigned to
 */
public final class TextDocument {

   private final static String LINE_SEP = System.lineSeparator();
   private final static Preferences PREFS = new Preferences();

   private final JTextPane textArea;
   private final EditArea editArea;
   private final TypingEdit type;

   private File docFile = null;
   private String filename = "";
   private String filepath = "";
   private String dir = "";
   private String content = "";
   private Languages lang;

   /**
    * Creates a TextDocument
    * @param editArea  the reference to the {@link EditArea}
    */
  public TextDocument(EditArea editArea) {
      this.editArea = editArea;
      this.textArea = editArea.textArea();
      type = new TypingEdit(editArea);
      PREFS.readPrefs();
      String indentUnit = PREFS.getProperty("indentUnit");
      setIndentUnit(indentUnit);    
   }

   /**
    * Creates a TextDocument with a specified language
    *
    * @param editArea  the reference to an {@link EditArea}
    * @param lang  the language that is one of the constants in
    * {@link Languages}
    */
   public TextDocument(EditArea editArea, Languages lang) {
      this(editArea);
      type.setUpEditing(lang);
      this.lang = lang;
   }

   /**
    * Returns this text area
    *
    * @return  this text area which is of type {@link EditArea}
    */
    public JTextPane getTextArea() {
       return textArea;
    }

   /**
    * Returns the name of this file
    *
    * @return  the name of this file
    */
   public String filename() {
      return filename;
   }

   /**
    * Returns the path of this file
    *
    * @return  the full path of this file
    */
   public String filepath() {
      return filepath;
   }

   /**
    * Returns the parent directory of this file
    *
    * @return  the parent directory of this file
    */
   public String dir() {
      return dir;
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
    * Returns this language
    *
    * @return  this language which has a constant value in {@link Languages}
    */
   public Languages language() {
      return lang;
   }

   /**
    * Sets the specified file and displays the file content.
    *
    * @param f  the file whose content is displayed in this text area
    */
   public void openFile(File f) {
      if (this.filepath().length() != 0) {
         throw new IllegalStateException("A file has been assigned already");
      }
      assignFile(f);
      displayFileContent(f);
      setLanguageBySuffix();
      setContent();
   }

   /**
    * Saves the current text content to this file
    *
    * @return  if the content was saved
    */
   public boolean saveToFile() {
      return writeToFile(docFile);
   }

   /**
    * Sets the specified file and saves the current text content
    *
    * @param f  the new file
    * @return  if the content was saved
    */
   public boolean saveFileAs(File f) {
      assignFile(f);
      setLanguageBySuffix();
      return writeToFile(f);
   }
   
   /**
    * Saves the current content to the specified file but does not
    * assign the file to this
    *
    * @param f  the file which the current content is saved to
    */
   public void saveCopy(File f) {
      writeToFile(f);
   }

   /**
    * Returns if the text content equals the content since the last
    * saving point
    *
    * @return  if the text content is saved
    */
   public boolean isContentSaved() {
      return content.equals(type.getText());
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
    * Enables/disables syntax coloring and auto-indentation
    *
    * @param isEnabled  true to enable coloring and auto-indentation,
    * false to disable. No effect if this language is plain text
    */
   public void enableTypeEdit(boolean isEnabled) {
      if (Languages.PLAIN_TEXT != lang) {
         type.enableTypeEdit(isEnabled);
      }
   }

   /**
    * Colors a section of text which also may be the entire text
    *
    * @param section  a section of the document. Null to color the entire
    * entire text
    * @param pos  the pos within the entire text where the section to
    * be colored starts. Is 0 if '{code section}' is null.
    */
   public void colorSection(String section, int pos) {
      if (Languages.PLAIN_TEXT != lang) {
         type.colorMultipleLines(section, pos);
      }
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
    * Returns the text in the document
    *
    * @return  the text in the document
    */
   public String getText() {
      return type.getText();
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

   /**
    * Changes the language if no file has been set
    *
    * @param lang  the language which has one of the constant values
    * in {@link eg.Languages}
    */
   public void changeLanguage(Languages lang) {
      if (filename.length() == 0) {
         this.lang = lang;
         type.setUpEditing(lang);
      }
   }

   //
   //----private methods----//
   //

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
      textArea.setCaretPosition(0);
      type.enableDocListen(true);
   }

   /**
    * Saves the current content to this file
    */
   private boolean writeToFile(File f) {
      setContent();
      String[] lines = content.split("\n");
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
