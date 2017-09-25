package eg;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.console.*;
import eg.Languages;
import eg.ui.MainWin;
import eg.ui.ConsoleOpenable;
import eg.projects.ProjectActions;
import eg.projects.SelectedProject;

import eg.document.TextDocument;

import eg.utils.JOptions;
import eg.utils.FileUtils;

/**
 * The configuration and execution of actions of projects.
 * <p>
 * A project is represented by an object of type {@link ProjectActions} and
 * is configured and/or set active depending on the {@link TextDocument} that
 * is set as selected at the time.
 * <p>
 * Several configured projects are stored in a {@code List} of projects. Any
 * of these can be (re-) activated depending on the currently set
 * {@link TextDocument}
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
   }

   /**
    * Assigns to this current project a project which a configuration
    * exists for in an 'eadconfig' file saved in the project's directory
    * or in the program's prefs file
    * @see eg.projects.ProjectConfig#retrieveProject(String)
    */
   public void retrieveProject() {
      if (current != null && current.isInProject(currDoc.dir())) {
         return;
      }
      EventQueue.invokeLater(() -> {
         ProjectActions prToFind = selProj.createProject(docSuffix);
         boolean isFound = prToFind != null
               && prToFind.retrieveProject(currDoc.dir());
         if (prToFind == null) {
            for (String opt : selProj.projectSuffixes) {
               prToFind = selProj.createProject(opt);
               isFound = prToFind != null
                     && prToFind.retrieveProject(currDoc.dir());
               if (isFound) {
                  break;
               }
            }
         }
         if (isFound) {
            ProjectActions prFin = prToFind;
            if (current == null) {
               current = prFin;
               current.addOkAction(e -> configureProject(current));
               projList.add(current);
               updateProjectSetting(current);
            }
            else {
               if (selectFromList(currDoc.dir(), true) == null) {
                  prFin.addOkAction(e -> configureProject(prFin));
                  projList.add(prFin);
                  changeProject(prFin);
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
            createNewProject();
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
    * <p>If the the currently set {@link TextDocument} belongs to an already
    * set project a dialog to confirm to proceed is shown.
    */
   public void newProject() {
      ProjectActions fromList = selectFromList(currDoc.dir(), false);
      if (fromList == null) {
         createNewProject();
      }
      else {
         confirmedNewProject(fromList);
      }
  }

   /**
    * Sets active the project from this <code>List</code> of configured
    * projects which the currently selected {@code TextDocument} belongs to.
    * <p>If the set {@link TextDocument} belongs to the currently active project
    * it is asked to set up a new project.
    */
   public void changeProject() {
      ProjectActions fromList = selectFromList(currDoc.dir(), false);
      if (fromList == null) {
         createNewProject();
      }
      else {
         if (fromList != current) {
            changeProject(fromList);
         }
         else {
            confirmedNewProject(fromList);
         }
      }
   }

   /**
    * Updates the file tree if the set {@link TextDocument} is in the
    * directory of the current project
    */
   public void updateFileTree() {
      if (current != null && current.isInProject(currDoc.dir())) {
         EventQueue.invokeLater(() -> mw.fileTree().updateTree());
      }
   }

   /**
    * Saves the source file of the selected {@code TextDocument} if it
    * belongs the current project and compiles the project
    */
   public void saveAndCompile() {
      if (!isCurrent("Compile")) {
         return;
      }
      try {
        mw.setBusyCursor(true);
        if (isFileToCompile(currDoc)) {
            boolean exists = new File(currDoc.filepath()).exists();
            if (exists) {
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
    * Saves all open source files of the current project and compiles the
    * project
    */
   public void saveAllAndCompile() {
      if (!isCurrent("Compile")) {
         return;
      }
      try {
         mw.setBusyCursor(true);
         StringBuilder missingFiles = new StringBuilder();
         for (int i = 0; i < txtDoc.length; i++) {
            if (isFileToCompile(txtDoc[i])) {
               boolean exists = new File(txtDoc[i].filepath()).exists();
               if (exists) {
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
    * Runs this project
    */
   public void runProj() {
      if (!isCurrent("Run")) {
         return;
      }
      current.runProject();
   }

   /**
    * Creates a build of this current project
    */
   public void buildProj() {
      if (!isCurrent("Build")) {
         return;
      }
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

   private void createNewProject() {
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

   private boolean isCurrent(String action) {
      boolean isCurrent = current.isInProject(currDoc.dir());
      int res = 0;
      if (!isCurrent) {
         res = JOptions.confirmYesNo(NOT_IN_PROJ_MESSAGE
             + "\n" + action + " project '" + current.getProjectName() + "' ?");
      }
      return isCurrent || res == 0;
   }
   
   private void confirmedNewProject(ProjectActions toConfirm) {
      int res = JOptions.confirmYesNo(currDoc.filename()
              + "\nThe file belongs the project "
              + "'" + toConfirm.getProjectName() + "'."
              + "\nStill set new project?");
      if (res == 0) {
         createNewProject();
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
      mw.setProjectName(projToSet.getProjectName());
      mw.fileTree().setDeletableDirName(projToSet.getExecutableDirName());
      mw.fileTree().setProjectTree(projToSet.getProjectPath());
   }

   private void enableActions(ProjectActions projToSet) {
      if (projList.size() == 1) {
         mw.enableOpenFileView();
      }
      mw.setBuildName("Build");
      String className = projToSet.getClass().getSimpleName();
      switch (className) {
         case "JavaActions":
            mw.enableProjActions(true, true, true);
            mw.setBuildName("Create jar");
            break;
         case "HtmlActions":
            mw.enableProjActions(false, true, false);
            break;
         case "PerlActions":
            mw.enableProjActions(false, true, false);
            break;
      }
   }
}
