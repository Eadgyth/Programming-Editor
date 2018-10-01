package eg;

import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;

import javax.swing.event.ChangeEvent;

import java.io.File;

//--Eadgyth--/
import eg.utils.Dialogs;

import eg.document.EditableDocument;
import eg.document.EditingStateReadable;
import eg.ui.MainWin;
import eg.ui.EditArea;
import eg.ui.tabpane.ExtTabbedPane;
import eg.ui.filetree.FileTree;

/**
 * The documents in the tabs.
 * <p>
 * Documents are represented by objects of {@link EditableDocument}
 */
public class TabbedDocuments {

   private final EditableDocument[] edtDoc;
   private final Prefs prefs = new Prefs();
   private final FileChooser fc;
   private final MainWin mw;
   private final ExtTabbedPane tabPane;
   private final Formatter format;
   private final Edit edit;
   private final Projects proj;

   private EditArea[] editArea = null;
   private int iTab = -1;
   private Languages lang;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param format  the reference to {@link Formatter}
    */
   public TabbedDocuments(MainWin mw, Formatter format) {
      this.mw = mw;
      this.format = format;
      editArea = format.editAreaArray();
      edtDoc = new EditableDocument[editArea.length];
      
      setLanguage();
      
      String indentUnit = prefs.getProperty("IndentUnit");      
      edit = new Edit(indentUnit);
      mw.setEditActions(edit);
      
      String projectRoot = prefs.getProperty("ProjectRoot");
      FileTree ft = new FileTree(mw.treePanel(), Openable);
      proj = new Projects(mw, ft, projectRoot, edtDoc);
      mw.setProjectActions(proj);

      String recentPath = prefs.getProperty("RecentPath");
      fc = new FileChooser(recentPath);
      
      tabPane = mw.tabPane();
      tabPane.addChangeListener((ChangeEvent ce) -> {
         JTabbedPane sourceTb = (JTabbedPane) ce.getSource();
         iTab = sourceTb.getSelectedIndex();
         if (iTab > -1) {
            changedTabUpdate();
         }
      });
   }

   /**
    * Changes this language and sets the language in the selected
    * document
    *
    * @param lang  a language in {@link Languages}
    */
   public void changeLanguage(Languages lang) {
      this.lang = lang;
      edtDoc[iTab].changeLanguage(lang);
      mw.displayLanguage(lang);
   }

   /**
    * Creates a new tab with a blank document
    */
   public void createBlankDocument() {
      if (isTabOpenable()) {
         createDocument();
      }
   }

   /**
    * Opens a tab with a document which a file selected in the file
    * chooser is assigned to
    */
   public void open() {
      File f = fc.fileToOpen();
      if (f == null) {
         return;
      }
      if (!f.exists()) {
         Dialogs.warnMessage(f.getName() + " was not found.");
      }
      else {
         open(f);
      }
   }
   
   /**
    * Saves the text content in the selected document to its file.
    */
   public void save() {
      save(true);
   }
   
   /**
    * Saves the text content in the selected document as a new file
    * that is selected in the file chooser
    */
   public void saveAs() {
      saveAs(true);
   }

