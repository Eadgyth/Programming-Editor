package eg;

import java.util.Observer;
import java.util.Observable;

import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;

import javax.swing.event.ChangeEvent;

import java.io.File;

//--Eadgyth--//
import eg.utils.Dialogs;

import eg.document.EditableDocument;
import eg.document.EditingStateReadable;
import eg.ui.MainWin;
import eg.ui.EditArea;
import eg.ui.tabpane.ExtTabbedPane;

/**
 * The documents in the tabs.
 * <p>
 * Documents are represented by objects of {@link EditableDocument}
 */
public class TabbedDocuments implements Observer {

   private final EditableDocument[] edtDoc;
   private final Prefs prefs = new Prefs();
   private final FileChooser fc;
   private final MainWin mw;
   private final ExtTabbedPane tabPane;
   private final Formatter format;
   private final Edit edit;
   private final Projects proj;

   private EditArea[] editArea = null;

   /*
    * The index of the selected tab */
   private int iTab = -1;
   /*
    * The language that is initially read from prefs and that may be
    * changed in the Edit>Language menu */
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
      edit = new Edit(prefs.getProperty("IndentUnit"));
      mw.setEditActions(edit);
      proj = new Projects(mw, edtDoc, prefs.getProperty("ProjectRoot"));
      mw.setProjectActions(proj);
      setLanguage();
      fc = new FileChooser(prefs.getProperty("RecentPath"));
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
    * Changes the language in the currently viewed document
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
    * Opens a tab with a file that is double clicked in
    * <code>FileTree</code>
    * @param obs  the Observable
    * @param o  the object that has changed in obs
    */
   @Override
   public void update(Observable obs, Object o) {
      open((File) o);
   }

   /**
    * Opens a tab with a file selected in the file chooser
    */
   public void openFileByChooser() {
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
    * Saves the text content in the selected document.
    * <p>
    * {@link #saveAs(boolean)} is called if the selected tab is
    * unnamed or if the content was read in from a file that no
    * longer exists on the hard drive.
    *
    * @param update  true to update the main window and this
    * {@link Projects} in case a new file is saved
    * @return  if the text content was saved
    */
   public boolean save(boolean update) {
      if (!edtDoc[iTab].hasFile() || !edtDoc[iTab].docFile().exists()) {
         return saveAs(update);
      }
      else {
         return edtDoc[iTab].saveFile();
      }
   }

   /**
    * Saves the text content in all open documents
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
         sb.insert(0, "These files could not be found:\n");
         Dialogs.warnMessage(sb.toString());
      }
   }

   /**
    * Saves the text content in the selected document as a new file
    * that is specified in the file chooser
    *
    * @param update  true to update the main window and this
    * {@link Projects} because of the chnaged file
    * @return  if the text content was saved
    */
   public boolean saveAs(boolean update) {
      File f = fc.fileToSave(edtDoc[iTab].filepath());
      boolean isSave = f != null;
      if (isSave && f.exists()) {
         isSave = 0 == replaceFileOption(f);
      }
      isSave = isSave && edtDoc[iTab].setFile(f);
      if (isSave && update) {
         changedFileUpdate(true);
         tabPane.setTitle(iTab, edtDoc[iTab].filename());
      }
      return isSave;
   }

   /**
    * Saves a copy of the content in the selected document to the file
    * that is specified in the file chooser
    */
   public void saveCopy() {
      File f = fc.fileToSave(edtDoc[iTab].filepath());
      boolean isSave = f != null;
      if (isSave && f.exists()) {
         isSave = 0 == replaceFileOption(f);
      }
      if (isSave) {
         edtDoc[iTab].saveCopy(f);
      }
   }

   /**
    * Tries to close the currently viewed tab
    *
    * @param createBlankDoc  the boolean that is true to create a new
    * blank document in the case that the tab to be closed is the only
    * open tab.
    */
   public void close(boolean createBlankDoc) {
      boolean removable = edtDoc[iTab].isSaved();
      if (!removable) {
         int res = saveOrCloseOption(iTab);
         if (JOptionPane.YES_OPTION == res) {
            removable = save(false);
         }
         else {
            removable = JOptionPane.NO_OPTION == res;
         }
      }
      if (removable) {
         removeTab();
         if (nTabs() == 0 && createBlankDoc) {
            createBlankDocument();
         }
      }
   }

   /**
    * Closes all tabs if files of the documents in all tabs have been
    * saved. Creates a new blank tab if the specified boolean
    * <code>createBlankTab</code> is true and all tabs could be closed.
    *
    * @param createBlankDoc  the boolean value, true to create a new
    * blank tab
    */
   public void closeAll(boolean createBlankDoc) {
      int count = unsavedTab();
      if (count == nTabs()) {
         int i = count - 1;
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
         tabPane.setSelectedIndex(count);
         int res = saveOrCloseOption(count);
         if (JOptionPane.YES_OPTION == res) {
            if (save(false)) {
               closeAll(createBlankDoc);
            }
         }
         else if (JOptionPane.NO_OPTION == res) {
            removeTab();
            closeAll(createBlankDoc);
         }
      }
   }

   /**
    * Closes all tabs. Calls {@link #closeAll(boolean)} without creating a
    * new blank tab.
    *
    * @return  the boolean value that, if true, indicates that all tabs
    * were closed
    */
   public boolean isAllClosed() {
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
      boolean isBlankFirstTab = nTabs() == 1 && !edtDoc[0].hasFile()
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
         changedFileUpdate(false);
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
      format.setIndex(iTab);
      mw.setWordWrapSelected(editArea[iTab].isWordwrap());
      edit.setDocument(edtDoc[iTab]);
      proj.setDocumentIndex(iTab);
      mw.editTools().forEach((t) -> {
         t.setEditableDocument(edtDoc[iTab]);
      });
      mw.displayFrameTitle(edtDoc[iTab].filepath());
      mw.enableShowTabbar(nTabs() == 1);
      mw.setLanguageSelected(edtDoc[iTab].language(), !edtDoc[iTab].hasFile());
      edtDoc[iTab].setFocused();
   }

   private void changedFileUpdate(boolean updateFiletree) {
      proj.setDocumentIndex(iTab);
      proj.retrieveProject();
      mw.setLanguageSelected(edtDoc[iTab].language(), false);
      mw.displayFrameTitle(edtDoc[iTab].filepath());
      if (updateFiletree) {
         proj.updateFileTree();
      }
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

   private int unsavedTab() {
      int i;
      for (i = 0; i < nTabs(); i++) {
         if (!edtDoc[i].isSaved()) {
            break;
         }
      }
      return i;
   }

   private int saveOrCloseOption(int i) {
      String filename = edtDoc[i].filename();
      if (filename.length() == 0) {
         filename = "unnamed";
      }
      return Dialogs.confirmYesNoCancel
            ("Save changes in " + filename + " ?");
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
