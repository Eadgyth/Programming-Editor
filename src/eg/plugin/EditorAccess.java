package eg.plugin;

import javax.swing.JTextPane;

//--Eadgyth--//
import eg.ui.FunctionPanel;
import eg.document.FileDocument;

/**
 * Makes accessible methods to work with the text that is currently
 * viewed in text area of the main window.
 * <p>
 * Includes the possibility to add a <code>Component</code> to the
 * {@link FunctionPanel} that is shown in the main window
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
    * Gets the text of the <code>StyledDocment</code> that is
    * shown in the currently viewed text area
    *
    * @return  the text of the document in the currently viewed
    * text area
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
    * Adds a <code>Component</code> to the 'function panel' in the
    * main window
    *
    * @param c  the {@code Component} that is added
    * @param title  the title for the function
    */
   public void addToFunctionPanel(java.awt.Component c, String title) {
      functPnl.addComponent(c, title);
   }
}
