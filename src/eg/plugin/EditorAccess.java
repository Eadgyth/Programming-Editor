package eg.plugin;

import javax.swing.JTextPane;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.document.TextDocument;

/**
 * Makes accessible for a plugin methods to work with the text area in the
 * selected tab and the possibility to add a Component to the 'function panel'
 * in the main window
 */
public class EditorAccess {
   
   MainWin mw;
   TextDocument txtDoc;

   EditorAccess(MainWin mw) {
      this.mw = mw;
   }
   
   void setTextDocument(TextDocument txtDoc) {
      this.txtDoc = txtDoc;
   }
   
   /**
    * Returns the text area in the currently selected tab
    * @return  the text area in the currently selected tab
    */
   public JTextPane textArea() {
      return txtDoc.getTextArea();
   }
   
   /**
    * Returns the text content in the {@code StyledDocument}
    * associated with the text area
    * @return  the text content in the {@code StyledDocument}
    * associated with the text area
    */
   public String getText() {
      return txtDoc.getText();
   }
  
   /**
    * Enables/disables keywords coloring during typing
    * @param isEnabled  true to enable keywords coloring, false
    * to disable
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
    * terms and turns on coloring during typing.
    * <p>
    * Returns with a warning if the current language is not plain text. 
    * @param searchTerms  the array of Strings that contain search terms
    * @param constrainWord  true to color only words
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
    * Removes text from the document associated with the text area
    * @param start  the position where text to be removed starts
    * @param length  the length of the text to be removed
    */  
   public void removeStr(int start, int length) {
      txtDoc.removeStr(start, length);
   }
   
   /**
    * Asks the text area to gain the focus
    */
   public void requestFocus() {
      txtDoc.requestFocus();
   }
   
   /**
    * Adds a component to the main window. The 'function panel' is found at the
    * right of the split window of the main window.
    * @param c  the Component that is added
    * @param title  the title for the function
    * @see MainWin#addToFunctionPanel(java.awt.Component, String)
    */
   public void addToFunctionPanel(java.awt.Component c, String title) {
      mw.addToFunctionPanel(c, title);
   }
}
