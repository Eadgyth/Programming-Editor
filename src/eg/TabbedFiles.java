package eg;

import java.util.Observer;
import java.util.Observable;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.File;

//--Eadgyth--//
import eg.utils.Dialogs;

import eg.document.FileDocument;
import eg.ui.MainWin;
import eg.ui.EditArea;
import eg.ui.tabpane.ExtTabbedPane;

/**
 * The control of operations that require knowledge of the documents in
 * the tabs
 */
public class TabbedFiles implements Observer {

   private final FileDocument[] fDoc = new FileDocument[10];
   private final EditArea[] editArea = new EditArea[10];
   private final Preferences prefs = new Preferences();
   private final FileChooser fc;
   private final MainWin mw;
   private final ExtTabbedPane tabPane;
   private final EditAreaFormat format;
   private final DocumentUpdate docUpdate;

   /*
    * The index of the selected tab */
   private int iTab = -1;
   /*
    * The language read from prefs and set in the Languge menu */
   private Languages lang;

   public TabbedFiles(EditAreaFormat format, MainWin mw) {
      this.format = format;
      this.mw = mw;
      tabPane = mw.tabPane();
      docUpdate = new DocumentUpdate(mw, fDoc);
      format.setEditAreaArr(editArea);
      prefs.readPrefs();
      lang = Languages.valueOf(prefs.getProperty("language"));
      String recentDir = prefs.getProperty("recentPath");
      fc = new FileChooser(recentDir);

      tabPane.addChangeListener((ChangeEvent ce) -> {
          changeTabEvent(ce);
      });
      
      mw.winListen(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent we) {
            exit();
         }
      });
   }

   /**
    * Changes the language language in the currently selected
    * <code>FileDocument</code>.
    *
    * @param lang  a language in {@link Languages}
    */
   public void changeLanguage(Languages lang) {
      this.lang = lang;
      fDoc[iTab].changeLanguage(lang);
      mw.displayFileType(lang);
   }

   /**
    * Opens a new empty tab
    */
   public void openEmptyTab() {
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
    * Saves the text content of the selected tab.
    * <p>{@link #saveAs(boolean)} is called if the selected tab is
    * unnamed or if the content was read in from a file that no longer
    * exists on the hard drive.
    *
    * @param update  if the view (e.g. tab title, file view) is
    * updated and if it is tried to retrieve a project
    * @return  if the text content was saved
    */
   public boolean save(boolean update) {
      if (!fDoc[iTab].hasFile() || !fDoc[iTab].docFile().exists()) {
         return saveAs(update);
      }
      else {
         return fDoc[iTab].saveFile();
      }
   }

   /**
    * Saves the text content in all open documents
    */
   public void saveAll() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < nTabs(); i++) {
         if (fDoc[i].hasFile()) {
            if (fDoc[i].docFile().exists()) {
               fDoc[i].saveFile();
            }
            else {
               sb.append(fDoc[i].filename());
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
    * Saves the text content in the selected document as a new
    * file that is specified in the file chooser
    *
    * @param update  if the view (e.g. tab title, file view) is
    * updated and it is tried to retrieve a project
    * @return  if the text content was saved
    */
   public boolean saveAs(boolean update) {
      File f = fc.fileToSave(fDoc[iTab].filepath());
      boolean isSave = f != null;
      if (isSave && f.exists()) {
         isSave = 0 == replaceOption(f);
      }     
      isSave = isSave && fDoc[iTab].setFile(f);
      if (isSave && update) {
         docUpdate.changedFileUpdate(iTab, true);
         tabPane.changeTitle(iTab, fDoc[iTab].filename());
         prefs.storePrefs("recentPath", fDoc[iTab].dir());
      }   
      return isSave;
   }

   /**
    * Saves a copy of the content in the selected document to the file
    * that is selected in the file chooser
    */
   public void saveCopy() {
      File f = fc.fileToSave(fDoc[iTab].filepath());
      boolean isSave = f != null;
      if (isSave && f.exists()) {
         isSave = 0 == replaceOption(f);
      }     
      if (isSave) {
         fDoc[iTab].saveCopy(f);
      }
   }

   /**
    * Closes a tab if the text content is saved
    *
    * @param createEmptyTab  true to create a new empty tab in case the
    * tab to close is the only opened one
    */
   public void close(boolean createEmptyTab) {
      boolean removable = fDoc[iTab].isContentSaved();
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
         if (nTabs() == 0 && createEmptyTab) {
            openEmptyTab();
         }
      }
   }

   /**
    * Closes all tabs if the text contents of the documents
    * are saved
    */
   public void closeAll() {
      int count = unsavedTab();
      if (count == nTabs()) {
         int i = count - 1;
         while(i > -1) {     
            tabPane.removeTabAt(i);
            fDoc[i] = null;
            editArea[i] = null;
            i--;
         }
         openEmptyTab();
      }
      else {
         tabPane.setSelectedIndex(count);
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            if (save(false)) {
               closeAll();
            }
         }
         else if (res == JOptionPane.NO_OPTION) {
            removeTab();
            closeAll();
         }
      }
   }

   /**
    * Exits the program if the text contents of all open documents
    * are saved
    */
   public void exit() {
      int count = unsavedTab();
      if (count == nTabs()) {
         prefs.storePrefs("language", lang.toString());
         System.exit(0);
      }
      else {
         tabPane.setSelectedIndex(count);
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            if (save(false)) {
               exit();
            }
         }
         else if (res == JOptionPane.NO_OPTION) {
            removeTab();
            exit();
         }
      }
   }

   /**
    * Prints the text content in the selected document to a printer
    */
   public void print() {
      editArea[iTab].print();
   }

   //
   //--private methods--//
   //

   private void open(File f) {
      if (isFileOpen(f) || isMaxTabNumber()) {
         return;
      }
      boolean isBlankFirstTab = nTabs() == 1 && !fDoc[0].hasFile()
            && fDoc[0].getDocLength() == 0;

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

   private void createDocument(File f) {
      try {
         mw.setBusyCursor();
         int n = nTabs();
         editArea[n] = format.createEditArea();
         fDoc[n] = new FileDocument(editArea[n], f);
         fDoc[n].setIndentUnit(prefs.getProperty("indentUnit"));
         addNewTab(fDoc[n].filename(), editArea[n].editAreaPnl());
         docUpdate.changedFileUpdate(n, false);
         setUIUpdatersAt(n);
         prefs.storePrefs("recentPath", fDoc[n].dir());
      }
      finally {
         mw.setDefaultCursor();
      } 
   }
   
   private void createDocument() {
      int n = nTabs();
      editArea[n] = format.createEditArea();
      fDoc[n] = new FileDocument(editArea[n], lang);
      fDoc[n].setIndentUnit(prefs.getProperty("indentUnit"));
      addNewTab("unnamed", editArea[n].editAreaPnl());
      setUIUpdatersAt(n);
   }
   
   private void addNewTab(String filename, JPanel pnl) {
      JButton closeBt = new JButton(eg.ui.IconFiles.CLOSE_ICON);
      tabPane.addTab(filename, pnl, closeBt);
      closeBt.addActionListener(e -> {
         iTab = tabPane.iTabMouseOver();
         close(true);
      });
   }
   
   private void setUIUpdatersAt(int i) {
      fDoc[i].setUndoableChangeListener(e ->
            mw.enableUndoRedo(e.canUndo(), e.canRedo()));
      fDoc[i].setTextSelectionListener(e ->
            mw.enableCutCopy(e.isSelection()));
      fDoc[i].setLineAndColumnReadable((j, k) ->
            setLineAndColNr(j, k));
   }
   
   private void setLineAndColNr(int lineNr, int colNr) {
      mw.displayLineAndColNr(lineNr, colNr);
   }
   
   private void removeTab() {
      int count = iTab;
      tabPane.removeTabAt(iTab);
      for (int i = count; i < nTabs(); i++) {
         fDoc[i] = fDoc[i + 1];
         editArea[i] = editArea[i + 1];
      }
      int n = nTabs();    
      fDoc[n] = null;
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
      docUpdate.changedDocUpdate(iTab, nTabs());
   }
   
   private boolean isFileOpen(File f) {
      boolean isFileOpen = false;
      for (int i = 0; i < nTabs(); i++) {
         if (fDoc[i].filepath().equals(f.toString())) {
           isFileOpen = true;
           Dialogs.warnMessage(f.getName() + " is already open.");
           break;
         }
      }
      return isFileOpen;
   }
   
   private boolean isMaxTabNumber() {
      boolean isMax = nTabs() == fDoc.length;
      if (isMax) {
         Dialogs.warnMessage("The maximum number of tabs is reached.");
      }
      return isMax;
   }
   
   private int unsavedTab() {
      int i;
      for (i = 0; i < nTabs(); i++) {
         if (!fDoc[i].isContentSaved()) {
            break;
         }
      }
      return i;
   }

   private int saveOrCloseOption(int i) {
      String filename = fDoc[i].filename();
      if (filename.length() == 0) {
         filename = "unnamed";
      }
      return Dialogs.confirmYesNoCancel
            ("Save changes in " + filename + " ?");
   }
   
   private int replaceOption(File f) {
      return Dialogs.confirmYesNo(
             f.getName() + " already exists.\nReplace file?");
   }

   private int nTabs() {
      return tabPane.getTabCount();
   }
}
