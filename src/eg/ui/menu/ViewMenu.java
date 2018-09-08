package eg.ui.menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

//--Eadgyth../
import eg.ui.ConsoleOpenable;

/**
 * The menu for view actions.
 * <p>Created in {@link MenuBar}
 */
public class ViewMenu {

   private final JMenu menu = new JMenu("View");

   private final JCheckBoxMenuItem consoleItm
         = new JCheckBoxMenuItem("Console");
   private final JCheckBoxMenuItem fileViewItm
         = new JCheckBoxMenuItem("Project explorer");
   private final JCheckBoxMenuItem tabItm
         = new JCheckBoxMenuItem("Files in tabs");
   private final JMenuItem openSettingsItm
         = new JMenuItem("Other...");
         
   private final ConsoleOpenable consoleOpener;

   public ViewMenu() {
      assembleMenu();
      consoleOpener = co;
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
    * Gets a new <code>ConsoleOpenable</code>
    *
    * @return  a new {@link ConsoleOpenable}
    */
   public ConsoleOpenable consoleOpener() {
      return consoleOpener;
   }

   /**
    * Sets the listener for actions to open or close the console
    * panel
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setConsoleItmAction(ActionListener al) {
      consoleItm.addActionListener(al);
   }

   /**
    * Sets the listener for actions to open or close the file view
    * panel
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setFileViewItmAction(ActionListener al) {
      fileViewItm.addActionListener(al);
   }

   /**
    * Sets the listener for actions to show or hide the tab bar
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setTabItmAction(ActionListener al) {
      tabItm.addActionListener(al);
   }

   /**
    * Sets the listener for actions to open the view settings dialog
    *
    * @param al  the <code>ActionListener</code>
    */
   public void openSettingWinItmAction(ActionListener al) {
      openSettingsItm.addActionListener(al);
   }

   /**
    * Return the boolean value that indicates if the item to open or
    * or close the console panel is selected
    *
    * @return  the boolean value
    */
   public boolean isConsoleItmSelected() {
      return consoleItm.isSelected();
   }

   /**
    * Returns the boolean that indicates if the item to open or close
    * the file view panel is selected
    *
    * @return  the boolean value
    */
   public boolean isFileViewItmSelected() {
      return fileViewItm.isSelected();
   }

   /**
    * Returns the boolean value that indicates if the item to show or
    * or hide the tab bar is selected
    *
    * @return  the boolean value
    */
   public boolean isTabItmSelected() {
      return tabItm.isSelected();
   }

   /**
    * Performs the action added to the item to show or hide the console
    * panel if the selection state of the item does not equal the
    * specified boolean value
    *
    * @param b  the boolean value. True to show, false to hide the panel
    */
  public void doConsoleItmAct(boolean b) {
      if (b != consoleItm.isSelected()) {
         consoleItm.doClick();
      }
   }

   /**
    * Performs the action added to the item to show or hide the file
    * view panel if the item is currently selected
    */
   public void doUnselectFileViewAct() {
      if (fileViewItm.isSelected()) {
          fileViewItm.doClick();
       }
   }

   /**
    * Sets the selection state, indicated by the boolean value, of the
    * item to show or hide the tab bar
    *
    * @param b  the boolean value
    */
   public void selectTabsItm(boolean b) {
      tabItm.setSelected(b);
   }
   
   /**
    * Sets the selection state, indicated by the boolean value, of the
    * item to show or hide the file tree
    *
    * @param b  the boolean value
    */
   public void selectFileViewItm(boolean b) {
      fileViewItm.setSelected(b);
   }

   /**
    * Sets the boolean that specifies if actions to show or hide the
    * tab bar are enabled
    *
    * @param b  the boolen value
    */
   public void enableTabItm(boolean b) {
      tabItm.setEnabled(b);
   }

   //
   //--private--/
   //
   
   private final ConsoleOpenable co = new ConsoleOpenable() {

      @Override
      public boolean isConsoleOpen() {
         return isConsoleItmSelected();
      }
   
      @Override
      public void openConsole() {
         doConsoleItmAct(true);
      }
   };

   private void assembleMenu() {
      menu.add(consoleItm);
      menu.add(fileViewItm);
      menu.add(tabItm);
      menu.addSeparator();
      menu.add(openSettingsItm);
      menu.setMnemonic(KeyEvent.VK_V);
   }
}
