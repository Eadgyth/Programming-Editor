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
 * Documents viewed when tabs are selected are represented by objects
 * of {@link EditableDocument}. If the tabbar is set invisible (see
 * {@link ExtTabbedPane}) the viewed document is regarded as (and
 * technically is) the 'selected document'.
 */
public class TabbedDocuments {

   private final MainWin mw;
   private final ExtTabbedPane tabPane;
   private final EditableDocument[] edtDoc;
   private final Prefs prefs = new Prefs();
   private final FileChooser fc;
   private final Formatter format;
   private final Edit edit;
   private final Projects proj;

   private EditArea[] editArea = null;
   private int iTab = -1;
   private Languages lang;
   private boolean isEdited = false;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param format  the reference to {@link Formatter}
    */
   public TabbedDocuments(MainWin mw, Formatter format) {
      this.mw = mw;
      this.format = format;
      editArea = format.editAreaArray();
      edtDoc = new EditableDocument[editArea.length];

      tabPane = mw.tabPane();
      tabPane.addChangeListener((ChangeEvent ce) -> {
         JTabbedPane sourceTb = (JTabbedPane) ce.getSource();
         iTab = sourceTb.getSelectedIndex();
         if (iTab > -1) {
            changedTabUpdate();
         }
      });

      initLanguage();

      String indentUnit = prefs.getProperty("IndentUnit");
      edit = new Edit(indentUnit);
      mw.setEditActions(edit);

      FileOpenable opener = (f) -> open(f);
      FileTree ft = new FileTree(mw.treePanel(), opener);
      proj = new Projects(mw, ft, edtDoc);
      mw.setProjectActions(proj);

      String projectRoot = prefs.getProperty("ProjectRoot");
      if (projectRoot != null && projectRoot.length() > 0) {
         ft.setProjectTree(projectRoot);
      }

      String recentPath = prefs.getProperty("RecentPath");
      fc = new FileChooser(recentPath);
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
    * Creates a new tab with a blank document in which this language
    * is set
    */
   public void createBlankDocument() {
      if (isTabOpenable()) {
         createDocument();
      }
   }

   /**
    * Opens a tab with a document in which a file selected in the file
    * chooser is set
    */
   public void open() {
      File f = fc.fileToOpen();
      open(f);
   }

   /**
    * Saves the text content in the selected document to its file or
    * uses {@link #saveAs} if no file is set in the document or a set
    * file no more exists.
    */
   public void save() {
      save(true);
   }

   /**
    * Saves the text content in the selected document as a new file
    * that is selected in the file chooser and sets the file in the
    * document
    */
   public void saveAs() {
      saveNew(true);
   }

   /**
    * Saves the text content in all documents that have an existing file
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
    * Saves a copy of the content in the selected document as new file
    * that is specified in the file chooser but does not set the file
    * in the document
    */
   public void saveCopy() {
      saveNew(false);
   }

   /**
    * Closes the selected tab and may ask to save the content of the
    * document before closing
    *
    * @param createBlankDoc  the boolean that is true to create a new
    * tab with a blank document if the tab to be closed is the only open
    * tab
    */
   public void close(boolean createBlankDoc) {
      if (createBlankDoc && isFirstTabUnnamed() && edtDoc[iTab].docLength() == 0) {
         return;
      }
      boolean b;
      if (!edtDoc[iTab].hasFile() || edtDoc[iTab].docFile().exists()) {
         b = edtDoc[iTab].isSaved();
         if (!b) {
            b = closeUnsavedFile(iTab);
         }
      }
      else {
         b = closeMissingFile(iTab);
      }
      if (b) {
         removeTab();
         if (nTabs() == 0 && createBlankDoc) {
            createBlankDocument();
         }
      }
   }

   /**
    * Closes all tabs and may ask to save the content of documents
    * before closing
    *
    * @param createBlankDoc  the boolean value that is true to create
    * a new blank document after closing
    */
   public void closeAll(boolean createBlankDoc) {
      int iMissing = missingFile();
      int iUnsaved = unsavedFile();
      boolean b = iUnsaved == nTabs() && iMissing == nTabs();
      if (b) {
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
            b = closeMissingFile(iMissing);
         }
         else {
            if (iUnsaved != nTabs()) {
               b = closeUnsavedFile(iUnsaved);
            }
         }
         if (b) {
            removeTab();
            closeAll(createBlankDoc);
         }
      }
   }

   /**
    * Closes all tabs and may ask to save the content of documents
    * before closing; saves properties to 'Prefs'
    *
    * @return  true if all tabs were closed, false otherwise
    */
   public boolean closeForExit() {
      closeAll(false);
      boolean b = iTab == -1;
      if (b) {
         format.setProperties();
         prefs.setProperty("IndentUnit", edit.changedIndentUnit());
         prefs.setProperty("Language", lang.toString());
         prefs.setProperty("RecentPath", fc.currentDir());
      }
      return b;
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
      if (f == null) {
         return;
      }
      if (!f.exists()) {
         Dialogs.warnMessage(f.getName() + " was not found.");
         return;
      }
      if (isFileOpen(f) || isMaxTabNumber()) {
         return;
      }
      if (isFirstTabUnnamed() && !isEdited) {
         removeTab();
      }
      if (isTabOpenable()) {
         createDocument(f);
      }
   }

   private boolean isTabOpenable() {
      boolean b = iTab == -1 || tabPane.isShowTabbar();
      if (!b) {
         close(false);
         b = iTab == -1;
      }
      return b;
   }

