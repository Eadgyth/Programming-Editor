package eg.document;

import javax.swing.JTextPane;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//--Eadgyth--//
import eg.Languages;
import eg.Constants;
import eg.utils.FileUtils;
import eg.utils.Dialogs;
import eg.ui.EditArea;

/**
 * Represents the editable document.
 * <p>
 * Uses {@link TypingEdit} for actions that shall happen in response to
 * text changes.
 */
public final class EditableDocument {

   private final TypingEdit type;
   private final TextDocument textDoc;

   private File docFile = null;
   private String filename = "";
   private String filepath = "";
   private String dir = "";
   String savedContent = "";
   private Languages lang;

   /**
    * Creates an <code>EditableDocument</code> with the specified file.
    * <p>
    * The file defines the language which remains unchangeable unless
    * another file that would define another language is set.
    *
    * @param editArea  a new {@link EditArea}
    * @param f  the file
    */
   public EditableDocument(EditArea editArea, File f) {
      this(editArea);
      displayFileContentImpl(f);
      setFileParams(f);
      savedContent = type.getText();
      setLanguageBySuffix();
   }

   /**
    * Creates a blank <code>EditableDocument</code> with the specified
    * language.
    * <p>
    * A file may be set afterwards which then may change the language.
    *
    * @param editArea  a new {@link EditArea}
    * @param lang  a language in {@link Languages}
    */
   public EditableDocument(EditArea editArea, Languages lang) {
      this(editArea);
      this.lang = lang;
      type.setEditingMode(lang);
   }

   /**
    * Sets an <code>EditingStateReadable</code>
    *
    * @param esr  the reference to {@link EditingStateReadable}
    */
   public void setEditingStateReadable(EditingStateReadable esr) {
      type.setEditingStateReadable(esr);
   }

   /**
    * Sets the indent unit which consists of spaces
    *
    * @param indentUnit  the indend unit
    */
   public void setIndentUnit(String indentUnit) {
      type.setIndentUnit(indentUnit);
   }

   /**
    * Asks the text area that shows this document to gain focus and reads
    * the current editing state
    * @see TypingEdit#readEditingState()
    */
   public void setFocused() {
      textDoc.textArea().requestFocusInWindow();
      type.readEditingState();
   }

   /**
    * Gets the text area that displays this document
    *
    * @return  the text area
    */
    public JTextPane docTextArea() {
       return textDoc.textArea();
    }

   /**
    * Gets the name of this file
    *
    * @return  the filename. The empty string  of no file has been
    * set
    */
   public String filename() {
      return filename;
   }

   /**
    * Gets the parent directory of this file
    *
    * @return  the parent directory. The empty string of no file been set
    */
   public String dir() {
      return dir;
   }

   /**
    * Gets the filepath of this file
    *
    * @return  the filepath. The empty string of no file has been set
    */
   public String filepath() {
      return filepath;
   }

   /**
    * Returns the boolean that, if true, indicates that a file is set
    *
    * @return  the boolean value
    */
   public boolean hasFile() {
      return docFile != null;
   }

   /**
    * Gets this file if a file is set or throws an exception
    *
    * @return  the file
    * @throws  IllegalStateException  if no file is set
    */
   public File docFile() {
      if (docFile == null) {
         throw new IllegalStateException("No file has been set");
      }
      return docFile;
   }

   /**
    * Saves the current text content to this file or throws an
    * exception if no file is set
    *
    * @return  if the content was saved
    * @throws  IllegalStateException  if no file is set
    */
   public boolean saveFile() {
      if (docFile == null) {
         throw new IllegalStateException("No file has been assigned");
      }
      boolean isWritten = writeToFile(docFile);
      if (isWritten) {
         savedContent = type.getText();
         type.resetInChangeState();
      }
      return isWritten;
   }

   /**
    * Sets the specified file and saves the current text content
    * to the file. A previously set file is replaced.
    *
    * @param f  the file
    * @return  if the content was saved to the file
    */
   public boolean setFile(File f) {
      setFileParams(f);
      setLanguageBySuffix();
      type.resetInChangeState();
      savedContent = type.getText();
      return writeToFile(f);
   }
   
   /**
    * Diplays the content of the specified file.
    * The file is not set and the text insertion is not undoable
    *
    * @param f  the file
    */
   public void displayFileContent(File f) {
      displayFileContentImpl(f);
   }

   /**
    * Saves the current text content to the specified file but does
    * not set the file
    *
    * @param f  the file
    */
   public void saveCopy(File f) {
      writeToFile(f);
   }

   /**
    * Returns if the current text equals the text at the last
    * saving point
    *
    * @return  if the current text is saved
    */
   public boolean isSaved() {
      return type.getText().equals(savedContent);
   }

