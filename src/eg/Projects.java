package eg;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.ui.MainWin;
import eg.ui.ProjectStateUpdate;
import eg.ui.ConsoleOpenable;
import eg.ui.filetree.FileTree;
import eg.projects.ProjectActions;
import eg.projects.ProjectSelector;
import eg.projects.ProjectTypes;
import eg.projects.ProjectTypeChange;
import eg.document.EditableDocument;
import eg.utils.Dialogs;

/**
 * The assigned projects which are reprented by objects of
 * <code>ProjectActions</code>.
 * <p>
 * Class maintains a <code>List</code> of assigned projects of which one
 * is set active. Assigning and retrieving projects as well as changing
 * between listed projects takes place based on the selected element
 * in the array of {@link EditableDocument} objects.
 *
 * @see ProjectActions
 */
public class Projects {

   private final MainWin mw;
   private final ProjectStateUpdate update;
   private final FileTree fileTree;
   private final ProjectSelector projSelect;
   private final ProcessStarter proc;
   private final EditableDocument[] edtDoc;
   private final ProjectTypeChange projTypeChg;
   private final List<ProjectActions> projList = new ArrayList<>();

   private ProjectActions current;
   private int iDoc;
   private boolean isReplace = false;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param fileTree  the reference to {@link FileTree}
    * @param edtDoc  the reference to the array of {@link EditableDocument}
    */
   public Projects(MainWin mw, FileTree fileTree, EditableDocument[] edtDoc) {
      this.mw = mw;
      this.fileTree = fileTree;
      this.edtDoc = edtDoc;
      update = mw.projectUpdate();
      projTypeChg = new ProjectTypeChange(update);
      Console cons = new Console(mw.consolePnl());
      proc = cons.processStarter();
      ConsoleOpenable co = mw.consoleOpener();
      projSelect = new ProjectSelector(co, cons);
   }

   /**
    * Selects the element in this array of <code>EditableDocument</code>
    *
    * @param i  the index
    */
   public void setDocumentAt(int i) {
      iDoc = i;
      updateUIForDocument();
   }

   /**
    * Enables or disables buttons and menu items depending on
    * the belonging of the file of the currently selected
    * <code>EditableDocument</code> to a listed or the active
    * project
    */
   public void updateUIForDocument() {
      ProjectActions inList = null;
      boolean isProject = false;
      if (edtDoc[iDoc].hasFile()) {
         inList = selectFromList(edtDoc[iDoc].dir(), false);
         isProject = inList != null;
      }
      update.enableOpenSettingsWin(isProject);
      update.enableChangeProject(isProject && inList != current);
      if (isProject) {
         if (!current.isInProject(edtDoc[iDoc].dir())) {
            update.enableProjectActions(false, false, false);
         }
         else {
            projTypeChg.enableProjectActions(current.projectType());
         }
      }
      else {
         update.enableProjectActions(false, false, false);
      }
   }