   private boolean isFileOpen(File f) {
      boolean b = false;
      for (int i = 0; i < nTabs(); i++) {
         if (edtDoc[i].filepath().equals(f.toString())) {
           b = true;
           Dialogs.infoMessage(
                 f.getName()
                 + " is already open.",
                 null);

           break;
         }
      }
      return b;
   }

   private boolean isMaxTabNumber() {
      boolean b = nTabs() == edtDoc.length;
      if (b) {
         Dialogs.errorMessage("The maximum number of tabs is reached.", null);
      }
      return b;
   }

   private boolean isFirstTabUnnamed() {
      return nTabs() == 1 && !edtDoc[iTab].hasFile();
   }

   private void createDocument() {
      int n = nTabs();
      format.createEditAreaAt(n);
      edtDoc[n] = new EditableDocument(editArea[n], lang);
      setupDocument(n);
      addTab("unnamed", editArea[n].content());
   }

   private void createDocument(File f) {
      try {
         mw.setBusyCursor();
         int n = nTabs();
         format.createEditAreaAt(n);
         edtDoc[n] = new EditableDocument(editArea[n], f);
         setupDocument(n);
         addTab(edtDoc[n].filename(), editArea[n].content());
         changedFileUpdate();
         proj.retrieve();
     }
     finally {
        mw.setDefaultCursor();
     }
   }

   private void setupDocument(int index) {
      edtDoc[index].setIndentUnit(edit.changedIndentUnit());
      edtDoc[index].setEditingStateReadable(editState);
   }

   private void addTab(String filename, JPanel pnl) {
      JButton closeBt = new JButton(eg.ui.IconFiles.CLOSE_ICON);
      tabPane.addTab(filename, pnl, closeBt);
      closeBt.addActionListener(e -> {
         iTab = tabPane.iTabMouseOver();
         close(true);
      });
   }

   private boolean save(boolean setFile) {
      if (!edtDoc[iTab].hasFile() || !edtDoc[iTab].docFile().exists()) {
         return saveNew(setFile);
      }
      else {
         return edtDoc[iTab].saveFile();
      }
   }

   private boolean saveNew(boolean setFile) {
      File f = fc.fileToSave(edtDoc[iTab].filepath());
      if (f == null) {
         return false;
      }
      if (f.exists() && 0 != replaceFileOption(f)) {
         return false;
      }
      boolean b;
      if (setFile) {
         b = edtDoc[iTab].setFile(f);
         if (b) {
            changedFileUpdate();
            tabPane.setTitle(iTab, edtDoc[iTab].filename());
            proj.retrieve();
         }
      }
      else {
         b = edtDoc[iTab].saveCopy(f);
      }
      if (b) {
         proj.updateFileTree(f.toString());
      }
      return b;
   }

   private int replaceFileOption(File f) {
      return Dialogs.warnConfirmYesNo(
            f.getName() + " already exists.\nReplace file?");
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

   private boolean closeUnsavedFile(int i) {
      tabPane.setSelectedIndex(i);
      String filename = edtDoc[i].filename();
      if (filename.isEmpty()) {
         filename = "unnamed";
      }
      int res = Dialogs.confirmYesNoCancel(
            "Save changes in "
            + filename
            + " ?");

      boolean b;
      if (JOptionPane.YES_OPTION == res) {
         b = save(false);
      }
      else {
         b = JOptionPane.NO_OPTION == res;
      }
      return b;
   }

   private boolean closeMissingFile(int i) {
      tabPane.setSelectedIndex(i);
      String filename = edtDoc[i].filename();
      int res = Dialogs.confirmYesNoCancel(
            filename
            + " could not be found anymore."
            + " Save as new file?");

      boolean b;
      if (JOptionPane.YES_OPTION == res) {
         b = saveNew(false);
      }
      else {
         b = JOptionPane.NO_OPTION == res;
      }
      return b;
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

   private int nTabs() {
      return tabPane.getTabCount();
   }

   private void changedTabUpdate() {
      edtDoc[iTab].setFocused();
      format.setIndex(iTab);
      proj.setDocumentAt(iTab);
      edit.setDocument(edtDoc[iTab]);
      mw.displayWordWrapState(editArea[iTab].isWordwrap());
      mw.editTools().forEach((t) -> {
         t.setEditableDocument(edtDoc[iTab]);
      });
      mw.displayFrameTitle(edtDoc[iTab].filepath());
      mw.enableHideTabbar(nTabs() == 1);
      mw.displayLanguage(edtDoc[iTab].language(), !edtDoc[iTab].hasFile());
   }

   private void changedFileUpdate() {
      proj.updateUIForDocument();
      mw.displayLanguage(edtDoc[iTab].language(), false);
      mw.displayFrameTitle(edtDoc[iTab].filepath());
   }

   private void initLanguage() {
      try {
         lang = Languages.valueOf(prefs.getProperty("Language"));
      }
      catch (IllegalArgumentException e) {
         lang = Languages.NORMAL_TEXT;
      }
   }

   private final EditingStateReadable editState = new EditingStateReadable() {

      @Override
      public void updateInChangeState(boolean b) {
        mw.enableSave(b);
      }

      @Override
      public void updateUndoableState(boolean canUndo, boolean canRedo) {
         mw.enableUndoRedo(canUndo, canRedo);
         isEdited = canUndo;
      }

      @Override
      public void updateSelectionState(boolean isSelection) {
         mw.enableCutCopy(isSelection);
      }

      @Override
      public void updateCursorState(int line, int col) {
         mw.displayCursorPosition(line, col);
      }
   };
}
