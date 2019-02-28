package eg;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.ui.ConsolePanel;
import eg.ui.MainWin;
import eg.ui.ProjectActionsUpdate;
import eg.ui.filetree.FileTree;
import eg.projects.ProjectCommands;
import eg.projects.ProjectSelector;
import eg.projects.ProjectTypes;
import eg.document.EditableDocument;
import eg.utils.Dialogs;

/**
 * The assigned projects.
 * <p>
 * Projets are represented by objects of {@link ProjectCommands}.
 */
public class Projects {

   private final MainWin mw;
   private final ProjectActionsUpdate update;
   private final FileTree fileTree;
   private final ProjectSelector projSelect;
   private final ProcessStarter proc;
   private final EditableDocument[] edtDoc;
   private final Runnable fileTreeUpdate;
   private final List<ProjectCommands> projList = new ArrayList<>();

   private ProjectCommands current;
   private int iDoc;
   private boolean isReplace = false;

   /**
    * @param mw  the reference to MainWin
    * @param fileTree  the reference to FileTree
    * @param edtDoc  the array of EditableDocument
    */
   public Projects(MainWin mw, FileTree fileTree, EditableDocument[] edtDoc) {
      this.mw = mw;
      this.fileTree = fileTree;
      this.edtDoc = edtDoc;
      update = mw.projActUpdate();
      fileTreeUpdate = () -> fileTree.updateTree();
      Console cons = new Console(mw.consolePnl());
      proc = new ProcessStarter(cons, fileTreeUpdate);
      TaskRunner runner = new TaskRunner(mw, cons, proc, fileTreeUpdate);     
      projSelect = new ProjectSelector(runner);
   }

   /**
    * Selects the element in this array of <code>EditableDocument</code>
    *
    * @param i  the index
    */
   public void setDocumentAt(int i) {
      iDoc = i;
   }

   /**
    * Enables or disables buttons and menu items depending on whether
    * the selected <code>EditableDocument</code> bolongs to the active,
    * a listed or no project
    */
   public void updateProjectControls() {
      if (current == null) {
         return;
      }
      ProjectCommands inList;
      boolean isInProject = false;
      boolean isCurrent = false;
      if (edtDoc[iDoc].hasFile()) {
         inList = selectFromList(edtDoc[iDoc].fileParent(), false);
         isInProject = inList != null;
         isCurrent = inList == current;
      }
      mw.enableOpenProjectSettings(isInProject);
      mw.enableChangeProject(isInProject && !isCurrent);
      if (isCurrent) {
         current.enable(update);
      }
      else {
         update.enable(false, false, false, null);
      }
   }

   /**
    * Updates the file tree
    */
   public void updateFileTree() {
      EventQueue.invokeLater(fileTreeUpdate);
   }

   /**
    * Updates the file tree if <code>file</code> is contained in the
    * currently shown directory
    *
    * @param file  the file
    */
   public void updateFileTree(String file) {
      if (file.startsWith(fileTree.currentRoot())) {
         EventQueue.invokeLater(fileTreeUpdate);
      }
   }

   /**
    * Assigns a new project that the file of the selected
    * <code>EditableDocument</code> belongs to; may ask to replace
    * the project
    *
    * @param projType  the project type
    */
   public void assign(ProjectTypes projType) {
      ProjectCommands inList = selectFromList(edtDoc[iDoc].fileParent(), false);
      if (inList == null) {
         assignImpl(projType);
      }
      else {
         if (inList == current || change(inList)) {
            replaceCurrent(projType, inList);
         }
      }
   }

   /**
    * Tries to retrieve a saved project that contains the directory
    * of the selected <code>EditableDocument</code>
    *
    * @see eg.projects.AbstractProject#retrieve
    */
   public void retrieve() {
      String dir = edtDoc[iDoc].fileParent();
      ProjectCommands inList = selectFromList(dir, false);
      if (current != null && inList != null) {
         return;
      }
      ProjectCommands projToFind = null;
      boolean isFound = false;
      for (ProjectTypes t : ProjectTypes.values()) {
         projToFind = projSelect.createProject(t);
         isFound = projToFind.retrieve(dir);
         if (isFound) {
            break;
         }
      }
      if (isFound) {
         if (current == null) {
            current = projToFind;
            projList.add(current);
            updateProjectSetting();
         }
         else {
            projList.add(projToFind);
            change(projToFind);
         }
         projToFind.buildSettingsWindow();
         ProjectCommands projFin = projToFind;
         projFin.setConfiguringAction(e -> configure(projFin));
      }
   }

   /**
    * Opens the window of the <code>SettingsWindow</code> of the project
    * that the selected <code>EditableDocument</code> belongs to
    */
   public void openSettingsWindow() {
      boolean open = true;
      ProjectCommands inList = selectFromList(edtDoc[iDoc].fileParent(), false);
      if (inList != null) {
         open = inList == current || change(inList);
      }
      if (open) {
         current.openSettingsWindow();
      }
   }

