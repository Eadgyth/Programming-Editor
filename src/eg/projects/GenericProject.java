package eg.projects;

import eg.ui.ProjectActionsUpdate;

/**
 * Represents a project that is only defined by its directory
 */
public final class GenericProject extends AbstractProject implements ProjectActions {

   public GenericProject() {
      super(ProjectTypes.GENERIC, false, null);
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.buildWindow();
   }

   @Override
   public void enableActions(ProjectActionsUpdate update) {
      update.enable(false, false, false, null);
   }

   /**
    * Not implemented
    */
   @Override
   protected void setCommandParameters() {}
}
