package eg.plugin;

import javax.swing.JTextPane;

//--Eadgyth--//
import eg.ui.FunctionPanel;
import eg.document.TextDocument;

/**
 * Makes accessible for a plugin methods to work with the text that is
 * currently viewed in the main window's text area (i.e., the opened or
 * tab selected document).
 * <p>
 * Also includes the possibility to add a {@code Component} to the 'function
 * panel' in the main window.
 */
public class EditorAccess {
   
   FunctionPanel functPnl;
   TextDocument txtDoc;

   EditorAccess(FunctionPanel functPnl) {
      this.functPnl = functPnl;
   }
   
   void setTextDocument(TextDocument txtDoc) {
      this.txtDoc = txtDoc;
   }
   
   /**
    * Gets the {@code JTextPane} object that is currently viewed
    *
    * @return  the {@code JTextPane} object
    */
   public JTextPane textArea() {
      return txtDoc.getTextArea();
   }
   
   /**
    * Gets the text in the currently viewed text area
    *
    * @return  the text in the currently viewed text area that is
    * is contained in the {@code Document} associated with the
    * {@code JTextPane}
    */
   public String getText() {
      return txtDoc.getText();
   }
   
   /**
    * Inserts text in the document that is currently viewed
    *
    * @param pos  the position where new text is inserted
    * @param toInsert  the String that contains the text to insert
    */
   public void insertStr(int pos, String toInsert) {
      txtDoc.insertStr(pos, toInsert);
   }

   /**
    * Removes text from the document that is currently viewed
    *
    * @param start  the position where text to be removed starts
    * @param length  the length of the text to be removed
    */  
   public void removeStr(int start, int length) {
      txtDoc.removeStr(start, length);
   }
   
   /**
    * Asks the currently viewed text area to gain the focus
    */
   public void requestFocus() {
      txtDoc.requestFocus();
   }
   
   /**
    * Adds a {@code Component} to the 'function panel' in the main window
    *
    * @param c  the {@code Component} that is added
    * @param title  the title for the function
    */
   public void addToFunctionPanel(java.awt.Component c, String title) {
      functPnl.addComponent(c, title);
   }
}
