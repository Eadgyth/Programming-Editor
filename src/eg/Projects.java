package eg;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.console.*;
import eg.ui.MainWin;
import eg.ui.filetree.FileTree;
import eg.projects.ProjectCommands;
import eg.projects.ProjectSelector;
import eg.projects.ProjectTypes;
import eg.document.EditableDocument;
import eg.utils.Dialogs;

/**
 * The processing of coding projects and updating of project UI
 * controls
 */
public class Projects {

   private final MainWin mw;
   private final FileTree fileTree;
   private final ProjectSelector selector;
   private final ProcessStarter proc;
   private final EditableDocument[] edtDoc;
   private final List<ProjectCommands> projCmnds = new ArrayList<>();
   private final ProjectActionsUpdate pau = new ProjectActionsUpdate();

   private ProjectCommands currentProject;
   private int iDoc;
   private boolean isSaveAndRun;

   /**
    * @param mw  the MainWin
    * @param fileTree  the FileTree
    * @param edtDoc  the array of EditableDocument
    */
   public Projects(MainWin mw, FileTree fileTree, EditableDocument[] edtDoc) {
      this.mw = mw;
      this.fileTree = fileTree;
      this.edtDoc = edtDoc;
      Console cons = new Console(mw.consolePanel());
      Runnable fileTreeUpdate = (fileTree::updateTree);
      proc = new ProcessStarter(cons, fileTreeUpdate);
      TaskRunner runner = new TaskRunner(mw, cons, proc, fileTreeUpdate);
      selector = new ProjectSelector(runner);
      enableProjectCommands(false);
   }

   /**
    * Selects the element in this array of documents
    *
    * @param i  the index
    */
   public void setDocumentAt(int i) {
      iDoc = i;
   }

   /**
    * Updates the project UI controls depending on the selected
    * document and/or the file of the document
    */
   public void changedDocumentUpdate() {
      if (currentProject == null) {
         mw.enableAssignProject(edtDoc[iDoc].hasFile(), null);
      }
      else {
         ProjectCommands fromList;
         boolean isProject = false;
         boolean isCurrentProject = false;
         boolean isAssignProject = false;
         ProjectTypes projType = null;
         if (edtDoc[iDoc].hasFile()) {
            fromList = selectFromList(edtDoc[iDoc].fileParent(), false);
            isProject = fromList != null;
            isCurrentProject = isProject && fromList == currentProject;
            isAssignProject = !isProject || isCurrentProject;
            if (isCurrentProject) {
               projType = currentProject.projectType();
            }
         }
         mw.enableAssignProject(isAssignProject, projType);
         mw.enableOpenProjectSettings(isCurrentProject);
         mw.enableChangeProject(isProject && !isCurrentProject);
         enableProjectCommands(isCurrentProject);
      }
   }

   /**
    * Updates the file tree
    */
   public void updateFileTree() {
      fileTree.updateTree();
   }

   /**
    * Updates the file tree if the specified directory is or
    * is contained in the currently shown directory
    *
    * @param dir  the directory
    */
   public void updateFileTree(String dir) {
      File f = new File(dir);
      File treeRoot = new File(fileTree.currentRoot());
      while (f != null) {
         if (f.equals(treeRoot)) {
            fileTree.updateTree();
            break;
         }
         f = f.getParentFile();
      }
   }

   /**
    * Opens the project settings to assign a new project. If the
    * selected file alreade belongs to project of the specified
    * project type the project settings for this project are opened.
    *
    * @param projType  the project type
    */
   public void assign(ProjectTypes projType) {
      String dir = edtDoc[iDoc].fileParent();
      ProjectCommands fromList = selectFromList(dir, false);
      boolean isNewProject = fromList == null;
      if (!isNewProject) {
         if (currentProject.projectType() == projType) {
            openSettingsWindow(currentProject, dir);
         }
         else {
            int res = replaceRes(projType, fromList.projectType());
            if (0 == res) {
               isNewProject = true;
            }
         }
      }
      if (isNewProject) {
         ProjectCommands toAssign = selector.createProject(projType);
         toAssign.buildSettingsWindow();
         openSettingsWindow(toAssign, dir);
         toAssign.setConfiguringAction(() -> configure(toAssign));
      }
      changedDocumentUpdate();
   }

   /**
    * Tries to retrieve a stored project that the file of the selected
    * document belongs to
    *
    * @see eg.projects.AbstractProject#retrieve
    */
   public void retrieve() {
      String dir = edtDoc[iDoc].fileParent();
      ProjectCommands fromList = selectFromList(dir, false);
      if (currentProject != null && fromList != null) {
         return;
      }
      ProjectCommands projToFind = null;
      boolean isFound = false;
      for (ProjectTypes t : ProjectTypes.values()) {
         projToFind = selector.createProject(t);
         projToFind.buildSettingsWindow();
         isFound = projToFind.retrieve(dir);
         if (isFound) {
            break;
         }
      }
      if (isFound) {
    	   projCmnds.add(projToFind);
         ProjectCommands projFin = projToFind;
         projFin.setConfiguringAction(() -> configure(projFin));
         if (currentProject == null) {
            currentProject = projToFind;
            currentProject.storeConfiguration();
            updateProjectSetting();
         }
         else {
            change(projToFind);
         }
      }
   }

   /**
    * Opens the project settings window of the current project
    */
   public void openSettingsWindow() {
      String dir = edtDoc[iDoc].fileParent();
      ProjectCommands fromList = selectFromList(dir, false);
      if (fromList != null && fromList == currentProject) {
         openSettingsWindow(currentProject, dir);
      }
   }

