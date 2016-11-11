package eg;

import java.util.Observer;
import java.util.Observable;

import java.awt.Container;
import java.awt.EventQueue;

import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.utils.ShowJOption;
import eg.utils.FileUtils;
import eg.document.TextDocument;
import eg.ui.MainWin;
import eg.ui.TabbedPane;
import eg.ui.filetree.FileTree;
import eg.projects.*;
import eg.plugin.PluginStarter;

/**
 * Controls actions that require knowledge of all open tabs and the
 * selected tab
 */
public class TabActions implements Observer{

   private final TabbedPane tabPane = new TabbedPane();
   private final MainWin mw;
   private final FileTree fileTree;
   private final ProjectFactory projFact;
   private final Edit edit;
   private final TextDocument[] txtDoc = new TextDocument[20];
   private final Preferences prefs = new Preferences();   
   private final FileChooserOpen fo = new FileChooserOpen();
   private final FileChooserSave fs = new FileChooserSave();
   private final PluginStarter plugStart;
   
   private ProjectActions projAct = null;

   /* The index of the selected tab */
   private int iTab = 0;

   /* Set if a java project is defined*/
   private boolean isProjectSet = false;
   
   public TabActions(MainWin mw, Edit edit,
          FileTree fileTree, ProjectFactory projFact,
          PluginStarter plugStart) {

      this.mw = mw;
      this.fileTree = fileTree;
      this.projFact = projFact;
      this.edit = edit;
      this.plugStart = plugStart;

      mw.addToTextArea(tabPane.tabbedPane());
      mw.winListen(winListener);
      tabPane.changeListen(changeListener);
      fileTree.addObserver(this);  
      prefs.readPrefs();  
      newEmptyTab();
   }

   /**
    * Returns the array of type {@code TextDocument}
    * @return  this array of type {@link TextDocument}
    */
   public TextDocument[] getTextDocument() {
      return txtDoc;
   }

   public void focusInSelectedTab() { 
      txtDoc[iTab].requestFocus();
   }
   
   @Override
   public void update(Observable o, Object arg) {
      File f = new File(arg.toString());
      open(f);
   }

   public void newEmptyTab() {
      txtDoc[tabPane.tabCount()] = new TextDocument();
      addNewTab("unnamed", txtDoc[tabPane.tabCount()].scrolledTextArea(),
            tabPane.tabCount());       
   }

   /**
    * Opens in a new tab a file that is selected in the file chooser
    */
   public void openFileByChooser() {
      File f = fo.chosenFile();     
      if (f == null) { // if cancel or close window clicked
         return;
      }     
      if (!f.exists()) {
         ShowJOption.warnMessage(f.getName() + " is was not found");
      }
      else {
         open(f);
      }
   }

   /**
    * Saves text area's content of the selected tab or calls
    * {@link #saveAs()} if the tab is unnamed
    */
   public void saveOrSaveAs() {  
      if (txtDoc[iTab].filename().length() == 0) {
         saveAs();
      }
      else {
         txtDoc[iTab].saveToFile();
      }
   }

   /**
    * Saves the content of all tabs
    */
   public void saveAll() {
      for (int count = 0; count < tabPane.tabCount(); count++) {
         if (txtDoc[count].filename().length() > 0) {
            txtDoc[count].saveToFile();
         }
      }
   }

   /**
    * Saves all open files and compiles project
    */
   public void saveAndCompile() {
      saveAll();
      projAct.compile();
   }

   /**
    * Saves the file of a selected tab as a new file selected
    * in file chooser
    */
   public void saveAs() {
      File f = fs.fileToSave();
      if (f == null) {
         return; // if cancel or close window clicked
      }
      if (f.exists()) {
         ShowJOption.warnMessage(f.getName() + " already exists");
      }
      else {      
         txtDoc[iTab].saveFileAs(f);
         if (!isProjectSet) {
            retrieveProject(txtDoc[iTab].dir());
         }
         else {
            if (projAct.isInProjectPath(txtDoc[iTab].dir())) {
               fileTree.addFile(txtDoc[iTab].filepath());
            }
         }
         tabPane.changeTabTitle(iTab, txtDoc[iTab].filename());
         mw.displayFrameTitle(txtDoc[iTab].filepath());
         prefs.storePrefs("recentPath", txtDoc[iTab].dir());
      }
   }

