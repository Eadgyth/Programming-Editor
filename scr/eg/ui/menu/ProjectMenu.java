package eg.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.CurrentProject;
import eg.TabbedFiles;

import eg.ui.IconFiles;

public class ProjectMenu {
   
   private final JMenu     menu       = new JMenu("Project");
   private final JMenuItem compile    = new JMenuItem("Save selected file and compile",
                                        IconFiles.compileIcon);
   private final JMenuItem run        = new JMenuItem("Run", IconFiles.runIcon);
   private final JMenuItem build      = new JMenuItem("Build");
   private final JMenuItem setProject = new JMenuItem("Project settings");
   private final JMenuItem changeProj = new JMenuItem("Change project");
   
   ProjectMenu() {
      assembleMenu();
      shortCuts();
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   public void registerAct(CurrentProject currProj, TabbedFiles tf) {
      changeProj.addActionListener(e -> currProj.changeProject());
      run.addActionListener(e -> currProj.runProj());
      setProject.addActionListener(e -> currProj.openSettingsWindow());
      build.addActionListener(e -> currProj.buildProj());
      compile.addActionListener(e -> tf.saveAndCompile());
   }
   
   public void enableChangeProjItm() {
      changeProj.setEnabled(true);
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
      changeProj.setEnabled(false);
   }
   
   private void shortCuts() {
      compile.setAccelerator(KeyStroke.getKeyStroke("control K"));
      run.setAccelerator(KeyStroke.getKeyStroke("control E"));
   }
}
      