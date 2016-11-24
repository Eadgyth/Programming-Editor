package eg;

import java.io.File;

//--Eadgyth--//
import eg.plugin.PluginStarter;
import eg.document.TextDocument;
import eg.ui.ViewSettings;
import eg.ui.EditArea;

/**
 * Class informs other objects which document is currently
 * selected in the tabs
 */
public class DocumentUpdate {

   private final ViewSettings viewSet;
   private final Edit edit;
   private final PluginStarter plugStart;
   
   private TextDocument[] txtDoc;
 
   public DocumentUpdate(ViewSettings viewSet, Edit edit,
         PluginStarter plugStart) {

      this.viewSet = viewSet;
      this.edit = edit;         
      this.plugStart = plugStart;
   }
   
   /**
    * Assigns to this the array of {@code TextDocument} objects and to this
    * object of {@code ViewSettings} the array of {@code EditArea} objects
    */
   public void setDocumentArrays(TextDocument[] txtDoc, EditArea[] editArea) {
      this.txtDoc = txtDoc;
      viewSet.setEditAreaArr(editArea);
   }

   /**
    * Passes the {@code TextDocument} object at the specified array index
    * to this objects of {@code Edit} and {@code PluginStarter} and only
    * the index to this object of {@code ViewSettings}
    * @param index  the index of this array of {@link TextDocument} and
    * {@link EditArea}
    */
   public void updateDocument(int index) {
      txtDoc[index].requestFocus();
      edit.setTextObject(txtDoc[index]);
      plugStart.setTextDocument(txtDoc[index]);
      viewSet.setEditAreaIndex(index);
   }
}