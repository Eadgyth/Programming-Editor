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

import eg.document.FileDocument;

import eg.utils.Dialogs;
import eg.utils.FileUtils;

/**
 * The configuration and execution of actions of projects.
 * <p>
 * A project is represented by an object of type {@link ProjectActions}
 * and is configured and assigned to this current project when the
 * {@link FileDocument} that is selected at the time is part of that
 * project.<br>
 * Several projects can be configured and would be maintained. Any of
 * these can be (re-)assigned to this current project if the selected
 * <code>FileDocument</code> is part of it.
 */
public class CurrentProject {

   private final String NO_FILE_IN_TAB_MESSAGE
         = "A project can be assigned if an opened or newly"
         + " saved file is selected.";

   private final String FILES_NOT_FOUND_MESSAGE
         = "The following files could not be found anymore:";

   private final static String[] PROJ_SUFFIXES = {
      "htm", "html", "java", "pl"
   };

   private final MainWin mw;
   private final SelectedProject selProj;
   private final ProcessStarter proc;
   private final List<ProjectActions> projList = new ArrayList<>();

   private ProjectActions current;
   private FileDocument[] fDoc;
   private int iCurr;
   private String docSuffix;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param fDoc  the array of {@link FileDocument}
    */
   public CurrentProject(MainWin mw, FileDocument[] fDoc) {
      this.mw = mw;
      this.fDoc = fDoc;
      proc = new ProcessStarter(mw.console());
      selProj = new SelectedProject(mw, proc, mw.console());
   }

   /**
    * Selects an element from this array of <code>FileDocument</code>
    *
    * @param i  the index of the array element
    */
   public void setFileDocumentAt(int i) {
      iCurr = i;
      docSuffix = FileUtils.fileSuffix(fDoc[iCurr].filename());
      ProjectActions inList = selectFromList(fDoc[iCurr].dir(), true);
      mw.enableChangeProject(inList != null);
      if (current != null) {
         if (!current.isInProject(fDoc[iCurr].dir())) {
            mw.enableSrcCodeActions(false, false, false);
         }
         else {
            enableActions(current);
         }
      }
   }

   /**
    * Tries to retrieve a project whose configuration is saved in the
    * 'eadconfig' file in the project's directory or, if not existent,
    * in the program's prefs file.
    * @see eg.projects.ProjectConfig#retrieveProject(String)
    */
   public void retrieveProject() {
      if (current != null && current.isInProject(fDoc[iCurr].dir())) {
         return;
      }
      EventQueue.invokeLater(() -> {
         ProjectActions projToFind = selProj.createProject(docSuffix);
         boolean isFound = projToFind != null
               && projToFind.retrieveProject(fDoc[iCurr].dir());
         if (projToFind == null) {
            for (String opt : PROJ_SUFFIXES) {
               projToFind = selProj.createProject(opt);
               isFound = projToFind != null
                     && projToFind.retrieveProject(fDoc[iCurr].dir());
               if (isFound) {
                  break;
               }
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
               if (selectFromList(fDoc[iCurr].dir(), true) == null) {
                  projFin.setConfiguringAction(e -> configureProject(projFin));
                  projList.add(projFin);
                  changeProject(projFin);
               }
            }
         }
      });
   }

   /**
    * Opens the window of the <code>SettingsWin</code> object that belongs
    * to a project.
    * <p>Depending on the currently set <code>FileDocument</code> the opened
    * window belongs to the current project, to one of this listed projects
    * or to a project that is newly assigned.
    */
   public void openSettingsWindow() {
      ProjectActions fromList = selectFromList(fDoc[iCurr].dir(), false);
      if (fromList == null) {
         int res = Dialogs.confirmYesNo("Set new project?");
         if (0 == res) {
            createProjectImpl();
         }
      }
      else {
         if (fromList == current) {
            current.makeSetWinVisible();
         }
         else {
            if (changeProject(fromList)) {
               current.makeSetWinVisible();
            }
         }
      }
   }

   /**
    * Assignes a new project.
    * <br>If the the currently set <code>FileDocument</code> belongs to a
    * project in the List of configured projects a confirmation dialog is
    * shown.
    */
   public void createProject() {
      ProjectActions fromList = selectFromList(fDoc[iCurr].dir(), false);
      if (fromList == null) {
         createProjectImpl();
      }
      else {
         confirmedNewProject(fromList);
      }
   }

   /**
    * Sets active the project from this List of configured projects
    * which the currently selected <code>FileDocument</code> belongs to
    */
   public void changeProject() {
      ProjectActions fromList = selectFromList(fDoc[iCurr].dir(), true);
      changeProject(fromList);
   }

   /**
    * Updates the file tree if the selected <code>FileDocument</code>
    * belongs to this current project
    */
   public void updateFileTree() {
      if (current != null && current.isInProject(fDoc[iCurr].dir())) {
         EventQueue.invokeLater(() -> mw.fileTree().updateTree());
      }
   }

