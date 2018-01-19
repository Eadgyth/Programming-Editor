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
 * The control of operations that require knowledge of the documents in
 * the tabs
 */
public class TabbedDocuments implements Observer {

   private final EditableDocument[] edtDoc = new EditableDocument[15];
   private final EditArea[] editArea = new EditArea[15];
   private final Preferences prefs = new Preferences();
   private final FileChooser fc;
   private final MainWin mw;
   private final ExtTabbedPane tabPane;
   private final EditAreaFormat format;
   private final Edit edit;
   private final CurrentProject currProj;

   /*
    * The index of the selected tab */
   private int iTab = -1;
   /*
    * The language initially read from prefs and maybe set in the
    * Language menu */
   private Languages lang;

   public TabbedDocuments(EditAreaFormat format, MainWin mw) {
      this.format = format;
      this.mw = mw;
      tabPane = mw.tabPane();
      edit = new Edit();
      currProj = new CurrentProject(mw, edtDoc);
      mw.setEditTextActions(edit);
      mw.setProjectActions(currProj);
      format.setEditAreaArr(editArea);
      prefs.readPrefs();
      readLanguageFromPrefs();
      String recentDir = prefs.getProperty("recentPath");
      fc = new FileChooser(recentDir);

      tabPane.addChangeListener((ChangeEvent ce) -> {
          changeTabEvent(ce);
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
    * Opens a new tab with a blank document
    */
   public void createBlankDocument() {
      if (isTabOpenable()) {
         createDocument();
      }
   }

   /**
    * Opens a file that is double clicked in <code>FileTree</code>
    */
   @Override
   public void update(Observable o, Object arg) {
      File f = new File(arg.toString());
      open(f);
   }

   /**
    * Opens a file that is selected in the file chooser.
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
    * <p>{@link #saveAs(boolean)} is called if the selected tab is
    * unnamed or if the content was read in from a file that no longer
    * exists on the hard drive.
    *
    * @param update  if the view (e.g. tab title, file view) is
    * updated and if it is tried to retrieve a project. This applies
    * to "save as mode" only
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
    * @param update  if the view (e.g. tab title, file view) is
    * updated and it is tried to retrieve a project
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
         prefs.storePrefs("recentPath", edtDoc[iTab].dir());
      }   
      return isSave;
   }

   /**
    * Saves a copy of the content in the selected document to the file
    * that is selected in the file chooser
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
    * Tries to close the currently viewed tab and creates a tab with a
    * new blank document if the closed tab was the only open one and the
    * specified boolean is true
    *
    * @param createBlankDoc  the boolean that indicates if a new blank
    * document is created
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
    * Tries to close all tabs and creates a new tab with a blank document
    * if all previous tabs were closed and the specified boolean is true
    *
    * @param createBlankDoc  the boolean that is true to create a new blank
    * document
    */
   public void closeAll(boolean createBlankDoc) {
      int count = unsavedTab();
      if (count == nTabs()) {
         int i = count - 1;
         while ( i > -1 ) {     
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
         if (res == JOptionPane.YES_OPTION) {
            if (save(false)) {
               closeAll(createBlankDoc);
            }
         }
         else if (res == JOptionPane.NO_OPTION) {
            removeTab();
            closeAll(createBlankDoc);
         }
      }
   }
   
   /**
    * Closes all tabs
    *
    * @return  the boolean value that indicates if all tabs were
    * closed
    */
   public boolean isAllClosed() {
      closeAll(false);
      boolean isClosed = iTab == -1;
      if (isClosed) {
         prefs.storePrefs("language", lang.toString());
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
   //--private--//
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
      editArea[n] = format.createEditArea();
      edtDoc[n] = new EditableDocument(editArea[n], lang);
      prefs.readPrefs();
      edtDoc[n].setIndentUnit(prefs.getProperty("indentUnit"));
      edtDoc[n].setEditingStateReadable(editReadable);
      addNewTab("unnamed", editArea[n].editAreaPnl());
   }

   private void createDocument(File f) {
      try {
         mw.setBusyCursor();
         int n = nTabs();
         editArea[n] = format.createEditArea();
         edtDoc[n] = new EditableDocument(editArea[n], f);
         prefs.readPrefs();
         edtDoc[n].setIndentUnit(prefs.getProperty("indentUnit"));
         edtDoc[n].setEditingStateReadable(editReadable);
         addNewTab(edtDoc[n].filename(), editArea[n].editAreaPnl());
         changedFileUpdate(false);
         prefs.storePrefs("recentPath", edtDoc[n].dir());
      }
      finally {
         mw.setDefaultCursor();
      } 
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
   
   private void changeTabEvent(ChangeEvent changeEvent) {
      JTabbedPane sourceTb = (JTabbedPane) changeEvent.getSource();
      iTab = sourceTb.getSelectedIndex();
      if (iTab > -1) {
         changedTabUpdate();
      }
   }
   
   private void changedTabUpdate() {
      format.setEditAreaAt(iTab);
      mw.setWordWrapSelected(editArea[iTab].isWordwrap());
      edit.setDocument(edtDoc[iTab]);
      currProj.setDocumentAt(iTab);
      mw.editTools().forEach((t) -> {
         t.setEditableDocument(edtDoc[iTab]);
      });
      mw.displayFrameTitle(edtDoc[iTab].filepath());
      mw.enableShowTabbar(nTabs() == 1);
      mw.setLanguageSelected(edtDoc[iTab].language(), !edtDoc[iTab].hasFile());
      edtDoc[iTab].setFocused();
   }
   
   private void changedFileUpdate(boolean updateFiletree) {
      currProj.setDocumentAt(iTab);
      currProj.retrieveProject();
      mw.setLanguageSelected(edtDoc[iTab].language(), false);
      mw.displayFrameTitle(edtDoc[iTab].filepath());
      if (updateFiletree) {
         currProj.updateFileTree();
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
         Dialogs.errorMessage("The maximum number of tabs is reached.");
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
   
   private void readLanguageFromPrefs() {
      try {
         lang = Languages.valueOf(prefs.getProperty("language"));
      }
      catch (IllegalArgumentException e) {
         lang = Languages.NORMAL_TEXT;
      }
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
         mw.displayLCursorPosition(line, col);
      }
   };
}
