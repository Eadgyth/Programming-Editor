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

   private final JMenu     menu       = new JMenu("Project");
   private final JMenuItem SaveCompile
         = new JMenuItem("Save selected source file and compile");
   private final JMenuItem SaveAllCompile
         = new JMenuItem("Save all source files and compile",
         IconFiles.COMPILE_ICON);
   private final JMenuItem run        = new JMenuItem("Run", IconFiles.RUN_ICON);
   private final JMenuItem build      = new JMenuItem("Build");
   private final JMenuItem newProj    = new JMenuItem("Assign as project");
   private final JMenuItem setProject = new JMenuItem("Project settings");
   private final JMenuItem changeProj
         = new JMenuItem("Change project", IconFiles.CHANGE_PROJ_ICON);

   public ProjectMenu() {
      assembleMenu();
      shortCuts();
   }

   public JMenu getMenu() {
      return menu;
   }

   public void setActions(CurrentProject currProj) {
      setProject.addActionListener(e -> currProj.openSettingsWindow());
      changeProj.addActionListener(e -> currProj.changeProject());
      newProj.addActionListener(e -> currProj.createProject());
      run.addActionListener(e -> currProj.runProj());
      build.addActionListener(e -> currProj.buildProj());
      SaveCompile.addActionListener(e -> currProj.saveAndCompile());
      SaveAllCompile.addActionListener(e -> currProj.saveAllAndCompile());
   }

   public void enableChangeProjItm(boolean isEnabled) {
      changeProj.setEnabled(isEnabled);
   }

   public void enableSrcCodeActionItms(boolean isCompile, boolean isRun,
         boolean isBuild) {

      SaveCompile.setEnabled(isCompile);
      SaveAllCompile.setEnabled(isCompile);
      run.setEnabled(isRun);
      build.setEnabled(isBuild);
   }

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
