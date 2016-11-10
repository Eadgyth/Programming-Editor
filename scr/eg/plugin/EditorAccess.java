package eg.plugin;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.document.TextDocument;

/**
 * Makes accessible for a plugin the {@code TextDocument} object that the
 * selected tab contains and the possibility to add a Component to the
 * 'function panel' in the main window
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
    * @return  the object of the {@link TextDocument} in the currently
    * selected tab
    */
   public TextDocument getTxtDoc() {
      return txtDoc;
   }
   
   /**
    * Adds a Component to the 'function panel' in the main window
    * @param c  the Component to be added
    * @param title  a title for the plugin
    */
   public void addToFunctionPanel(java.awt.Component c, String title) {
      mw.addToFunctionPanel(c, title);
   }
}