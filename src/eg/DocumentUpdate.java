package eg;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.edittools.*;
import eg.utils.FileUtils;
import eg.document.FileDocument;
import eg.ui.MainWin;

/**
 * Holds objects that need to be updated when the tab is changed and when
 * the file of a selected document changes
 */
public class DocumentUpdate {

   private final MainWin mw;
   private final Edit edit;
   private final CurrentProject currProj;
   private final FileDocument[] fDoc;

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
      currProj = new CurrentProject(mw, fDoc);
      mw.setEditTextActions(edit);
      mw.setProjectActions(currProj);
   }

   /**
    * Does updates when another document is selected
    *
    * @param i  the index of the element in this <code>FileDocument</code>
    * array
    * @param nTabs  the number of open tabs
    */
   public void updateForChangedDoc(int i, int nTabs) {
      edit.setFileDocument(fDoc[i]);
      currProj.setFileDocumentAt(i);
      for (AddableEditTool t : mw.editTools()) {
         t.setFileDocument(fDoc[i]);
      }
      mw.enableUndoRedo(fDoc[i].canUndo(), fDoc[i].canRedo());
      mw.enableCutCopy(fDoc[i].docTextArea().getSelectedText() != null);
      mw.displayFrameTitle(fDoc[i].filepath());
      mw.enableShowTabbar(nTabs == 1);
      mw.setLanguageSelected(fDoc[i].language(),
            fDoc[i].filename().length() == 0);

      mw.displayLCursorPosition(fDoc[i].lineNrAtCursor(),
            fDoc[i].columnNrAtCursor());

      fDoc[i].requestFocus();
   }

   /**
    * Does updates when a new file is assigned to a document 
    *
    * @param i  the index of the element in this <code>FileDocument</code>
    * array
    * @param updateFiletree  if the file tree is updated (after save as)
    */
   public void updateForChangedFile(int i, boolean updateFiletree) {
      retrieveProject(i);
      mw.setLanguageSelected(fDoc[i].language(), false);
      mw.displayFrameTitle(fDoc[i].filepath());
      if (updateFiletree) {
         currProj.updateFileTree();
      }
   }

   //
   //--private--/
   //

   private void retrieveProject(int i) {
      currProj.setFileDocumentAt(i);
      currProj.retrieveProject();
   }
}
