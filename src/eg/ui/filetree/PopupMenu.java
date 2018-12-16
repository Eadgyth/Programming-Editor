package eg.ui.filetree;

import java.awt.event.*;
import java.awt.Component;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 * A popup menu with menu items for actions that can be selected
 * after a file node or a folder node was right clicked.<br>
 *
 * Created in {@link FileTree}
 */
public class PopupMenu {

   /**
    * The constant for indicating that items for file operations are shown */
   public final static int FILE_OPT = 0;
   /**
    * The constant for indicating that items for folder operations are shown */
   public final static int FOLDER_OPT = 1;

   private final JPopupMenu popMenu = new JPopupMenu();
   private final JMenuItem openItm = new JMenuItem("Open");
   private final JMenuItem deleteItm = new JMenuItem("Delete");
   private final JMenuItem newFolderItm = new JMenuItem("Create new folder");

   /**
    * @param option  one of <code>PopupMenu.FILE_OPT</code> and
    * <code>popupMenu.FOLDER_OPT</code>
    */
   public PopupMenu(int option) {
      if (option == FILE_OPT) {
         popMenu.add(openItm);
         popMenu.add(deleteItm);
      }
      else {
         popMenu.add(newFolderItm);
         popMenu.add(deleteItm);
      }
   }

   /**
    * Sets the boolean that specifies if the item for deleting
    * actions is enabled (true) or disabled
    *
    * @param b  the boolean value
    */
   public void enableDelete(boolean b) {
      deleteItm.setEnabled(b);
   }

   /**
    * Shows the menu at the specified component
    *
    * @param c  the <code>Component</code>
    * @param x  the x coordinate
    * @param y  the y coordiante
    */
   public void showMenu(Component c, int x, int y) {
      popMenu.show(c, x, y);
   }
   
   /**
    * Sets the listener for actions to open a file
    *
    * @param al  the <code>ActionListener</code>
    */
   void setOpenAction(ActionListener al) {
      openItm.addActionListener(al);
   }

   /**
    * Sets the listener for deleting actions
    *
    * @param al  the <code>ActionListener</code>
    */
   void deleteAct(ActionListener al) {
      deleteItm.addActionListener(al);
   }

   /**
    * Sets the listener for actions to create a new folder
    *
    * @param al  the <code>ActionListener</code>
    */
   void newFolderAct(ActionListener al) {
      newFolderItm.addActionListener(al);
   }
}
