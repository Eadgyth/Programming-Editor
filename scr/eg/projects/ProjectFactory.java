package eg.projects;

import eg.console.*;

import eg.ui.ViewSettings;
import eg.ui.filetree.FileTree;

/**
 * The creation of objects of the classes that represent a project. These
 * classes are of type {@link ProjectActions}
 */
public class ProjectFactory {
   
   private final ViewSettings viewSet;
   private final ProcessStarter proc;
   private final ConsolePanel cw;
   private final FileTree fileTree;

   public ProjectFactory(ViewSettings viewSet, ProcessStarter proc, ConsolePanel cw,
         FileTree fileTree) {
      this.viewSet = viewSet;
      this.proc = proc;
      this.cw = cw;
      this.fileTree = fileTree;
   }

   /**
    * Returns an object of type {@code ProjectActions}
    * @param extension  the extension of the file which a project
    * is to be defined for (has the form ".java", for instance)
    * @return  an object of type {@link ProjectActions} or null if no
    * class that implement ProjectActions exists for the given file
    * extension.
    */
   public ProjectActions getProjAct(String extension) {
      ProjectActions newProj = null;
      switch (extension) {
         case ".java":
            newProj = new JavaActions(viewSet, proc, cw, fileTree);
            break;
         case ".html":
            newProj = new HtmlActions(proc, fileTree);
            break;
         case ".txt":
            newProj = new TxtActions(proc, fileTree);
            break;
      }
      return newProj;
   }
}