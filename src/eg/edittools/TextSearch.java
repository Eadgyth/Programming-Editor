package eg.edittools;

import javax.swing.JTextPane;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.document.FileDocument;

/**
 * The search and maybe replacemant of text or words in the
 * <code>FileDocument</code> that is currently viewed in the main editor area
 */
public class TextSearch {
   
   private boolean isUpward = false;
   private boolean reqWord = false;
   private boolean isCaseSensitive = false;
   private int pos = 0;

   private FileDocument doc;
   private JTextPane textArea;
   
   /**
    * Sets the <code>FileDocument</code> which text in searched in
    *
    * @param doc  the <code>FileDocument</code>
    */
   public void setFileDocument(FileDocument doc) {
      this.doc = doc;
      this.textArea = doc.docTextArea();
   }
   
   /**
    * Sets the boolean that indicates if the search is restricted to a word.
    * Default is not restricted
    *
    * @param b  the boolen value
    */
   public void setRequireWord(boolean b) {
      reqWord = b;
   }
   
   /**
    * Sets the boolean that indicates the search direction. This is true
    * for an upward search and false for a downward search. Default is down.
    *
    * @param b  the boolean value
    */
    public void setUpwardSearch(boolean b) {
       isUpward = b;
    }
    
    /**
     * Sets the boolean that indicates if the text search is case
     * sensitive. This true for sensitivity and false for insensitivity.
     * Default is insensitive.
     *
     * @param b  the boolean value
     */
    public void setCaseSensitivity(boolean b) {
       isCaseSensitive = b;
    }
   
   /**
    * Resets the search to the start or end of the document depending
    * on whether the search direction is down- or upward, respectively 
    */
   public void resetSearchStart() {
      if (isUpward) {
         pos = doc.docLength() - 1;
      }
      else {
         pos = 0;
      }
   }
   
   /**
    * Searches the specified string in this document.
    * The search direction is set in {@link #setUpwardSearch(boolean)}
    * and starts at current caret position
    *
    * @param searchTerm  the string to search
    */
   public void searchText(String searchTerm) {
      String content = doc.docText();
      if (!isCaseSensitive) {
         content = content.toLowerCase();
         searchTerm = searchTerm.toLowerCase();
      }
      if (isUpward) {
         pos = textArea.getSelectionStart() - 1;
         searchTextUp(content, searchTerm);
      }
      else {
         pos = textArea.getCaretPosition();
         searchTextDown(content, searchTerm);
      }
   }
   
   /**
    * Replaces text selected in this document with the specified string
    *
    * @param replaceWith  the string
    */
   public void replaceSel(String replaceWith) {
      if (textArea.getSelectedText() != null) {
         textArea.replaceSelection(replaceWith);
      }
   }
   
   //
   //--private--
   //
   
   private void searchTextDown(String content, String searchTerm) {
      int ind = nextIndex(content, searchTerm, pos);
      if (ind == -1 & pos > 0) {
         resetSearchStart();
         ind = nextIndex(content, searchTerm, pos);
      }
      if (ind != -1) {
         pos = ind;
         textArea.select(pos, pos + searchTerm.length());
      }
      else {
         Dialogs.infoMessage("\"" + searchTerm + "\" cannot be found.", null);
      }
   }

   private int nextIndex(String content, String searchTerm, int pos) {
      int index = content.indexOf(searchTerm, pos);
      if (reqWord) {
         while (index != -1 && !isWord(content, searchTerm, index)) {
            index = content.indexOf(searchTerm, index + 1);
         }
      }
      return index;
   }
   
   private void searchTextUp(String content, String searchTerm) {
      int ind = lastIndex(content, searchTerm, pos);
      if (ind == -1 & pos < content.length() - 1) {
         resetSearchStart();
         ind = lastIndex(content, searchTerm, pos);
      }
      if (ind != -1) {
         pos = ind;
         textArea.select(pos, pos + searchTerm.length());
      }
      else {
         Dialogs.infoMessage("\"" + searchTerm + "\" cannot be found.", null);
      }
   }

   private int lastIndex(String content, String searchTerm, int pos) {
      int index = content.lastIndexOf(searchTerm, pos);
      if (reqWord) {
         while (index != -1 && !isWord(content, searchTerm, index)) {
            index = content.lastIndexOf(searchTerm, index - 1);
         }
      }
      return index;
   }

   private boolean isWord(String content, String searchTerm, int pos) {
      return eg.syntax.SyntaxUtils.isWord(content, pos, searchTerm.length(), null);
   }
}