   /**
    * Saves the source file of the selected <code>TextDocument</code>
    * if it belongs to this current project and compiles the source
    * files of the project
    */
   public void saveAndCompile() {
      try {
        mw.setBusyCursor();
        if (isFileToCompile(fDoc[iCurr])) {
            if (fDoc[iCurr].docFile().exists()) {
               fDoc[iCurr].saveFile();
               current.compile();
               updateFileTree();
            }
            else {
               Dialogs.warnMessage(
                     fDoc[iCurr].filename()
                     + ":\nThe file could not be found anymore");
            }
         }
      }
      finally {
         EventQueue.invokeLater(() ->  mw.setDefaultCursor());
      }
   }

   /**
    * Saves all open source files of this current project and compiles
    * the files
    */
   public void saveAllAndCompile() {
      try {
         mw.setBusyCursor();
         StringBuilder missingFiles = new StringBuilder();
         for (int i = 0; i < fDoc.length; i++) {
            if (isFileToCompile(fDoc[i])) {
               if (fDoc[i].docFile().exists()) {
                  fDoc[i].saveFile();
               }
               else {
                  missingFiles.append("\n");
                  missingFiles.append(fDoc[i].filename());
               }
            }
         }
         if (missingFiles.length() == 0) {
            current.compile();
            updateFileTree();
         }
         else {
            Dialogs.warnMessage(FILES_NOT_FOUND_MESSAGE + missingFiles);
         }
      }
      finally {
         EventQueue.invokeLater(() -> mw.setDefaultCursor());
      }
   }

   /**
    * Runs this current project
    */
   public void runProj() {
      current.runProject();
   }

   /**
    * Creates a build of this current project
    */
   public void buildProj() {
      try {
         mw.setBusyCursor();
         current.build();
         updateFileTree();
      }
      finally {
         EventQueue.invokeLater(() -> mw.setDefaultCursor());
      }
   }

   //
   //--private--/
   //

   private void createProjectImpl() {
      if (!fDoc[iCurr].hasFile()) {
         Dialogs.infoMessage(NO_FILE_IN_TAB_MESSAGE, "Note");
         return;
      }
      ProjectActions projNew = selProj.createProject(docSuffix);
      if (projNew == null) {
         projNew = selectBySuffix();
      }
      if (projNew != null) {
         ProjectActions projFin = projNew;
         projFin.makeSetWinVisible();
         projFin.setConfiguringAction(e -> configureProject(projFin));
      }
   }
   
   private ProjectActions selectBySuffix() {
      String selectedSuffix
            = Dialogs.comboBoxOpt(wrongExtentionMessage(fDoc[iCurr].filename()),
            "File extension", PROJ_SUFFIXES, null, true);
      if (selectedSuffix != null) {
         return selProj.createProject(selectedSuffix);
      }
      else {
         return null;
      }
   }

   private boolean changeProject(ProjectActions toChangeTo) {
      int result = Dialogs.confirmYesNo("Switch to project '"
                 + toChangeTo.getProjectName() + "'?");
      if (result == 0) {
         current = toChangeTo;
         current.storeConfiguration();
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
      if (projToConf.configureProject(fDoc[iCurr].dir())) {
         current = projToConf;
         current.storeConfiguration();
         projList.add(current);
         updateProjectSetting(current);
         updateFileTree();
      }      
   }

   private boolean isFileToCompile(FileDocument fd) {
       return fd != null
             && fd.filename().endsWith(current.getSourceSuffix())
             && current.isInProject(fd.dir());
   }

   private void confirmedNewProject(ProjectActions toConfirm) {
      int res = Dialogs.confirmYesNo(
            "<html>"
            + fDoc[iCurr].filename() + " belongs to the project"
            + " <i>" + toConfirm.getProjectName() + "</i>.<br>"
            + "Still set new project?"
            + "</html>");

      if (0 == res) {
         createProjectImpl();
      }
   }
 
   private String wrongExtentionMessage(String filename) {
      return
         "<html>"
         + filename + " cannot define a project category."
         + "<br><br>"
         + "If the file belongs to a project select the extension of the"
         + " <i>existing</i> main source file in this project."
         + "</html>";
   }

   private void updateProjectSetting(ProjectActions projToSet) {
      proc.setWorkingDir(projToSet.getProjectPath());
      enableActions(projToSet);
      setBuildLabel(projToSet);
      mw.displayProjectName(projToSet.getProjectName());
      mw.fileTree().setDeletableDirName(projToSet.getExecutableDirName());
      mw.fileTree().setProjectTree(projToSet.getProjectPath());
      if (projList.size() == 1) {
         mw.enableOpenFileView();
      }
   }

   private void enableActions(ProjectActions projToSet) {
      switch (className(projToSet)) {
         case "JavaActions":
            mw.enableSrcCodeActions(true, true, true);
            break;
         case "HtmlActions":
            mw.enableSrcCodeActions(false, true, false);
            break;
         case "PerlActions":
            mw.enableSrcCodeActions(false, true, false);
            break;
      }
   }
   
   private void setBuildLabel(ProjectActions projToSet) {
      switch (className(projToSet)) {
         case "JavaActions":
            mw.setBuildLabel("Create jar");
            break;
         default:
            mw.setBuildLabel("Build");
      }
   }
   
   private String className(ProjectActions projToSet) {
      return projToSet.getClass().getSimpleName();
   }
}
