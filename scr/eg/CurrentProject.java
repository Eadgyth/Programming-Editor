package eg;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

import java.awt.EventQueue;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.ui.Toolbar;
import eg.ui.filetree.FileTree;
import eg.ui.menu.Menu;

import eg.projects.ProjectActions;
import eg.projects.SelectedProject;

import eg.document.TextDocument;

import eg.utils.FileUtils;
import eg.utils.JOptions;

/**
 * The managing of projects.
 * <p>
 * A project is represented by an object of type {@link ProjectActions}
 * and is configured and/or set active depending on the {@link TextDocument}
 * that is set as selected at the time.
 * <p>
 * Several configured projects are stored in a {@code List} of projects.
 * Any of these can be (re-) activated depending on the currently set
 * {@link TextDocument}
 */
public class CurrentProject {
   
   private final String NO_FILE_IN_TAB_MESSAGE
         = "A project can be set after opening a file or"
         + " saving a new file";
   
   private final String IS_IN_PROJ_MESSAGE 
         = "The selected file belongs to the"
         + " currently active project";
         
   private final String WRONG_TYPE_MESSAGE
         = "No project can be defined for this file type";

   private final SelectedProject selProj;
   private final MainWin mw;
   private final FileTree fileTree;
   private final Menu menu;
   private final Toolbar tBar;
   private final List<ProjectActions> recent = new ArrayList<>();

   private ProjectActions proj;
   private TextDocument[] txtDoc;
   private TextDocument currDoc;
   private String sourceExt;
   private String currExt;

   public CurrentProject(SelectedProject selProj, MainWin mw,
         FileTree fileTree, Menu menu, Toolbar tBar) {
      this.selProj = selProj;
      this.mw = mw;
      this.fileTree = fileTree;
      this.menu = menu;
      this.tBar = tBar;
   }

   /**
    * Sets in this the array of {@code TextDocument}
    * @param txtDoc  the array of {@link TextDocument}
    */
   public void setDocumentArr(TextDocument[] txtDoc) {
      this.txtDoc = txtDoc;
   }
   
   /**
    * Selects the object of this array of {@code TextDocument}
    * that is used to configure and/or set active a project
    * @param docIndex  the index of the element in this array
    * of {@link TextDocument}
    */
   public void setDocumentIndex(int docIndex) {
      currDoc = txtDoc[docIndex];
      currExt = FileUtils.extension(currDoc.filepath());
   }

   /**
    * If at least one project has been created
    * @return  if at least one project has been created
    */
   public boolean isProjectSet() {
      return proj != null;
   }

   /**
    * Tries to assign to this current project a project which a configuration
    * exists for in a local 'eadconfig' file or in the program's 'prefs' file.
    * <p>
    * Assignment of the project to this current project (setting the project
    * active) will happen only if no other project was assigned before.
    * However, if a local 'config' is found the project is always added to
    * this list of configured projects.
    */
   public void retrieveProject() {
      if (isProjectSet() && proj.isProjectInPath(currDoc.dir())) {
         return;
      }
      ProjectActions prToFind = selProj.createProject(currExt, true);
      boolean isFound
            =  prToFind != null
            && prToFind.retrieveProject(currDoc.dir());
      if (isFound) {
         if (!isProjectSet()) {   
            proj = prToFind;
            proj.addOkAction(e -> configureProject(proj)); 
            recent.add(proj); 
            updateProjectSetting(proj);
         }
         else {
            if (searchRecent() == null) {
               prToFind.addOkAction(e -> configureProject(prToFind));
               recent.add(prToFind);
               if (recent.size() == 2) {
                  menu.getProjectMenu().enableChangeProjItm();
               }
            }
         }
      }
   }

   /**
    * Opens the window of the {@code SettingsWin} object that blongs to
    * a project.
    * <p>
    * Depending on the currently set {@link TextDocument} the opened window
    * belongs to the current project, to one of this listed projects that were
    * set before or to a newly created project.
    */
   public void openSettingsWindow() {
      if (!isProjectSet()) {
         if (currDoc.filename().length() == 0) {
            JOptions.titledInfoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         }
         else {
            newProject();
         }
      }
      else {
         boolean openCurrent
              =  currDoc.filename().length() == 0
              || proj.isProjectInPath(currDoc.dir());
         if (openCurrent) {
            proj.makeSetWinVisible(true);
         }      
         else {
            ProjectActions inList = searchRecent();
            if (inList != null) {
               if (changeProject(inList)) {
                  proj.makeSetWinVisible(true);
               }
            }
            else {
               newProject();
            }
         }
      }       
   }
   
