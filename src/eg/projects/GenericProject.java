package eg.projects;

import eg.ui.ProjectActionsControl;

/**
 * Represents a project that is only defined by its directory
 */
public final class GenericProject extends AbstractProject implements ProjectActions {

   private final ProjectActionsControl update;

   public GenericProject(ProjectActionsControl update) {
      super(ProjectTypes.GENERIC, false, null);
      this.update = update;
   }

   @Override
   public void buildSettingsWindow() {
      inputOptions.buildWindow();
   }

   @Override
   public void enableActions() {
      update.disable();
   }

   /**
    * Not implemented
    */
   @Override
   protected void setCommandParameters() {}
}
