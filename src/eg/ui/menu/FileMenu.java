package eg.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//--Eadgyth--
import eg.TabbedDocuments;
import eg.ui.IconFiles;

/**
 * The menu for file actions
 */
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
   private final JMenuItem saveCopyItm = new JMenuItem("Save copy as ...");
   private final JMenuItem printItm    = new JMenuItem("Print...");
   private final JMenuItem exitItm     = new JMenuItem("Exit");
   
   public FileMenu() {
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
    * Sets listeners for file actions defined in <code>TabbedFiles</code>
    * except for the action to exit the program
    *
    * @param td  the reference to {@link TabbedDocuments}
    */
   public void setActions(TabbedDocuments td) {
      newFileItm.addActionListener(e -> td.createBlankDocument());
      openItm.addActionListener(e -> td.open());
      closeItm.addActionListener(e -> td.close(true));
      closeAllItm.addActionListener(e -> td.closeAll(true));
      saveItm.addActionListener(e -> td.save());     
      saveAllItm.addActionListener(e -> td.saveAll());
      saveAsItm.addActionListener(e -> td.saveAs());
      saveCopyItm.addActionListener(e -> td.saveCopy());
      printItm.addActionListener(e -> td.print());
   }
   
   /**
    * Sets the listener for the action to exit the program
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setExitActions(ActionListener al) {
      exitItm.addActionListener(al);
   }
   
   /**
    * Enables or disables the item for saving actions
    *
    * @param b  true to enable, false to disable
    */
    public void enableSaveItm(boolean b) {
       saveItm.setEnabled(b);
    }
   
   //
   //--private--/
   //
   
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