   /**
    * Assigns to this current project the project from this {@code List} of
    * configured projects which the currently selected {@code TextDocument}
    * belongs to.
    * <p>
    * If the currently set {@code TextDocument} does not belong to a listed
    * project it is asked to set up a new project.
    */
   public void changeProject() {
      ProjectActions inList = searchRecent();
      if (inList == proj) {
         JOptions.infoMessage(IS_IN_PROJ_MESSAGE);
      }
      else {
         if (inList != null) {
            changeProject(inList);
         }
         else {
            newProject();
         }   
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
      if (isProjectSet() && proj.isProjectInPath(path)) {
         fileTree.updateTree();
      }
   }

   /**
    * Compiles this current project
    */
   public void compile() {         
      mw.setBusyCursor(true);
      int missingIndex = 0;
      try {
         for (int i = 0; i < txtDoc.length; i++) {
            boolean approved
                  = txtDoc[i] != null
                  && txtDoc[i].filename().endsWith(sourceExt)
                  && proj.isProjectInPath(txtDoc[i].dir());
            if (approved) {
               boolean exists = new File(txtDoc[i].filepath()).exists();
               if (!exists) {
                  missingIndex = i;
               }
               txtDoc[i].saveToFile();
            }
         }  
         if (missingIndex == 0) {
            proj.compile();
         }
         else {
            JOptions.warnMessage(
                    txtDoc[missingIndex].filename()
                  + " cannot be not found anymore");
         }
      }   
      finally {
         EventQueue.invokeLater(() -> {
            mw.setBusyCursor(false);
         });
      }
   }

   /**
    * Runs this project
    */
   public void runProj() {
      proj.runProject();
   }

   /**
    * Creates a build of this current project
    */
   public void buildProj() {
      try {
         mw.setBusyCursor(true);
         proj.build();
      }
      finally {
         mw.setBusyCursor(false);
      }
   }

   //
   //--private methods
   //
   
   private void newProject() {
      ProjectActions projNew = selProj.createProject(currExt, false);
      if (projNew == null) {
         JOptions.titledInfoMessage(WRONG_TYPE_MESSAGE, "Note");
      }
      else {    
         int result = 0;
         if (isProjectSet()) {
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
                 + toChangeTo.getProjectName() + "'");
      if (result == 0) {
         proj = toChangeTo;
         proj.storeInPrefs();
         updateProjectSetting(proj);
         return true;
      }
      else {
         return false;
      }
   }

   private ProjectActions searchRecent() {
      ProjectActions old = null;
      for (ProjectActions p : recent) {
         if (p.isProjectInPath(currDoc.dir())) {
            old = p;
         }
      }
      return old;
   }
   
   private void configureProject(ProjectActions projToConf) {
      if (projToConf.configureProject(currDoc.dir())) {
         if (proj != projToConf) {
            proj = projToConf;
            recent.add(proj);
         }
         updateProjectSetting(proj);
      }
   }

   private void updateProjectSetting(ProjectActions projToSet) {
      mw.showProjectInfo(projToSet.getProjectName());
      projToSet.applyProject();
      enableActions(projToSet.getClass().getSimpleName());
   }
   
   private void enableActions(String className) {
      switch (className) {
         case "JavaActions":
            enableActions(true, true, true);
            menu.getProjectMenu().setBuildKind("Create jar");
            sourceExt = ".java";
            break;
         case "HtmlActions":
            enableActions(false, true, false);
            break;
         case "PerlActions":
            enableActions(false, true, false);
            break;
      }
   }
   
   private void enableActions(boolean isCompile, boolean isRun, boolean isBuild) {
      if (recent.size() == 1) {
         menu.getViewMenu().enableFileView();
      }
      if (recent.size() == 2) {
         menu.getProjectMenu().enableChangeProjItm();
      }
      menu.getProjectMenu().enableProjItms(isCompile, isRun, isBuild);
      tBar.enableProjBts(isCompile, isRun);
   }
}
