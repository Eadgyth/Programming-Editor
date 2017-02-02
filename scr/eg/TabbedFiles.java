package eg;

import java.util.Observer;
import java.util.Observable;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.File;

//--Eadgyth--//
import eg.utils.JOptions;
import eg.document.TextDocument;
import eg.ui.MainWin;
import eg.ui.TabbedPane;
import eg.ui.EditArea;

import eg.projects.ProjectActions;

/**
 * Controls file operations that require knowledge of the opened tabs and 
 * the selected tab
 */
public class TabbedFiles implements Observer{

   private final TextDocument[] txtDoc = new TextDocument[10];
   private final EditArea[] edArea = new EditArea[10];
   private final FileChooserOpen fo = new FileChooserOpen();
   private final FileChooserSave fs = new FileChooserSave();
   private final Preferences prefs = new Preferences();   
   private final TabbedPane tp;
   private final MainWin mw;
   private final DocumentUpdate docUpdate;
   private final ChangeListener changeListener;
   private final CurrentProject currProj;

   /* The index of the selected tab */
   private int iTab = 0;
   
   public TabbedFiles(TabbedPane tp, MainWin mw, CurrentProject currProj,
         DocumentUpdate docUpdate) {

      this.tp = tp;
      this.mw = mw;
      this.docUpdate = docUpdate;
      this.currProj = currProj;

      currProj.setDocumentArr(txtDoc);
      docUpdate.setDocumentArrays(txtDoc, edArea);
      changeListener = (ChangeEvent changeEvent) -> {
         changeTabEvent(changeEvent);
      };
      tp.changeListen(changeListener);
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
      return edArea;
   }

   public void focusInSelectedTab() { 
      txtDoc[iTab].requestFocus();
   }

   /**
    * Opens a new 'unnamed' Tab to which no file is assigned
    */
   public final void newEmptyTab() {
      edArea[tp.nTabs()] = new EditArea();
      txtDoc[tp.nTabs()] = new TextDocument(edArea[tp.nTabs()]);
      addNewTab("unnamed", edArea[tp.nTabs()].scrolledArea(),
            tp.nTabs());       
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
    * If a project is not yet defined and a {@link ProjectActions}
    * exists for the file type it is tried to set active a project 
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
    * in all tabs.
    * <p>
    * In the case that TextDocuments are found whose filepath do
    * not refer to an existing file a list of this file is shown
    * in a dialog.
    */
   public void saveAll() {
      StringBuilder sb = new StringBuilder();
      for (int count = 0; count < tp.nTabs(); count++) {
         if (txtDoc[count].filename().length() > 0) {
            if (new File(txtDoc[count].filepath()).exists()) {
               txtDoc[count].saveToFile();
            }
            else {
               sb.append(txtDoc[count].filename());
               sb.append("\n");
            }
         } 
      }
      if (sb.length() > 0) {
         sb.insert(0, "These files seem to be deleted and were not newly saved:\n");
         JOptions.warnMessage(sb.toString());
      }
   }

   /**
    * Saves the text content of the {@code TextDocument} in the selected
    * tab as a new file that is specified in the file chooser
    */
   public void saveAs() {
      File f = fs.fileToSave(txtDoc[iTab].filepath());
      if (f == null) {
         return; // if cancel or close window clicked
      }
      if (f.exists()) {
         JOptions.warnMessage(f.getName() + " already exists");
      }
      else {      
         txtDoc[iTab].saveFileAs(f);
         currProj.setDocumentIndex(iTab);
         currProj.retrieveProject();
         currProj.updateFileTree(txtDoc[iTab].dir());
         tp.changeTabTitle(iTab, txtDoc[iTab].filename());
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
      if (count == tp.nTabs()) {     
         while(tp.nTabs() > 0) {
            tp.removeTab(iTab);
         }
         newEmptyTab();
      }
      else {
         tp.selectTab(count);                 
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
      if (count == tp.nTabs()) {
         System.exit(0);
      }
      else {
         tp.selectTab(count);                 
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
      if (tp.nTabs() == txtDoc.length) {
         JOptions.warnMessage("The maximum number of tabs is reached.");
         return;
      }
      
      int openIndex = 0;
      boolean isUnnamedBlank = txtDoc[openIndex].filename().length() == 0
            && txtDoc[openIndex].textLength() == 0;
      if (isUnnamedBlank && tp.nTabs() == 1) { 
         txtDoc[openIndex].openFile(file);
      }
      else {
         openIndex = tp.nTabs();       
         edArea[openIndex] = new EditArea();
         txtDoc[openIndex] = new TextDocument(edArea[openIndex]);
         txtDoc[openIndex].openFile(file);
      }
      addNewTab(txtDoc[openIndex].filename(),
      edArea[openIndex].scrolledArea(), openIndex);
      mw.displayFrameTitle(txtDoc[openIndex].filepath());         
      currProj.retrieveProject();      
      prefs.storePrefs("recentPath", txtDoc[openIndex].dir());
   }

   private boolean isFileOpen(String fileToOpen) {
      boolean isFileOpen = false;
      for (int i = 0; i < tp.nTabs(); i++) {
         if (txtDoc[i].filepath().equals(fileToOpen)) {
           isFileOpen = true;
         }
      }
      return isFileOpen;
   }
   
   private void addNewTab(String filename, JPanel pnl, int index) {
      JButton closeBt = new JButton();
      tp.addNewTab(filename, pnl, closeBt, index);
      closeBt.addActionListener(e -> {
         iTab = tp.iTabMouseOver();
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
      for (count = 0; count < tp.nTabs(); count++) { 
         if (!txtDoc[count].isContentSaved()) {
            break;
         }
      }
      return count;
   }

   private void close() {
      int count = iTab; // remember the index of the tab that will be removed
      tp.removeTab(iTab);
      for (int i = count; i < tp.nTabs(); i++) {
         txtDoc[i] = txtDoc[i + 1];
         edArea[i] = edArea[i+1];
      }
      if (tp.nTabs() > 0) {
         txtDoc[tp.nTabs()] = null;
         edArea[tp.nTabs()] = null;
         int index = tp.selectedIndex();
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
         txtDoc[iTab].requestFocus();
         docUpdate.updateDocument(iTab);
         currProj.setDocumentIndex(iTab);
         mw.displayFrameTitle(txtDoc[iTab].filepath());
      }
   }
}
