package eg;

//--Eadgyth--//
import eg.plugin.PluginStarter;
import eg.document.TextDocument;
import eg.ui.EditArea;

/**
 * Class informs other objects which document is currently displayed in
 * the main window
 */
public class DocumentUpdate {

   private final Edit edit;
   private final PluginStarter plugStart;
   
   private TextDocument[] txtDoc;
 
   public DocumentUpdate(Edit edit, PluginStarter plugStart) {
      this.edit = edit;
      this.plugStart = plugStart;
   }
   
   /**
    * Assigns the array of {@code TextDocument} and {@code EditArea} objects
    * and to this {@code DisplaySetter} the array of {@code EditArea} objects
    * @param txtDoc  the array of {@link TextDocument}
    */
   public void setDocumentArrays(TextDocument[] txtDoc) {
      this.txtDoc = txtDoc;
   }

   /**
    * @param index  the index of the element in the arrays of
    * {@link TextDocument} and {@link EditArea}
    */
   public void updateDocument(int index) {
      edit.setTextDocument(txtDoc[index]);
      plugStart.setTextDocument(txtDoc[index]);
   }
}