   /**
    * Gets the text of this document
    *
    * @return  the text
    */
   public String docText() {
      return type.getText();
   }

   /**
    * Gets the length of the text of this document
    *
    * @return  the length
    */
   public int docLength() {
      return type.getText().length();
   }

   /**
    * Gets this language
    *
    * @return  this language which is a constant in {@link Languages}
    */
   public Languages language() {
      return lang;
   }

   /**
    * Gets the currently set indent unit
    *
    * @return  the indent unit
    */
   public String currIndentUnit() {
      return type.getIndentUnit();
   }

   /**
    * Changes the language if no file has been assigned
    *
    * @param lang  the language which is a constant in {@link eg.Languages}
    */
   public void changeLanguage(Languages lang) {
      if (null != docFile) {
         throw new IllegalStateException(
               "The language cannot be changed once a file is assigned.");
      }
      this.lang = lang;
      type.setEditingMode(lang);
   }

   /**
    * Enables merging then following text changes to a single undoable
    * unit such that the default division of changes into undoable
    * units is ignored.
    *
    * @param b  the boolean value. True to enable, false to re-disable
    * merging
    * @see TypingEdit #disableBreakpointAdding(boolean)
    */
   public void enableMerging(boolean b) {
      type.disableBreakpointAdding(b);
   }
   
   /**
    * Inserts the specified string at the specified position
    *
    * @param pos  the position
    * @param toInsert  the String to insert
    */
   public void insert(int pos, String toInsert) {
      type.insert(pos, toInsert);
   }

    /**
    * Replaces a section of the documemnt with the specified string
    *
    * @param pos  the position where the section to be replaced starts
    * @param length  the length of the section
    * @param toInsert  the String to insert
    */
   public void replace(int pos, int length, String toInsert) {
      type.replace(pos, length, toInsert);
   }

   /**
    * Removes a section from the document
    *
    * @param pos  the position where the section starts
    * @param length  the length of the section
    * @param useHighlighting  if syntax highlighting of the line that
    * contains the position should be done. Is effectless if this language
    * is not a coding language
    */
   public void remove(int pos, int length, boolean useHighlighting) {
      if (lang == Languages.NORMAL_TEXT) {
         useHighlighting = false;
      }
      type.remove(pos, length, useHighlighting);
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

   //
   //--private--//
   //

   private EditableDocument(EditArea editArea) {
      textDoc = new TextDocument(editArea.textArea());
      LineNumberDocument lineNrDoc = new LineNumberDocument(editArea.lineNrArea(),
            editArea.lineNrWidth());

      type = new TypingEdit(textDoc, lineNrDoc);
   }

   private void displayFileContentImpl(File f) {
      type.enableDocUpdate(false);
      try (BufferedReader br = new BufferedReader(new FileReader(f))) {
         String line = br.readLine();
         String nextLine = br.readLine();
         while (null != line) {
            if (null == nextLine) {
               textDoc.insert(textDoc.doclength(), line);
            }
            else {
               textDoc.insert(textDoc.doclength(), line + "\n");
            }
            line = nextLine;
            nextLine = br.readLine();
         }
      }
      catch (IOException e) {
         FileUtils.logStack(e);
      }
      finally {
         type.enableDocUpdate(true);
      }
   }

   private boolean writeToFile(File f) {
      boolean isWritable;     
      File sameName = new File(f.toString());
      isWritable = f.renameTo(sameName);
      String[] lines = type.getText().split("\n");  
      try (FileWriter writer = new FileWriter(f)) {     
         for (String s : lines) {
            writer.write(s + Constants.LINE_SEP);
         }
         return true;
      }
      catch (IOException e) {
         if (!isWritable) {
            Dialogs.errorMessage(
                  f.getName() + " cannot be saved."
                  + " It may be used by another process.",
                  null);
         }
         else {
            FileUtils.logStack(e);
         }
      }
      return false;
   }

   private void setFileParams(File f) {
      docFile = f;
      filename = f.getName();
      filepath = f.toString();
      dir = f.getParent();
   }

   private void setLanguageBySuffix() {
      String ext = FileUtils.fileExtension(filename);
      switch (ext) {
         case "java":
           lang = Languages.JAVA;
           break;
         case "html": case "htm":
            lang = Languages.HTML;
            break;
         case "js":
            lang = Languages.JAVASCRIPT;
            break;
         case "css":
            lang = Languages.CSS;
            break;
         case "pl": case "pm":
            lang = Languages.PERL;
            break;
         case "R":
            lang = Languages.R;
            break;
         default:
            lang = Languages.NORMAL_TEXT;
      }
      type.setEditingMode(lang);
   }
}
