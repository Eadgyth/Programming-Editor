package eg;

//--Eadgyth--//
import eg.plugin.PluginStarter;
import eg.document.TextDocument;
import eg.ui.MainWin;
import eg.ui.EditArea;

/**
 * Holds object that need to be updated when the tab is changed and when
 * the file of a selected document changes.
 * The updated classes are {@link Edit}, {@link PluginStarter},
 * {@link CurrentProject} and {@link MainWin}
 */
public class DocumentUpdate {

   private final MainWin mw;
   private final Edit edit;
   private final PluginStarter plugStart;
   private final CurrentProject currProj;

   private TextDocument[] txtDoc;

   /**
    * Creates a DocumentUpdate
    *
    * @param mw  the reference to {@link MainWin}
    * @param txtDoc  the array of {@link TextDocument}
    */
   public DocumentUpdate(MainWin mw, TextDocument[] txtDoc) {
      this.mw = mw;
      this.txtDoc = txtDoc;
      edit = new Edit();
      plugStart = new PluginStarter(mw.functionPanel());
      currProj = new CurrentProject(mw, txtDoc);
      mw.registerEditTextAct(edit);
      mw.registerPlugAct(plugStart);
      mw.registerProjectAct(currProj);
   }

   /**
    * Sets in the interested object the selected <code>TextDocument</code>
    * and updates the <code>MainWin</code>
    *
    * @param i  the index of the selected {@link TextDocument}
    * @param nTabs  the number of open tabs
    */
   public void changedDocUpdate(int i, int nTabs) {
      edit.setTextDocument(txtDoc[i]);
      plugStart.setTextDocument(txtDoc[i]);
      currProj.setTextDocumentAt(i);
      mw.enableUndoRedo(txtDoc[i].canUndo(), txtDoc[i].canRedo());
      mw.enableCutCopy(txtDoc[i].textArea().getSelectedText() != null);
      mw.displayFrameTitle(txtDoc[i].filepath());
      mw.enableShowHideTabbar(nTabs == 1);
      mw.setLanguagesSelected(txtDoc[i].language(),
            txtDoc[i].filename().length() == 0);
      txtDoc[i].requestFocus();
   }
   
   /**
    * Updates <code>MainWin</code> when a file is assigned but the tab is
    * not changed
    *
    * @param i  the index of the selected {@link TextDocument}
    * @param nTabs  the number of open tabs
    * @param updateFiletree  if the file tree is updated (after save as)
    */
   public void changedFileUpdate(int i, int nTabs, boolean updateFiletree) {
      retrieveProject(i);
      mw.setLanguagesSelected(txtDoc[i].language(), false);
      mw.enableShowHideTabbar(nTabs == 1);
      mw.displayFrameTitle(txtDoc[i].filepath());
      if (updateFiletree) {
         currProj.updateFileTree();
      }
   }
   
   //
   //--private--//
   //
   
   private void retrieveProject(int i) {
      currProj.setTextDocumentAt(i);
      currProj.retrieveProject();
   }
}
