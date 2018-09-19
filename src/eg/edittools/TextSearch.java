package eg.edittools;

import javax.swing.JTextPane;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.document.EditableDocument;

/**
 * The search and replacemant of text or words in the
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
      this.textArea = doc.textArea();
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
    * Sets the boolean that indicates the search direction. True for
    * upward and false for downward. Default is downward.
    *
    * @param b  the boolean value
    */
    public void setUpwardSearch(boolean b) {
       isUpward = b;
    }
    
    /**
     * Sets the boolean that indicates if the text search is case
     * sensitive. Default is insensitive.
     *
     * @param b  the boolean value
     */
    public void setCaseSensitivity(boolean b) {
       isCaseSensitive = b;
    }
   
   /**
    * Searches the next occurence of the search term and, if found,
    * selects it
    *
    * @param searchTerm  the search term
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
    * Replaces a selected search term or searches the next occurrence
    * of the search term and, if found, selects it
    *
    * @param searchTerm  the search term
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
         // check selection since a selection may be made "by hand"
         boolean isSearchTermSelected;
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
    * Replaces all occurrences of the search term
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
            if (replacement.length() == 0) {
               ind++;
            }
            else {
               ind += replacement.length();
            }
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
   
   private void resetSearchStart() {
      if (isUpward) {
         pos = doc.docLength() - 1;
      }
      else {
         pos = 0;
      }
   }
}
