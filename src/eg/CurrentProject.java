package eg;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.console.*;
import eg.ui.filetree.FileTree;
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
         = "A project can be set after opening a file or"
         + " saving a new file.";

   private final String IS_IN_PROJ_MESSAGE 
         = "The selected file belongs to the"
         + " currently active project.";
         
   private final String NOT_IN_PROJ_MESSAGE 
         = "The selected file does not belong to the"
         + " currently active project.";

   private final String WRONG_TYPE_MESSAGE
         = "No project can be defined for this file type.";
         
   private final String FILES_NOT_FOUND_MESSAGE
         = "The following file could not be found anymore:";

   private final SelectedProject selProj;
   private final ProjectUpdate update;
   private final ProcessStarter proc;
   private final List<ProjectActions> projList = new ArrayList<>();

   private ProjectActions current;
   private TextDocument[] txtDoc;
   private TextDocument currDoc;
   private String currExt;
   private Languages lang;

   public CurrentProject(ProjectUpdate update, ConsolePanel consPnl) {
      this.update = update;
      proc = new ProcessStarter(consPnl);
      selProj = new SelectedProject(update, proc, consPnl);
   }

   /**
    * Sets the array of {@code TextDocument}
    * @param txtDoc  the array of {@link TextDocument}
    */
   public void setDocumentArr(TextDocument[] txtDoc) {
      this.txtDoc = txtDoc;
   }

   /**
    * Selects the object of this array of {@code TextDocument}
    * @param docIndex  the index of the element in this array of
    * {@link TextDocument}
    */
   public void selectDocument(int docIndex) {
      currDoc = txtDoc[docIndex];
      currExt = FileUtils.fileSuffix(currDoc.filename());
   }

   /**
    * Sets the current language
    * @param lang  the language that is one of the constants in
    * {@link Languages}
    */   
   public void setLanguage(Languages lang) {
      this.lang = lang;
   }

   /**
    * If at least one project has been created
    * @return  if at least one project has been created
    */
   public boolean isProjectSet() {
      return current != null;
   }

   /**
    * Assign to this current project a project which a configuration exists
    * for in a local 'eadconfig' file or in the program's 'prefs' file
    * @see eg.projects.ProjectConfig#retrieveProject(String)
    */
   public void retrieveProject() {
      if (isProjectSet() && current.isInProject(currDoc.dir())) {
         return;
      }
      
      EventQueue.invokeLater(() -> {
         ProjectActions prToFind = selProj.createProject(currExt, lang);
         boolean isFound = prToFind != null
               && prToFind.retrieveProject(currDoc.dir());
         if (isFound) {
            if (!isProjectSet()) {   
               current = prToFind;
               current.addOkAction(e -> configureProject(current)); 
               projList.add(current); 
               updateProjectSetting(current);
            }
            else {
               if (selectFromList(currDoc.dir(), true) == null) {
                  prToFind.addOkAction(e -> configureProject(prToFind));
                  projList.add(prToFind);
                  if (projList.size() == 2) {
                     update.enableChangeProj();
                  }
                  changeProject(prToFind);            
               }
            }
         }
      });
   }

   /**
    * Opens the window of the {@code SettingsWin} object that belongs to
    * a project.
    * <p>
    * Depending on the currently set {@link TextDocument} the opened window
    * belongs to the current project, to one of this listed projects or to
    * a newly created project.
    */
   public void openSettingsWindow() {
      if (!isProjectSet()) {
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
    * <p>
    * If the the curently set {@link TextDocument} belongs to an already
    * set project a dialog to confirm to proceed is shown.
    */
   public void newProject() {
      if (!isProjectSet()) {
         createNewProject(false);
      }
      else {
         ProjectActions test = selectFromList(currDoc.dir(), false);
         int res = 0;
         if (test != null) {
            res = JOptions.confirmYesNo("'" + currDoc.filename()
                  + "' belongs to project '" + projectName(test) + "'."
                  + "\nStill set new project?");
         }
         if (res == 0) {
            createNewProject(false);
         }
      }       
  }         

   /**
    * Sets active the project from this {@code List} of configured
    * projects which the currently selected {@code TextDocument}
    * belongs to.
    * <p>
    * If the currently set {@code TextDocument} does not belong to a listed
    * project or to the currently active project it is asked to set up a
    * new project.
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
    * Updates the file tree of {@code FileTree} if the specified
    * directory includes the project's root directory.
    * @param path  the directory that may include the project's root
    * directory
    * See {@link FileTree#updateTree()}
    */
   public void updateFileTree(String path) {
      if (isProjectSet() && current.isInProject(path)) {
         update.updateFileTree();
      }
   }
   
   /**
    * Saves the source file of the selected {@code TextDocument}
    * and compiles the project
    */
   public void saveAndCompile() {
      if (!isCurrent("Compile")) {
         return;
      }
      try {
        update.setBusyCursor(true);
        if (isFileToCompile(currDoc)) {
            boolean exists = new File(currDoc.filepath()).exists();
            if (exists) {
               currDoc.saveToFile();
               current.compile();
            }
            else {
               JOptions.warnMessage(currDoc.filename()
                     + " could not be found anymore");
            }
         }
      }
      finally {
         endCompilation();
      }
   }

   /**
    * Saves all open source files of the project directory and compiles the
    * project
    */
   public void saveAllAndCompile() {
      if (!isCurrent("Compile")) {
         return;
      }
      StringBuilder missingFiles = new StringBuilder();
      try {
         update.setBusyCursor(true);
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
      if (current.isRunByFile()) {
         current.runProject(currDoc.filename());
      }
      else {
         current.runProject();
      }
   }

   /**
    * Creates a build of this current project
    */
   public void buildProj() {
      if (!isCurrent("Build")) {
         return;
      }
      try {
         update.setBusyCursor(true);
         current.build();
      }
      finally {
         update.setBusyCursor(false);
      }
   }

   //
   //--private methods
   //

   /* 
    * @param confirm  true to confirm in a dialog that a new project will be set
    */
   private void createNewProject(boolean needConfirm) {
      if (currDoc.filename().length() == 0) {
         JOptions.titledInfoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         return;
      }
      ProjectActions projNew = selProj.createProject(currExt, lang);
      if (projNew == null) {
         JOptions.titledInfoMessage(WRONG_TYPE_MESSAGE, "Note");
      }
      else {    
         int result = 0;
         if (needConfirm && isProjectSet()) {
            result = JOptions.confirmYesNo("Set new project ?");
         }
         if (result == 0) {
            projNew.makeSetWinVisible(true);
            projNew.addOkAction(e -> configureProject(projNew));
         }
      }
   }

   private boolean changeProject(ProjectActions toChangeTo) {
      int result = JOptions.confirmYesNo("Change to project '"
                 + projectName(toChangeTo) + "' ?");
      if (result == 0) {
         current = toChangeTo;
         current.storeInPrefs();
         updateProjectSetting(current);
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
         updateProjectSetting(current);
      }
   }

   private void updateProjectSetting(ProjectActions projToSet) {
      update.setDeletableDirName(projToSet.getExecutableDirName());
      proc.addWorkingDir(projToSet.getProjectPath());
      update.showProjectInfo(projectName(projToSet));
      selProj.enableActions(projToSet.getClass().getSimpleName(), projList.size());
      EventQueue.invokeLater(() -> 
            update.setProjectTree(projToSet.getProjectPath()));
   }
   
   private String projectName(ProjectActions toName) {
      return new File(toName.getProjectPath()).getName();
   }
   
   private boolean isFileToCompile(TextDocument td) {
       return td != null
                 && td.filename().endsWith(current.getSourceSuffix())
                 && current.isInProject(td.dir());
   }
   
   private void endCompilation() {
      EventQueue.invokeLater(() -> {
         update.updateFileTree();
         update.setBusyCursor(false);
      });
   }
   
   private boolean isCurrent(String action) {
      boolean useCurrentProj = current.isInProject(currDoc.dir());
      int res = 0;
      if (!useCurrentProj) {
         res = JOptions.confirmYesNo(NOT_IN_PROJ_MESSAGE
             + "\n" + action + " '" + projectName(current) + "'?");
      }
      return useCurrentProj || res == 0;
   }
}
