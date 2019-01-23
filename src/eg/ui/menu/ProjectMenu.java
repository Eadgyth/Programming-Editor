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
 * The menu for project actions
 */
public class ProjectMenu {

   private final JMenu menu = new JMenu("Project");
   private final JMenuItem saveCompileItm = new JMenuItem(
         "Save selected project file and compile project");

   private final JMenuItem saveAllCompileItm = new JMenuItem(
         "Save all open project files and compile project",
         IconFiles.COMPILE_ICON);

   private final JMenuItem runItm = new JMenuItem("Run project",
         IconFiles.RUN_ICON);

   private final JMenuItem buildItm        = new JMenuItem("Build");
   private final JMenu assignProjMenu      = new JMenu("Assign as project");
   private final JMenuItem[] assignProjItm
         = new JMenuItem[ProjectTypes.values().length];

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
      changeProjItm.addActionListener(e -> p.change());
      runItm.addActionListener(e -> p.run());
      buildItm.addActionListener(e -> p.build());
      saveCompileItm.addActionListener(e -> p.saveAndCompile());
      saveAllCompileItm.addActionListener(e -> p.saveAllAndCompile());
   }

   /**
    * Enables or disables the item for actions to change project
    *
    * @param b  true to enable, false to disable
    */
   public void enableChangeProjItm(boolean b) {
      changeProjItm.setEnabled(b);
   }

   /**
    * Enables or disables the item for actions to open the project
    * settings window
    *
    * @param b  true to enable, false to disable
    */
   public void enableOpenSetWinItm(boolean b) {
      openSetWinItm.setEnabled(b);
   }

   /**
    * Enables or disables the items for actions to compile, run and
    * build a project. The specified booleans each are true to enable,
    * false to disable
    *
    * @param isCompile  the boolean for compile actions
    * @param isRun  the boolean for run actions
    * @param isBuild  the boolean for build actions
    */
   public void enableProjectActionsItms(boolean isCompile, boolean isRun,
         boolean isBuild) {

      saveCompileItm.setEnabled(isCompile);
      saveAllCompileItm.setEnabled(isCompile);
      runItm.setEnabled(isRun);
      buildItm.setEnabled(isBuild);
   }

   /**
    * Enables or disables the sub-menu for actions to assign a project
    *
    * @param b  true to enable, false  to disable
    */
   public void enableAssignProjMenu(boolean b) {
      assignProjMenu.setEnabled(b);
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
            p.assign(projType);
         }
      }
   }

   private void assembleMenu() {
      menu.add(saveCompileItm);
      menu.add(saveAllCompileItm);
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
      assignProjMenu.setEnabled(false);
      openSetWinItm.setEnabled(false);
      changeProjItm.setEnabled(false);
      enableProjectActionsItms(false, false, false);
   }

   private void shortCuts() {
      saveAllCompileItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
            ActionEvent.CTRL_MASK));

      runItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
            ActionEvent.CTRL_MASK));
   }
}
