package eg;

//--Eadgyth--//
import eg.edittools.AddableEditTool;
import eg.document.FileDocument;
import eg.ui.MainWin;

/**
 * Holds objects that need to be updated when the viewed document
 * changes or when a new file is assigned to the viewed document
 */
public class DocumentUpdate {

   private final MainWin mw;
   private final Edit edit;
   private final CurrentProject currProj;
   private final FileDocument[] fDoc;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param fDoc  the array of {@link FileDocument}
    */
   public DocumentUpdate(MainWin mw, FileDocument[] fDoc) {
      this.mw = mw;
      this.fDoc = fDoc;
      edit = new Edit();
      currProj = new CurrentProject(mw, fDoc);
      mw.setEditTextActions(edit);
      mw.setProjectActions(currProj);
   }

   /**
    * Does updates when the viewed document changes
    *
    * @param i  the index of the element in this <code>FileDocument</code>
    * array
    * @param nTabs  the number of open tabs
    */
   public void updateForChangedDoc(int i, int nTabs) {
      edit.setFileDocument(fDoc[i]);
      currProj.setFileDocumentAt(i);
      mw.editTools().forEach((t) -> {
         t.setFileDocument(fDoc[i]);
      });
      mw.displayFrameTitle(fDoc[i].filepath());
      mw.enableShowTabbar(nTabs == 1);
      mw.setLanguageSelected(fDoc[i].language(), fDoc[i].filename().length() == 0);
      fDoc[i].setFocused();
   }

   /**
    * Does updates when a new file is assigned to the viewed document
    *
    * @param i  the index of the element in this <code>FileDocument</code>
    * array
    * @param updateFiletree  if the file tree needs to be updated
    */
   public void updateForChangedFile(int i, boolean updateFiletree) {
      currProj.setFileDocumentAt(i);
      currProj.retrieveProject();
      mw.setLanguageSelected(fDoc[i].language(), false);
      mw.displayFrameTitle(fDoc[i].filepath());
      if (updateFiletree) {
         currProj.updateFileTree();
      }
   }
}
