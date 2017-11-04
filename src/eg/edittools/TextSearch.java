package eg.edittools;

import javax.swing.JTextPane;
import javax.swing.JTextField;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.document.FileDocument;

public class TextSearch {
   
   private boolean reqWord;
   private int index = -1;

   private FileDocument doc;
   private JTextPane textArea;
   
   public void setFileDocument(FileDocument doc) {
      this.doc = doc;
      this.textArea = doc.docTextArea();
   }
   
   public void setRequireWord(boolean b) {
      reqWord = b;
   }
   
   public void resetSearchToStart() {
      index = -1;
   }
   
   public void searchText(String toSearch) {
      int caret = textArea.getCaretPosition();
      textArea.setSelectionStart(caret);
      textArea.setSelectionEnd(caret);
      String content = doc.getDocText();
      boolean notFound = false;
      int ind = 0;
      int nextStep = 0;
      if (index > -1) {
         nextStep = 1;
      }
      ind = nextIndex(content, toSearch, index + nextStep);
      /*
       * go back to start if last match is reached */
      if (ind == -1 & index > -1) {
         index = 0;
         nextStep = 0;
         ind = nextIndex(content, toSearch, index + nextStep);
      }
      if (ind != -1) {
         index = ind;
         doc.requestFocus();
         textArea.select(index, index + toSearch.length());
      }
      else {
         Dialogs.infoMessage(toSearch + " could not be found", null);
         doc.requestFocus();
         index = -1;
      }
   }
   
   public void replaceSel(String replaceWith) {
      if (index != -1 && textArea.getSelectedText() != null) {
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
      return eg.syntax.SyntaxUtils.isWord(content, pos, toSearch.length());
   }
}
