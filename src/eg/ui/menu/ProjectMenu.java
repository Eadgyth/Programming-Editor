package eg.ui.menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.Projects;
import eg.projects.ProjectTypes;

import eg.ui.IconFiles;

/**
 * The menu for project actions.
 * <p>
 * Created in {@link MenuBar}
 */
public class ProjectMenu {

   private final JMenu menu = new JMenu("Project");
   private final JMenuItem SaveCompileItm = new JMenuItem(
         "Save selected file and compile project");

   private final JMenuItem SaveAllCompileItm = new JMenuItem(
         "Save all open project files and compile project",
         IconFiles.COMPILE_ICON);

   private final JMenuItem runItm = new JMenuItem("Run", IconFiles.RUN_ICON);

   private final JMenuItem buildItm        = new JMenuItem("Build");
   private final JMenu assignProjMenu      = new JMenu("Assign as project by category...");
   private final JMenuItem[] assignProjItm = new JMenuItem[ProjectTypes.values().length];
   private final JMenuItem openSetWinItm   = new JMenuItem("Project settings");
   private final JMenuItem changeProjItm   = new JMenuItem("Change project",
         IconFiles.CHANGE_PROJ_ICON);

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
    * Sets listeners for actions defined in <code>Projects</code>
    *
    * @param p  the reference to {@link Projects}
    */
   public void setActions(Projects p) {
      for (JMenuItem itm : assignProjItm) {
         itm.addActionListener(e -> assignProject(e, p));
      }
      openSetWinItm.addActionListener(e -> p.openSettingsWindow());
      changeProjItm.addActionListener(e -> p.changeProject());
      runItm.addActionListener(e -> p.runProj());
      buildItm.addActionListener(e -> p.buildProj());
      SaveCompileItm.addActionListener(e -> p.saveAndCompile());
      SaveAllCompileItm.addActionListener(e -> p.saveAllAndCompile());
   }

   /**
    * Sets the boolean that indicates if the item for actions to change
    * project are enabled
    *
    * @param b  the boolean value
    */
   public void enableChangeProjItm(boolean b) {
      changeProjItm.setEnabled(b);
   }
   
   /**
    * Sets the boolean that indicates if the item for actions to open the
    *  project settings window are enabled
    *
    * @param b  the boolean value
    */
   public void enableOpenSetWinItm(boolean b) {
      openSetWinItm.setEnabled(b);
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

      SaveCompileItm.setEnabled(isCompile);
      SaveAllCompileItm.setEnabled(isCompile);
      runItm.setEnabled(isRun);
      buildItm.setEnabled(isBuild);
   }

   /**
    * Sets the label for the item for building actions
    *
    * @param label  the label
    */
   public void setBuildLabel(String label) {
      buildItm.setText(label);
   }

   //
   //--private--/
   //
   
   private void assignProject(ActionEvent e, Projects p) {
      for (int i = 0; i < assignProjItm.length; i++) {
         if (e.getSource() == assignProjItm[i]) {
            ProjectTypes projType = ProjectTypes.values()[i];
            p.assignProject(projType);
         }
      }
   }

   private void assembleMenu() {
      menu.add(SaveCompileItm);
      menu.add(SaveAllCompileItm);
      menu.add(runItm);
      menu.addSeparator();
      menu.add(buildItm);
      menu.addSeparator();
      menu.add(assignProjMenu);
      for (int i = 0; i < assignProjItm.length; i++) {
         assignProjItm[i] = new JMenuItem(ProjectTypes.values()[i].display());
         assignProjMenu.add(assignProjItm[i]);
      }
      menu.add(openSetWinItm);
      menu.add(changeProjItm);
      menu.setMnemonic(KeyEvent.VK_P);
      openSetWinItm.setEnabled(false);
      changeProjItm.setEnabled(false);
      enableSrcCodeActionItms(false, false, false);
   }

   private void shortCuts() {
      SaveAllCompileItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
            ActionEvent.CTRL_MASK));
      runItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
            ActionEvent.CTRL_MASK));
   }
}
