package eg.plugin;

import javax.swing.JTextPane;

//--Eadgyth--//
import eg.ui.FunctionPanel;
import eg.document.FileDocument;

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
   FileDocument fDoc;

   EditorAccess(FunctionPanel functPnl) {
      this.functPnl = functPnl;
   }
   
   void setFileDocument(FileDocument fDoc) {
      this.fDoc = fDoc;
   }
   
   /**
    * Returns the text area that shows the currently opened or selected
    * document
    *
    * @return  the text area
    */
   public JTextPane textArea() {
      return fDoc.docTextArea();
   }
   
   /**
    * Gets the text in the currently viewed text area
    *
    * @return  the text in the currently viewed text area that is
    * is contained in the {@code Document} associated with the
    * {@code JTextPane}
    */
   public String getText() {
      return fDoc.getText();
   }
   
   /**
    * Inserts text in the document that is currently viewed
    *
    * @param pos  the position where new text is inserted
    * @param toInsert  the String that contains the text to insert
    */
   public void insertStr(int pos, String toInsert) {
      fDoc.insert(pos, toInsert);
   }

   /**
    * Removes text from the document that is currently viewed
    *
    * @param start  the position where text to be removed starts
    * @param length  the length of the text to be removed
    */  
   public void removeStr(int start, int length) {
      fDoc.remove(start, length);
   }
   
   /**
    * Asks the currently viewed text area to gain the focus
    */
   public void requestFocus() {
      fDoc.requestFocus();
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
