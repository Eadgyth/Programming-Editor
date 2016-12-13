package eg;

import java.util.Observer;
import java.util.Observable;

import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--//
import eg.utils.JOptions;
import eg.utils.FileUtils;

import eg.document.TextDocument;

import eg.ui.MainWin;
import eg.ui.TabbedPane;
import eg.ui.EditArea;

import eg.ui.filetree.FileTree;

import eg.projects.ProjectActions;
import eg.projects.ProjectFactory;

/**
 * Controls file operations that require knowledge of the opened tabs and 
 * the selected tab
 */
public class TabbedFiles implements Observer{

   private final TextDocument[] txtDoc = new TextDocument[10];
   private final EditArea[] editArea = new EditArea[10];
   private final FileChooserOpen fo = new FileChooserOpen();
   private final FileChooserSave fs = new FileChooserSave();
   private final Preferences prefs = new Preferences();   
   private final TabbedPane tabPane;
   private final MainWin mw;
   private final DocumentUpdate docUpdate;
   private final ChangeListener changeListener;
   private final CurrentProject currProj;

   /* The index of the selected tab */
   private int iTab = 0;
   
   public TabbedFiles(TabbedPane tabPane, MainWin mw, CurrentProject currProj,
         DocumentUpdate docUpdate) {

      this.tabPane = tabPane;
      this.mw = mw;
      this.docUpdate = docUpdate;
      this.currProj = currProj;

      currProj.setDocumentArr(txtDoc);
      docUpdate.setDocumentArrays(txtDoc, editArea);
      changeListener = (ChangeEvent changeEvent) -> {
         changeTabEvent(changeEvent);
      };
      tabPane.changeListen(changeListener);
      prefs.readPrefs();  
      newEmptyTab();
   }

   /**
    * Returns this array of type {@code TextDocument}
    * @return  this array of type {@link TextDocument}
    */
   public TextDocument[] getTextDocument() {
      return txtDoc;
   }
   
   /**
    * Returns this array of type {@code EditArea}
    * @return  this array of type {@link EditArea}
    */
   public EditArea[] getEditArea() {
      return editArea;
   }

   public void focusInSelectedTab() { 
      txtDoc[iTab].requestFocus();
   }

   /**
    * Opens a new 'unnamed' Tab to which no file is assigned
    */
   public void newEmptyTab() {
      editArea[tabPane.tabCount()] = new EditArea();
      txtDoc[tabPane.tabCount()] = new TextDocument(editArea[tabPane.tabCount()]);
      addNewTab("unnamed", editArea[tabPane.tabCount()].scrolledArea(),
            tabPane.tabCount());       
   }
   
   /**
    * Opens a file selected in {@code FileTree} in a new tab
    */
   @Override
   public void update(Observable o, Object arg) {
      File f = new File(arg.toString());
      open(f);
   }

   /**
    * Opens a file that is selected in the file chooser.
    * <p>
    * The file is opened in a new tab unless the only opened tab 
    * is unnamed and has an empty text area.
    * <p>
    * If a project is not yet defined and a {@link ProjectActions}
    * exists for the file type it is tried to set the project that 
    * was active when the program was closed the last time. 
    */
   public void openFileByChooser() {
      File f = fo.chosenFile();     
      if (f == null) { // if cancel or close window clicked
         return;
      }     
      if (!f.exists()) {
         JOptions.warnMessage(f.getName() + " is was not found");
      }
      else {
         open(f);
      }
   }

   /**
    * Saves the text content of the {@code TextDocument} in the selected
    * tab if a file has been assigned to it or saves the content as a new
    * file that is specified in the file chooser.
    * <p>
    * 'Save-as-mode' also applies if a file has been assigned to the
    * currently selected {@link TextDocument} but the file no longer
    * exists on the hard drive
    */
   public void saveOrSaveAs() {  
      if (txtDoc[iTab].filename().length() == 0 
            || !new File(txtDoc[iTab].filepath()).exists()) {
         saveAs();
      }
      else {
         txtDoc[iTab].saveToFile();
      }
   }

   /**
    * Saves the text content of the {@code TextDocument} objects
    * in all tabs
    */
   public void saveAll() {
      for (int count = 0; count < tabPane.tabCount(); count++) {
         if (txtDoc[count].filename().length() > 0) {
            txtDoc[count].saveToFile();
         } 
      }
   }

   /**
    * Saves the text content of the {@code TextDocument} in the selected tab
    * as a new file that is specified in the file chooser
    */
   public void saveAs() {
      File f = fs.fileToSave();
      if (f == null) {
         return; // if cancel or close window clicked
      }
      if (f.exists()) {
         JOptions.warnMessage(f.getName() + " already exists");
      }
      else {      
         txtDoc[iTab].saveFileAs(f);
         currProj.retrieveProject();
         currProj.updateFileTree(txtDoc[iTab].dir());
         tabPane.changeTabTitle(iTab, txtDoc[iTab].filename());
         mw.displayFrameTitle(txtDoc[iTab].filepath());
         prefs.storePrefs("recentPath", txtDoc[iTab].dir());
      }
   }