   /**
    * Changes to the project that the selected document belongs to
    */
   public void change() {
      ProjectCommands fromList = selectFromList(edtDoc[iDoc].fileParent(), true);
      if (fromList != null) {
         change(fromList);
      }
   }

   /**
    * The enabling of actions to compile, run and build a project
    */
   public final class ProjectActionsUpdate {

      private static final String SAVE_AND_RUN_LB = "Save and run";
      private static final String RUN_LB = "Run";
      private static final String DEF_BUILD_LB = "Build";

      private boolean isRun;
      private boolean isCompile;
      private boolean isBuild;
      private String buildLabel = DEF_BUILD_LB;

      /**
       * Enables actions to compile a project
       */
      public void enableCompile() {
         isCompile = true;
      }

      /**
       * Enables actions to run a project
       *
       * @param save  true to save project files before running, false
       * otherwise
       */
      public void enableRun(boolean save) {
         isRun = true;
         isSaveAndRun = save; // isSaveAndRun in outer class
      }

      /**
       * Enables actions to build a project
       *
       * @param label  the label for the action that indicates the kind
       * of build; null or the empty string to set default 'Build'
       */
      public void enableBuild(String label) {
         isBuild = true;
         if (label != null && !label.isEmpty()) {
            buildLabel = label;
         }
      }

      private void update() {
         String runLb = isSaveAndRun ? SAVE_AND_RUN_LB : RUN_LB;
         mw.enableRunProject(isRun, runLb);
         mw.enableCompileProject(isCompile);
         mw.enableBuildProject(isBuild, buildLabel);
         isRun = false;
         isCompile = false;
         isBuild = false;
         buildLabel = DEF_BUILD_LB;
      }

      private ProjectActionsUpdate() {}
   }

   /**
    * Saves open files of the current project and compiles the project
    */
   public void compile() {
      if (save()) {
         currentProject.compile();
      }
   }

   /**
    * Saves open files of the current project if saving is enabled
    * and runs the project.
    * Saving is enabled in {@link ProjectCommands#enable}
    */
   public void run() {
      if (isSaveAndRun && !save()) {
         return;
      }
      if (currentProject.hasSetSourceFile()
            || currentProject.projectType() == ProjectTypes.GENERIC) {

         currentProject.run();
      }
      else {
         currentProject.run(edtDoc[iDoc].filepath());
      }
   }

   /**
    * Creates a build of the current project
    */
   public void build() {
      currentProject.build();
   }

   //
   //--private--/
   //

   private void openSettingsWindow(ProjectCommands toSet, String dir) {
      mw.busyFunction().execute(() -> toSet.openSettingsWindow(dir));
   }

   private void change(ProjectCommands toChangeTo) {
      int res = changeRes(toChangeTo.projectName());
      if (res == 0) {
         currentProject = toChangeTo;
         currentProject.storeConfiguration();
         updateProjectSetting();
      }
      else {
         changedDocumentUpdate();
      }
   }

   private ProjectCommands selectFromList(String dir, boolean excludeCurrent) {
      ProjectCommands fromList = null;
      for (ProjectCommands p : projCmnds) {
         if (p.isInProject(dir) && (!excludeCurrent || p != currentProject)) {
            fromList = p;
            break;
         }
      }
      return fromList;
   }

   private void configure(ProjectCommands toConfig) {
      if (toConfig.configure()) {
         ProjectCommands compareType = selectFromList(toConfig.projectDir(), false);
         boolean replace = compareType != null
               && toConfig.projectType() != compareType.projectType();

         if (replace) {
            projCmnds.remove(compareType);
         }
         if (!projCmnds.contains(toConfig)) {
        	   projCmnds.add(toConfig);
            toConfig.storeConfiguration();
         }
         //
         // a non-project tab may have been selected after opening
         // the settings        
         ProjectCommands fromList = null;
         if (edtDoc[iDoc].hasFile()) {
            fromList = selectFromList(edtDoc[iDoc].fileParent(), false);
         }
         boolean apply = fromList == null || fromList == toConfig;
         if (apply) {
            currentProject = toConfig;
            updateProjectSetting();
         }
      }
      else {
         changedDocumentUpdate();
      }
   }

   private void updateProjectSetting() {
      enableProjectCommands(true);
      proc.setWorkingDir(currentProject.projectDir());
      mw.displayProjectName(currentProject.projectName());
      changedDocumentUpdate();
      fileTree.setProjectTree(currentProject.projectDir());
      fileTree.setDeletableDir(currentProject.executableDir());
   }

   private void enableProjectCommands(boolean enable) {
      if (enable) {
         currentProject.enable(pau);
      }
      pau.update();
   }

   private boolean save() {
      StringBuilder missingFiles = new StringBuilder();
      for (EditableDocument d : edtDoc) {
         if (d != null && d.hasFile() && currentProject.isInProject(d.fileParent())) {
            if (d.file().exists()) {
               if (d.isChanged()) {
                  d.saveFile();
               }
            }
            else {
               missingFiles.append("\n").append(d.filename());
            }
         }
      }
      if (missingFiles.length() > 0) {
         missingFilesMsg(missingFiles.toString());
         return false;
      }
      return true;
   }

   private int replaceRes(ProjectTypes newProjType, ProjectTypes prevProjType) {
      return Dialogs.warnConfirmYesNo(
            "Change from the category \'"
            + prevProjType.display()
            + "\' to \'"
            + newProjType.display()
            + "\'?");
   }

   private int changeRes(String projName) {
      return Dialogs.confirmYesNo(
            "Change to project directory '"
            + projName
            + "'?");
   }

   private void missingFilesMsg(String filenames) {
      Dialogs.errorMessage(
            "The following file(s) could not be found anymore:"
            + filenames,
            null);
   }
}
