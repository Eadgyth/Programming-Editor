package eg;

//--Eadgyth--//
import eg.plugin.PluginStarter;
import eg.document.FileDocument;
import eg.ui.MainWin;
import eg.ui.EditArea;

/**
 * Holds objects that need to be updated when the tab is changed and when
 * the file of a selected document changes.
 * The updated classes are {@link Edit}, {@link PluginStarter},
 * {@link CurrentProject} and {@link MainWin}
 */
public class DocumentUpdate {

   private final MainWin mw;
   private final Edit edit;
   private final PluginStarter plugStart;
   private final CurrentProject currProj;

   private FileDocument[] fDoc;

   /**
    * Creates a DocumentUpdate
    *
    * @param mw  the reference to {@link MainWin}
    * @param fDoc  the array of {@link FileDocument}
    */
   public DocumentUpdate(MainWin mw, FileDocument[] fDoc) {
      this.mw = mw;
      this.fDoc = fDoc;
      edit = new Edit();
      plugStart = new PluginStarter(mw.functionPanel());
      currProj = new CurrentProject(mw, fDoc);
      mw.registerEditTextAct(edit);
      mw.registerPlugAct(plugStart);
      mw.registerProjectAct(currProj);
   }

   /**
    * Does updates for a changed document
    *
    * @param i  the index of the element in this <code>FileDocument</code>
    * array
    * @param nTabs  the number of open tabs
    */
   public void changedDocUpdate(int i, int nTabs) {
      edit.setFileDocument(fDoc[i]);
      plugStart.setFileDocument(fDoc[i]);
      currProj.setFileDocumentAt(i);
      mw.enableUndoRedo(fDoc[i].canUndo(), fDoc[i].canRedo());
      mw.enableCutCopy(fDoc[i].docTextArea().getSelectedText() != null);
      mw.displayFrameTitle(fDoc[i].filepath());
      mw.enableShowHideTabbar(nTabs == 1);
      mw.setLanguagesSelected(fDoc[i].language(),
            fDoc[i].filename().length() == 0);
      fDoc[i].requestFocus();
   }
   
   /**
    * Does updates for a changed file in a document
    *
    * @param i  the index of the element in this <code>FileDocument</code>
    * array
    * @param updateFiletree  if the file tree is updated (after save as)
    */
   public void changedFileUpdate(int i, boolean updateFiletree) {
      retrieveProject(i);
      mw.setLanguagesSelected(fDoc[i].language(), false);
      mw.displayFrameTitle(fDoc[i].filepath());
      if (updateFiletree) {
         currProj.updateFileTree();
      }
   }
   
   //
   //--private--//
   //
   
   private void retrieveProject(int i) {
      currProj.setFileDocumentAt(i);
      currProj.retrieveProject();
   }
}
