package eg.ui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.TabbedFiles;

import eg.ui.IconFiles;

public class FileMenu {
   
   private final JMenu     menu     = new JMenu("File");
   private final JMenuItem newFile  = new JMenuItem("New" );
   private final JMenuItem open     = new JMenuItem("Open",
                                      IconFiles.OPEN_ICON);
   private final JMenuItem close    = new JMenuItem("Close",
                                      IconFiles.CLOSE_ICON);
   private final JMenuItem closeAll = new JMenuItem("Close all");
   private final JMenuItem save     = new JMenuItem("Save",
                                      IconFiles.SAVE_ICON);
   private final JMenuItem saveAll  = new JMenuItem("Save all");
   private final JMenuItem saveAs   = new JMenuItem("Save as ...");
   private final JMenuItem exit     = new JMenuItem("Exit");
   
   FileMenu() {
      assembleMenu();
      shortCuts();
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   public void registerAct(TabbedFiles tf) {
      newFile.addActionListener(e -> tf.newEmptyTab());
      open.addActionListener(e -> tf.openFileByChooser());
      close.addActionListener(e -> tf.tryClose());
      closeAll.addActionListener(e -> tf.tryCloseAll());
      save.addActionListener(e -> tf.saveOrSaveAs());     
      saveAll.addActionListener(e -> tf.saveAll());
      saveAs.addActionListener(e -> tf.saveAs());      
      exit.addActionListener(e -> tf.tryExit());
   }
   
   private void assembleMenu() {
      menu.add(newFile);
      menu.add(open);
      menu.add(close);
      menu.add(closeAll);
      menu.addSeparator();
      menu.add(save);
      menu.add(saveAll);
      menu.add(saveAs);
      menu.addSeparator();
      menu.add(exit);
   }
   
   private void shortCuts() {
      newFile.setAccelerator(KeyStroke.getKeyStroke("control N"));
      open.setAccelerator(KeyStroke.getKeyStroke("control O"));
      save.setAccelerator(KeyStroke.getKeyStroke("control S"));
   }
}
