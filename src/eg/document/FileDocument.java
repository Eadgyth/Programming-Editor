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
import eg.ui.EditArea;

/**
 * Represents the document that is either initialized with a language and
 * which a file may be assigned to afterwards or is initialized with a file
 * which defines the then unchangeable language.
 */
public final class FileDocument {

   private final TypingEdit type;
   private final TextDocument textDoc;

   private File docFile = null;
   private String filename = "";
   private String filepath = "";
   private String dir = "";
   String content = "";
   private Languages lang;
   
   /**
    * Creates a <code>FileDocument</code> with the specified file whose
    * content is displayed.
    *
    * @param editArea  a new {@link EditArea}
    * @param f  the file
    */
   public FileDocument(EditArea editArea, File f) {
      this(editArea);
      assignFile(f);
      displayFileContent(f);
      content = type.getText();
      setLanguageBySuffix();
   }

   /**
    * Creates a blank <code>FileDocument</code> with the specified
    * language
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
    * Sets an <code>EditingStateReadable</code>
    *
    * @param esr  a {@link EditingStateReadable}
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
    * Returns the boolean that indicates if a file has been assigned
    *
    * @return  the boolean value, true if a file has been assigned
    */
   public boolean hasFile() {
      return docFile != null;
   }
   
   /**
    * Gets this file. Method must be used only after assigning a
    * file.
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
    * Saves the current text content to this file if a file was set
    *
    * @return  if the content was saved
    */
   public boolean saveFile() {
      if (docFile == null) {
         throw new IllegalStateException("No file has been assigned");
      }
      type.resetChangeState();
      content = type.getText();
      return writeToFile(docFile);
   }

   /**
    * Sets the specified file and saves the current text content to the
    * this file. Any previously set file reference is replaced
    *
    * @param f  the file
    * @return  if the content was saved to the file
    */
   public boolean setFile(File f) {
      assignFile(f);
      setLanguageBySuffix();
      type.resetChangeState();
      content = type.getText();
      return writeToFile(f);
   }
   
   /**
    * Displays the content of the specified file but does not set the file
    *
    * @param f  the file
    */
   public void displayFileContent(File f) {
      type.enableDocListen(false);
      try (BufferedReader br = new BufferedReader(new FileReader(f))) {
         String line = br.readLine();
         String nextLine = br.readLine();
         while (null != line) {            
            if (null == nextLine) {
               insert(textDoc.doclength(), line);
            }
            else {
               insert(textDoc.doclength(), line + "\n");
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
   
   /**
    * Saves the current content to the specified file but does not set
    * the file
    *
    * @param f  the file which the current content is saved to
    */
   public void saveCopy(File f) {
      writeToFile(f);
   }
   
   /**
    * Returns if the text equals the text since the last saving point
    *
    * @return  if the current text is saved
    */
   public boolean isSaved() {
      return type.getText().equals(content);
   }
   
   /**
    * Gets the text in this document that is stored in
    * <code>TypingEdit</code>
    *
    * @return  the text
    */
   public String docText() {
      return type.getText();
   }
   
   /**
    * Gets the text length in this document
    * 
    * @return  the length
    */
   public int docLength() {
      return textDoc.doclength();
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
      if (hasFile()) {
         throw new IllegalStateException(
               "The language cannot be changed once a file is assigned.");
      }
      this.lang = lang;
      type.setEditingMode(lang);
   }

   /**
    * Sets the boolean that specifies if actions in responce to the editing
    * of source code are enabled.<br>
    * These actions are syntax coloring and auto-indentation.
    *
    * @param b  the boolean value. Has no effect if this language is normal
    * text
    */
   public void enableCodeEditing(boolean b) {
      if (Languages.NORMAL_TEXT != lang) {
         type.enableCodeEditing(b);
      }
   }

   /**
    * Highlights the specified <code>section</code> of the document
    * text.<br>
    * Has no effect if this language is normal text
    *
    * @param section  the section
    * @param pos  the position where section starts
    * @see TypingEdit#highlightMultipleLines(String, int)
    */
   public void highlightSection(String section, int pos) {
      if (Languages.NORMAL_TEXT != lang) {
         type.highlightMultipleLines(section, pos);
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
    * Inserts the specified string at the specified position
    *
    * @param pos  the position
    * @param toInsert  the String
    */
   public void insert(int pos, String toInsert) {
      textDoc.insert(pos, toInsert);
   }

   /**
    * Removes text with the specified length that starts at the specified
    * position
    *
    * @param pos  the position
    * @param length  the length
    */
   public void remove(int pos, int length) {
      textDoc.remove(pos, length);
   }

   //
   //--private--
   //

   private FileDocument(EditArea editArea) {
      textDoc = new TextDocument(editArea.textArea());
      LineNumberDocument lineNrDoc = new LineNumberDocument(editArea.lineNrArea(),
            editArea.lineNrWidth());

      type = new TypingEdit(textDoc, lineNrDoc);
   }

   private boolean writeToFile(File f) {
      String[] lines = type.getText().split("\n");
      try (FileWriter writer = new FileWriter(f)) {
         for (String s : lines) {
            writer.write(s + Constants.LINE_SEP);
         }
         return true;
      }
      catch(IOException e) {
         FileUtils.logStack(e);
      }
      return false;
   }

   private void assignFile(File f) {
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
         case "html": case "htm": case "xml": // no project associated with xml
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
         default:
            lang = Languages.NORMAL_TEXT;
      }
      type.setEditingMode(lang);
   }
}
