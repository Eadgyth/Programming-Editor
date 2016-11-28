package eg.projects;

import eg.console.*;
import eg.ui.MainWin;
import eg.ui.menu.Menu;
import eg.ui.Toolbar;

/**
 * The creation of objects of the classes that represent a project. These
 * classes are of type {@link ProjectActions}
 */
public class ProjectFactory {

   private final MainWin mw;
   private final ProcessStarter proc;
   private final ConsolePanel cw;

   public ProjectFactory(MainWin mw, ProcessStarter proc, ConsolePanel cw) {
      this.mw = mw;
      this.proc = proc;
      this.cw = cw;
   }

   /**
    * Returns an object of a class that implements the ProjectActions
    * interface and that is selected based on the extension of the
    * file that belongs to the project.
    * <p>
    * This method has to be modified if new project actions (languages,
    * file types) are implemented.
    * @param extension  the extension of the file for which a project
    * is to be defined (has the form ".java", for instance)
    * @return  an object of type {@link ProjectActions} or null if no
    * ProjectActions exists fot the given file type.
    */
   public ProjectActions getProjAct(String extension) {
      ProjectActions newProj = null;
      switch (extension) {
         case ".java":
            newProj = new JavaActions(mw, proc, cw);
            break;
         case ".html":
            newProj = new HtmlActions();
            break;
         case ".txt":
            newProj = new TxtActions();
            break;
      }
      return newProj;
   }
}