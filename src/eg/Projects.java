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
 * The assigned projects.
 * <p>
 * Projets are are represented by objects of {@link ProjectActions}.
 * <p>
 * A <code>List</code> of assigned projects of which one is set active
 * is mantained. Assigning and retrieving projects as well as changing
 * between listed projects takes place based on the file of the
 * {@link EditableDocument} that is selected at a time.
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
      update.enableAssignProject(edtDoc[iDoc].hasFile());
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
    * @param projType  the project type
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
    * Tries to retrieve a project based on the directory of the
    * currently selected <code>EditableDocument</code>
    *
    * @see eg.projects.AbstractProject#retrieve
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
         BusyFunction bf = () -> {
            edtDoc[iDoc].saveFile();
            current.compile();
         };
         mw.runBusyFunction(bf);
         updateFileTree();
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
         BusyFunction bf = () -> current.compile();
         mw.runBusyFunction(bf);
         updateFileTree();
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
      BusyFunction bf = () -> current.build();
      mw.runBusyFunction(bf);
      updateFileTree();
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
         String previousProj, String newProj) {

      return Dialogs.warnConfirmYesNo(
            filename
            + " belongs to the "
            + previousProj
            + " project "
            + projName
            + ".\n\n"
            + "Remove "
            + projName
            + " and assign a new project in the category \'"
            + newProj
            + "\'?");
   }

   private void projectAssignedMsg(String filename, String projName,
         String currProj) {

      Dialogs.infoMessage(
            edtDoc[iDoc].filename()
            + " already belongs to the project "
            + projName
            + " in the category \'"
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