   /**
    * Closes a tab if the content of the text document is saved
    * or asks if closing shall happen with or without saving
    */
   public void tryClose() {
      if (txtDoc[iTab].isContentSaved()) {
         close();
      }
      else {                 
         int res = saveOrCloseOption(iTab);
         if (res == JOptionPane.YES_OPTION) {
            if (txtDoc[iTab].filename().length() == 0) {
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
    * Exits the programm or selects the first tab which is found
    * unsaved
    */
   public void tryExit() {
      int count;
      for (count = 0; count < tabPane.tabCount(); count++) { 
         if (!txtDoc[count].isContentSaved()) {
            break;
         }
      }

      if (count == tabPane.tabCount()) {     
         System.exit(0);
      }
      else {
         tabPane.selectTab(count);                 
         int res = saveOrCloseOption(count);
         if (res == JOptionPane.YES_OPTION) {
            txtDoc[iTab].saveToFile();
            tryExit();
         }
         else if (res == JOptionPane.NO_OPTION) {
            close();
            tryExit();
         }
      }
   }

   /**
    * Configures a project by entries in the project settings
    * window using the file at the selected tab
    */
   public void openProjectSetWin() {
      if (tabPane.tabCount() == 1 & txtDoc[iTab].filename().length() == 0) {
         ShowJOption.infoMessage("A file must be opened to set a project");
         return;
      }
      int result = -1;
      if (isProjectSet & !projAct.isInProjectPath(txtDoc[iTab].dir())) {
         result = ShowJOption.confirmYesNo("Change project ?");
      }
      /*
       * new ProjectActions object depending on the file extension */
      if (result == JOptionPane.YES_OPTION) {
         ProjectActions prNew = projFact.getProjAct(txtDoc[iTab].filepath());
         if (prNew != null & projAct != prNew) {
            projAct = prNew;
            projAct.getSetWin().okAct(e -> setNewProject());
         }
         result = -1;
      }
      if (result == -1) {
         projAct.makeSetWinVisible(true);
      }
   }

   //
   //---private methods --//
   //

   private void open(File file) {
      if (isFileOpen(file.toString())) {
         ShowJOption.warnMessage(file.getName() + " is open");
      }
      else {
         int openIndex = 0;
         boolean isUnnamedBlank = txtDoc[openIndex].filename().length() == 0
               && txtDoc[openIndex].getText().length() == 0;
         if (isUnnamedBlank && tabPane.tabCount() == 1) { 
            txtDoc[openIndex].openFile(file);
         }
         else {
            openIndex = tabPane.tabCount();
            if (openIndex < txtDoc.length) {
               txtDoc[openIndex] = new TextDocument();
               txtDoc[openIndex].openFile(file);
            }
            else {
               ShowJOption.warnMessage("Could not open " + file.getName()
                  + ". The maximum number of tabs is reached.");
               return;
            }
         }

         addNewTab(txtDoc[openIndex].filename(),
               txtDoc[openIndex].scrolledTextArea(), openIndex);
         mw.displayFrameTitle(txtDoc[openIndex].filepath());
           
         if (!isProjectSet) {
            retrieveProject(txtDoc[openIndex].dir());
         }      
         prefs.storePrefs("recentPath", txtDoc[openIndex].dir());
      }
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
   
   private void retrieveProject(String newPath) {
      ProjectActions prNew = projFact.getProjAct(txtDoc[iTab].filepath());
      if (prNew != null && projAct != prNew) {
         projAct = null;
         projAct = prNew;
         registerProjectActions();
         projAct.getSetWin().okAct(e -> setNewProject());
      }
      if (projAct != null) {
         mw.enableExtra(projFact.isCompile(), projFact.isRun(), projFact.isBuild());
         projAct.findPreviousProjectRoot(newPath);
         if (projAct.getProjectRoot().length() > 0) {
            isProjectSet = true;
            updateProjectDisplay(projAct.getProjectRoot());
         }
         else {
            mw.showProjectInfo("not set");
         }
      }
   }

   private void setNewProject() {
      projAct.configFromSetWin(txtDoc[iTab].dir(),
            FileUtils.extension(txtDoc[iTab].filename()));
      if (projAct.getProjectRoot().length() > 0) {        
         updateProjectDisplay(projAct.getProjectRoot());
         mw.enableExtra(projFact.isCompile(), projFact.isRun(), projFact.isBuild());
         isProjectSet = true;
      }
   }
   
   /**
    * Updates the display when a new project is defined either by
    * finding the recent project upon opening the first file or by
    * the configs in the project settings window
    */
   private void updateProjectDisplay(String path) {
      File file = new File(path);
      mw.showProjectInfo(file.getName());
      mw.enableFileViewItm(true);
      fileTree.setProjectTree(path);
   }
   
   private int saveOrCloseOption(int index) {
      String filename = txtDoc[index].filename();
      if (filename.length() == 0) {
         filename = "unnamed";
      }
      return ShowJOption.confirmYesNoCancel
            ("Save changes in " + filename + " ?");
   }    

   private void close() {
      int count = iTab; // remember the index of the tab that will be removed
      tabPane.removeTab(iTab);
      for (int i = count; i < tabPane.tabCount(); i++) {
         txtDoc[i] = txtDoc[i + 1];
      }
      if (tabPane.tabCount() > 0) {
         int index = tabPane.selectedIndex();
         mw.displayFrameTitle(txtDoc[index].filepath());
      }
      else { 
         newEmptyTab();
      }     
   }
   
   private ChangeListener changeListener = new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
         JTabbedPane sourceTb = (JTabbedPane) changeEvent.getSource();
         iTab = sourceTb.getSelectedIndex();
         if (iTab > -1) {           
            mw.displayFrameTitle(txtDoc[iTab].filepath());       
            focusInSelectedTab();
            edit.setTextObject(txtDoc[iTab]);
            plugStart.setTextDocument(txtDoc[iTab]);
         }
      }
   };

   private WindowListener winListener = new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
         tryExit();
      }
   };
   
   private void registerProjectActions() {
      mw.runAct(e -> projAct.runProject());
      mw.buildAct(e -> projAct.build());
   }
}