   /**
    * Assigns a new project or asks to replace the project if the
    * selected <code>EditableDocument</code> already bolongs to a project
    *
    * @param projType  the project type which has a valaue in
    * {@link ProjectTypes}
    */
   public void assign(ProjectTypes projType) {
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), false);
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
    * Retrieves a project from a configuration that was saved in a
    * ProjConfig file in a project or in the Prefs file in the program
    * directory
    *
    * @see eg.projects.AbstractProject#retrieve(String)
    */
   public void retrieve() {
      String dir = edtDoc[iDoc].dir();
      if (current != null && current.isInProject(dir)) {
         return;
      }
      EventQueue.invokeLater(() -> {
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
      });
   }

   /**
    * Updates the file tree if <code>file</code> is contained in the
    * directory of the currently shown file tree
    *
    * @param file  the file
    */
   public void updateFileTree(String file) {
      if (file.startsWith(fileTree.currentRoot())) {
         updateFileTree();
      }
   }

   /**
    * Opens the window of the <code>SettingsWindow</code> object
    * that belongs to the active project or to one that is changed
    * to
    */
   public void openSettingsWindow() {
      boolean open = true;
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), false);
      if (inList != null) {
         open = inList == current || change(inList);
      }
      if (open) {
         current.openSettingsWindow();
      }
   }

   /**
    * Changes to the project which the currently selected
    * <code>EditableDocument</code> belongs to
    */
   public void change() {
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), true);
      change(inList);
   }

   /**
    * Saves the selected file of the active project and copmiles
    * compiles the project
    */
   public void saveAndCompile() {
      if (!edtDoc[iDoc].docFile().exists()) {
         fileNotFoundMsg(edtDoc[iDoc].filename());
      }
      else {
         try {
            edtDoc[iDoc].saveFile();
            current.compile();
         }
         finally {
            updateFileTree();
            EventQueue.invokeLater(() ->  mw.setDefaultCursor());
         }
      }
   }

   /**
    * Saves all open files of the active project and compiles
    * the project
    */
   public void saveAllAndCompile() {
      StringBuilder missingFiles = new StringBuilder();
      for (EditableDocument d : edtDoc) {
         boolean isProjSrc = d != null && current.isInProject(d.dir());
         if (isProjSrc) {
             if (d.docFile().exists()) {
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
         try {
            mw.setBusyCursor();
            current.compile();
         }
         finally {
            updateFileTree();
            EventQueue.invokeLater(() -> mw.setDefaultCursor());
         }
      }
   }

   /**
    * Runs the currently active project
    */
   public void run() {
      if (current.usesProjectFile()) {
         current.runProject();
      }
      else {
         current.runProject(edtDoc[iDoc].filepath());
      }
   }

   /**
    * Creates a build of the currently active project
    */
   public void build() {
      try {
         mw.setBusyCursor();
         current.build();
      }
      finally {
         updateFileTree();
         EventQueue.invokeLater(() ->  mw.setDefaultCursor());
      }
   }

   //
   //--private--/
   //

   private void assignImpl(ProjectTypes projType) {
      if (!edtDoc[iDoc].hasFile()) {
         Dialogs.infoMessage(NO_FILE_MSG, null);
         return;
      }
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
         update.enableChangeProject(true);
         update.enableProjectActions(false, false, false);
         return false;
      }
   }

   private void replaceCurrent(ProjectTypes projType, ProjectActions newProj) {
      if (projType != newProj.projectType()) {
         int res = replaceProjectRes(
               edtDoc[iDoc].filename(),
               newProj.projectName(),
               newProj.projectType().display(),
               projType.display());

         isReplace = 0 == res;
         if (isReplace) {
            assignImpl(projType);
         }
      }
      else {
         projectAssignedMsg(
               edtDoc[iDoc].filename(),
               newProj.projectName(),
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
      if (toConfig.configure(edtDoc[iDoc].dir())) {
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
         updateFileTree();
      }
   }

   private void updateProjectSetting() {
      proc.setWorkingDir(current.projectPath());
      projTypeChg.enableProjectActions(current.projectType());
      projTypeChg.setBuildLabel(current.projectType());
      update.displayProjectName(current.projectName(),
            current.projectType().display());

      fileTree.setProjectTree(current.projectPath());
      fileTree.setDeletableDir(current.executableDirName());
      update.enableChangeProject(false);
      update.enableOpenSettingsWin(true);
   }

   public void updateFileTree() {
      EventQueue.invokeLater(() -> fileTree.updateTree());
   }

   private int replaceProjectRes(String filename, String projName,
         String previousProjDispl, String newProjDispl) {

      return Dialogs.warnConfirmYesNo(
            filename
            + " belongs to the "
            + previousProjDispl
            + " project \""
            + projName
            + "\".\n"
            + "Remove "
            + projName
            + " and assign a new project"
            + " of the category \""
            + newProjDispl
            + "\"?");
   }

   private void projectAssignedMsg(String filename, String projName,
         String currProjDispl) {

      Dialogs.infoMessage(
            edtDoc[iDoc].filename()
            + " already belongs to the project "
            + projName
            + " in the category \""
            + currProjDispl
            + "\".",
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

   private final String NO_FILE_MSG
         = "To assign a project open a file or save a new file that is part"
         + " of the project.\n"
         + "If files are viewed in tabs this file also must be selected.";
}
