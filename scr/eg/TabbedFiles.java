package eg;

import java.util.Observer;
import java.util.Observable;

import java.awt.EventQueue;

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
import eg.ui.TabbedPane;
import eg.ui.EditArea;

/**
 * Controls file operations that require knowledge of the opened tabs and 
 * the selected tab
 */
public class TabbedFiles implements Observer{

   private final TextDocument[] txtDoc = new TextDocument[10];
   private final EditArea[] edArea = new EditArea[10];
   private final FileChooserOpen fo;
   private final FileChooserSave fs;
   private final Preferences prefs = new Preferences();   
   private final TabbedPane tp;
   private final DisplaySetter displSet;
   private final FontSetter fontSet;
   private final DocumentUpdate docUpdate;
   private final ChangeListener changeListener;
   private final CurrentProject currProj;
   
   private Languages lang;

   /* The index of the selected tab */
   private int iTab = 0;
   
   public TabbedFiles(TabbedPane tp, DisplaySetter displSet,
         CurrentProject currProj, DocumentUpdate docUpdate) {

      this.tp = tp;
      this.displSet = displSet;
      this.docUpdate = docUpdate;
      this.currProj = currProj;

      fontSet = new FontSetter(edArea);
      docUpdate.setDocumentArrays(txtDoc, edArea);
      changeListener = (ChangeEvent changeEvent) -> {
         changeTabEvent(changeEvent);
      };
      tp.changeListen(changeListener);
      prefs.readPrefs();
      lang = Languages.valueOf(prefs.getProperty("language"));
      currProj.setDocumentArr(txtDoc);
      currProj.setLanguage(lang);
      String recentDir = prefs.getProperty("recentPath");
      fo = new FileChooserOpen(recentDir);
      fs = new FileChooserSave(recentDir);
      newEmptyTab();
   }
   
   /**
    * Sets the current language
    * @param lang  the language that has one of the constant
    * values in {@link Languages}
    */
   public void setLanguage(Languages lang) {
      this.lang = lang;
      currProj.setLanguage(lang);
      for (TextDocument t : txtDoc) {
         if (t != null) {
            t.changeLanguage(lang); // no effect if a file is assigned
         }
      }
      prefs.storePrefs("language", lang.toString());
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
   
   /**
    * Makes the window of this {@code FontSetter} visible/invisible
    */
   public void makeFontSetWinVisible() {
      fontSet.makeFontSetWinVisible();
   }

   /**
    * Sets the focus in the selected document
    */
   public void focusInSelectedTab() { 
      txtDoc[iTab].requestFocus();
   }

   /**
    * Opens a new 'unnamed' Tab to which no file is assigned
    */
   public final void newEmptyTab() {
      edArea[tp.nTabs()] = createEditArea();
      txtDoc[tp.nTabs()] = new TextDocument(edArea[tp.nTabs()], lang);
      addNewTab("unnamed", edArea[tp.nTabs()].textPanel(), tp.nTabs());
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
    * If a project is not yet defined it is tried to set active a
    * project 
    */
   public void openFileByChooser() {
      File f = fo.chosenFile();     
      if (f == null) {
         return;
      }     
      if (!f.exists()) {
         JOptions.warnMessage(f.getName() + " was not found");
      }
      else {
         open(f);
      }
   }

   /**
    * Saves the text content of the selected document.
    * <p>
    * If the selected document is unnamed {@link #saveAs()} is used. 
    * <p>
    * 'Save-as-mode' also applies if a file has been assigned to the
    * selected document but the file does not exists anymore.
    */
   public void save() {  
      if (txtDoc[iTab].filename().length() == 0 
            || !new File(txtDoc[iTab].filepath()).exists()) {
         saveAs();
      }
      else {
         txtDoc[iTab].saveToFile();
      }
   }

   /**
    * Saves the text content in all tabs.
    * <p>
    * A warning is shown if files no longer exist.
    */
   public void saveAll() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < tp.nTabs(); i++) {
         if (txtDoc[i].filename().length() > 0) {
            if (new File(txtDoc[i].filepath()).exists()) {
               txtDoc[i].saveToFile();
            }
            else {
               sb.append(txtDoc[i].filename());
               sb.append("\n");
            }
         } 
      }
      if (sb.length() > 0) {
         sb.insert(0, "These files were not found:\n");
         JOptions.warnMessage(sb.toString());
      }
   }

   /**
    * Saves the text content of the selected document as new file that
    * is specified in the file chooser
    */
   public void saveAs() {
      File f = fs.fileToSave(txtDoc[iTab].filepath());
      if (f == null) {
         return;
      }
      if (f.exists()) {
         JOptions.warnMessage(f.getName() + " already exists");
      }
      else {      
         txtDoc[iTab].saveFileAs(f);
         currProj.setDocumentIndex(iTab);
         currProj.retrieveProject();
         tp.changeTabTitle(iTab, txtDoc[iTab].filename());
         displSet.displayFrameTitle(txtDoc[iTab].filepath());
         prefs.storePrefs("recentPath", txtDoc[iTab].dir());
         EventQueue.invokeLater(() ->
               currProj.updateFileTree(txtDoc[iTab].dir()));
      }
   }
   
   /**
    * Saves a copy of the content in the selected document to the file
    * that is selected in the file chooser.
    * <p>
    * Method does not change the file of the document in the tab
    */
   public void saveCopy() {
      File f = fs.fileToSave(txtDoc[iTab].filepath());
      if (f == null) {
         return;
      }
      int res = 0;
      boolean storable = true;
      if (f.exists()) {
         res = JOptions.confirmYesNo(f.getName() + " already exists.\n"
               + " Replace file?");
         if (res == 0) {
            storable = f.delete();
         }            
      }
      if (res == 0 & storable) {
         txtDoc[iTab].saveCopy(f);
      }
      if (!storable) {
         JOptions.warnMessage(txtDoc[iTab].filepath()
               + "could not be replaces");
      }
   }      
   
   /**
    * Prints the text content of the selected document to a printer
    */
   public void print() {
      edArea[iTab].print();
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
         tp.selectTab(iTab);             
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
         for (int i = 0; i < txtDoc.length; i++) {
            txtDoc[i] = null;
            edArea[i] = null;
         }
         newEmptyTab();
      }
      else {
         tp.selectTab(count);                 
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            save();
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
            save();
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
            && edArea[openIndex].getDocText().length() == 0;
      if (isUnnamedBlank && tp.nTabs() == 1) {
         txtDoc[openIndex].openFile(file);
      }
      else {
         openIndex = tp.nTabs();       
         edArea[openIndex] = createEditArea();
         txtDoc[openIndex] = new TextDocument(edArea[openIndex]);
         txtDoc[openIndex].openFile(file);
      }
      addNewTab(txtDoc[openIndex].filename(),
           edArea[openIndex].textPanel(), openIndex);
      displSet.displayFrameTitle(txtDoc[openIndex].filepath());         
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
   
   private EditArea createEditArea() {
      boolean isWordWrap = displSet.isWordWrap();
      boolean isLineNr = displSet.isLineNumbers();
      String font = fontSet.getFont();
      int fontSize = fontSet.getFontSize();
      return new EditArea(isWordWrap, isLineNr, font, fontSize);
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
         displSet.displayFrameTitle(txtDoc[index].filepath());
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
         displSet.displayFrameTitle(txtDoc[iTab].filepath());
      }
   }
}