   /**
    * Closes a tab if the text content of its {@code TextDocument} is saved
    * or asks if closing shall happen with or without saving
    */
   public void tryClose() {
      if (txtDoc[iTab].isContentSaved()) {
         close();
      }
      else {                 
         int res = saveOrCloseOption(iTab);
         if (res == JOptionPane.YES_OPTION) {
            if (txtDoc[iTab].filename().length() == 0
                  || !new File(txtDoc[iTab].filepath()).exists()) {
               saveAs();
            }
            else {
               txtDoc[iTab].saveToFile();
               close();
            }
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
         }
      } 
   }
   
   /**
    * Closes all tabs or selects the first tab whose text content is found
    * unsaved
    */
   public void tryCloseAll() {
      int count = unsavedTab();
      if (count == tabPane.tabCount()) {     
         while(tabPane.tabCount() > 0) {
            tabPane.removeTab(iTab);
         }
         newEmptyTab();
      }
      else {
         tabPane.selectTab(count);                 
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            saveOrSaveAs();
            tryCloseAll();
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
            tryCloseAll();
         }
      }    
   }

   /**
    * Exits the program or selects the first tab whose text content is found
    * unsaved
    */
   public void tryExit() {
      int count = unsavedTab();
      if (count == tabPane.tabCount()) {
         System.exit(0);
      }
      else {
         tabPane.selectTab(count);                 
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            saveOrSaveAs();
            tryExit();
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
            tryExit();
         }
      }
   }

   //
   //---private methods --//
   //

   private void open(File file) {
      if (isFileOpen(file.toString())) {
         JOptions.warnMessage(file.getName() + " is open");
         return;
      }
      if (tabPane.tabCount() == txtDoc.length) {
         JOptions.warnMessage("The maximum number of tabs is reached.");
         return;
      }
      
      int openIndex = 0;
      boolean isUnnamedBlank = txtDoc[openIndex].filename().length() == 0
            && txtDoc[openIndex].textLength() == 0;
      if (isUnnamedBlank && tabPane.tabCount() == 1) { 
         txtDoc[openIndex].openFile(file);
      }
      else {
         openIndex = tabPane.tabCount();       
         editArea[openIndex] = new EditArea();
         txtDoc[openIndex] = new TextDocument(editArea[openIndex]);
         txtDoc[openIndex].openFile(file);
      }
      addNewTab(txtDoc[openIndex].filename(),
      editArea[openIndex].scrolledArea(), openIndex);
      mw.displayFrameTitle(txtDoc[openIndex].filepath());         
      currProj.retrieveProject();      
      prefs.storePrefs("recentPath", txtDoc[openIndex].dir());
   }

   private boolean isFileOpen(String fileToOpen) {
      boolean isFileOpen = false;
      for (int i = 0; i < tabPane.tabCount(); i++) {
         if (txtDoc[i].filepath().equals(fileToOpen)) {
           isFileOpen = true;
         }
      }
      return isFileOpen;
   }
   
   private void addNewTab(String filename, JPanel pnl, int index) {
      JButton closeBt = new JButton();
      tabPane.addNewTab(filename, pnl, closeBt, index);
      closeBt.addActionListener(e -> {
         iTab = tabPane.iTabMouseOver();
         tryClose();
      });
   }
   
   private int saveOrCloseOption(int index) {
      String filename = txtDoc[index].filename();
      if (filename.length() == 0) {
         filename = "unnamed";
      }
      return JOptions.confirmYesNoCancel
            ("Save changes in " + filename + " ?");
   }
   
   private int unsavedTab() {
      int count;
      for (count = 0; count < tabPane.tabCount(); count++) { 
         if (!txtDoc[count].isContentSaved()) {
            break;
         }
      }
      return count;
   }

   private void close() {
      int count = iTab; // remember the index of the tab that will be removed
      tabPane.removeTab(iTab);
      for (int i = count; i < tabPane.tabCount(); i++) {
         txtDoc[i] = txtDoc[i + 1];
         editArea[i] = editArea[i+1];
      }
      if (tabPane.tabCount() > 0) {
         txtDoc[tabPane.tabCount()] = null;
         editArea[tabPane.tabCount()] = null;
         int index = tabPane.selectedIndex();
         mw.displayFrameTitle(txtDoc[index].filepath());
      }
      else { 
         newEmptyTab();
      }     
   }
   
   private void changeTabEvent(ChangeEvent changeEvent) {
      JTabbedPane sourceTb = (JTabbedPane) changeEvent.getSource();
      iTab = sourceTb.getSelectedIndex();
      if (iTab > -1) {
         docUpdate.updateDocument(iTab);
         currProj.setDocumentIndex(iTab);
         mw.displayFrameTitle(txtDoc[iTab].filepath());
      }
   }
}