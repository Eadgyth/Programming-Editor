package eg;

//--Eadgyth--//
import eg.plugin.PluginStarter;
import eg.document.TextDocument;
import eg.ui.EditArea;

/**
 * Class informs other objects which document is currently
 * selected in the tabs
 */
public class DocumentUpdate {

   private final DisplaySetter displSet;
   private final Edit edit;
   private final PluginStarter plugStart;
   
   private TextDocument[] txtDoc;
 
   public DocumentUpdate(DisplaySetter displSet, Edit edit,
         PluginStarter plugStart) {

      this.displSet = displSet;
      this.edit = edit;         
      this.plugStart = plugStart;
   }
   
   /**
    * Assigns to this the array of {@code TextDocument} objects and to this
    * object of {@code ViewSettings} the array of {@code EditArea} objects
    * @param txtDoc  the array of {@link TextDocument}
    * @param editArea  the array of {@link EditArea}
    */
   public void setDocumentArrays(TextDocument[] txtDoc, EditArea[] editArea) {
      this.txtDoc = txtDoc;
      displSet.setEditAreaArr(editArea);
   }

   /**
    * @param index  the index of the element in the arrays of
    * {@link TextDocument} and {@link EditArea}
    */
   public void updateDocument(int index) {
      edit.setTextDocument(txtDoc[index]);
      plugStart.setTextDocument(txtDoc[index]);
      displSet.setEditAreaIndex(index);
   }
}
