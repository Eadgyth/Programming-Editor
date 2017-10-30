package eg;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.edittools.*;
import eg.utils.FileUtils;
import eg.document.FileDocument;
import eg.ui.MainWin;
import eg.ui.EditArea;

/**
 * Holds objects that need to be updated when the tab is changed and when
 * the file of a selected document changes
 */
public class DocumentUpdate {

   private final MainWin mw;
   private final Edit edit;
   private final List<AddableEditTool> tools = new ArrayList<>();
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
      currProj = new CurrentProject(mw, fDoc);
      createAddableEditTools();
      mw.setEditTextActions(edit);
      mw.setProjectActions(currProj);
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
      currProj.setFileDocumentAt(i);
      for (AddableEditTool t : tools) {
         t.setFileDocument(fDoc[i]);
      }
      mw.enableUndoRedo(fDoc[i].canUndo(), fDoc[i].canRedo());
      mw.enableCutCopy(fDoc[i].docTextArea().getSelectedText() != null);
      mw.displayFrameTitle(fDoc[i].filepath());
      mw.enableShowTabbar(nTabs == 1);
      mw.setLanguageSelected(fDoc[i].language(),
            fDoc[i].filename().length() == 0);
      mw.displayLineAndColNr(fDoc[i].lineNrAtCursor(),
            fDoc[i].columnNrAtCursor());
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
      mw.setLanguageSelected(fDoc[i].language(), false);
      mw.displayFrameTitle(fDoc[i].filepath());
      if (updateFiletree) {
         currProj.updateFileTree();
      }
   }

   //
   //--private--//
   //

   private void createAddableEditTools() {
      try {
         for (int i = 0; i < EditTools.values().length; i++) {
            tools.add((AddableEditTool)
                  Class.forName("eg.edittools."
                        + EditTools.values()[i].className())
                  .newInstance());

            mw.setEditToolsActions(tools.get(i), i);
         }
      }
      catch (ClassNotFoundException
            | InstantiationException | IllegalAccessException e) {

         FileUtils.logStack(e);
      }
   }

   private void retrieveProject(int i) {
      currProj.setFileDocumentAt(i);
      currProj.retrieveProject();
   }
}
