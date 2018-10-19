package eg.ui.menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

/**
 * The menu for view actions
 */
public class ViewMenu {

   private final JMenu menu = new JMenu("View");

   private final JCheckBoxMenuItem consoleItm = new JCheckBoxMenuItem("Console");
   private final JCheckBoxMenuItem fileViewItm
         = new JCheckBoxMenuItem("Project explorer");

   private final JCheckBoxMenuItem tabItm
         = new JCheckBoxMenuItem("Files in tabs");

   private final JMenuItem openSettingsItm
         = new JMenuItem("Other...");

   public ViewMenu() {
      assembleMenu();
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
    * Return if the item for actions to open or close the console is
    * selected
    *
    * @return  true if selected
    */
   public boolean isConsoleItmSelected() {
      return consoleItm.isSelected();
   }

   /**
    * Returns if the item for actions to open or close the file view
    * is selected
    *
    * @return  true if selected
    */
   public boolean isFileViewItmSelected() {
      return fileViewItm.isSelected();
   }

   /**
    * Returns if the item for actions to to show or or hide the tabbar
    * is selected
    *
    * @return  true if selected
    */
   public boolean isTabItmSelected() {
      return tabItm.isSelected();
   }

   /**
    * Performs the action to show or hide the console
    *
    * @param b  true to show, false to hide the console
    */
  public void doConsoleItmAct(boolean b) {
      if (b != consoleItm.isSelected()) {
         consoleItm.doClick();
      }
   }

   /**
    * Performs the action to hide the file view panel
    */
   public void doUnselectFileViewAct() {
      if (fileViewItm.isSelected()) {
          fileViewItm.doClick();
       }
   }

   /**
    * Selects or unselects the item for actions to show or hide the tabbar
    *
    * @param b  true to select, false to unselect
    */
   public void selectTabsItm(boolean b) {
      tabItm.setSelected(b);
   }

   /**
    * Selects or unselects the item for actions to show or hide the
    * file tree
    *
    * @param b  true to select, false to unselect
    */
   public void selectFileViewItm(boolean b) {
      fileViewItm.setSelected(b);
   }

   /**
    * Enables or disables the item for actions to show or hide the tabbar
    *
    * @param b  true to enable, false to disable
    */
   public void enableTabItm(boolean b) {
      tabItm.setEnabled(b);
   }

   //
   //--private--/
   //

   private void assembleMenu() {
      menu.add(consoleItm);
      menu.add(fileViewItm);
      menu.add(tabItm);
      menu.addSeparator();
      menu.add(openSettingsItm);
      menu.setMnemonic(KeyEvent.VK_V);
   }
}
