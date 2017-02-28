package eg.ui.menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.CurrentProject;

import eg.ui.IconFiles;

public class ProjectMenu {
   
   private final JMenu     menu         = new JMenu("Project");
   private final JMenuItem compile      = new JMenuItem("Save open files and compile",
                                          IconFiles.COMPILE_ICON);
   private final JMenuItem run          = new JMenuItem("Run", IconFiles.RUN_ICON);
   private final JMenuItem build        = new JMenuItem("Build");
   private final JMenuItem setProject   = new JMenuItem("Project settings");
   private final JMenuItem changeProj   = new JMenuItem("Change project");
   private final JMenuItem setNewProj   = new JMenuItem("New project settings");
   
   ProjectMenu() {
      assembleMenu();
      shortCuts();
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   public void registerAct(CurrentProject currProj) {
      setProject.addActionListener(e -> currProj.openSettingsWindow());
      changeProj.addActionListener(e -> currProj.changeProject());
      setNewProj.addActionListener(e -> currProj.createNewProject());
      run.addActionListener(e -> currProj.runProj());
      build.addActionListener(e -> currProj.buildProj());
      compile.addActionListener(e -> currProj.compile());
   }
   
   public void enableChangeProjItm() {
      changeProj.setEnabled(true);
   }
   
   public void enableSetNewProjItm() {
      setNewProj.setEnabled(true);
   }

   public void enableProjItms(boolean isCompile, boolean isRun, boolean isBuild) {
      compile.setEnabled(isCompile);
      run.setEnabled(isRun);
      build.setEnabled(isBuild);
   }
   
   public void setBuildKind(String kind) {
      build.setText(kind);
   }

   private void assembleMenu() {
      menu.add(compile);
      compile.setEnabled(false);
      menu.add(run);
      run.setEnabled(false);
      menu.addSeparator();
      menu.add(build);
      build.setEnabled(false);
      menu.addSeparator();
      menu.add(setProject);
      menu.add(changeProj);
      menu.add(setNewProj);
      changeProj.setEnabled(false);
      setNewProj.setEnabled(false);
      menu.setMnemonic(KeyEvent.VK_P);
   }
   
   private void shortCuts() {
      compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
            ActionEvent.CTRL_MASK));
      run.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
            ActionEvent.CTRL_MASK));
   }
}
      
