package eg;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.console.*;
import eg.Languages;
import eg.ui.MainWin;
import eg.projects.ProjectActions;
import eg.projects.SelectedProject;

import eg.document.TextDocument;

import eg.utils.JOptions;
import eg.utils.FileUtils;

/**
 * The configuration and execution of actions of projects.
 * <p>
 * A project is represented by an object of type {@link ProjectActions}
 * and is configured and assigned to this current project when the
 * {@link TextDocument} that is selected at the time is part of that
 * project.<br>
 * Several projects can be configured and would be maintained. Any of
 * these can be (re-)assigned to this current project if the selected
 * <code>TextDocument</code> is part of it.
 */
public class CurrentProject {

   private final String NO_FILE_IN_TAB_MESSAGE
         = "A project can be set after a file was opened or"
         + " newly saved.";

   private final String NOT_IN_PROJ_MESSAGE
         = "The selected file does not belong to the active project";

   private final String FILES_NOT_FOUND_MESSAGE
         = "The following file could not be found anymore:";

   private final MainWin mw;
   private final SelectedProject selProj;
   private final ProcessStarter proc;
   private final List<ProjectActions> projList = new ArrayList<>();
   /*
    * Options for a Comobox */
   private final String[] projectOptions;

   private ProjectActions current;
   private TextDocument currDoc;
   private TextDocument[] txtDoc;
   private String docSuffix;

   /**
    * Creates a CurrentProject
    *
    * @param mw  the reference to {@link MainWin}
    * @param txtDoc  the array of {@link TextDocument}
    */
   public CurrentProject(MainWin mw, TextDocument[] txtDoc) {
      this.mw = mw;
      this.txtDoc = txtDoc;
      proc = new ProcessStarter(mw.console());
      selProj = new SelectedProject(mw, proc, mw.console());
      projectOptions = new String[selProj.projectSuffixes.length + 1];
      projectOptions[0] = "File extensions...";
      for (int i = 0; i < selProj.projectSuffixes.length; i++) {
         projectOptions[i + 1] = selProj.projectSuffixes[i];
      }
   }

   /**
    * Selects an element from this array of {@code TextDocument}
    *
    * @param i  the index of the array element
    */
   public void setTextDocumentAt(int i) {
      currDoc = txtDoc[i];
      docSuffix = FileUtils.fileSuffix(currDoc.filename());
      ProjectActions inList = selectFromList(currDoc.dir(), true);
      mw.enableChangeProject(inList != null);
      if (current != null) {
         if (!current.isInProject(currDoc.dir())) {
            mw.enableProjActions(false, false, false);
         }
         else {
            enableActions(current);
         }
      }
   }

   /**
    * Assigns to this current project a project which a configuration
    * exists for in an 'eadconfig' file saved in the project's directory
    * or, if not existent, in the program's prefs file
    * @see eg.projects.ProjectConfig#retrieveProject(String)
    */
   public void retrieveProject() {
      if (current != null && current.isInProject(currDoc.dir())) {
         return;
      }
      EventQueue.invokeLater(() -> {
         ProjectActions projToFind = selProj.createProject(docSuffix);
         boolean isFound = projToFind != null
               && projToFind.retrieveProject(currDoc.dir());
         if (projToFind == null) {
            for (String opt : selProj.projectSuffixes) {
               projToFind = selProj.createProject(opt);
               isFound = projToFind != null
                     && projToFind.retrieveProject(currDoc.dir());
               if (isFound) {
                  break;
               }
            }
         }
         if (isFound) {
            ProjectActions projFin = projToFind;
            if (current == null) {
               current = projFin;
               current.addOkAction(e -> configureProject(current));
               projList.add(current);
               updateProjectSetting(current);
            }
            else {
               if (selectFromList(currDoc.dir(), true) == null) {
                  projFin.addOkAction(e -> configureProject(projFin));
                  projList.add(projFin);
                  changeProject(projFin);
               }
            }
         }
      });
   }

   /**
    * Opens the window of the {@code SettingsWin} object that belongs to
    * a project.
    * <p>Depending on the currently set {@link TextDocument} the opened
    * window belongs to the current project, to one of this listed projects
    * or to a newly created project.
    */
   public void openSettingsWindow() {
      ProjectActions fromList = selectFromList(currDoc.dir(), false);
      if (fromList == null) {
         int res = JOptions.confirmYesNo("Set new project?");
         if (res == 0) {
            createProjectImpl();
         }
      }
      else {
         if (fromList == current) {
            current.makeSetWinVisible(true);
         }
         else {
            if (changeProject(fromList)) {
               current.makeSetWinVisible(true);
            }
         }
      }
   }

   /**
    * Creates a new project.
    * <p>If the the currently set {@link TextDocument} belongs to a project
    * in the List of configured projects a dialog to confirm to proceed is
    * shown.
    */
   public void createProject() {
      ProjectActions fromList = selectFromList(currDoc.dir(), false);
      if (fromList == null) {
         createProjectImpl();
      }
      else {
         confirmedNewProject(fromList);
      }
  }

   /**
    * Sets active the project from this <code>List</code> of configured
    * projects which the currently selected {@code TextDocument} belongs to.
    */
   public void changeProject() {
      ProjectActions fromList = selectFromList(currDoc.dir(), true);
      changeProject(fromList);
   }

