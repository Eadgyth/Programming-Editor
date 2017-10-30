package eg.document;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JTextPane;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//--Eadgyth--//
import eg.Languages;

import eg.utils.FileUtils;
import eg.ui.EditArea;

/**
 * Represents the document that is either initialized with a language and
 * which a file may be assigned to afterwards or is initialized with a file
 * which defines the language
 */
public final class FileDocument {

   private final static String LINE_SEP = System.lineSeparator();

   private final TypingEdit type;
   private final TextDocument textDoc;
   private final LineNumberDocument lineNrDoc;

   private File docFile = null;
   private Languages lang;
   private String filename = "";
   private String filepath = "";
   private String dir = "";
   private String content = "";
   
   /**
    * Creates a <code>FileDocument</code> with the specified file whose
    * content is displayed.<br>
    * The file extension defines the language. The language cannot be
    * changed afterwards unless a new file with another extension is set.
    *
    * @param editArea  a new {@link EditArea}
    * @param f  the file
    */
   public FileDocument(EditArea editArea, File f) {
      this(editArea);
      assignFile(f);
      displayFileContent(f);
      setLanguageBySuffix();
      setContent();
   }

   /**
    * Creates a <code>FileDocument</code> with the specified
    * language and which a file can be assigned to afterwards
    *
    * @param editArea  a new {@link EditArea}
    * @param lang  a language in {@link Languages}
    */
   public FileDocument(EditArea editArea, Languages lang) {
      this(editArea);
      this.lang = lang;
      type.setEditingMode(lang);
   }
   
   /**
    * Gets the text area that displays this document
    *
    * @return  the text area
    */
    public JTextPane docTextArea() {
       return textDoc.docTextArea();
    }
    
   /**
    * Gets the name of this file
    *
    * @return  the filename. The empty string  of no file has been
    * assinged
    */
   public String filename() {
      return filename;
   }
   
   /**
    * Gets the parent directory of this file
    *
    * @return  the parent directory. The empty string of no file
    + has been assinged
    */
   public String dir() {
      return dir;
   }

   /**
    * Gets the path of this file
    *
    * @return  the filepath. The empty string of no file has been
    * assinged
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
    * @return  the file
    */
   public File docFile() {
      if (docFile == null) {
         throw new IllegalStateException("No file has been assigned");
      }
      return docFile;
   }
   
   /**
    * Sets a <code>TextSelectionLister</code>
    *
    * @param tsl  a {@link TextSelectionListener}
    */
   public void setTextSelectionListener(TextSelectionListener tsl) {
      type.setTextSelectionListener(tsl);
   }
   
   /**
    * Sets an <code>UndoableChangeListener</code>
    *
    * @param ucl  an {@link UndoableChangeListener}
    */
   public void setUndoableChangeListener(UndoableChangeListener ucl) {
      type.setUndoableChangeListener(ucl);
   }
   
   /**
    * Sets an <code>LineAndColumnReadable</code>
    *
    * @param lcr  an {@link LineAndColumnReadable}
    */
   public void setLineAndColumnReadable(LineAndColumnReadable lcr) {
      type.setLineAndColumnReadable(lcr);
   }
   
   /**
    * Sets the indentation unit which consists in any number of spaces
    *
    * @param indentUnit  the String that consists of a certain number of
    * white spaces
    */
   public void setIndentUnit(String indentUnit) {
      type.setIndentUnit(indentUnit);
   }

   /**
    * Saves the current text content to this file
    *
    * @return  if the content was saved
    */
   public boolean saveFile() {
      if (docFile == null) {
         throw new IllegalStateException("No file has been assigned");
      }
      setContent();
      return writeToFile(docFile);
   }

   /**
    * Sets the specified file and saves the current text content.
    * This file is replaced
    *
    * @param f  the file
    * @return  if the content was saved to the file
    */
   public boolean setFile(File f) {
      assignFile(f);
      setLanguageBySuffix();
      setContent();
      return writeToFile(f);
   }
   
   /**
    * Saves the current content to the specified file but does not
    * replace this file
    *
    * @param f  the file which the current content is saved to
    */
   public void saveCopy(File f) {
      writeToFile(f);
   }
   
   /**
    * Returns if the text equals the content since the last saving
    * point
    *
    * @return  if the current text is saved
    */
   public boolean isContentSaved() {
      return content.equals(type.getText());
   }
   
   /**
    * Gets the text in this documument
    *
    * @return  the text
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
    * Changes the language if no file has been assigned
    *
    * @param lang  the language which has one of the constant values in
    * {@link eg.Languages}
    */
   public void changeLanguage(Languages lang) {
      if (hasFile()) {
         throw new IllegalStateException(
               "The language cannot be changed since a file is already set.");
      }
      this.lang = lang;
      type.setEditingMode(lang);
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
    * Enables or disables actions in responce to the editing of source code.
    * This affects syntax coloring and auto-indentation. Has no effect if this
    * language is not a coding language.
    *
    * @param isEnabled  true to enable, false to disable
    */
   public void enableCodeEditing(boolean isEnabled) {
      if (Languages.NORMAL_TEXT != lang) {
         type.enableCodeEditing(isEnabled);
      }
   }

   /**
    * Colors a section of the document that starts at the specified
    * position. Has no effect if this language is not a coding language
    *
    * @param section  the section
    * @param pos  the position
    */
   public void colorSection(String section, int pos) {
      if (Languages.NORMAL_TEXT != lang) {
         type.colorMultipleLines(section, pos);
      }
   }
   
   /**
    * Gets the number of the line where the cursor is positioned
    *
    * @return  the number
    */
   public int lineNrAtCursor() {
      return type.lineNrAtCursor();
   }
   
   /**
    * Gets the number of the column within the line where the cursor
    * is located
    *
    * @return  the number
    */
   public int columnNrAtCursor() {
      return type.columnNrAtCursor();
   }
   
   /**
    * Returns if edits can be undone
    * 
    * @return  if edits can be undone
    */
   public boolean canUndo() {
      return type.canUndo();
   }
   
   /**
    * Returns if edits can be redone
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
    * Inserts the string <code>toInsert</code> at the specified
    * position
    *
    * @param pos  the position
    * @param toInsert  the String to insert
    */
   public void insert(int pos, String toInsert) {
      textDoc.insert(pos, toInsert);
   }

   /**
    * Removes text of the specified length at the specified
    * position
    *
    * @param pos  the position
    * @param length  the length
    */
   public void remove(int pos, int length) {
      textDoc.remove(pos, length);
   }

   /**
    * Asks the text area that shows this text document to gain the focus
    */
   public void requestFocus() {
      textDoc.docTextArea().requestFocusInWindow();
   }

   //
   //--private methods--/
   //

   private FileDocument(EditArea editArea) {
      textDoc = new TextDocument(editArea.textArea());
      lineNrDoc = new LineNumberDocument(editArea.lineNrDoc(),
            editArea.lineNrWidth());
      type = new TypingEdit(textDoc, lineNrDoc);
   }

   private void displayFileContent(File f) {
      type.enableDocListen(false);
      try (BufferedReader br = new BufferedReader(new FileReader(f))) {
         String line = br.readLine();
         String nextLine = br.readLine();
         while (null != line) {            
            if (null == nextLine) {
               insert(textDoc.length(), line);
            }
            else {
               insert(textDoc.length(), line + "\n");
            }
            line = nextLine;
            nextLine = br.readLine();
         }
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }
      finally {
         type.enableDocListen(true);
      }
   }

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
            lang = Languages.NORMAL_TEXT;
      }
      type.setEditingMode(lang);
   }
}
