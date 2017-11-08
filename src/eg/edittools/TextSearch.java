package eg.edittools;

import javax.swing.JTextPane;

//--Eadgyth--/
import eg.utils.Dialogs;
import eg.document.FileDocument;

public class TextSearch {
   
   private boolean reqWord;
   private int pos = 0;

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
      pos = -1;
   }
   
   public void searchText(String toSearch) {
      int caret = textArea.getCaretPosition();
      textArea.setSelectionStart(caret);
      textArea.setSelectionEnd(caret);
      pos = caret;
      String content = doc.getDocText();
      boolean notFound = false;
      int nextStep = 0;
      int ind = nextIndex(content, toSearch, pos + nextStep);
      /*
       * go to start if last match is reached */
      if (ind == -1 & pos > -1) {
         pos = 0;
         nextStep = 0;
         ind = nextIndex(content, toSearch, pos);
      }
      if (ind != -1) {
         pos = ind;
         textArea.select(pos, pos + toSearch.length());
         nextStep = 1;
      }
      else {
         Dialogs.infoMessage(toSearch + " could not be found", null);
      }
   }
   
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
      return eg.syntax.SyntaxUtils.isWord(content, pos, toSearch.length());
   }
}
