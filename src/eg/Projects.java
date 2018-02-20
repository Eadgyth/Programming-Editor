package eg;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.ui.MainWin;
import eg.projects.ProjectActions;
import eg.projects.ProjectSelector;
import eg.document.EditableDocument;
import eg.utils.Dialogs;
import eg.utils.FileUtils;

/**
 * The projects that files in a given directory are part of.
 * <p>
 * A project is represented by an object of {@link ProjectActions}.
 */
public class Projects {

   private final MainWin mw;
   private final ProjectSelector selector;
   private final ProcessStarter proc;
   private final List<ProjectActions> projList = new ArrayList<>();
   private final EditableDocument[] edtDoc;

   private ProjectActions current;
   private int iDoc;
   private String docExt = "";

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
      docExt = FileUtils.fileExtension(edtDoc[iDoc].filename());
      ProjectActions inList = selectFromList(edtDoc[iDoc].dir(), true);
      mw.enableChangeProject(inList != null);
      if (current != null) {
         if (!current.isInProject(edtDoc[iDoc].dir())) {
            mw.enableSrcCodeActions(false, false, false);
         }
         else {
            enableActions(current);
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
      retrieveProject(edtDoc[iDoc].dir());
   }

   /**
    * Opens the window of the <code>SettingsWindow</code> object that belongs
    * to a project.
    * <p>
    * Depending on the currently set <code>EditableDocument</code> the
    * window belongs to the currently active project, to one of this
    * listed projects or to a project that can be newly assigned.
    */
   public void openSettingsWindow() {
      if (!edtDoc[iDoc].hasFile()) {
         Dialogs.infoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         return;
      }
      ProjectActions fromList = selectFromList(edtDoc[iDoc].dir(), false);
      if (fromList == null) {
         int res = Dialogs.confirmYesNo("Assign new project?");
         if (0 == res) {
            assignProjectImpl();
         }
      }
      else {
         if (fromList == current || changeProject(fromList)) {
            current.makeSettingsWindowVisible();
         }
      }
   }

   /**
    * Assigns a new project
    */
   public void assignProject() {
      ProjectActions fromList = selectFromList(edtDoc[iDoc].dir(), false);
      if (fromList == null) {
         assignProjectImpl();
      }
      else {
         Dialogs.warnMessage(
               edtDoc[iDoc].filename() + " belongs to the project "
               + fromList.getProjectName());
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
                  edtDoc[iDoc].filename()
                  + ":\nThe file could not be found anymore",
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
                  FILES_NOT_FOUND_MESSAGE + missingFiles,
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

   private void retrieveProject(String dir) {
      if (current != null && current.isInProject(dir)) {
         return;
      }
      EventQueue.invokeLater(() -> {
         ProjectActions projToFind = selector.createProject(docExt);
         boolean isFound = false;
         /*if (projToFind != null) {
            isFound = projToFind.retrieveProject(dir);
         }
         else {*/
            for (String exts : ProjectSelector.PROJ_EXTENSIONS) {
               projToFind = selector.createProject(exts);
               isFound = projToFind.retrieveProject(dir);
               if (isFound) {
                  break;
               }
               else {
                 projToFind = null;
              } 
            }
        // }
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

   private void assignProjectImpl() {
      if (!edtDoc[iDoc].hasFile()) {
         Dialogs.infoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         return;
      }
      ProjectActions projNew = selector.createProject(docExt);
      if (projNew == null) {
         projNew = selectByExtension();
      }
      if (projNew != null) {
         ProjectActions projFin = projNew;
         projFin.makeSettingsWindowVisible();
         projFin.setConfiguringAction(e -> configureProject(projFin));
      }
   }

   private ProjectActions selectByExtension() {
      String selectedExt
            = Dialogs.comboBoxOpt(wrongExtensionMessage(edtDoc[iDoc].filename()),
            "File extension", ProjectSelector.PROJ_EXTENSIONS, null, true);

      if (selectedExt != null) {
         return selector.createProject(selectedExt);
      }
      else {
         return null;
      }
   }

   private boolean changeProject(ProjectActions toChangeTo) {
      int result = Dialogs.confirmYesNo(
            "Switch to project " + toChangeTo.getProjectName() + "?");

      if (result == 0) {
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
         current = projToConf;
         current.storeConfiguration();
         projList.add(current);
         updateProjectSetting(current);
         updateFileTree();
      }
   }

   private void updateProjectSetting(ProjectActions projToSet) {
      proc.setWorkingDir(projToSet.getProjectPath());
      enableActions(projToSet);
      setBuildLabel(projToSet);
      mw.displayProjectName(projToSet.getProjectName());
      mw.fileTree().setDeletableDirName(projToSet.getExecutableDirName());
      mw.fileTree().setProjectTree(projToSet.getProjectPath());
   }

   private void enableActions(ProjectActions projToSet) {
      switch (className(projToSet)) {
         case "JavaProject":
            mw.enableSrcCodeActions(true, true, true);
            break;
         case "HtmlProject":
            mw.enableSrcCodeActions(false, true, false);
            break;
         case "PerlProject":
            mw.enableSrcCodeActions(false, true, false);
            break;
         case "RProject":
            mw.enableSrcCodeActions(false, true, false);
      }
   }

   private void setBuildLabel(ProjectActions projToSet) {
      switch (className(projToSet)) {
         case "JavaProject":
            mw.setBuildLabel("Create jar");
            break;
         default:
            mw.setBuildLabel("Build");
      }
   }

   private String className(ProjectActions projToSet) {
      return projToSet.getClass().getSimpleName();
   }

   //
   //--Strings for messages
   //

   private final String NO_FILE_IN_TAB_MESSAGE
         = "To assign a project open or newly save a file"
         + " that is part of the project.";

   private final String FILES_NOT_FOUND_MESSAGE
         = "The following files could not be found anymore:";
         
   private String wrongExtensionMessage(String filename) {
      return
         "<html>"
         + filename + " does not define a project category.<br>"
         + "If the file belongs to a project select the"
         + " extension of source files:"
         + "</html>";
    }
}
