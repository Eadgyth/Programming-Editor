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
   private final ProjectSelector projSelect;
   private final ProcessStarter proc;
   private final EditableDocument[] edtDoc;
   private final ProjectTypeChange projTypeChg;
   private final List<ProjectActions> projList = new ArrayList<>();

   private ProjectActions current;
   private int iDoc;
   private boolean isReplaceProj = false;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param edtDoc  the array of {@link EditableDocument}
    * @param lastProject  the recent project directory that is shown in the
    * file tree (the project is only set active when a file is opened though).
    * Null or the empty string to not show a directory
    */
   public Projects(MainWin mw, EditableDocument[] edtDoc, String lastProject) {
      this.mw = mw;
      this.edtDoc = edtDoc;
      projTypeChg = new ProjectTypeChange(mw.projControlsUpdate());
      proc = new ProcessStarter(mw.console());
      projSelect = new ProjectSelector(mw.consoleOpener(), proc, mw.console());
      if (lastProject != null && lastProject.length() > 0) {
         mw.fileTree().setProjectTree(lastProject);
      }
   }

   /**
    * Selects an element from this array of <code>EditableDocument</code> by
    * the specified index
    *
    * @param i  the index
    */
   public void setDocumentAt(int i) {
      iDoc = i;
      if (!edtDoc[iDoc].hasFile()) {
         return;
      }
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), false);
      boolean isListed = inList != null;
      if (isListed) {
         mw.enableChangeProject(inList != current);
         if (!current.isInProject(edtDoc[iDoc].dir())) {
            projTypeChg.disableProjectActions();
         }
         else {
            projTypeChg.enableProjectActions(current.getProjectType());
         }
      }
   }

   /**
    * Assigns a new project
    *
    * @param projType  the project type which has a valaue in {@link ProjectTypes}
    */
   public void assignProject(ProjectTypes projType) {
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), false);
      if (inList == null) {
         assignProjectImpl(projType);
      }
      else {
         if (inList == current) {
            isReplaceProj = projType != inList.getProjectType();
            if (isReplaceProj) {
               int res = Dialogs.warnConfirmYesNo(
                     replaceProjectMessage(
                           edtDoc[iDoc].filename(),
                           inList.getProjectName(),
                           inList.getProjectType().display(),
                           projType.display()));

               if (0 == res) {
                  assignProjectImpl(projType);
               }
            }
            else {
               Dialogs.infoMessage(
                     projectAssignedMessage(
                           edtDoc[iDoc].filename(),
                           inList.getProjectName(),
                           inList.getProjectType().display()),
                           null);
            }
         }
         else {
            if (changeProject(inList)) {
               assignProject(projType);
            }
         }
      }
   }

   /**
    * Tries to retrieve a project whose configuration is saved in an
    * 'eadproject' file in the project folder or, if such file is not
    * existent, in the program's prefs file.
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
               break;
            }
            else {
              projToFind = null;
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
               if (selectFromList(dir, true) == null) {
                  projFin.setConfiguringAction(e -> configureProject(projFin));
                  projList.add(projFin);
                  changeProject(projFin);
               }
            }
         }
      });
   }

   /**
    * Opens the window of the <code>SettingsWindow</code> object that belongs
    * to a project.
    * <p>
    * Depending on the currently selected <code>EditableDocument</code> the
    * window belongs to the currently active project or to one of this
    * listed projects.
    */
   public void openSettingsWindow() {
      boolean open = true;
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), false);
      if (inList != null) {
         open = inList == current || changeProject(inList);
      }
      if (open) {
         current.makeSettingsWindowVisible();
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
    * Updates the file tree if the selected <code>EditableDocument</code>
    * belongs to the currently active project
    */
   public void updateFileTree() {
      if (current != null && current.isInProject(edtDoc[iDoc].dir())) {
         EventQueue.invokeLater(() -> mw.fileTree().updateTree());
      }
   }

   /**
    * Saves the selected file of the currently active project and
    * compiles the project
    */
   public void saveAndCompile() {
      try {
         mw.setBusyCursor();
         if (edtDoc[iDoc].docFile().exists()) {
            edtDoc[iDoc].saveFile();
            current.compile();
            updateFileTree();
         }
         else {
            Dialogs.errorMessage(
                  fileNotFoundMessage(edtDoc[iDoc].filename()), "Missing files");
         }
      }
      finally {
         EventQueue.invokeLater(() ->  mw.setDefaultCursor());
      }
   }

   /**
    * Saves all open files of the currently active project and compiles
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
                    missingFiles.append("\n");
                    missingFiles.append(d.filename());
                }
            }
         }
         if (missingFiles.length() == 0) {
            current.compile();
            updateFileTree();
         }
         else {
            Dialogs.errorMessage(
                  filesNotFoundMessage(missingFiles.toString()),
                  "Missing files");
         }
      }
      finally {
         EventQueue.invokeLater(() -> mw.setDefaultCursor());
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
         updateFileTree();
      }
      finally {
         EventQueue.invokeLater(() ->  mw.setDefaultCursor());
      }
   }

   //
   //--private--/
   //

   private void assignProjectImpl(ProjectTypes projType) {
      if (!edtDoc[iDoc].hasFile()) {
         Dialogs.infoMessage(NO_FILE_IN_TAB_MESSAGE, null);
         return;
      }
      ProjectActions toAssign = projSelect.createProject(projType);
      if (toAssign != null) {
         ProjectActions projFin = toAssign;
         projFin.makeSettingsWindowVisible();
         projFin.setConfiguringAction(e -> configureProject(projFin));
      }
   }

   private boolean changeProject(ProjectActions toChangeTo) {
      int res = Dialogs.confirmYesNo(
            switchProjectMessage(toChangeTo.getProjectName()));

      if (res == 0) {
         current = toChangeTo;
         current.storeConfiguration();
         updateProjectSetting();
         return true;
      }
      else {
         mw.enableChangeProject(true);
         return false;
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
         if (isReplaceProj) {
            projList.remove(current);
            isReplaceProj = false;
         }
         current = toConfig;
         current.storeConfiguration();
         projList.add(current);
         updateProjectSetting();
      }
   }

   private void updateProjectSetting() {
      proc.setWorkingDir(current.getProjectPath());
      projTypeChg.enableProjectActions(current.getProjectType());
      projTypeChg.setBuildLabel(current.getProjectType());
      mw.displayProjectName(current.getProjectName(),
            current.getProjectType().display());

      mw.fileTree().setProjectTree(current.getProjectPath());
      mw.fileTree().setDeletableDir(current.getExecutableDirName());
      mw.enableChangeProject(false);
      mw.enableOpenProjSetWinActions(true);
   }

   //
   //--Strings for messages
   //

   private final String NO_FILE_IN_TAB_MESSAGE
         = "To assign a project open or save a new file that is part"
         + " of the project.\n"
         + "If files are viewed in tabs the file also must be selected.";

   private String replaceProjectMessage(String filename, String projName,
         String previousProjDispl, String newProjDispl) {

      return  filename + " belongs to the " + previousProjDispl + " project \""
            + projName +"\".\n"
            + "Remove " + projName + " and assign a new project"
            + " of the category \"" + newProjDispl + "\"?";
   }

   private String projectAssignedMessage(String filename, String projName,
         String currProjDispl) {

      return edtDoc[iDoc].filename() + " already belongs to the project "
           + projName + " in the category \"" + currProjDispl + "\".";
   }

   private String fileNotFoundMessage(String filename) {
      return filename + ":\nThe file could not be found anymore";
   }

   private String filesNotFoundMessage(String filenames) {
      return "The following files could not be found anymore:" + filenames;
   }

   private String switchProjectMessage(String projName) {
      return  "Switch to project " + projName + "?";
   }
}