   /**
    * Saves the text content in all documents that have a file
    */
   public void saveAll() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < nTabs(); i++) {
         if (edtDoc[i].hasFile()) {
            if (edtDoc[i].docFile().exists()) {
               edtDoc[i].saveFile();
            }
            else {
               sb.append(edtDoc[i].filename());
               sb.append("\n");
            }
         }
      }
      if (sb.length() > 0) {
         sb.insert(0, "These files could not be found anymore:\n");
         Dialogs.warnMessage(sb.toString());
      }
   }  

   /**
    * Saves a copy of the content in the selected document to the file
    * that is specified in the file chooser but does not set the file
    * in the document
    *
    * @return  the boolen value that is true if the text content was
    * saved
    */
   public boolean saveCopy() {
      File f = fc.fileToSave(edtDoc[iTab].filepath());
      boolean isSaved = f != null;
      if (isSaved && f.exists()) {
         isSaved = 0 == replaceFileOption(f);
      }
      isSaved = isSaved && edtDoc[iTab].saveCopy(f);
      if (isSaved) {
         proj.updateFileTree(f.toString());
      }
      return isSaved;
   }

   /**
    * Closes the selected tab and may ask to save the content of the
    * document
    *
    * @param createBlankDoc  the boolean that is true to create a new
    * blank document in the case that the tab to be closed is the only
    * open tab.
    */
   public void close(boolean createBlankDoc) {
      boolean removable;
      boolean exists = !edtDoc[iTab].hasFile() || edtDoc[iTab].docFile().exists();
      if (exists) {
         removable = edtDoc[iTab].isSaved();
         if (!removable) {
            removable = removeUnsavedFile(iTab);
         }
      }
      else {
         removable = removeMissingFile(iTab);
      }
      if (removable) {
         removeTab();
         if (nTabs() == 0 && createBlankDoc) {
            createBlankDocument();
         }
      }
   }

   /**
    * Closes all tabs and may ask to save the content of the documents
    *
    * @param createBlankDoc  the boolean value that is true to create
    * a new blank document after closing
    */
   public void closeAll(boolean createBlankDoc) {
      int iMissing = missingFile();
      int iUnsaved = unsavedFile();
      boolean removable = iUnsaved == nTabs() && iMissing == nTabs();
      if (removable) {
         int i = nTabs() - 1;
         while (i > -1) {
            tabPane.removeTabAt(i);
            edtDoc[i] = null;
            editArea[i] = null;
            i--;
         }
         if (createBlankDoc) {
            createBlankDocument();
         }
      }
      else {
         if (iMissing != nTabs()) {
            tabPane.setSelectedIndex(iMissing);
            removable = removeMissingFile(iMissing);
         }
         else {
            if (iUnsaved != nTabs()) {
               tabPane.setSelectedIndex(iUnsaved);
               removable = removeUnsavedFile(iUnsaved);
            }
         }
         if (removable) {
            removeTab();
            closeAll(createBlankDoc);
         }
      }
   }

   /**
    * Closes all tabs and may ask to save the content of the documents
    *
    * @return  the boolean value that, if true, indicates that all tabs
    * were closed
    */
   public boolean closeForExit() {
      closeAll(false);
      boolean isClosed = iTab == -1;
      if (isClosed) {
         format.setProperties();
         prefs.setProperty("IndentUnit", edit.changedIndentUnit());
         prefs.setProperty("Language", lang.toString());
         prefs.setProperty("RecentPath", fc.currentDir());
      }
      return isClosed;
   }

   /**
    * Prints the text content in the selected document to a printer
    */
   public void print() {
      editArea[iTab].print();
   }

   //
   //--private--/
   //

   private void open(File f) {
      if (isFileOpen(f) || isMaxTabNumber()) {
         return;
      }
      boolean isBlankFirstTab
            = nTabs() == 1
            && !edtDoc[0].hasFile()
            && edtDoc[0].docLength() == 0;

      if (isBlankFirstTab) {
         removeTab();
      }
      if (isTabOpenable()) {
         createDocument(f);
      }
   }

   private boolean isTabOpenable() {
      boolean isOpenable = iTab == -1 || tabPane.isShowTabbar();
      if (!isOpenable) {
         close(false);
         isOpenable = iTab == -1;
      }
      return isOpenable;
   }

   private void createDocument() {
      int n = nTabs();
      format.createEditAreaAt(n);
      edtDoc[n] = new EditableDocument(editArea[n], lang);
      setupDocument(n);
      addNewTab("unnamed", editArea[n].content());
   }

   private void createDocument(File f) {
      try {
         mw.setBusyCursor();
         int n = nTabs();
         format.createEditAreaAt(n);
         edtDoc[n] = new EditableDocument(editArea[n], f);
         setupDocument(n);
         addNewTab(edtDoc[n].filename(), editArea[n].content());
         changedFileUpdate();
         proj.retrieveProject();
      }
      finally {
         mw.setDefaultCursor();
      }
   }

   private void setupDocument(int index) {
      edtDoc[index].setIndentUnit(edit.changedIndentUnit());
      edtDoc[index].setEditingStateReadable(editReadable);
   }

   private void addNewTab(String filename, JPanel pnl) {
      JButton closeBt = new JButton(eg.ui.IconFiles.CLOSE_ICON);
      tabPane.addTab(filename, pnl, closeBt);
      closeBt.addActionListener(e -> {
         iTab = tabPane.iTabMouseOver();
         close(true);
      });
   }
   
   private boolean save(boolean update) {
      if (!edtDoc[iTab].hasFile() || !edtDoc[iTab].docFile().exists()) {
         return saveAs(update);
      }
      else {
         return edtDoc[iTab].saveFile();
      }
   }
   
   private boolean saveAs(boolean update) {
      File f = fc.fileToSave(edtDoc[iTab].filepath());
      boolean isSaved = f != null;
      if (isSaved && f.exists()) {
         isSaved = 0 == replaceFileOption(f);
      }
      isSaved = isSaved && edtDoc[iTab].setFile(f);
      if (isSaved) {
         if (update) {
            changedFileUpdate();
            tabPane.setTitle(iTab, edtDoc[iTab].filename());
            proj.retrieveProject();
         }
         proj.updateFileTree(f.toString());
      }
      return isSaved;
   }

   private void removeTab() {
      int count = iTab;
      tabPane.removeTabAt(iTab);
      for (int i = count; i < nTabs(); i++) {
         edtDoc[i] = edtDoc[i + 1];
         editArea[i] = editArea[i + 1];
      }
      int n = nTabs();
      edtDoc[n] = null;
      editArea[n] = null;
      if (n > 0) {
         iTab = tabPane.getSelectedIndex();
         changedTabUpdate();
      }
   }

   private void changedTabUpdate() {
      edtDoc[iTab].setFocused();
      format.setIndex(iTab);
      proj.setIndex(iTab);
      edit.setDocument(edtDoc[iTab]);
      mw.setWordWrapSelected(editArea[iTab].isWordwrap());
      mw.editTools().forEach((t) -> {
         t.setEditableDocument(edtDoc[iTab]);
      });
      mw.displayFrameTitle(edtDoc[iTab].filepath());
      mw.enableShowTabbar(nTabs() == 1);
      mw.setLanguageSelected(edtDoc[iTab].language(), !edtDoc[iTab].hasFile());
   }

   private void changedFileUpdate() {
      proj.updateDocument();
      mw.setLanguageSelected(edtDoc[iTab].language(), false);
      mw.displayFrameTitle(edtDoc[iTab].filepath());
   }

   private boolean isFileOpen(File f) {
      boolean isFileOpen = false;
      for (int i = 0; i < nTabs(); i++) {
         if (edtDoc[i].filepath().equals(f.toString())) {
           isFileOpen = true;
           Dialogs.infoMessage(f.getName() + " is already open.", null);
           break;
         }
      }
      return isFileOpen;
   }

   private boolean isMaxTabNumber() {
      boolean isMax = nTabs() == edtDoc.length;
      if (isMax) {
         Dialogs.errorMessage("The maximum number of tabs is reached.", null);
      }
      return isMax;
   }

   private int unsavedFile() {
      int i;
      for (i = 0; i < nTabs(); i++) {
         if (!edtDoc[i].isSaved()) {
            break;
         }
      }
      return i;
   }
   
   private int missingFile() {
      int i;
      for (i = 0; i < nTabs(); i++) {
         if (edtDoc[i].hasFile() && !edtDoc[i].docFile().exists()) {
            break;
         }
      }
      return i;
   }
   
   private boolean removeUnsavedFile(int i) {
      String filename = edtDoc[i].filename();
      if (filename.length() == 0) {
         filename = "unnamed";
      }
      int res = Dialogs.confirmYesNoCancel(
            "Save changes in " + filename + " ?");
            
      boolean removable;
      if (JOptionPane.YES_OPTION == res) {
         removable = save(false);
      }
      else {
         removable = JOptionPane.NO_OPTION == res;
      }
      return removable;
   }
   
   private boolean removeMissingFile(int i) {
      String filename = edtDoc[i].filename();
      int res = Dialogs.confirmYesNoCancel(
            filename
            + " could not be found anymore."
            + " Save as new file?");
      
      boolean removable = false;
      if (JOptionPane.YES_OPTION == res) {
         removable = saveCopy();       
      }
      else if (JOptionPane.NO_OPTION == res) {
         removable = true;
      }
      return removable;
   }       

   private int replaceFileOption(File f) {
      return Dialogs.warnConfirmYesNo(
             f.getName() + " already exists.\nReplace file?");
   }

   private int nTabs() {
      return tabPane.getTabCount();
   }

   private void setLanguage() {
      try {
         lang = Languages.valueOf(prefs.getProperty("Language"));
      }
      catch (IllegalArgumentException e) {
         lang = Languages.NORMAL_TEXT;
      }
      mw.setLanguageSelected(lang, false);
   }
   
   private final FileOpenable Openable = new FileOpenable() {
      
      @Override
      public void openFile(File f) {
         if (f == null || (!f.exists() || f.isDirectory())) {
            throw new IllegalArgumentException(
                  "The file f cannot be opened");
         }
         open(f);
      }
   };

   private final EditingStateReadable editReadable = new EditingStateReadable() {

      @Override
      public void setInChangeState(boolean isChange) {
         mw.enableSave(isChange);
      }

      @Override
      public void setUndoableState(boolean canUndo, boolean canRedo) {
         mw.enableUndoRedo(canUndo, canRedo);
      }

      @Override
      public void setSelectionState(boolean isSelection) {
         mw.enableCutCopy(isSelection);
      }

      @Override
      public void setCursorPosition(int line, int col) {
         mw.displayCursorPosition(line, col);
      }
   };
}
