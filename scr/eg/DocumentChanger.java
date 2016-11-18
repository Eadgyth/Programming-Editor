package eg;

import eg.plugin.PluginStarter;
import eg.document.TextDocument;
import eg.ui.MainWin;

public class DocumentChanger {

   private final MainWin mw;
   private final Edit edit;
   private final PluginStarter plugStart;
 
   public DocumentChanger(MainWin mw, Edit edit, PluginStarter plugStart) {
      this.mw = mw;          
      this.edit = edit;
      this.plugStart = plugStart;
   }
   
   /**
    * Assigns the specified object of {@code TextDocument}
    * to this objects of {@code Edit} and {@code PluginStarter}
    */
   public void assignTextDocument(TextDocument txtDoc) {
      txtDoc.requestFocus();
      mw.displayFrameTitle(txtDoc.filepath());
      edit.setTextObject(txtDoc);
      plugStart.setTextDocument(txtDoc);
   }
}