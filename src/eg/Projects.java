package eg;

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
 * The assigned projects
 * <p>
 * Projets are represented by objects of {@link ProjectCommands}. The
 * assignment of projects, the changing between assigned projects and
 * the update of project UI controls depend on the document that is
 * selected and on the file that is set in a document. Documents are
 * represented by objects of {@link EditableDocument}.
 */
public class Projects {

   private final MainWin mw;
   private final FileTree fileTree;
   private final ProjectSelector selector;
   private final ProcessStarter proc;
   private final EditableDocument[] edtDoc;
   private final List<ProjectCommands> projects = new ArrayList<>();
   private final ProjectActionsUpdate pau = new ProjectActionsUpdate();

   private ProjectCommands currentProject;
   private int iDoc;
   private boolean replace = false;
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
      Console cons = new Console(mw.consolePnl());
      Runnable fileTreeUpdate = () -> fileTree.updateTree();
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
    * document and the file that is set in a document
    */
   public void changedDocumentUpdate() {
      if (currentProject == null) {
         mw.enableAssignProject(edtDoc[iDoc].hasFile(), null);
      }
      else {
         ProjectCommands inList;
         boolean isProject = false;
         boolean isCurrentProject = false;
         boolean isAssignProject = false;
         ProjectTypes projType = null;
         if (edtDoc[iDoc].hasFile()) {
            inList = selectFromList(edtDoc[iDoc].fileParent(), false);
            isProject = inList != null;
            isCurrentProject = inList == currentProject;
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
    * Updates the file tree if <code>file</code> is contained in the
    * currently shown directory
    *
    * @param file  the file
    */
   public void updateFileTree(String file) {
      if (file.startsWith(fileTree.currentRoot())) {
         fileTree.updateTree();
      }
   }

   /**
    * Assigns a new project that the file of the selected document
    * belongs to or opens the settings window if the file already
    * belongs to the current project of the specified type
    *
    * @param projType  the project type
    */
   public void assign(ProjectTypes projType) {
      String dir = edtDoc[iDoc].fileParent();
      ProjectCommands inList = selectFromList(dir, false);
      boolean assign = inList == null;
      if (!assign) {
         if (currentProject.projectType() == projType) {
            currentProject.openSettingsWindow(dir);
         }
         else {
            int res = replaceRes(projType, inList.projectType());
            if (0 == res) {
               replace = true;
               assign = true;
            }
         }
      }
      if (assign) {
         ProjectCommands toAssign = selector.createProject(projType);
         toAssign.buildSettingsWindow();
         toAssign.openSettingsWindow(dir);
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
      ProjectCommands inList = selectFromList(dir, false);
      if (currentProject != null && inList != null) {
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
         projects.add(projToFind);
         if (currentProject == null) {
            currentProject = projToFind;
            currentProject.storeConfiguration();
            updateProjectSetting();
         }
         else {
            change(projToFind);
         }
         ProjectCommands projFin = projToFind;
         projFin.setConfiguringAction(() -> configure(projFin));
      }
   }

   /**
    * Opens the project settings of the current project
    */
   public void openSettingsWindow() {
      String dir = edtDoc[iDoc].fileParent();
      ProjectCommands inList = selectFromList(dir, false);
      if (inList != null && inList == currentProject) {
         currentProject.openSettingsWindow(dir);
      }
   }

   /**
    * Changes to the project that the file of the selected document
    * belongs to
    */
   public void change() {
      ProjectCommands inList = selectFromList(edtDoc[iDoc].fileParent(), true);
      change(inList);
   }

   /**
    * The enabling of actions to compile, run and build a project
    */
   public final class ProjectActionsUpdate {

      private final static String SAVE_AND_RUN_LB = "Save and run";
      private final static String RUN_LB = "Run";
      private final static String DEF_BUILD_LB = "Build";

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
         isSaveAndRun = save; // in outer class
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
    * Saves the open project files if required by the project and runs
    * the project.
    * Saving is set in {@link ProjectCommands#enable}
    */
   public void run() {
      if (isSaveAndRun && !save()) {
         return;
      }
      if (currentProject.usesMainFile()
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
      ProjectCommands inList = null;
      for (ProjectCommands p : projects) {
         if (p.isInProject(dir) && (!excludeCurrent || p != currentProject)) {
            inList = p;
            break;
         }
      }
      return inList;
   }

   private void configure(ProjectCommands toConfig) {
      if (toConfig.configure()) {
         if (replace) {
            projects.remove(currentProject);
         }
         if (toConfig != currentProject) {
            projects.add(toConfig);
         }
         //
         // Another tab may have been selected after opening the settings
         ProjectCommands inList = null;
         if (edtDoc[iDoc].hasFile()) {
            inList = selectFromList(edtDoc[iDoc].fileParent(), false);
         }
         boolean apply = !edtDoc[iDoc].hasFile() || inList == null
               || inList == toConfig;

         if (apply) {
            toConfig.storeConfiguration();
            currentProject = toConfig;
            updateProjectSetting();
            fileTree.updateTree();
         }
      }
      else {
         changedDocumentUpdate();
      }
      replace = false;
   }

   private void updateProjectSetting() {
      enableProjectCommands(true);
      proc.setWorkingDir(currentProject.projectPath());
      mw.displayProjectName(currentProject.projectName());
      changedDocumentUpdate();
      fileTree.setProjectTree(currentProject.projectPath());
      fileTree.setDeletableDir(currentProject.executableDirName());
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
         if (d != null && d.hasFile()) {
            boolean isProjFile = currentProject.isInProject(d.fileParent());
            if (isProjFile) {
                if (d.file().exists()) {
                   d.saveFile();
                } else {
                   missingFiles.append("\n").append(d.filename());
                }
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
