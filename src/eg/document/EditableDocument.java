package eg.document;

import javax.swing.JTextPane;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//--Eadgyth--/
import eg.Languages;
import eg.utils.FileUtils;
import eg.ui.EditArea;
import eg.syntax.SyntaxHighlighter;
import eg.syntax.Highlighter;
import eg.document.styledtext.EditableText;
import eg.document.styledtext.PrintableText;

/**
 * Represents the editable document with a language and possibly a
 * file
 */
public final class EditableDocument {

   private final EditorUpdating update;
   private final EditableText txt;
   private final UndoEditing undo;
   private final Indentation indent;
   private final CurrentLanguage currLang = new CurrentLanguage();

   private File file = null;
   private String filename = "";
   private String filepath = "";
   private String fileParent = "";

   /**
    * Creates an <code>EditableDocument</code> with the specified file
    *
    * @param editArea  the {@link EditArea}
    * @param prevLang  the language set previously
    * @param f  the file
    */
   public EditableDocument(EditArea editArea, File f, Languages prevLang) {
      this(editArea);
      currLang.setLanguage(prevLang);
      setFileParams(f);
      setEditingMode(f);
      update.editText(() -> displayFileContentImpl(f), EditorUpdating.ALL_TEXT);
   }

   /**
    * Creates a blank <code>EditableDocument</code> with the specified
    * language
    *
    * @param editArea  the {@link EditArea}
    * @param lang  the language
    */
   public EditableDocument(EditArea editArea, Languages lang) {
      this(editArea);
      currLang.setLanguage(lang);
      setEditingMode();
   }

   /**
    * Sets an <code>EditingStateReadable</code>
    *
    * @param esr  the {@link EditingStateReadable}
    */
   public void setEditingStateReadable(EditingStateReadable esr) {
      update.setEditingStateReadable(esr);
   }

   /**
    * Sets the indentation mode
    *
    * @param indentUnit  the indent unit which consists of empty spaces
    * @param useTabs  true to indent tabs; false to indent spaces
    */
   public void setIndentationMode(String indentUnit, boolean useTabs) {
      indent.setMode(indentUnit, useTabs);
      txt.setTabLength(indent.indentUnit().length());
   }

   /**
    * Requests that the text area that shows this document gains
    * focus
    */
   public void setFocused() {
      txt.textArea().requestFocusInWindow();
   }

   /**
    * Reads the parameters for the current editing state by
    * invoking all methods in {@link EditingStateReadable}
    *
    */
   public void readEditingState() {
      update.readEditingState();
   }

   /**
    * Returns the text area that displays the text
    *
    * @return  the text area
    */
    public JTextPane textArea() {
       return txt.textArea();
    }

   /**
    * Returns if a file is set
    *
    * @return  true if a file is set, false otherwise
    */
   public boolean hasFile() {
      return file != null;
   }

   /**
    * Returns this file
    *
    * @return  the file
    */
   public File file() {
      checkFileForNull();
      return file;
   }

   /**
    * Returns the path of the parent directory of this file
    *
    * @return  the parent directory
    */
   public String fileParent() {
      checkFileForNull();
      return fileParent;
   }

   /**
    * Returns the last name in the path of this file
    *
    * @return  the filename; the empty string if no file is set
    */
   public String filename() {
      return filename;
   }

   /**
    * Returns the path of this file
    *
    * @return  the path
    */
    public String filepath() {
       checkFileForNull();
       return filepath;
    }

   /**
    * Saves the text content to this file
    *
    * @return  true if the text content could be saved; false
    * otherwise
    */
   public boolean saveFile() {
      checkFileForNull();
      boolean isWritten = writeToFile(file);
      if (isWritten) {
         update.resetChangedState();
      }
      return isWritten;
   }

   /**
    * Sets the specified file and saves the text content to the file
    *
    * @param f  the file
    * @return  true if the text content could be saved; false otherwise
    */
   public boolean setFile(File f) {
      setFileParams(f);
      setEditingMode(f);
      update.editText(() -> {}, EditorUpdating.ALL_TEXT);
      update.resetChangedState();
      return writeToFile(f);
   }

   /**
    * Saves the text content to the specified file but does not
    * set the file in this <code>EditableDocument</code>
    *
    * @param f  the file
    * @return  true if the text content could be saved, false
    * otherwise
    */
   public boolean saveCopy(File f) {
      return writeToFile(f);
   }

   /**
    * Changes the language
    *
    * @param lang  the language to change to
    */
   public void changeLanguage(Languages lang) {
      currLang.setLanguage(lang);
      setEditingMode();
      update.editText(() -> {}, EditorUpdating.ALL_TEXT);
   }

   /**
    * Returns if the text has been changed since creating the
    * document or since the last saving point
    *
    * @return  true if changed; false otherwise
    */
   public boolean isChanged() {
      return update.isChanged();
   }

   /**
    * Returns the document text
    *
    * @return  the text
    */
   public String text() {
      return txt.text();
   }

   /**
    * Returns the length of the document text
    *
    * @return  the length
    */
   public int textLength() {
      return txt.text().length();
   }

   /**
    * Returns the current language
    *
    * @return  the language
    */
   public Languages language() {
      return currLang.lang();
   }

   /**
    * Returns the current indent unit
    *
    * @return  the indent unit
    */
   public String indentUnit() {
      return indent.indentUnit();
   }

   /**
    * Returns if tabs are currently used for indentation
    *
    * @return  true if tabs, false if spaces are used
    */
   public boolean useTabs() {
      return indent.useTabs();
   }

   /**
    * Marks the beginning or the end of a merged undoable unit
    *
    * @param b  true to begin merging, false to end merging
    * @see UndoEditing#disableBreakpointAdding
    */
   public void enableUndoMerging(boolean b) {
      undo.disableBreakpointAdding(b);
   }