   /**
    * Changes to the project that the selected
    * <code>EditableDocument</code> belongs to
    */
   public void change() {
      ProjectCommands inList = selectFromList(edtDoc[iDoc].fileParent(), true);
      change(inList);
   }

   /**
    * Saves the selected file of the active project and compiles the
    * project
    */
   public void saveAndCompile() {
      if (!edtDoc[iDoc].file().exists()) {
         fileNotFoundMsg(edtDoc[iDoc].filename());
      }
      else {
         edtDoc[iDoc].saveFile();
         current.compile();
       }
   }

   /**
    * Saves all open files of the active project and compiles the project
    */
   public void saveAllAndCompile() {
      StringBuilder missingFiles = new StringBuilder();
      for (EditableDocument d : edtDoc) {
         boolean isProjSrc = d != null && d.hasFile()
               && current.isInProject(d.fileParent());

         if (isProjSrc) {
             if (d.file().exists()) {
                 d.saveFile();
             } else {
                 missingFiles.append("\n").append(d.filename());
             }
         }
      }
      if (missingFiles.length() > 0) {
         filesNotFoundMsg(missingFiles.toString());
      }
      else {
         current.compile();
      }
   }

   /**
    * Runs the active project
    */
   public void run() {
      if (current.usesProjectFile()) {
         current.run();
      }
      else {
        current.run(edtDoc[iDoc].filepath());
      }
   }

   /**
    * Creates a build of the active project
    */
   public void build() {
      current.build();
   }

   //
   //--private--/
   //

   private void assignImpl(ProjectTypes projType) {
      ProjectCommands toAssign = projSelect.createProject(projType);
      if (toAssign != null) {
         ProjectCommands projFin = toAssign;
         projFin.buildSettingsWindow();
         projFin.openSettingsWindow();
         projFin.setConfiguringAction(e -> configure(projFin));
      }
   }

   private boolean change(ProjectCommands toChangeTo) {
      int res = switchProjectRes(toChangeTo.projectName());
      if (res == 0) {
         current = toChangeTo;
         current.storeConfiguration();
         updateProjectSetting();
         return true;
      }
      else {
         mw.enableChangeProject(true);
         update.enable(false, false, false, null);
         return false;
      }
   }

   private void replaceCurrent(ProjectTypes projType, ProjectCommands newProj) {
      if (projType != newProj.projectType()) {
         int res = replaceProjectRes(
               newProj.projectName(),
               newProj.projectType().display(),
               projType.display());

         isReplace = 0 == res;
         if (isReplace) {
            assignImpl(projType);
         }
      }
      else {
         projectAssignedMsg(newProj.projectName(),
               newProj.projectType().display());
      }
   }

   private ProjectCommands selectFromList(String dir, boolean excludeCurrent) {
      ProjectCommands inList = null;
      for (ProjectCommands p : projList) {
         if (p.isInProject(dir) && (!excludeCurrent || p != current)) {
            inList = p;
            break;
         }
      }
      return inList;
   }

   private void configure(ProjectCommands toConfig) {
      if (toConfig.configure(edtDoc[iDoc].fileParent())) {
         if (isReplace) {
            projList.remove(current);
            isReplace = false;
         }
         if (toConfig != current) {
            current = toConfig;
            projList.add(current);
         }
         toConfig.storeConfiguration();
         updateProjectSetting();
         EventQueue.invokeLater(fileTreeUpdate);
      }
   }

   private void updateProjectSetting() {
      proc.setWorkingDir(current.projectPath());
      current.enable(update);
      mw.displayProjectName(current.projectName(),
            current.projectType().display());

      mw.enableChangeProject(false);
      mw.enableOpenProjectSettings(true);
      fileTree.setProjectTree(current.projectPath());
      fileTree.setDeletableDir(current.executableDirName());
   }

   private int replaceProjectRes(String projName, String previousProj,
         String newProj) {

      return Dialogs.warnConfirmYesNo(
            edtDoc[iDoc].filename()
            + " belongs to the "
            + previousProj
            + " project \'"
            + projName
            + "\'.\n"
            + "Replace the project with a new project in the category \'"
            + newProj
            + "\'?");
   }

   private void projectAssignedMsg(String projName, String currProj) {
      Dialogs.errorMessage(
            "A new project cannot be assigned.\n"
            + edtDoc[iDoc].filename()
            + " already belongs to the project \'"
            + projName
            + "\' in the category \'"
            + currProj
            + "\'.",
            null);
   }

   private int switchProjectRes(String projName) {
      return Dialogs.confirmYesNo("Switch to project " + projName + "?");
   }

   private void fileNotFoundMsg(String filename) {
       Dialogs.errorMessage(
              filename
              + ":\nThe file could not be found anymore",
              "Missing files");
   }

   private void filesNotFoundMsg(String filenames) {
      Dialogs.errorMessage(
              "The following files could not be found anymore:\n"
              + filenames,
              "Missing files");
   }
}
