package eg.projects;

import eg.ui.ProjectStateUpdate;

/**
 * The changes that are necessary when a project of a given type
 * is set active
 */
public class ProjectTypeChange {

   private final ProjectStateUpdate update;

   /**
    * @param update  the {@link ProjectStateUpdate}
    */
   public ProjectTypeChange(ProjectStateUpdate update) {
      this.update = update;
   }

   /**
    * Enables/disables menu items and buttons for project actions
    *
    * @param projType  the project type
    */
   public void enableProjectActions(ProjectTypes projType) {
      switch (projType) {
         case GENERIC:
            update.enableProjectActions(false, false, false);
            break;
         case JAVA:
            update.enableProjectActions(true, true, true);
            break;
         case HTML:
            update.enableProjectActions(false, true, false);
            break;
         case PERL:
            update.enableProjectActions(false, true, false);
            break;
         case R:
            update.enableProjectActions(false, true, false);
      }
   }

   /**
    * Sets the label for the menu item for building actions
    *
    * @param projType  the project type
    */
   public void setBuildLabel(ProjectTypes projType) {
      switch (projType) {
         case JAVA:
            update.setBuildLabel("Create jar");
            break;
         default:
            update.setBuildLabel("Build");
      }
   }
}
