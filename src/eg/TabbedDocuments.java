package eg;

import java.awt.EventQueue;

import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;

import javax.swing.event.ChangeEvent;

import java.io.File;

//--Eadgyth--/
import eg.document.EditableDocument;
import eg.document.EditingStateReadable;
import eg.ui.MainWin;
import eg.ui.EditArea;
import eg.ui.tabpane.ExtTabbedPane;
import eg.ui.filetree.FileTree;
import eg.utils.Dialogs;

/**
 * The documents in the tabs.
 * <p>
 * Documents are represented by objects of {@link EditableDocument}
 * which are each displayed in a tab by an associated object of
 * {@link EditArea}.
 * <p>
 * If the tab bar is set invisible the single open document is also
 * referred to as the 'selected' document
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

   /**
    * @param mw  the reference to MainWin
    * @param format  the reference to Formatter
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
      mw.setEditActions(edit, (l) -> changeLanguage(l));

      FileTree ft = new FileTree(mw.treePanel(), (f) -> open(f));
      proj = new Projects(mw, ft, edtDoc);
      mw.setProjectActions(proj);

      String projectRoot = prefs.getProperty("ProjectRoot");
      if (projectRoot != null && projectRoot.length() > 0) {
         ft.setProjectTree(projectRoot);
      }

      String recentPath = prefs.getProperty("RecentPath");
      fc = new FileChooser(recentPath);

      createDocument();
   }

   /**
    * Opens a new blank document with the currently selected language.
    * Creates a new tab if the tab bar is visible and may ask to save
    * before opening otherwise.
    */
   public void openBlankDocument() {
      if (isTabOpenable() && !isMaxTabNumber()) {
         createDocument();
      }
   }

   /**
    * Opens a document with a file selected in the file chooser.
    * Creates a new tab if the tab bar is visible and may ask to save
    * before opening otherwise.
    */
   public void open() {
      File f = fc.fileToOpen();
      open(f);
   }

   /**
    * Saves the text content of the selected document; uses 'save as' if
    * the document has no file or a file that no more exists
    */
   public void save() {
      save(true);
   }

   /**
    * Saves the text content in all open documents; uses 'save as' for
    * documents that have no file or a file that no more exists
    */
   public void saveAll() {
      for (int i = 0; i < nTabs(); i++) {
         if (edtDoc[i].isSaved()) {
            continue;
         }
         if (edtDoc[i].hasFile() && edtDoc[i].file().exists()) {
             edtDoc[i].saveFile();
         }
         else {
            tabPane.setSelectedIndex(i);
            saveAs(true);
         }
      }
   }

   /**
    * Saves the text content of the selected document as a new file that
    * is specified in the file chooser
    */
   public void saveAs() {
      saveAs(true);
   }

   /**
    * Saves a copy of the text content of the selected document as new
    * file that is specified in the file chooser; the file is not set in
    * the document
    */
   public void saveCopy() {
      saveAs(false);
   }

   /**
    * Closes the selected document and may ask for saving; opens a new
    * blank document if the only one open was closed
    */
   public void close() {
      close(true);
   }

   /**
    * Closes all documents and may ask for saving; opens a new blank
    * document if all documents were closed
    */
   public void closeAll() {
      closeAll(true);
   }

   /**
    * Closes all documents and may ask for saving; stores different
    * properties in <code>Prefs</code>
    *
    * @return  true if all tabs were closed, false otherwise
    */
   public boolean closeAllForExit() {
      closeAll(false);
      boolean b = iTab == -1;
      if (b) {
         format.setProperties();
         prefs.setProperty("IndentUnit", edit.indentUnitSelection());
         prefs.setProperty("Language", lang.toString());
         prefs.setProperty("RecentPath", fc.currentDir());
      }
      return b;
   }

   /**
    * Prints the text content of the selected document to a printer
    */
   public void print() {
      mw.busyFunction().execute(() -> edtDoc[iTab].print());
   }

   //
   //--private--/
   //

   private void open(File f) {
      if (f == null || !exists(f) || isFileOpen(f) || isMaxTabNumber()) {
         return;
      }
      if (isOnlyUnnamedBlank()) {
         removeTab();
      }
      if (isTabOpenable()) {     
         mw.busyFunction().execute(() -> createDocument(f));
      }
   }

   private static boolean exists(File f) {
      if (f.exists()) {
         return true;
      }
      else {
         Dialogs.warnMessage(f.getName() + " was not found.");
         return false;
      }
   }

   private boolean isFileOpen(File f) {
      for (int i = 0; i < nTabs(); i++) {
         if (edtDoc[i].hasFile() && edtDoc[i].file().equals(f)) {
            Dialogs.infoMessage(
                  f.getName()
                  + " is already open.",
                  null);

            return true;
         }
      }
      return false;
   }

   private boolean isMaxTabNumber() {
      if (nTabs() < edtDoc.length) {
         return false;
      }
      else {
         Dialogs.errorMessage("The maximum number of tabs is reached.", null);
         return true;
      }
   }

   private boolean isOnlyUnnamedBlank() {
      return nTabs() == 1
            && !edtDoc[iTab].hasFile()
            && edtDoc[iTab].textLength() == 0;
   }

   private boolean isTabOpenable() {
      boolean b = iTab == -1 || tabPane.isShowTabbar();
      if (!b) {
         close(false);
         b = iTab == -1;
      }
      return b;
   }

   private void createDocument() {
      int n = nTabs();
      format.createEditAreaAt(n);
      edtDoc[n] = new EditableDocument(editArea[n], lang);
      setupDocument(n);
      tabPane.addTab("unnamed", editArea[n].content(), closeAct());
   }

   private void createDocument(File f) {
      int n = nTabs();
      format.createEditAreaAt(n);
      edtDoc[n] = new EditableDocument(editArea[n], f);
      setupDocument(n);
      tabPane.addTab(edtDoc[n].filename(), editArea[n].content(), closeAct());
      EventQueue.invokeLater(() -> proj.retrieve());
   }

   private void setupDocument(int index) {
      edtDoc[index].setIndentUnit(edit.indentUnitSelection());
      edtDoc[index].setEditingStateReadable(editState);
   }

   private FunctionalAction closeAct() {
      ActionListener close = e -> {
         iTab = tabPane.iTabMouseOver();
         close(true);
      };
      return new FunctionalAction(
            "", eg.ui.IconFiles.CLOSE_ICON, close);
   }

   private boolean save(boolean setFile) {
      if (edtDoc[iTab].hasFile() && edtDoc[iTab].file().exists()) {
         return edtDoc[iTab].saveFile();
      }
      else {
         return saveAs(setFile);
      }
   }

   private boolean saveAs(boolean setFile) {
      File f = fc.fileToSave(displayFilename());
      if (f == null) {
         return false;
      }
      if (f.exists() && JOptionPane.YES_OPTION != Dialogs.warnConfirmYesNo(
            f.getName() + " already exists.\nReplace file?")) {

         return false;
      }
      boolean b;
      if (setFile) {
         b = edtDoc[iTab].setFile(f);
         if (b) {
            changedFileUpdate(edtDoc[iTab]);
            tabPane.setTitle(iTab, edtDoc[iTab].filename());
            EventQueue.invokeLater(() -> proj.retrieve());
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

   private void close(boolean createBlankDoc) {
      if (createBlankDoc && isOnlyUnnamedBlank()) {
         return;
      }
      boolean b;
      if (!edtDoc[iTab].hasFile() || edtDoc[iTab].file().exists()) {
         b = edtDoc[iTab].isSaved();
         if (!b) {
            b = canCloseUnsavedFile(iTab);
         }
      }
      else {
         b = canCloseDeletedFile(iTab);
      }
      if (b) {
         removeTab();
         if (nTabs() == 0 && createBlankDoc) {
            openBlankDocument();
         }
      }
   }

   private void closeAll(boolean openBlankDoc) {
      if (openBlankDoc && isOnlyUnnamedBlank()) {
         return;
      }
      int iDeleted = iDeletedFile();
      int iUnsaved = iUnsavedFile();
      boolean b = iUnsaved == nTabs() && iDeleted == nTabs();
      if (b) {
         int i = nTabs() - 1;
         while (i > -1) {
            tabPane.removeTabAt(i);
            edtDoc[i] = null;
            editArea[i] = null;
            i--;
         }
         if (openBlankDoc) {
            openBlankDocument();
         }
      }
      else {
         if (iDeleted != nTabs()) {
            b = canCloseDeletedFile(iDeleted);
         }
         else {
            if (iUnsaved != nTabs()) {
               b = canCloseUnsavedFile(iUnsaved);
            }
         }
         if (b) {
            removeTab();
            closeAll(openBlankDoc);
         }
      }
   }

   private int iUnsavedFile() {
      int i;
      for (i = 0; i < nTabs(); i++) {
         if (!edtDoc[i].isSaved()) {
            break;
         }
      }
      return i;
   }

   private int iDeletedFile() {
      int i;
      for (i = 0; i < nTabs(); i++) {
         if (edtDoc[i].hasFile() && !edtDoc[i].file().exists()) {
            break;
         }
      }
      return i;
   }

   private boolean canCloseUnsavedFile(int i) {
      tabPane.setSelectedIndex(i);
      int res = Dialogs.confirmYesNoCancel(
            "Save changes in "
            + displayFilename()
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

   private boolean canCloseDeletedFile(int i) {
      tabPane.setSelectedIndex(i);
      int res = Dialogs.confirmYesNoCancel(
            edtDoc[i].filename()
            + " could not be found anymore."
            + " Save as new file?");

      boolean b;
      if (JOptionPane.YES_OPTION == res) {
         b = saveAs(false);
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

   String displayFilename() {
      if (edtDoc[iTab].hasFile()) {
         return edtDoc[iTab].filename();
      }
      else {
         return "unnamed";
      }
   }

   private int nTabs() {
      return tabPane.getTabCount();
   }

   private void changedTabUpdate() {
      EditableDocument doc = edtDoc[iTab];
      doc.setFocused();
      doc.readEditingState();
      format.setIndex(iTab);
      proj.setDocumentAt(iTab);
      edit.setDocument(doc);
      mw.editTools().forEach((t) -> {
         t.setDocument(doc);
      });
      mw.displayWordWrapState(editArea[iTab].isWordwrap());
      mw.enableHideTabbar(nTabs() == 1);
      changedFileUpdate(doc);
   }

   private void changedFileUpdate(EditableDocument doc) {
      proj.updateProjectControls();
      mw.enableAssignProject(doc.hasFile());
      mw.setLanguageSelected(doc.language());
      mw.displayFrameTitle(doc.filepath());
   }

   private void changeLanguage(Languages lang) {
      this.lang = lang;
      edtDoc[iTab].changeLanguage(lang);
      mw.displayLanguage(lang);
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
      public void updateInChangeState(boolean isSave) {
         mw.enableSave(isSave);
      }

      @Override
      public void updateUndoableState(boolean canUndo, boolean canRedo) {
         mw.enableUndoRedo(canUndo, canRedo);
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
