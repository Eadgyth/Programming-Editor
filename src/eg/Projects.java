package eg;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.ui.MainWin;
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
 * A project is represented by an object of {@link ProjectActions}.
 */
public class Projects {

   private final MainWin mw;
   private final FileTree fileTree;
   private final ProjectSelector projSelect;
   private final ProcessStarter proc;
   private final EditableDocument[] edtDoc;
   private final ProjectTypeChange projTypeChg;
   private final List<ProjectActions> projList = new ArrayList<>();

   private ProjectActions current;
   String projRoot = "";
   private int iDoc;
   private boolean isReplace = false;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param fileTree  the refence to {@link FileTree}
    * @param dir  the directory that is initially shown by fileTree.
    * Null or the empty string to not show a directory
    * @param edtDoc  the array of {@link EditableDocument}
    */
   public Projects(MainWin mw, FileTree fileTree, String dir,
         EditableDocument[] edtDoc) {

      this.mw = mw;
      this.fileTree = fileTree;
      this.edtDoc = edtDoc;
      projRoot = dir;
      projTypeChg = new ProjectTypeChange(mw.projControlsUpdate());
      Console cons = new Console(mw.consolePnl());
      proc = cons.getProcessStarter();
      projSelect = new ProjectSelector(mw.consoleOpener(), cons);
      if (dir != null && dir.length() > 0) {
         fileTree.setProjectTree(dir);
      }
   }

   /**
    * Selects the element in this array of <code>EditableDocument</code>
    * that corresponds to the document that is viewed in the editor
    *
    * @param i  the index
    */
   public void setIndex(int i) {
      iDoc = i;
      ProjectActions inList = null;
      boolean isProject = false;
      if (edtDoc[iDoc].hasFile()) {
         inList = selectFromList(edtDoc[iDoc].dir(), false);
         isProject = inList != null;
      }
      mw.enableOpenProjSetWinActions(isProject);
      mw.enableChangeProject(isProject && inList != current);
      if (isProject) {
         if (!current.isInProject(edtDoc[iDoc].dir())) {
            projTypeChg.disableProjectActions();
         }
         else {
            projTypeChg.enableProjectActions(current.getProjectType());
         }
      }
      else {
         projTypeChg.disableProjectActions();
      }
   }