   /**
    * Updates the file tree if the selected {@link TextDocument} belongs to
    * this current project
    */
   public void updateFileTree() {
      if (current != null && current.isInProject(currDoc.dir())) {
         EventQueue.invokeLater(() -> mw.fileTree().updateTree());
      }
   }

   /**
    * Saves the source file of the selected {@code TextDocument} if it
    * belongs this current project and compiles the project. Updates the
    * file tree
    */
   public void saveAndCompile() {
      try {
        mw.setBusyCursor(true);
        if (isFileToCompile(currDoc)) {
            if (currDoc.docFile().exists()) {
               currDoc.saveToFile();
               current.compile();
               updateFileTree();
            }
            else {
               JOptions.warnMessage(currDoc.filename()
                     + ":\nThe file could not be found anymore");
            }
         }
      }
      finally {
         EventQueue.invokeLater(() ->  mw.setBusyCursor(false));
      }
   }

   /**
    * Saves all open source files of this current project and compiles the
    * project. Updates the file tree
    */
   public void saveAllAndCompile() {
      try {
         mw.setBusyCursor(true);
         StringBuilder missingFiles = new StringBuilder();
         for (int i = 0; i < txtDoc.length; i++) {
            if (isFileToCompile(txtDoc[i])) {
               if (txtDoc[i].docFile().exists()) {
                  txtDoc[i].saveToFile();
               }
               else {
                  missingFiles.append("\n");
                  missingFiles.append(txtDoc[i].filename());
               }
            }
         }
         if (missingFiles.length() == 0) {
            current.compile();
            updateFileTree();
         }
         else {
            JOptions.warnMessage(FILES_NOT_FOUND_MESSAGE + missingFiles);
         }
      }
      finally {
         EventQueue.invokeLater(() ->  mw.setBusyCursor(false));
      }
   }

   /**
    * Runs this current project
    */
   public void runProj() {
      current.runProject();
   }

   /**
    * Creates a build of this current project and updates the file tree
    */
   public void buildProj() {
      try {
         mw.setBusyCursor(true);
         current.build();
         updateFileTree();
      }
      finally {
         EventQueue.invokeLater(() ->  mw.setBusyCursor(false));
      }
   }

   //
   //--private methods--//
   //

   private void createProjectImpl() {
      if (currDoc.filename().length() == 0) {
         JOptions.titledInfoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         return;
      }
      ProjectActions projNew = selProj.createProject(docSuffix);
      if (projNew == null) {
         String selectedSuffix
               = JOptions.comboBoxRes(wrongExtentionMessage(currDoc.filename()),
               "File extension", projectOptions, null, true);
         if (selectedSuffix != null && !selectedSuffix.equals(projectOptions[0])) {
            projNew = selProj.createProject(selectedSuffix);
         }
      }
      if (projNew != null) {
         ProjectActions projFin = projNew;
         projFin.makeSetWinVisible(true);
         projFin.addOkAction(e -> configureProject(projFin));
      }
   }

   private boolean changeProject(ProjectActions toChangeTo) {
      int result = JOptions.confirmYesNo("Switch to project '"
                 + toChangeTo.getProjectName() + "'?");
      if (result == 0) {
         current = toChangeTo;
         current.storeInPrefs();
         updateProjectSetting(current);
         mw.enableChangeProject(false);
         return true;
      }
      else {
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
      if (projToConf.configureProject(currDoc.dir())) {
         current = projToConf;
         projList.add(current);
         updateProjectSetting(current);
      }
   }

   private boolean isFileToCompile(TextDocument td) {
       return td != null
             && td.filename().endsWith(current.getSourceSuffix())
             && current.isInProject(td.dir());
   }

   private void confirmedNewProject(ProjectActions toConfirm) {
      int res = JOptions.confirmYesNo(currDoc.filename()
              + "\nThe file belongs the project "
              + "'" + toConfirm.getProjectName() + "'."
              + "\nStill set new project?");
      if (res == 0) {
         createProjectImpl();
      }
   }

   private String wrongExtentionMessage(String filename) {
      return "<html>"
         + filename + "<br>"
         + "If the file belongs to a project specify the extension of<br>"
         + "the source files:"
         + "</html>";
   }

   private void updateProjectSetting(ProjectActions projToSet) {
      proc.setWorkingDir(projToSet.getProjectPath());
      enableActions(projToSet);
      setBuildName(projToSet);
      mw.setProjectName(projToSet.getProjectName());
      mw.fileTree().setDeletableDirName(projToSet.getExecutableDirName());
      mw.fileTree().setProjectTree(projToSet.getProjectPath());
      if (projList.size() == 1) {
         mw.enableOpenFileView();
      }
   }

   private void enableActions(ProjectActions projToSet) {
      switch (className(projToSet)) {
         case "JavaActions":
            mw.enableProjActions(true, true, true);
            break;
         case "HtmlActions":
            mw.enableProjActions(false, true, false);
            break;
         case "PerlActions":
            mw.enableProjActions(false, true, false);
            break;
      }
   }
   
   private void setBuildName(ProjectActions projToSet) {
      switch (className(projToSet)) {
         case "JavaActions":
            mw.setBuildName("Create jar");
            break;
         default:
            mw.setBuildName("Build");
      }
   }
   
   private String className(ProjectActions projToSet) {
      return projToSet.getClass().getSimpleName();
   }
}
