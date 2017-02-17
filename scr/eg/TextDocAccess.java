package eg;

import javax.swing.JTextPane;

//--Eadgyth--//
import eg.document.TextDocument;

class TextDocAccess {
   
   TextDocument txtDoc;
  
   TextDocAccess(TextDocument txtDoc) {
      this.txtDoc = txtDoc;
   }
  
   public JTextPane textArea() {
      return txtDoc.getTextArea();
   }
  
   public String getText() {
      return txtDoc.getText();
   }
  
   /**
    * Enables/disables syntax coloring and auto-indentation
    * @param isEnabled  true to enable syntax coloring and
    * auto-indentation, false to disable
    */
   public void enableTypeEdit(boolean isEnabled) {
      txtDoc.enableTypeEdit(isEnabled);
   }

   /**
    * (Re-)colors the text starting at the specified position
    * and spanning the specified length in the default color
    * @param length  the length of text that is colored in the
    * default color
    * @param pos  the position where the text to color starts
    */
   public void textToBlack(int length, int pos) {
      txtDoc.textToBlack(length, pos);
   }
   
   /**
    * Colors in keyword color text elements specified by the array of search
    * terms.
    * <p>
    * The method returns with a warning if the current language is not plain
    * text. 
    * @param searchTerms  the array of Strings that contain search terms
    * @param constrainWord  true to color only words
    * @throws IllegalArgumentException  if searchTerms is null or contains
    * empty Strings
    */
   public void colorSearchedText(String[] searchTerms, boolean constrainWord) {
      txtDoc.colorSearchedText(searchTerms, constrainWord);
   }

   /**
    * Colors keyword/syntax of the entire text in this document.
    */
   public void colorAll() {
      txtDoc.colorAll();
   }
   
    /**
    * Inserts text
    * @param pos  the position where new text is inserted
    * @param toInsert  the String that contains the text to insert
    */
   public void insertStr(int pos, String toInsert) {
      txtDoc.insertStr(pos, toInsert);
   }

   /**
    * Removes text
    * @param start  the position where text to be removed starts
    * @param length  the length of the text to be removed
    */  
   public void removeStr(int start, int length) {
      txtDoc.removeStr(start, length);
   }
   
   /**
    * Asks text area to gain the focus
    */
   public void requestFocus() {
      txtDoc.requestFocus();
   }
}
  
  
     
