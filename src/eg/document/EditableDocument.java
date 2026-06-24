package eg.document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.swing.JTextPane;

//--Eadgyth--/
import eg.Languages;
import eg.document.styledtext.EditableText;
import eg.document.styledtext.PrintableText;
import eg.syntax.Highlighter;
import eg.syntax.SyntaxHighlighter;
import eg.ui.EditArea;
import eg.utils.Dialogs;
import eg.utils.FileUtils;
import eg.utils.LinesFinder;

/**
 * Represents the editable document with a language and possibly a
 * file
 */
public final class EditableDocument {

   private final EditorUpdating update;
   private final EditableText txt;
   private final UndoEditing undo;
   private final Indentation indent;
   private final FileCharset fileCharset;
   private final CurrentLanguage currLang = new CurrentLanguage();

   private File file = null;
   private String filename = "";
   private String filepath = "";
   private String fileParent = "";

   private Charset currentCharset = StandardCharsets.UTF_8;
   private String charsetDisplay = currentCharset.displayName();
   private boolean hasEncodingErrors = false;

   /**
    * Creates an <code>EditableDocument</code> with the specified file
    *
    * @param editArea  the {@link EditArea}
    * @param prevLang  the language set previously
    * @param fileCharset  the {@link FileCharset} which is supposed
    * to be configured
    * @param f  the file
    */
   public EditableDocument(EditArea editArea, File f, FileCharset fileCharset,
         Languages prevLang) {

      this(editArea, fileCharset);
      currLang.setLanguage(prevLang);
      setFileParams(f);
      setEditingMode(f);
      update.editText(this::displayFileContentImpl, EditorUpdating.ALL_TEXT);
   }

   /**
    * Creates a blank <code>EditableDocument</code> with the specified
    * language
    *
    * @param editArea  the {@link EditArea}
    * @param lang  the language
    */
   public EditableDocument(EditArea editArea, Languages lang) {
      this(editArea, new FileCharset());
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
    * Returns the display for the encoding state.
    *
    * The display name may be suffixed with:
    * <ul>
    * <li>"error", indicating UTF-8 decoding mismatches.
    * <li>"system default", indicating a fallback to the system or
    * JVM default encoding if a file is not UTF-8.
    * <li>"selected", indicating a fallback to a selected fallback
    * charset.
    * </ul>
    *
    * @return  the charset display
    */
   public String charsetDisplay() {
      return charsetDisplay;
   }

   /**
    * Returns if this file was read in with a fallback charset.
    *
    * False is returned if the file was detected as valid UTF-8,
    * or if this is not the case and a fallback was not possible.
    *
    * @return  true if file was read in with a fallback charset,
    * false otherwise
    */
   public boolean isFallback() {
      return fileCharset.isFallback();
   }

   /**
    * Converts to UTF-8 with pending confirmation by saving.
    */
   public void convertToUtf8() {
      checkFileForNull();
      if (currentCharset == StandardCharsets.UTF_8) {
         throw new IllegalStateException(
               "Can't convert.The charset is already UTF-8");
      }
      currentCharset = StandardCharsets.UTF_8;
      update.setPendingChange(true);
      updateCharsetDisplay();
   }

   /**
    * Reverts to the previous fallback charset with pending
    * confirmation by saving.
    */
   public void revertToFallback() {
      checkFileForNull();
      if (currentCharset != StandardCharsets.UTF_8) {
         throw new IllegalStateException(
               "Can't revert. The charset is not UTF-8");
      }
      currentCharset = fileCharset.charset();
      update.setPendingChange(true);
      updateCharsetDisplay();
   }

   /**
    * Reads the parameters for the current editing state by invoking
    * all methods in {@link EditingStateReadable}
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
    * Sets the specified file and saves the text content to the file.
    *
    * @param f  the file
    * @return  true if the text content could be saved; false otherwise
    */
   public boolean setFile(File f) {
      boolean isWritten = writeToFile(f);
      if (isWritten) {
         setFileParams(f);
         setEditingMode(f);
         update.editText(() -> {}, EditorUpdating.ALL_TEXT);
         update.resetChangedState();
      }
      return isWritten;
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
      fileCharset.readBytes(f);
      update.editText(this::displayFileContentImpl, EditorUpdating.ALL_TEXT);
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

   private EditableDocument(EditArea editArea, FileCharset fileCharset) {
      this.fileCharset = fileCharset;
      currentCharset = fileCharset.charset();
      hasEncodingErrors = fileCharset.isFailedFallback();
      updateCharsetDisplay();
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

   private void displayFileContentImpl() {
      update.disableUpdating(true);
      readFileContent();
      update.disableUpdating(false);
   }

   private void readFileContent() {
      String content = new String(fileCharset.getBytes(), currentCharset);
      if (!content.isEmpty()) {
         if (fileCharset.hasBOM()) {
            content = content.substring(1);
         }
         content = content.replace("\r\n", "\n").replace("\r", "\n");
         if (!content.isEmpty() && !content.endsWith("\n")) {
            content += "\n";
         }
         txt.textArea().setText(content);
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
      if (!FileUtils.isWriteable(f)) {
         return false;
      }
      if (hasEncodingErrors) {
         int find = findReplacementChar();
         if (find != -1 && keepUtf8ErrWarning(find)) {
           return false;
         }
      }
      try (BufferedWriter writer = new BufferedWriter(
         new OutputStreamWriter(
            new FileOutputStream(f), currentCharset))) {

         String text = txt.text();
         if (fileCharset.hasBOM()) {
            writer.write("\uFEFF");
         }
         writer.write(text.replace("\n", System.lineSeparator()));
         hasEncodingErrors = false;
         update.setPendingChange(false);
         updateCharsetDisplay();
         return true;
      }
      catch (IOException e) {
         FileUtils.log(e);
      }
      return false;
   }

   private int findReplacementChar() {
      for (int i = 0; i < txt.text().length(); i++) {
         char c = txt.text().charAt(i);
         if (c == '\uFFFD' || c == '\u0000') {
            hasEncodingErrors = true;
            return i;
         }
      }
      hasEncodingErrors = false;
      updateCharsetDisplay();
      return -1;
   }

   private void updateCharsetDisplay() {
      String name = currentCharset.displayName();
      if (hasEncodingErrors) {
         //
         // invalid UTF-8 with no fallback
         name = name + " error";
      }
      //
      // fallback but no pending conversion to UTF-8
      if (fileCharset.isFallback()
            && currentCharset != StandardCharsets.UTF_8) {

         if (fileCharset.isUserFallback()) {
            name = name + " (selected)";
         }
         else {
            name = name + " (system default)";
         }
      }
      if (fileCharset.hasBOM()) {
         name = name + " (with BOM)";
      }
      charsetDisplay = name;
   }

   private boolean keepUtf8ErrWarning(int findReplacement) {
      Object[] options = { "Cancel", "Save" };
      int result = Dialogs.warnConfirmOptions(
            utf8ErrWarning(findReplacement), "Encoding", options);

      return result != 1;
   }

   private String utf8ErrWarning(int findReplacement) {
      int line = LinesFinder.lineNrAtPos(txt.text(), findReplacement);
      return
         "<html>" +
         "<b>The file contains invalid UTF-8 characters.</b><br><br>" +
         "First replacement character at line " + line + "." +
         " Invalid characters cannot be recovered once saved." +
         "</html>";
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
