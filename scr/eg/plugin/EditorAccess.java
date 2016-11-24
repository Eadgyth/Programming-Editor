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
    * Adds a component to the 'function panel' of the Main Window. The panel
    * is at the right split area and has a BorderLayout. The title is shown NORTH
    * and the Component is shown in the CENTER of the Layout.
    * @param c  the Component that is added in teh center of the
    * 'function panel'
    * @param title  the title for the function
    */
   public void addToFunctionPanel(java.awt.Component c, String title) {
      mw.addToFunctionPanel(c, title);
   }
}