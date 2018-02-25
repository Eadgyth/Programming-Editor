package eg;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.ui.MainWin;
import eg.projects.ProjectActions;
import eg.projects.ProjectSelector;
import eg.projects.ProjectTypes;
import eg.document.EditableDocument;
import eg.utils.Dialogs;
import eg.utils.FileUtils;

/**
 * The assigned projects.
 * <p>
 * A project is represented by an object of {@link ProjectActions}.
 */
public class Projects {

   private final MainWin mw;
   private final ProjectSelector selector;
   private final ProcessStarter proc;
   private final EditableDocument[] edtDoc;
   private final List<ProjectActions> projList = new ArrayList<>();

   private ProjectActions current;
   private int iDoc;
   private boolean isReplaceProj = false;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param edtDoc  the array of {@link EditableDocument}
    */
   public Projects(MainWin mw, EditableDocument[] edtDoc) {
      this.mw = mw;
      this.edtDoc = edtDoc;
      proc = new ProcessStarter(mw.console());
      selector = new ProjectSelector(mw.consoleOpener(), proc, mw.console());
   }

   /**
    * Selects an element from this array of <code>EditableDocument</code> by
    * the specified index
    *
    * @param i  the index
    */
   public void setDocumentAt(int i) {
      iDoc = i;
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), false);
      mw.enableOpenProjSettingActions(inList != null);
      if (inList != null) {
         if (current != null) {
            mw.enableChangeProject(inList != current);
            if (!current.isInProject(edtDoc[iDoc].dir())) {
               mw.enableSrcCodeActions(false, false, false);
            }
            else {
               enableActions();
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
            projToFind = selector.createProject(t);
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
               updateProjectSetting(current);
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
    * Assigns a new project
    *
    * @param projType  the project type which has a valaue in {@link ProjectTypes}
    */
   public void assignProject(ProjectTypes projType) {
      ProjectActions fromList = selectFromList(edtDoc[iDoc].dir(), false);
      if (fromList == null) {
         assignProjectImpl(projType);
      }
      else {
         if (fromList == current) {
            isReplaceProj = projType != fromList.getProjectType();
            if (isReplaceProj) {
               int res = Dialogs.confirmYesNo(
                     replaceProjectMessage(
                           edtDoc[iDoc].filename(),
                           fromList.getProjectName(),
                           projType.display()));

               if (0 == res) {
                  assignProjectImpl(projType);
               }
            }
            else {
               Dialogs.infoMessage(
                     projectAssignedMessage(
                           edtDoc[iDoc].filename(),
                           fromList.getProjectName(),
                           fromList.getProjectType().display()),
                     "Note");
            }
         }
         else {
            if (changeProject(fromList)) {
               assignProjectImpl(projType);
            }
         }
      }
   }

   /**
    * Opens the window of the <code>SettingsWindow</code> object that belongs
    * to a project.
    * <p>
    * Depending on the currently set <code>EditableDocument</code> the
    * window belongs to the currently active project or to one of this
    * listed projects.
    */
   public void openSettingsWindow() {
      ProjectActions fromList = selectFromList(edtDoc[iDoc].dir(), false);
      if (fromList != null) {
         if (fromList == current || changeProject(fromList)) {
            current.makeSettingsWindowVisible();
         }
      }
   }

   /**
    * Sets active the project which the currently selected
    * <code>EditableDocument</code> belongs to
    */
   public void changeProject() {
      ProjectActions fromList = selectFromList(edtDoc[iDoc].dir(), true);
      changeProject(fromList);
   }

   /**
    * Updates the file tree if the selected <code>EditableDocument</code>
    * belongs to currently active project
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
                  fileNotFoundMessage(edtDoc[iDoc].filename()),
                  "Missing files");
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
   public void runProj() {
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
         Dialogs.infoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         return;
      }
      ProjectActions toAssign = selector.createProject(projType);
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
         updateProjectSetting(current);
         mw.enableChangeProject(false);
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
         }
      }
      return inList;
   }

   private void configureProject(ProjectActions projToConf) {
      if (projToConf.configureProject(edtDoc[iDoc].dir())) {
         if (isReplaceProj) {
            projList.remove(current);
            isReplaceProj = false;
         }
         current = projToConf;
         current.storeConfiguration();
         projList.add(current);
         updateProjectSetting(current);
         updateFileTree();
      }
   }

   private void updateProjectSetting(ProjectActions projToSet) {
      proc.setWorkingDir(projToSet.getProjectPath());
      enableActions();
      setBuildLabel();
      mw.displayProjectName(projToSet.getProjectName(),
            projToSet.getProjectType().display());

      mw.fileTree().setDeletableDirName(projToSet.getExecutableDirName());
      mw.fileTree().setProjectTree(projToSet.getProjectPath());
      mw.enableChangeProject(false);
      mw.enableOpenProjSettingActions(true);
   }

   private void enableActions() {
      switch (current.getProjectType()) {
         case GENERIC:
            mw.enableSrcCodeActions(false, false, false);
            break;
         case JAVA:
            mw.enableSrcCodeActions(true, true, true);
            break;
         case HTML:
            mw.enableSrcCodeActions(false, true, false);
            break;
         case PERL:
            mw.enableSrcCodeActions(false, true, false);
            break;
         case R:
            mw.enableSrcCodeActions(false, true, false);
      }
   }

   private void setBuildLabel() {
      switch (current.getProjectType()) {
         case JAVA:
            mw.setBuildLabel("Create jar");
            break;
         default:
            mw.setBuildLabel("Build");
      }
   }

   //
   //--Strings for messages
   //

   private final String NO_FILE_IN_TAB_MESSAGE
         = "Open or newly save a file that is part of the project to be assigned.";

   private String replaceProjectMessage(String filename, String projName,
         String newProjDispl) {

      return  filename + " belongs to the project " + projName +".\n"
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