   /**
    * Displays the content of the specified file if no file is set
    * in this <code>EditableDocument</code> and the document has not
    * been edited.
    * <p>
    * The file is not set either, the current language is used
    * irrespectively of the file type and the text insertion is not
    * undoable.
    *
    * @param f  the file
    */
   public void displayFileContent(File f) {
      checkFileForNonNull();
      update.editText(() -> displayFileContentImpl(f), EditorUpdating.ALL_TEXT);
   }

   /**
    * Replaces the current text with the content of the specified
    * if no file is set in this <code>EditableDocument</code>.
    * <p>
    * The file is not set either and the language is set according to
    * the file type
    *
    * @param f  the file
    */
   public void replaceWithFileContent(File f) {
      checkFileForNonNull();
      setEditingMode(f);
      TextChange tc = () -> {
         txt.remove(0, textLength());
         undo.disableBreakpointAdding(true);
         readFileContent(f);
         undo.disableBreakpointAdding(false);
         txt.textArea().setCaretPosition(0);
      };
      update.editText(tc, EditorUpdating.ALL_TEXT);
   }

   /**
    * Inserts text
    *
    * @param pos  the insert position
    * @param s  the string containing the insertion
    */
   public void insert(int pos, String s) {
      update.editText(() -> txt.insert(pos, s), EditorUpdating.INSERT);
   }

   /**
    * Inserts text and ignores syntax highlighing
    *
    * @param pos  the insert position
    * @param s  the string containing the insertion
    */
   public void insertIgnoreSyntax(int pos, String s) {
      update.editText(() -> txt.insert(pos, s), EditorUpdating.OMIT);
   }

   /**
    * Removes text
    *
    * @param pos  the start position of the removal
    * @param length  the length of the removal
    */
   public void remove(int pos, int length) {
      txt.remove(pos, length);
   }

   /**
    * Removes text and ignores syntax highlighting
    *
    * @param pos  the start position of the removal
    * @param length  the length of the removal
    */
   public void removeIgnoreSyntax(int pos, int length) {
      update.editText(() -> txt.remove(pos, length), EditorUpdating.OMIT);
   }

   /**
    * Replaces text
    *
    * @param pos  the position of the replacement
    * @param length  the length of the section to remove
    * @param s  the string containing the replacement
    * @param merge  true for merging into one undoable edit, false
    * to treat removal and insertion as two separate edits
    */
   public void replace(int pos, int length, String s, boolean merge) {
      TextChange tc = () -> {
         if (merge) {
            undo.disableBreakpointAdding(true);
         }
         if (length != 0) {
            txt.remove(pos, length);
         }
         txt.insert(pos, s);
         if (merge) {
            undo.disableBreakpointAdding(false);
         }
      };
      update.editText(tc, EditorUpdating.INSERT);
   }

   /**
    * Undoes edits
    */
   public void undo() {
      update.updateUndoRedo(undo::undo);
   }

   /**
    * Redoes edits
    */
   public void redo() {
      update.updateUndoRedo(undo::redo);
   }

   /**
    * Prints the document text to a printer
    */
    public void print() {
      PrintableText printTxt = new PrintableText(txt.text(), textArea().getFont());
      if (currLang.lang() != Languages.NORMAL_TEXT) {
         Highlighter hl = currLang.createHighlighter();
         SyntaxHighlighter sh = new SyntaxHighlighter(printTxt);
         sh.setHighlighter(hl);
         sh.highlight();
      }
      printTxt.print();
   }

   //
   //--private--/
   //

   private EditableDocument(EditArea editArea) {
      txt = new EditableText(editArea.textArea());
      undo = new UndoEditing(txt);
      LineNumbers lineNum = new LineNumbers(editArea.lineNrArea());
      indent = new Indentation(txt);
      update = new EditorUpdating(txt, undo, lineNum, indent);
      editArea.textArea().addPropertyChangeListener("font", e ->
         txt.setTabLength(indent.indentUnit().length())
      );
   }

   private void setFileParams(File f) {
      file = f;
      filename = f.getName();
      filepath = f.getPath();
      fileParent = f.getParent();
   }

   private void displayFileContentImpl(File f) {
      update.disableUpdating(true);
      readFileContent(f);
      update.disableUpdating(false);
   }

   private void readFileContent(File f) {
      try (BufferedReader br = new BufferedReader(new FileReader(f))) {
         StringBuilder sb = new StringBuilder();
         String line;
         while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
         }
         txt.textArea().setText(sb.toString());
      }
      catch (IOException e) {
         FileUtils.log(e);
      }
   }

   private void setEditingMode(File f) {
      currLang.setLanguage(f.toString());
      setEditingMode();
   }

   private void setEditingMode() {
      update.setEditingMode(currLang);
   }

   private boolean writeToFile(File f) {
      boolean isWriteable = FileUtils.isWriteable(f);
      if (!isWriteable) {
          return false;
      }
      try (FileWriter writer = new FileWriter(f)) {
         if (txt.text().equals("")) {
            writer.write("");
         }
         else {
            String[] lines = txt.text().split("\n");
            for (String s : lines) {
               writer.write(s + System.lineSeparator());
            }
         }
         return true;
      }
      catch (IOException e) {
         FileUtils.log(e);
      }
      return false;
   }

   private void checkFileForNull() {
      if (file == null) {
         throw new IllegalStateException("No file has been set.");
      }
   }

   private void checkFileForNonNull() {
      if (file != null) {
         throw new IllegalStateException(
               "Cannot read in a file in EditableDocument that already has a file");
      }
   }
}
