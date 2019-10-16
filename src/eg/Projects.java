package eg;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.ui.MainWin;
import eg.ui.ProjectActionsUpdate;
import eg.ui.filetree.FileTree;
import eg.projects.ProjectCommands;
import eg.projects.ProjectSelector;
import eg.projects.ProjectTypes;
import eg.document.EditableDocument;
import eg.utils.Dialogs;

/**
 * The assigned projects
 * <p>
 * Projets are represented by objects of {@link ProjectCommands}.
 */
public class Projects {

   private final MainWin mw;
   private final FileTree fileTree;
   private final ProjectSelector selector;
   private final ProcessStarter proc;
   private final EditableDocument[] edtDoc;
   private final Runnable fileTreeUpdate;
   private final List<ProjectCommands> projList = new ArrayList<>();

   private ProjectCommands current;
   private int iDoc;
   private boolean isReplace = false;

   private boolean isRun;
   private boolean isSaveAndRun;
   private boolean isCompile;
   private boolean isBuild;
   private String buildLabel = "Build";

   /**
    * @param mw  the reference to MainWin
    * @param fileTree  the reference to FileTree
    * @param edtDoc  the array of EditableDocument
    */
   public Projects(MainWin mw, FileTree fileTree, EditableDocument[] edtDoc) {
      this.mw = mw;
      this.fileTree = fileTree;
      this.edtDoc = edtDoc;
      fileTreeUpdate = () -> fileTree.updateTree();
      Console cons = new Console(mw.consolePnl());
      proc = new ProcessStarter(cons, fileTreeUpdate);
      TaskRunner runner = new TaskRunner(mw, cons, proc, fileTreeUpdate);
      selector = new ProjectSelector(runner);
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
    * Updates buttons and menu items. Method is called after another
    * <code>EditableDocument</code> has been selected or its file
    * has changed
    */
   public void changedDocumentUpdate() {
      if (current == null) {
         mw.enableAssignProject(edtDoc[iDoc].hasFile());
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
      mw.enableAssignProject(edtDoc[iDoc].hasFile() && (!isInProject || isCurrent));
      mw.enableOpenProjectSettings(isCurrent);
      mw.enableChangeProject(isInProject && !isCurrent);
      enableProjectCommands(isCurrent);
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
         if (projType != inList.projectType()) {
            int res = replaceProjectRes(
                  inList.projectName(),
                  inList.projectType().display(),
                  projType.display());

            isReplace = 0 == res;
            if (isReplace) {
               assignImpl(projType);
            }
         }
         else {
            projectAssignedMsg(inList.projectName(),
                  inList.projectType().display());
         }
      }
   }

   /**
    * Tries to retrieve a saved project
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
         projToFind = selector.createProject(t);
         projToFind.buildSettingsWindow();
         isFound = projToFind.retrieve(dir);
         if (isFound) {
            break;
         }
      }
      if (isFound) {
         projList.add(projToFind);
         if (current == null) {
            current = projToFind;
            current.storeConfiguration();
            updateProjectSetting();
         }
         else {
            change(projToFind);
         }
         ProjectCommands projFin = projToFind;
         projFin.setConfiguringAction(e -> configure(projFin));
      }
   }

   /**
    * Opens the window of the <code>SettingsWindow</code> of the project
    * that the selected <code>EditableDocument</code> belongs to
    */
   public void openSettingsWindow() {
      ProjectCommands inList = selectFromList(edtDoc[iDoc].fileParent(), false);
      if (inList != null && inList == current) {
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
    * Saves open files of the active project and compiles the project
    */
   public void compile() {
      if (saveAllProjFiles()) {
         current.compile();
      }
   }

   /**
    * Runs the active project
    */
   public void run() {
      if (isSaveAndRun && !saveAllProjFiles()) {
         return;
      }
      if (current.usesMainFile()) {
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
      ProjectCommands toAssign = selector.createProject(projType);
      if (toAssign != null) {
         ProjectCommands projFin = toAssign;
         projFin.buildSettingsWindow();
         projFin.openSettingsWindow();
         projFin.setConfiguringAction(e -> configure(projFin));
      }
   }

   private void change(ProjectCommands toChangeTo) {
      int res = switchProjectRes(toChangeTo.projectName());
      if (res == 0) {
         current = toChangeTo;
         current.storeConfiguration();
         updateProjectSetting();
      }
      else {
         mw.enableChangeProject(true);
         mw.enableAssignProject(false);
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
      enableProjectCommands(true);
      proc.setWorkingDir(current.projectPath());
      mw.displayProjectName(current.projectName(),
            current.projectType().display());

      mw.enableChangeProject(false);
      mw.enableOpenProjectSettings(true);
      mw.enableAssignProject(true);
      fileTree.setProjectTree(current.projectPath());
      fileTree.setDeletableDir(current.executableDirName());
   }

   private void enableProjectCommands(boolean isCurrent) {
      isRun = false;
      isCompile = false;
      isBuild = false;
      buildLabel = "Build";
      if (isCurrent) {
         ProjectActionsUpdate update = new ProjectActionsUpdate() {

            @Override
            public void enableCompile() {
               isCompile = true;
            }

            @Override
            public void enableRun(boolean save) {
               isRun = true;
               isSaveAndRun = save;
            }

            @Override
            public void enableBuild(String buildLb) {
               isBuild = true;
               if (buildLb != null) {
                  buildLabel = buildLb;
               }
            }
         };
         current.enable(update);
      }
      String runLb = isSaveAndRun ? SAVE_AND_RUN_LB : RUN_LB;
      mw.enableCompileProject(isCompile);
      mw.enableRunProject(isRun, runLb);
      mw.enableBuildProject(isBuild, buildLabel);
   }

   private boolean saveAllProjFiles() {
      StringBuilder missingFiles = new StringBuilder();
      for (EditableDocument d : edtDoc) {
         boolean isProjFile = d != null && d.hasFile()
               && current.isInProject(d.fileParent());

         if (isProjFile) {
             if (d.file().exists()) {
                 d.saveFile();
             } else {
                 missingFiles.append("\n").append(d.filename());
             }
         }
      }
      if (missingFiles.length() > 0) {
         filesNotFoundMsg(missingFiles.toString());
         return false;
      }
      else {
         return true;
      }
   }

   private int replaceProjectRes(String projName, String previousProj,
         String newProj) {

      return Dialogs.warnConfirmYesNo(
            edtDoc[iDoc].filename()
            + " belongs to the "
            + previousProj
            + " project \'"
            + projName
            + "\'.\n\n"
            + "Replace the project with a new project in the category \'"
            + newProj
            + "\'?");
   }

   private void projectAssignedMsg(String projName, String currProj) {
      Dialogs.errorMessage(
            "A new project cannot be assigned.\n\n"
            + edtDoc[iDoc].filename()
            + " already belongs to the project \'"
            + projName
            + "\' in the category \'"
            + currProj
            + "\'.",
            null);
   }

   private int switchProjectRes(String projName) {
      return Dialogs.confirmYesNo(
            "Switch to project "
            + projName
            + "?");
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

   private final static String SAVE_AND_RUN_LB
         = "Save and run project";

   private final static String RUN_LB
         = "Run project";

   private final static String DEF_BUILD_LB
         = "Build";
}
