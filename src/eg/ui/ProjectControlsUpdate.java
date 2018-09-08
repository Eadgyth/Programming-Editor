package eg.ui;

/**
 * Interface to udate menu items and buttons for project actions
 */
public interface ProjectControlsUpdate {
   
   /**
    * Sets the booleans that specify if actions to compile, run and
    * build a project are enabled (true) or disabled.
    *
    * @param isCompile  the boolean value for compile actions
    * @param isRun  the boolean value for run actions
    * @param isBuild  the boolean value for build actions
    */
   public void enableProjectActions(boolean isCompile, boolean isRun,
         boolean isBuild);
         
   /**
    * Sets the specified label for the menu item for building actions
    *
    * @param label  the label
    */
   public void setBuildLabel(String label);
}
