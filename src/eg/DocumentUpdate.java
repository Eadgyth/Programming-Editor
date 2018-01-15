package eg;

//--Eadgyth--//
import eg.edittools.AddableEditTool;
import eg.document.EditableDocument;
import eg.ui.MainWin;

/** 
 * Holds objects that need to be updated when another tab is selected
 * or when a new file is assigned to the document in the selected tab.
 * <p>
 * The interested objects are {@link MainWin} which is passed through
 * the constructor and also {@link Edit} and {@link CurrentProject}
 * which are created in this class.
 */
public class DocumentUpdate {

   private final MainWin mw;
   private final Edit edit;
   private final CurrentProject currProj;
   private final EditableDocument[] edtDoc;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param edtDoc  the array of {@link EditableDocument}
    */
   public DocumentUpdate(MainWin mw, EditableDocument[] edtDoc) {
      this.mw = mw;
      this.edtDoc = edtDoc;
      edit = new Edit();
      currProj = new CurrentProject(mw, edtDoc);
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
   public void changeDocument(int i, int nTabs) {
      edit.setDocument(edtDoc[i]);
      currProj.setDocumentAt(i);
      mw.editTools().forEach((t) -> {
         t.setEditableDocument(edtDoc[i]);
      });
      mw.displayFrameTitle(edtDoc[i].filepath());
      mw.enableShowTabbar(nTabs == 1);
      mw.setLanguageSelected(edtDoc[i].language(), !edtDoc[i].hasFile());
      edtDoc[i].setFocused();
   }

   /**
    * Does updates when a new file is assigned to the viewed document
    *
    * @param i  the index of the element in this <code>FileDocument</code>
    * array
    * @param updateFiletree  if the file tree needs to be updated
    */
   public void changeFile(int i, boolean updateFiletree) {
      currProj.setDocumentAt(i);
      currProj.retrieveProject();
      mw.setLanguageSelected(edtDoc[i].language(), false);
      mw.displayFrameTitle(edtDoc[i].filepath());
      if (updateFiletree) {
         currProj.updateFileTree();
      }
   }
}
