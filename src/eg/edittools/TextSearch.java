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
    * Resets the search to the start of the document
    */
   public void resetSearchToStart() {
      pos = 0;
   }
   
   /**
    * Searches and selects the specified string in the text area of the
    * currently set document. A search starts from the position of the cursor
    * and repeated searches each start from the last match position.
    *
    * @param toSearch  the string to search
    */
   public void searchText(String toSearch) {
      int caret = textArea.getCaretPosition();
      pos = caret;
      String content = doc.getDocText();
      int ind = nextIndex(content, toSearch, pos);
      if (ind == -1 & pos > 0) {
         resetSearchToStart();
         ind = nextIndex(content, toSearch, pos);
      }
      if (ind != -1) {
         pos = ind;
         textArea.select(pos, pos + toSearch.length());
      }
      else {
         Dialogs.infoMessage(toSearch + " could not be found", null);
      }
   }
   
   /**
    * Replaces text selected in the document with the specified string
    *
    * @param replaceWith  the string
    */
   public void replaceSel(String replaceWith) {
      if (textArea.getSelectedText() != null) {
         textArea.replaceSelection(replaceWith);
      }
   }
   
   //
   //--private--/
   //

   private int nextIndex(String content, String toSearch, int pos) {
      if (reqWord) {
         return findWordIndex(content, toSearch, pos);
      }
      else {
         return content.indexOf(toSearch, pos);
      }
   }

   private int findWordIndex(String content, String toSearch, int pos) {
      int result = -1;
      int ind = 0;
      int nextStep = 0;
      while (ind != -1) {
         ind = content.indexOf(toSearch, pos + ind + nextStep);
         if (ind != -1 & isWord(content, toSearch, ind)) {
            result = ind;
            ind = -1;
         }
         nextStep = 1;
      }
      return result;
   }

   private boolean isWord(String content, String toSearch, int pos) {
      return eg.syntax.SyntaxUtils.isWord(content, pos, toSearch.length(), null);
   }
}
