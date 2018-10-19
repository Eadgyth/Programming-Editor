package eg.projects;

import eg.ui.ProjectStateUpdate;

/**
 * The changes that are necessary when a project of a given type is set active+
 */
public class ProjectTypeChange {

   private final ProjectStateUpdate update;

   /**
    * @param update  the reference to {@link ProjectStateUpdate}
    */
   public ProjectTypeChange(ProjectStateUpdate update) {
      this.update = update;
   }

   /**
    * Enables/disables menu items and buttons for project actions.<br>
    *
    * @param projType  the project type which has a valaue in
    * {@link ProjectTypes}
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
    * Sets the label for the menu item for building actions.<br>
    *
    * @param projType  the project type which has a valaue in {@link ProjectTypes}
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
