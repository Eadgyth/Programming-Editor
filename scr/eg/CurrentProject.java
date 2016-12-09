package eg;

import java.io.File;

import java.util.List;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import java.awt.EventQueue;

import java.lang.reflect.InvocationTargetException;

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
 * The current project that is configured for the file of a
 * {@code TextDocument}.
 * <p>
 * A project is represented by an object of type {@link ProjectActions}.
 * <p>
 * A successfully configured project is assigned to this current
 * project and also added to this list of projects. By this any of
 * the listed projects are re-assigned to the current project if the
 * currently set {@link TextDocument} belongs to it.
 * <p>
 * See also {@link eg.projects.ProjectConfig}
 */
public class CurrentProject {

   private final ProjectFactory projFact;
   private final MainWin mw;
   private final FileTree fileTree;
   private final Menu menu;
   private final Toolbar tBar;
   
   private final List<ProjectActions> recent = new ArrayList<>();
   private ProjectActions proj;
   private TextDocument txtDoc;

   public CurrentProject(ProjectFactory projFact, MainWin mw,
         FileTree fileTree, Menu menu, Toolbar tBar) {
      this.projFact = projFact;
      this.mw = mw;
      this.fileTree = fileTree;
      this.menu = menu;
      this.tBar = tBar;
   }

   /**
    * Sets in this the TextDocument which a project is configured for
    * @param txtDoc  an object of {@link TextDocument}
    */
   public void setTextDocument(TextDocument txtDoc) {
      this.txtDoc = txtDoc;
   }

   /**
    * If at least one project has been created
    */
   public boolean isProjectSet() {
      return proj != null;
   }
   
   /**
    * If the currently set {@code TextDocument} is part of the current
    * project
    * @param dir  the directory that may include the project's root
    * directory 
    * @return if the currently set {@TextDocument} is part of the current
    * project. False if no project has been assigned
    */
   public boolean isInProjectPath(String dir) {
      return isProjectSet() && proj.isInProjectPath(dir);
   }

   /**
    * Tries to assign to this the project that was active when the program
    * was closed the last time.
    * <p>
    * Method requires that (i) no other project has been assigned before
    * during the lifetime of the program and (ii) the currently set
    * {@link TextDocument} is part of last project
    */
   public void retrieveLastProject() {
      if (!isProjectSet()) {
         ProjectActions prPrevious
               = projFact.getProjAct(FileUtils.extension(txtDoc.filepath()));
         if (prPrevious != null) {
            if (prPrevious.retrieveLastProject(txtDoc.dir())) {        
               prPrevious.addOkAction(e -> configureProject(prPrevious));
               recent.add(prPrevious);
               proj = prPrevious;
               updateProjectSetting(proj);
            }
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
         if (txtDoc.filename().length() == 0) {
            JOptions.titledInfoMessage("A project can be set after a file was"
                  + " opened or a new file was saved", "Note");
         }
         else {
            newProject();
         }
      }
      else {  
         if (txtDoc.filename().length() == 0
               || proj.isInProjectPath(txtDoc.dir())) {
            proj.makeSetWinVisible(true);
         }      
         else if (txtDoc.filename().length() > 0
               & !proj.isInProjectPath(txtDoc.dir())) {
            ProjectActions recent = searchRecent(txtDoc.dir());
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
    * Assigns to this current project a project that was previously
    * set during the program's liftime.
    * <p>
    * If the currently set {@code TextDocument} bolongs to a previous
    * project this project is set active.
    * If it does not belong to a project set before the settings window
    * is opened (same effect as {@link #openSettingsWindow()}
    */
   public void changeProject() {
      ProjectActions recent = searchRecent(txtDoc.dir());
      if (recent == proj) {
         JOptions.infoMessage("The selected file belongs to the"
               + " currently active project");
      }
      else {
         if (recent != null) {
            changeProject(recent);
         }
         else {
            openSettingsWindow();
         }   
      }
   } 

   /**
    * Adds a file to the file tree if the file belongs
    * to this current project
    * @param dir  the directory that include the project's
    * directory
    * 
    */
   public void updateFileTree(String dir) {
      if (isInProjectPath(dir)) {
         fileTree.updateTree();
      }
   }

   /**
    * compiles this project
    */
   public void compile() {
      try {
         mw.setCursor(MainWin.BUSY_CURSOR);
         proj.compile();
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
   
   /**
    * Stores the settings of this project to prefs
    */
   public void storeConfig() {
      if (isProjectSet()) {
         proj.storeConfig();
      }
   }

   //
   //--private methods
   //
   
   private void newProject() {
      ProjectActions projNew 
            = projFact.getProjAct(FileUtils.extension(txtDoc.filepath()));
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
      if (projToConf.configFromSetWin(txtDoc.dir(),
            FileUtils.extension(txtDoc.filename()))) {
         if (proj != projToConf) {
            proj = projToConf;
            recent.add(proj);
         }
         updateProjectSetting(proj);
      }
   }
   
   private boolean changeProject(ProjectActions recent) {     
      int result = JOptions.confirmYesNo("Change to project '"
            + recent.getProjectName() + "' ?");
      if (result == 0) {
         proj = recent;
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
         if (p.isInProjectPath(dir)) {
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
      String ext = FileUtils.extension(txtDoc.filepath());
      switch (ext) {
         case ".java":
            enableActions(true, true, true);
            menu.getProjectMenu().setBuildKind("Create jar");
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