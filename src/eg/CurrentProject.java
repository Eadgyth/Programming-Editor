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
 * A project is represented by an object of type {@link ProjectActions} and
 * is configured and/or set active depending on the {@link TextDocument} that
 * is set as selected at the time.
 * <p>
 * Several configured projects are stored in a {@code List} of projects. Any
 * of these can be (re-) activated depending on the currently set
 * {@link TextDocument}
 */
public class CurrentProject {

   private final static String F_SEP = File.separator;

   private final String NO_FILE_IN_TAB_MESSAGE
         = "A project can be set after a file was opened or"
         + " newly saved.";

   private final String NOT_IN_PROJ_MESSAGE
         = "The selected file is not in the root directory"
         + " of the currently active project.";
   /*
    * Formatted for use in a JLabel */
   private final String WRONG_TYPE_MESSAGE
         = "<html>"
         + "The selected file does not specify a project category.<br>"
         + "If the file belongs to a project specify the extension of<br>"
         + "the source files:"
         + "</html>";

   private final String FILES_NOT_FOUND_MESSAGE
         = "The following file could not be found anymore:";

   private final MainWin mw;
   private final SelectedProject selProj;
   private final ProcessStarter proc;
   private final ProjectUIUpdate update;
   private final List<ProjectActions> projList = new ArrayList<>();
   /*
    * Options for a Comobox*/
   private final String[] projectOptions;
   
   private ProjectActions current;
   private TextDocument currDoc;
   private TextDocument[] txtDoc;
   private String docSuffix;

   public CurrentProject(MainWin mw) {
      this.mw = mw;
      proc = new ProcessStarter(mw.console());
      update = new ProjectUIUpdate(mw);
      selProj = new SelectedProject(update, proc, mw.console());
      projectOptions = new String[selProj.projectSuffixes.length + 1];
      projectOptions[0] = "File extensions...";
      for (int i = 0; i < selProj.projectSuffixes.length; i++) {
         projectOptions[i + 1] = selProj.projectSuffixes[i];
      }
   }
   
   /**
    * Sets the array of {@code TextDocument}
    *
    * @param txtDoc  the array of {@link TextDocument}
    */
   public void setDocumentArr(TextDocument[] txtDoc) {
      this.txtDoc = txtDoc;
   }

   /**
    * Selects an element from this array of {@code TextDocument}
    *
    * @param i  the index of the array element
    */
   public void setTextDocumentAt(int i) {
      currDoc = txtDoc[i];
      docSuffix = FileUtils.fileSuffix(currDoc.filename());
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
               proc.setWorkingDir(current.getProjectPath());
               update.updateProjectSetting(current, projList.size());
            }
            else {
               if (selectFromList(currDoc.dir(), true) == null) {
                  prFin.addOkAction(e -> configureProject(prFin));
                  projList.add(prFin);
                  update.enableChangeProject(projList.size());
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
      if (current == null) {
         createNewProject(false);
      }
      else {
         boolean openCurrent
              =  currDoc.filename().length() == 0
              || current.isInProject(currDoc.dir());
         if (openCurrent) {
            current.makeSetWinVisible(true);
         }
         else {
            ProjectActions fromList = selectFromList(currDoc.dir(), true);
            if (fromList != null && changeProject(fromList)) {
               current.makeSetWinVisible(true);
            }
            else {
               createNewProject(true);
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
      if (current == null) {
         createNewProject(false);
      }
      else {
         ProjectActions test = selectFromList(currDoc.dir(), false);
         int res = 0;
         if (test != null) {
            res = JOptions.confirmYesNo(currDoc.filename()
                  + "\nThe file belongs the project "
                  + "'" + test.getProjectName() + "'."
                  + "\nStill set new project?");
         }
         if (res == 0) {
            createNewProject(false);
         }
      }
  }

   /**
    * Sets active the project from this <code>List</code> of configured
    * projects which the currently selected {@code TextDocument} belongs to.
    * <p>If the set {@link TextDocument} does not belong to a listed project
    * or to the currently active project it is asked to set up a new project.
    */
   public void changeProject() {
      ProjectActions fromList = selectFromList(currDoc.dir(), true);
      if (fromList != null) {
         changeProject(fromList);
      }
      else {
         createNewProject(true);
      }
   }

   /**
    * Updates the file tree if the set {@link TextDocument} is in the
    * directory of the current project
    */
   public void updateFileTree() {
      if (current != null && current.isInProject(currDoc.dir())) {
         mw.fileTree().updateTree();
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
            }
            else {
               JOptions.warnMessage(currDoc.filename()
                     + ":\nThe file could not be found anymore");
            }
         }
      }
      finally {
         endCompilation();
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
         }
         else {
            JOptions.warnMessage(FILES_NOT_FOUND_MESSAGE + missingFiles);
         }
      }
      finally {
         endCompilation();
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
      }
      finally {
         mw.setBusyCursor(false);
      }
   }

   //
   //--private methods--//
   //

   private void createNewProject(boolean needConfirm) {
      if (currDoc.filename().length() == 0) {
         JOptions.titledInfoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         return;
      }
      ProjectActions projNew = selProj.createProject(docSuffix);
      if (projNew == null) {
         String selectedSuffix = JOptions.comboBoxRes(WRONG_TYPE_MESSAGE,
               "Project category", projectOptions, null, true);
         if (selectedSuffix != null && !selectedSuffix.equals(projectOptions[0])) {
            projNew = selProj.createProject(selectedSuffix);
         }
      }
      if (projNew != null) {
         ProjectActions prFin = projNew;
         int res = 0;
         if (needConfirm && current != null) {
            res = JOptions.confirmYesNo("Set new project ?");
         }
         if (res == 0) {
            prFin.makeSetWinVisible(true);
            prFin.addOkAction(e -> configureProject(prFin));
         }
      }
   }

   private boolean changeProject(ProjectActions toChangeTo) {
      int result = JOptions.confirmYesNo("Set active the project '"
                 + toChangeTo.getProjectName() + "'?");
      if (result == 0) {
         current = toChangeTo;
         current.storeInPrefs();
         proc.setWorkingDir(current.getProjectPath());
         update.updateProjectSetting(current, projList.size());
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
         if (current != projToConf) {
            current = projToConf;
            projList.add(current);
         }
         proc.setWorkingDir(current.getProjectPath());
         update.updateProjectSetting(current, projList.size());
      }
   }

   private boolean isFileToCompile(TextDocument td) {
       return td != null
             && td.filename().endsWith(current.getSourceSuffix())
             && current.isInProject(td.dir());
   }

   private void endCompilation() {
      EventQueue.invokeLater(() -> {
         update.updateFileTree();
         mw.setBusyCursor(false);
      });
   }

   private boolean isCurrent(String action) {
      boolean useCurrentProj = current.isInProject(currDoc.dir());
      int res = 0;
      if (!useCurrentProj) {
         res = JOptions.confirmYesNo(NOT_IN_PROJ_MESSAGE
             + "\n" + action + " " + current.getProjectName() + "?");
      }
      return useCurrentProj || res == 0;
   }
}
