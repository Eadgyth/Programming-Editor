package eg;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.ui.MainWin;
import eg.ui.ProjectActionsControl;
import eg.ui.filetree.FileTree;
import eg.projects.ProjectActions;
import eg.projects.ProjectSelector;
import eg.projects.ProjectTypes;
import eg.document.EditableDocument;
import eg.utils.Dialogs;

/**
 * The assigned projects.
 * <p>
 * Projets are are represented by objects of {@link ProjectActions}.
 */
public class Projects {

   private final MainWin mw;
   private final ProjectActionsControl update;
   private final FileTree fileTree;
   private final ProjectSelector projSelect;
   private final ProcessStarter proc;
   private final EditableDocument[] edtDoc;
   private final Runnable fileTreeUpdate;
   private final List<ProjectActions> projList = new ArrayList<>();

   private ProjectActions current;
   private int iDoc;
   private boolean isReplace = false;

   /**
    * @param mw  the {@link MainWin}
    * @param fileTree  the {@link FileTree}
    * @param edtDoc  the array of {@link EditableDocument}
    */
   public Projects(MainWin mw, FileTree fileTree, EditableDocument[] edtDoc) {
      this.mw = mw;
      this.fileTree = fileTree;
      this.edtDoc = edtDoc;
      update = mw.projActControl();
      fileTreeUpdate = () -> fileTree.updateTree();
      Console cons = new Console(mw.consolePnl(), fileTreeUpdate);
      proc = cons.processStarter();
      projSelect = new ProjectSelector(update, cons);
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
      mw.enableAssignProject(edtDoc[iDoc].hasFile());
      if (current == null) {
         return;
      }
      ProjectActions inList;
      boolean isInProject = false;
      boolean isCurrent = false;
      if (edtDoc[iDoc].hasFile()) {
         inList = selectFromList(edtDoc[iDoc].fileParent(), false);
         isInProject = inList != null;
         isCurrent = inList == current;         
      }  
      mw.enableOpenSettingsWin(isInProject);
      mw.enableChangeProject(isInProject && !isCurrent);
      if (isCurrent) {
         current.enableActions();
      }
      else {
        update.disableProjectActions();
      }
   }

   /**
    * Updates the file tree
    */
   public void updateFileTree() {
      updateFileTreeImpl();
   }

   /**
    * Updates the file tree if <code>file</code> is contained in the
    * currently shown directory
    *
    * @param file  the file
    */
   public void updateFileTree(String file) {
      if (file.startsWith(fileTree.currentRoot())) {
         updateFileTreeImpl();
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
      ProjectActions inList = selectFromList(edtDoc[iDoc].fileParent(), false);
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
    * Tries to retrieve a saved project based on the directory of the
    * selected <code>EditableDocument</code>
    *
    * @see eg.projects.AbstractProject#retrieve
    */
   public void retrieve() {
      String dir = edtDoc[iDoc].fileParent();
      if (current != null && current.isInProject(dir)) {
         return;
      }
      ProjectActions projToFind = null;
      boolean isFound = false;
      for (ProjectTypes t : ProjectTypes.values()) {
         projToFind = projSelect.createProject(t);
         isFound = projToFind.retrieve(dir);
         if (isFound) {
            projToFind.buildSettingsWindow();
            break;
         }
      }
      if (isFound) {
         ProjectActions projFin = projToFind;
         if (current == null) {
            current = projFin;
            current.setConfiguringAction(e -> configure(current));
            projList.add(current);
            updateProjectSetting();
         }
         else {
            ProjectActions fromList = selectFromList(dir, true);
            if (fromList == null) {
               projFin.setConfiguringAction(e -> configure(projFin));
               projList.add(projFin);
               change(projFin);
            }
         }
      }
   }

   /**
    * Opens the window of the <code>SettingsWindow</code> of the project
    * that the selected <code>EditableDocument</code> belongs to
    */
   public void openSettingsWindow() {
      boolean open = true;
      ProjectActions inList = selectFromList(edtDoc[iDoc].fileParent(), false);
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
      ProjectActions inList = selectFromList(edtDoc[iDoc].fileParent(), true);
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
         BusyFunction bf = () -> {
            edtDoc[iDoc].saveFile();
            current.compile();
         };
         mw.runBusyFunction(bf);
         updateFileTreeImpl();
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
         BusyFunction bf = () -> current.compile();
         mw.runBusyFunction(bf);
         updateFileTreeImpl();
      }
   }

   /**
    * Runs the active project
    */
   public void run() {
      if (current.usesProjectFile()) {
         current.runProject();
      }
      else {
         current.runProject(edtDoc[iDoc].file().getPath());
      }
   }

   /**
    * Creates a build of the active project
    */
   public void build() {
      BusyFunction bf = () -> current.build();
      mw.runBusyFunction(bf);
      updateFileTreeImpl();
   }

   //
   //--private--/
   //

   private void assignImpl(ProjectTypes projType) {
      ProjectActions toAssign = projSelect.createProject(projType);
      if (toAssign != null) {
         ProjectActions projFin = toAssign;
         projFin.buildSettingsWindow();
         projFin.openSettingsWindow();
         projFin.setConfiguringAction(e -> configure(projFin));
      }
   }

   private boolean change(ProjectActions toChangeTo) {
      int res = switchProjectRes(toChangeTo.projectName());
      if (res == 0) {
         current = toChangeTo;
         current.storeConfiguration();
         updateProjectSetting();
         return true;
      }
      else {
         mw.enableChangeProject(true);
         update.disableProjectActions();
         return false;
      }
   }

   private void replaceCurrent(ProjectTypes projType, ProjectActions newProj) {
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

   private ProjectActions selectFromList(String dir, boolean excludeCurrent) {
      ProjectActions inList = null;
      for (ProjectActions p : projList) {
         if (p.isInProject(dir) && (!excludeCurrent || p != current)) {
            inList = p;
            break;
         }
      }
      return inList;
   }

   private void configure(ProjectActions toConfig) {
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
         updateFileTreeImpl();
      }
   }

   private void updateProjectSetting() {
      proc.setWorkingDir(current.projectPath());
      current.enableActions();
      mw.displayProjectName(current.projectName(),
            current.projectType().display());

      mw.enableChangeProject(false);
      mw.enableOpenSettingsWin(true);
      fileTree.setProjectTree(current.projectPath());
      fileTree.setDeletableDir(current.executableDirName());
   }

   private void updateFileTreeImpl() {
      EventQueue.invokeLater(fileTreeUpdate);
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
