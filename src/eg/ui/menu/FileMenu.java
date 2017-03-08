package eg.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--//
import eg.TabbedFiles;
import eg.Edit;

import eg.ui.IconFiles;

public class FileMenu {
   
   private final JMenu     menu        = new JMenu("File");
   private final JMenuItem newFileItm  = new JMenuItem("New" );
   private final JMenuItem openItm     = new JMenuItem("Open...",
                                         IconFiles.OPEN_ICON);
   private final JMenuItem closeItm    = new JMenuItem("Close",
                                         IconFiles.CLOSE_ICON);
   private final JMenuItem closeAllItm = new JMenuItem("Close all");
   private final JMenuItem saveItm     = new JMenuItem("Save",
                                         IconFiles.SAVE_ICON);
   private final JMenuItem saveAllItm  = new JMenuItem("Save all");
   private final JMenuItem saveAsItm   = new JMenuItem("Save as ...");
   private final JMenuItem saveCopyItm = new JMenuItem("Save copy ...");
   private final JMenuItem printItm    = new JMenuItem("Print...");
   private final JMenuItem exitItm     = new JMenuItem("Exit");
   
   FileMenu() {
      assembleMenu();
      shortCuts();
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   /**
    * Adds action listeners to this menu items
    * @param tf  the reference to {@link TabbedFiles}
    */
   public void registerAct(TabbedFiles tf) {
      newFileItm.addActionListener(e -> tf.newEmptyTab());
      openItm.addActionListener(e -> tf.openFileByChooser());
      closeItm.addActionListener(e -> tf.tryClose());
      closeAllItm.addActionListener(e -> tf.tryCloseAll());
      saveItm.addActionListener(e -> tf.save());     
      saveAllItm.addActionListener(e -> tf.saveAll());
      saveAsItm.addActionListener(e -> tf.saveAs());
      saveCopyItm.addActionListener(e -> tf.saveCopy());
      printItm.addActionListener(e -> tf.print());      
      exitItm.addActionListener(e -> tf.tryExit());
   }
   
   private void assembleMenu() {
      menu.add(newFileItm);
      menu.add(openItm);
      menu.add(closeItm);
      menu.add(closeAllItm);
      menu.addSeparator();
      menu.add(saveItm);
      menu.add(saveAllItm);
      menu.add(saveAsItm);
      menu.add(saveCopyItm);
      menu.addSeparator();
      menu.add(printItm);
      menu.addSeparator();
      menu.add(exitItm);
      menu.setMnemonic(KeyEvent.VK_F);
   }
   
   private void shortCuts() {
      newFileItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
            ActionEvent.CTRL_MASK));
      openItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
            ActionEvent.CTRL_MASK));
      saveItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
            ActionEvent.CTRL_MASK));
   }
}
