package eg.edittools;

import javax.swing.JTextPane;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.document.EditableDocument;

/**
 * The search and maybe replacemant of text or words in the
 * <code>EditableDocument</code> that is currently viewed in the main
 * editor area
 */
public class TextSearch {
   
   private boolean isUpward = false;
   private boolean reqWord = false;
   private boolean isCaseSensitive = false;
   private int pos = 0;

   private EditableDocument doc;
   private JTextPane textArea;
   
   /**
    * Sets the <code>EditableDocument</code> which text in searched in
    *
    * @param doc  the <code>EditableDocument</code>
    */
   public void setDocument(EditableDocument doc) {
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
    * Replaces text
    *
    * @param searchTerm  search term that is replaced
    * @param replacement  the replacement for the search term
    */
   public void replace(String searchTerm, String replacement) {
      if (searchTerm.length() == 0) {
         return;
      }
      String sel = textArea.getSelectedText();
      if (sel == null) {
         searchText(searchTerm);
      }
      else {
         //
         // check selection since selection may be made "by hand"
         boolean isSearchTermSelected = false;
         if (isCaseSensitive) {
            isSearchTermSelected = sel.equals(searchTerm);
         }
         else {
            isSearchTermSelected = sel.equalsIgnoreCase(searchTerm);
         }
         if (!isSearchTermSelected) {
            searchText(searchTerm);
         }
         else {
            textArea.replaceSelection(replacement);
            searchText(searchTerm);
         }
      }
   }
   
   /**
    * Replaces all occurrences of the specified search term
    *
    * @param searchTerm  the search term
    * @param replacement  the replacement for the search term
    */
   public void replaceAll(String searchTerm, String replacement) {
      if (searchTerm.length() == 0) {
         return;
      }
      String content = doc.docText();
      if (!isCaseSensitive) {
         content = content.toLowerCase();
         searchTerm = searchTerm.toLowerCase();
      }
      doc.enableMerging(true);
      int count = 0;
      int diff = searchTerm.length() - replacement.length();
      int sumDiff = 0;
      int ind = 0;
      while (ind != -1) {
         ind = nextIndex(content, searchTerm, ind);
         if (ind != -1) {
            count++;
            doc.replace(ind - sumDiff, searchTerm.length(), replacement);
            sumDiff += diff;
            textArea.setCaretPosition(ind - sumDiff + diff + replacement.length());
            ind += replacement.length();
         }
      }
      doc.enableMerging(false);
      
      if (count > 0) {
         Dialogs.infoMessage("\"" + searchTerm + "\" was replaced "
               + count + " times.", null);
      }
      else {
         Dialogs.infoMessage("\"" + searchTerm + "\" was not found.", null);
      }
      textArea.requestFocusInWindow();
   }
   
   //
   //--private--/
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
         Dialogs.infoMessage("\"" + searchTerm + "\" was not found.", null);
         textArea.requestFocusInWindow();
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
         Dialogs.infoMessage("\"" + searchTerm + "\" was not found.", null);
         textArea.requestFocusInWindow();
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
