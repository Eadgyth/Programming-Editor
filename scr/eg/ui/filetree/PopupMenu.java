package eg.ui.filetree;

import java.awt.event.*;
import java.awt.Component;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 * A popup menu with menu items for deleting a file and creating
 * a new folder
 */
class PopupMenu {
   
   static int FILE_OPT = 0;
   static int FOLDER_OPT = 1;

   private final JPopupMenu popMenu = new JPopupMenu();
   private final JMenuItem deleteItm = new JMenuItem("Delete");
   private final JMenuItem newFolderItm = new JMenuItem("Create new folder");

   PopupMenu(int opt) {
      if (opt == FILE_OPT) {
         popMenu.add(deleteItm);
      }
      else if (opt == FOLDER_OPT) {
         popMenu.add(newFolderItm);
      }
   }

   void showMenu(Component c, int x, int y) {
      popMenu.show(c, x, y);
   }

   void deleteAct(ActionListener al) {
      deleteItm.addActionListener(al);
   }
   
   void newFolderAct(ActionListener al) {
      newFolderItm.addActionListener(al);
   }
}