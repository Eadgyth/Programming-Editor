package eg;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.console.*;
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
         = "A project can be set after opening a file or"
         + " saving a new file.";
         
   private final String NOT_IN_PROJ_MESSAGE 
         = "The selected file is not in the root directory"
         + " of the currently active project.";
         
   private final String ASK_FOR_LANG
         = "Set new project defined by the selected language";

   private final String WRONG_TYPE_MESSAGE
         = "A project is not defined for this file.\n"
         + "If the file is part of a project the associated language"
         + " must be selected in the Edit menu";
         
   private final String FILES_NOT_FOUND_MESSAGE
         = "The following file could not be found anymore:";

   private final MainWin mw;
   private final SelectedProject selProj;
   private final ProcessStarter proc;
   private final List<ProjectActions> projList = new ArrayList<>();

   private ProjectActions current;
   private TextDocument[] txtDoc;
   private TextDocument currDoc;
   private String currExt;
   private Languages lang;

   public CurrentProject(MainWin mw) {
      this.mw = mw;
      proc = new ProcessStarter(mw.console());
      ProjectUIUpdate update = new ProjectUIUpdate(mw.menu().viewMenu(), mw.fileTree());
      selProj = new SelectedProject(update, proc, mw.console());
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
    * @param index  the index of the array element
    */
   public void setCurrTextDocument(int index) {
      currDoc = txtDoc[index];
      currExt = FileUtils.fileSuffix(currDoc.filename());      
   }

   /**
    * Sets the current language
    *
    * @param lang  the language that has one of the values in
    * {@link Languages}
    */   
   public void setLanguage(Languages lang) {
      this.lang = lang;
   }

   /**
    * If at least one project has been created
    *
    * @return  if at least one project has been created
    */
   public boolean isProjectSet() {
      return current != null;
   }

   /**
    * Assigns to this current project a project which a configuration exists
    * for in a local 'eadconfig' file or in the program's 'prefs' file
    * @see eg.projects.ProjectConfig#retrieveProject(String)
    */
   public void retrieveProject() {
      if (isProjectSet() && current.isInProject(currDoc.dir())) {
         return;
      }
      EventQueue.invokeLater(() -> {
         ProjectActions prToFind = selProj.createProjectByExt(currExt);
             
         boolean isFound = prToFind != null
               && prToFind.retrieveProject(currDoc.dir());
               
         if (prToFind == null) {
            for (Languages l : Languages.values()) {
               prToFind = selProj.createProjectByLang(l);
               isFound = prToFind != null
                     && prToFind.retrieveProject(currDoc.dir());
               if (isFound) {
                  break;
               }
            }
         }
         if (isFound) {
            ProjectActions prFin = prToFind;
            if (!isProjectSet()) {
               current = prFin;
               current.addOkAction(e -> configureProject(current)); 
               projList.add(current); 
               updateProjectSetting(current);
            }
            else {
               if (selectFromList(currDoc.dir(), true) == null) {
                  prFin.addOkAction(e -> configureProject(prFin));
                  projList.add(prFin);
                  enableChangeProject();
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
    * <p>If the the currently set {@link TextDocument} belongs to an
    * already set project a dialog to confirm to proceed is shown.
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
                  + "' belongs to project '" + test.getProjectName() + "'."
                  + "\nStill set new project?");
         }
         if (res == 0) {
            createNewProject(false);
         }
      }       
  }         

   /**
    * Sets active the project from this {@code List} of configured projects
    * which the currently selected {@code TextDocument} belongs to.
    * <p>If the currently set {@code TextDocument} does not belong to a
    * listed project or to the currently active project it is asked to set
    * up a new project.
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
    * Updates the file tree of {@code FileTree} if the specified directory
    * includes the project's root directory.
    *
    * @param path  the directory that may include the project's root
    * directory
    */
   public void updateFileTree(String path) {
      if (isProjectSet() && current.isInProject(path)) {
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
                     + " could not be found anymore");
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
      StringBuilder missingFiles = new StringBuilder();
      try {
         mw.setBusyCursor(true);
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
   //--private methods--
   //

   private void createNewProject(boolean needConfirm) {
      if (currDoc.filename().length() == 0) {
         JOptions.titledInfoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         return;
      }
      ProjectActions projNew = selProj.createProjectByExt(currExt);
      if (projNew == null) {
         if (lang != Languages.PLAIN_TEXT) {
            int res = JOptions.confirmYesNo(ASK_FOR_LANG + " (" + lang + ") ?");
            if (res == 0) {
               projNew = selProj.createProjectByLang(lang);
            }
         }
         else {
            JOptions.titledInfoMessage(WRONG_TYPE_MESSAGE, "Note");
         }
      }
      if (projNew != null) {
         ProjectActions prFin = projNew;   
         int res = 0;
         if (needConfirm && isProjectSet()) {
            res = JOptions.confirmYesNo("Set new project ?");
         }
         if (res == 0) {
            prFin.makeSetWinVisible(true);
            prFin.addOkAction(e -> configureProject(prFin));
         }
      }
   }

   private boolean changeProject(ProjectActions toChangeTo) {
      int result = JOptions.confirmYesNo("Set active project '"
                 + toChangeTo.getProjectName() + "' ?");
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
      mw.fileTree().setDeletableDirName(projToSet.getExecutableDirName());
      proc.addWorkingDir(projToSet.getProjectPath());
      mw.showProjectInfo(projToSet.getProjectName());
      enableActions(projToSet);
      mw.fileTree().setProjectTree(projToSet.getProjectPath());
   }
   
   private void enableActions(ProjectActions projToSet) {
      if (projList.size() == 1) {
         mw.menu().viewMenu().enableFileView();
      }
      enableChangeProject();
      selProj.enableActions(projToSet.getClass().getSimpleName(), mw);
   }
   
   private void enableChangeProject() {
      if (projList.size() == 2) {
         mw.menu().projectMenu().enableChangeProjItm();
         mw.toolbar().enableChangeProjBt();
      }
   }
   
   private boolean isFileToCompile(TextDocument td) {
       return td != null
                 && td.filename().endsWith(current.getSourceSuffix())
                 && current.isInProject(td.dir());
   }
   
   private void endCompilation() {
      EventQueue.invokeLater(() -> {
         mw.fileTree().updateTree();
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
