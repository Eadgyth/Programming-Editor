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
import eg.projects.ProjectFactory;

import eg.document.TextDocument;

import eg.utils.FileUtils;
import eg.utils.JOptions;

import eg.javatools.SearchFiles;

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

   private final ProjectFactory projFact;
   private final MainWin mw;
   private final FileTree fileTree;
   private final Menu menu;
   private final Toolbar tBar;
   private final List<ProjectActions> recent = new ArrayList<>();

   private ProjectActions proj;
   private TextDocument[] txtDoc;
   private TextDocument docSel;
   private String extension;

   public CurrentProject(ProjectFactory projFact, MainWin mw,
         FileTree fileTree, Menu menu, Toolbar tBar) {
      this.projFact = projFact;
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
      docSel = txtDoc[docIndex];
   }

   /**
    * If at least one project has been created
    */
   public boolean isProjectSet() {
      return proj != null;
   }

   /**
    * Tries to assign to this current project a project which a configuration
    * exists for in a local 'config' file or in the program's 'prefs' file.
    * <p>
    * Assignment of the project to this current project (setting the project
    * active) will happen only if no other project was assigned before.
    * However, if a local 'config' is found the project is always added to
    * this list of configured projects.
    */
   public void retrieveProject() {
      if (isProjectSet() && proj.isProjectInPath(docSel.dir())) {
         return;
      }

      ProjectActions prPrevious
            = projFact.getProjAct(FileUtils.extension(docSel.filepath()));
      boolean isFound = prPrevious != null
            && prPrevious.retrieveProject(docSel.dir());
      if (isFound) {
         if (!isProjectSet()) {          
            proj = prPrevious;
            proj.addOkAction(e -> configureProject(prPrevious));
            recent.add(proj); 
            updateProjectSetting(proj);
         }
         else {
            ProjectActions inList = searchRecent(docSel.dir());
            if (inList == null) {
               prPrevious.addOkAction(e -> configureProject(prPrevious));
               recent.add(prPrevious);
            }
         }
         if (recent.size() == 2) {
            menu.getProjectMenu().enableChangeProjItm();
         }
      }
   }

   /**
    * Opens the window of the {@code SettingsWin} object of a project.
    * <p>
    * Depending on the currently set {@link TextDocument} the opened window
    * belongs to the current project, to one of this listed projects that were
    * set before or to a newly created project.
    */
   public void openSettingsWindow() {
      if (!isProjectSet()) {
         if (docSel.filename().length() == 0) {
            JOptions.titledInfoMessage(
                    "A project can be set after a file was"
                  + " opened or a new file was saved", "Note");
         }
         else {
            newProject();
         }
      }
      else {  
         if (docSel.filename().length() == 0
               || proj.isProjectInPath(docSel.dir())) {
            proj.makeSetWinVisible(true);
         }      
         else if (docSel.filename().length() > 0
               & !proj.isProjectInPath(docSel.dir())) {
            ProjectActions recent = searchRecent(docSel.dir());
            if (recent != null) {
               if (changeProject(recent)) {
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
      ProjectActions recent = searchRecent(docSel.dir());
      if (recent == proj) {
         JOptions.infoMessage(
                "The selected file belongs to the"
              + " currently active project");
      }
      else {
         if (recent != null) {
            changeProject(recent);
         }
         else {
            newProject();
         }   
      }
   } 

   /**
    * Updates the file tree of {@code FileTree} if the specified
    * directory includes the project's root directory.
    * @param path  the directory that includes the project's root
    * directory
    * See {@link FileTree #updateTree()}
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
      mw.setCursor(MainWin.BUSY_CURSOR);
      int i = 0;
      try {
          for (i = 0; i < txtDoc.length; i++) {
            if (txtDoc[i] != null && proj.isProjectInPath(txtDoc[i].dir())) {
               boolean noProblem = !txtDoc[i].filename().endsWith(extension);
               if (noProblem && !new File(txtDoc[i].filepath()).exists()) {
                  break;
               }
               txtDoc[i].saveToFile();
            }
         }  
         if (i == txtDoc.length) {
            proj.compile();
         }
         else {
            JOptions.warnMessage(txtDoc[i].filename() + " does not exists anymore");
         }
      }   
      finally {
         EventQueue.invokeLater(() -> {
            mw.setCursor(MainWin.DEF_CURSOR);
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
    * Creates a build of this project
    */
   public void buildProj() {
      proj.build();
   }

   //
   //--private methods
   //
   
   private void newProject() {
      ProjectActions projNew 
            = projFact.getProjAct(FileUtils.extension(docSel.filepath()));
      if (projNew == null) {
         JOptions.titledInfoMessage(
               "A project cannot be set for this file type", "Note");
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

   private void configureProject(ProjectActions projToConf) {
      if (projToConf.configureProject(docSel.dir())) {
         if (proj != projToConf) {
            proj = projToConf;
            recent.add(proj);
         }
         updateProjectSetting(proj);
      }
   }
   
   private boolean changeProject(ProjectActions toChangeTo) {     
      int result = JOptions.confirmYesNo("Change to project '"
                     + toChangeTo.getProjectName() + "'");
      if (result == 0) {
         proj = toChangeTo;
         updateProjectSetting(proj);
         return true;
      }
      else {
         return false;
      }
   }

   private ProjectActions searchRecent(String dir) {
      ProjectActions old = null;
      for (ProjectActions p : recent) {
         if (p.isProjectInPath(dir)) {
            old = p;
         }
      }
      return old;
   }

   private void updateProjectSetting(ProjectActions projToSet) {
      mw.showProjectInfo(projToSet.getProjectName());
      projToSet.applyProject();
      enableActions();
   }
   
   private void enableActions() {
      String ext = FileUtils.extension(docSel.filepath());
      switch (ext) {
         case ".java":
            enableActions(true, true, true);
            menu.getProjectMenu().setBuildKind("Create jar");
            extension = ".java";
            break;
         case ".html":
            enableActions(false, true, false);
            break;
         case ".txt":
            enableActions(false, false, false);
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