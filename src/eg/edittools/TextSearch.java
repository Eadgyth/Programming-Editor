package eg.edittools;

import javax.swing.JTextPane;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.document.FileDocument;

/**
 * The search of text or words in the <code>FileDocument</code> that
 * is currently viewed in the main editor area
 */
public class TextSearch {
   
   private boolean reqWord;
   private boolean isUpward = false;
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
    * Sets the boolean which indicates if the search is restricted to words
    *
    * @param b  the boolen value
    */
   public void setRequireWord(boolean b) {
      reqWord = b;
   }
   
   /**
    * Sets the boolean that indicates the search direction. This is true
    * for an upward search and false for a downward search.
    *
    * @param b  the boolean value
    */
    public void setUpwardSearch(boolean b) {
       isUpward = b;
    }
   
   /**
    * Resets the search to the start of the document
    */
   public void resetSearch() {
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
    * @param toSearch  the string to search
    */
   public void searchText(String toSearch) {
      if (isUpward) {
         pos = textArea.getSelectionStart() - 1;
         searchTextUp(toSearch);
      }
      else {
         pos = textArea.getCaretPosition();
         searchTextDown(toSearch);
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
   
   private void searchTextUp(String toSearch) {
      int ind = lastIndex(doc.docText(), toSearch, pos);
      if (ind == -1 & pos < doc.docLength() - 1) {
         resetSearch();
         ind = lastIndex(doc.docText(), toSearch, pos);
      }
      if (ind != -1) {
         pos = ind;
         textArea.select(pos, pos + toSearch.length());
      }
      else {
         Dialogs.infoMessage(toSearch + " could not be found", null);
      }
   }

   private int lastIndex(String content, String toSearch, int pos) {
      int index = content.lastIndexOf(toSearch, pos);
      if (reqWord) {
         while (index != -1 && !isWord(content, toSearch, index)) {
            index = content.lastIndexOf(toSearch, index - 1);
         }
      }
      return index;
   }
   
   private void searchTextDown(String toSearch) {
      int ind = nextIndex(doc.docText(), toSearch, pos);
      if (ind == -1 & pos > 0) {
         resetSearch();
         ind = nextIndex(doc.docText(), toSearch, pos);
      }
      if (ind != -1) {
         pos = ind;
         textArea.select(pos, pos + toSearch.length());
      }
      else {
         Dialogs.infoMessage(toSearch + " could not be found", null);
      }
   }

   private int nextIndex(String content, String toSearch, int pos) {
      int index = content.indexOf(toSearch, pos);
      if (reqWord) {
         while (index != -1 && !isWord(content, toSearch, index)) {
            index = content.indexOf(toSearch, index + 1);
         }
      }
      return index;
   }

   private boolean isWord(String content, String toSearch, int pos) {
      return eg.syntax.SyntaxUtils.isWord(content, pos, toSearch.length(), null);
   }
}
