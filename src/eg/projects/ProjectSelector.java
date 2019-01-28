package eg.projects;

import eg.console.Console;
import eg.ui.ConsoleOpener;

/**
 * The selection and creation of a <code>ProjectActions</code>
 * based on the project type
 */
public class ProjectSelector {

   private final Console console;
   private final ConsoleOpener opener;
   private final Runnable fileTreeUpdate;

   /**
    * @param console  the Console
    * @param opener  the ConsoleOpener
    * @param fileTreeUpdate  the updating of the file tree
    */
   public ProjectSelector(Console console, ConsoleOpener opener,
         Runnable fileTreeUpdate) {

      this.console = console;
      this.opener = opener;
      this.fileTreeUpdate = fileTreeUpdate;
   }

   /**
    * Returns a new <code>ProjectActions</code>
    *
    * @param projType  the project type
    * @return  the ProjectActions
    */
   public ProjectActions createProject(ProjectTypes projType) {
      ProjectActions newProj = null;
      switch (projType) {
         case JAVA:
            newProj = new JavaProject(console, opener, fileTreeUpdate);
            break;
         case HTML:
            newProj = new HtmlProject();
            break;
         case PERL:
            newProj = new PerlProject(console.processStarter(), opener);
            break;
         case PYTHON:
            newProj = new PythonProject(console.processStarter(), opener);
            break;
         case R:
            newProj = new RProject(console.processStarter(), opener);
            break;
         case GENERIC:
            newProj = new GenericProject();
            break;
      }
      return newProj;
   }
}