   /**
    * Assigns a new project
    *
    * @param projType  the project type which has a valaue in
    * {@link ProjectTypes}
    */
   public void assignProject(ProjectTypes projType) {
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), false);
      if (inList == null) {
         assignProjectImpl(projType);
      }
      else {
         if (inList == current || changeProject(inList)) {
            replaceCurrent(projType, inList);
         }
      }
   }

   /**
    * Retrieves a project from a configuration that was saved in a
    * ProjConfig or the Prefs file
    *
    * @see eg.projects.AbstractProject#retrieveProject(String)
    */
   public void retrieveProject() {
      String dir = edtDoc[iDoc].dir();
      if (current != null && current.isInProject(dir)) {
         return;
      }
      EventQueue.invokeLater(() -> {
         ProjectActions projToFind = null;
         boolean isFound = false;
         for (ProjectTypes t : ProjectTypes.values()) {
            projToFind = projSelect.createProject(t);
            isFound = projToFind.retrieveProject(dir);
            if (isFound) {
               projToFind.buildSettingsWindow();
               break;
            }
         }
         if (isFound) {
            ProjectActions projFin = projToFind;
            if (current == null) {
               current = projFin;
               current.setConfiguringAction(e -> configureProject(current));
               projList.add(current);
               updateProjectSetting();
            }
            else {
               ProjectActions fromList = selectFromList(dir, true);
               if (fromList == null) {
                  projFin.setConfiguringAction(e -> configureProject(projFin));
                  projList.add(projFin);
                  changeProject(projFin);
               }
            }
         }
      });
   }

   /**
    * Updates the file tree if <code>file</code> is contained
    * in the directory of the currently shown file tree
    *
    * @param file  the file
    */
   public void updateFileTree(String file) {
      if (file.startsWith(projRoot)) {
         updateFileTree();
      }
   }

   /**
    * Opens the window of the <code>SettingsWindow</code> object
    * that belongs to the active project or to on that is changed
    * to. Which settings window is shown depeds on the selected
    * <code>EditableDocument</code>.
    */
   public void openSettingsWindow() {
      boolean open = true;
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), false);
      if (inList != null) {
         open = inList == current || changeProject(inList);
      }
      if (open) {
         current.openSettingsWindow();
      }
   }

   /**
    * Changes to the project which the currently selected
    * <code>EditableDocument</code> belongs to
    */
   public void changeProject() {
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), true);
      changeProject(inList);
   }

   /**
    * Saves the selected file of the active project and copmiles
    * compiles the project
    */
   public void saveAndCompile() {
      try {
         mw.setBusyCursor();
         if (edtDoc[iDoc].docFile().exists()) {
            edtDoc[iDoc].saveFile();
            current.compile();
         }
         else {
            fileNotFoundMsg(edtDoc[iDoc].filename());
         }
      }
      finally {
         EventQueue.invokeLater(() ->  mw.setDefaultCursor());
         updateFileTree();
      }
   }

   /**
    * Saves all open files of the active project and compiles
    * the project
    */
   public void saveAllAndCompile() {
      try {
         mw.setBusyCursor();
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
         if (missingFiles.length() == 0) {
            current.compile();
         }
         else {
            filesNotFoundMsg(missingFiles.toString());
         }
      }
      finally {
         EventQueue.invokeLater(() -> mw.setDefaultCursor());
         updateFileTree();
      }
   }

   /**
    * Runs the currently active project
    */
   public void runProject() {
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
   public void buildProj() {
      try {
         mw.setBusyCursor();
         current.build();
      }
      finally {
         EventQueue.invokeLater(() ->  mw.setDefaultCursor());
         updateFileTree();
      }
   }

   //
   //--private--/
   //

   private void assignProjectImpl(ProjectTypes projType) {
      if (!edtDoc[iDoc].hasFile()) {
         Dialogs.infoMessage(NO_FILE_MSG, null);
         return;
      }
      ProjectActions toAssign = projSelect.createProject(projType);
      if (toAssign != null) {
         ProjectActions projFin = toAssign;
         projFin.buildSettingsWindow();
         projFin.openSettingsWindow();
         projFin.setConfiguringAction(e -> configureProject(projFin));
      }
   }

   private boolean changeProject(ProjectActions toChangeTo) {
      int res = switchProjectRes(toChangeTo.getProjectName());
      if (res == 0) {
         current = toChangeTo;
         current.storeConfiguration();
         updateProjectSetting();
         return true;
      }
      else {
         mw.enableChangeProject(true);
         projTypeChg.disableProjectActions();
         return false;
      }
   }

   private void replaceCurrent(ProjectTypes projType, ProjectActions newProj) {
      if (projType != newProj.getProjectType()) {
         int res = replaceProjectRes(
               edtDoc[iDoc].filename(),
               newProj.getProjectName(),
               newProj.getProjectType().display(),
               projType.display());

         isReplace = 0 == res;
         if (isReplace) {
            assignProjectImpl(projType);
         }
      }
      else {
         projectAssignedMsg(
               edtDoc[iDoc].filename(),
               newProj.getProjectName(),
               newProj.getProjectType().display());
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

   private void configureProject(ProjectActions toConfig) {
      if (toConfig.configureProject(edtDoc[iDoc].dir())) {
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
      projRoot = current.getProjectPath();
      proc.setWorkingDir(current.getProjectPath());
      projTypeChg.enableProjectActions(current.getProjectType());
      projTypeChg.setBuildLabel(current.getProjectType());
      mw.displayProjectName(current.getProjectName(),
            current.getProjectType().display());

      fileTree.setProjectTree(current.getProjectPath());
      fileTree.setDeletableDir(current.getExecutableDirName());
      mw.enableChangeProject(false);
      mw.enableOpenProjSetWinActions(true);
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
