package eg.ui.menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.CurrentProject;

import eg.ui.IconFiles;

/**
 * The menu for project actions.
 * <p> Created in {@link MenuBar}
 */
public class ProjectMenu {

   private final JMenu     menu           = new JMenu("Project");
   private final JMenuItem SaveCompile    = new JMenuItem(
         "Save selected source file and compile");

   private final JMenuItem SaveAllCompile = new JMenuItem(
         "Save all source files and compile", IconFiles.COMPILE_ICON);

   private final JMenuItem run            = new JMenuItem("Run", IconFiles.RUN_ICON);
   private final JMenuItem build          = new JMenuItem("Build");
   private final JMenuItem newProj        = new JMenuItem("Assign as project");
   private final JMenuItem setProject     = new JMenuItem("Project settings");
   private final JMenuItem changeProj     = new JMenuItem(
         "Change project", IconFiles.CHANGE_PROJ_ICON);

   public ProjectMenu() {
      assembleMenu();
      shortCuts();
   }

   /**
    * Gets this menu
    *
    * @return  the menu
    */
   public JMenu getMenu() {
      return menu;
   }

   /**
    * Sets listeners for actions defined in <code>CurrentProject</code>
    *
    * @param currProj  the reference to {@link CurrentProject}
    */
   public void setActions(CurrentProject currProj) {
      setProject.addActionListener(e -> currProj.openSettingsWindow());
      changeProj.addActionListener(e -> currProj.changeProject());
      newProj.addActionListener(e -> currProj.assignProject());
      run.addActionListener(e -> currProj.runProj());
      build.addActionListener(e -> currProj.buildProj());
      SaveCompile.addActionListener(e -> currProj.saveAndCompile());
      SaveAllCompile.addActionListener(e -> currProj.saveAllAndCompile());
   }

   /**
    * Sets the boolean that indicates if actions to change project are
    * enabled
    *
    * @param b  the boolean value
    */
   public void enableChangeProjItm(boolean b) {
      changeProj.setEnabled(b);
   }

   /**
    * Sets the booleans that specify if the items for actions to
    * compile, run and build a project are enabled or disabled
    *
    * @param isCompile  the boolean value for compile actions
    * @param isRun  the boolean value for run actions
    * @param isBuild  the boolean value for build actions
    */
   public void enableSrcCodeActionItms(boolean isCompile, boolean isRun,
         boolean isBuild) {

      SaveCompile.setEnabled(isCompile);
      SaveAllCompile.setEnabled(isCompile);
      run.setEnabled(isRun);
      build.setEnabled(isBuild);
   }

   /**
    * Sets the specified label for the item for building actions
    *
    * @param label  the label
    */
   public void setBuildLabel(String label) {
      build.setText(label);
   }

   //
   //--private--/
   //

   private void assembleMenu() {
      menu.add(SaveCompile);
      menu.add(SaveAllCompile);
      menu.add(run);
      menu.addSeparator();
      menu.add(build);
      menu.addSeparator();
      menu.add(newProj);
      menu.add(setProject);
      menu.add(changeProj);
      menu.setMnemonic(KeyEvent.VK_P);
      changeProj.setEnabled(false);
      enableSrcCodeActionItms(false, false, false);
   }

   private void shortCuts() {
      SaveAllCompile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
            ActionEvent.CTRL_MASK));
      run.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
            ActionEvent.CTRL_MASK));
   }
}
