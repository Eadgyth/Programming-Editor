package eg.ui.menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

public class ViewMenu {
   
   private final JMenu menu = new JMenu("View");

   private final JCheckBoxMenuItem consoleItm
         = new JCheckBoxMenuItem("Console");
   private final JCheckBoxMenuItem fileViewItm
         = new JCheckBoxMenuItem("Project explorer");
   private final JCheckBoxMenuItem functionItm
         = new JCheckBoxMenuItem("Function panel");
   private final JCheckBoxMenuItem tabItm
         = new JCheckBoxMenuItem("Multiple files");
   private final JMenuItem openSettingsItm
         = new JMenuItem("Other settings...");

   ViewMenu() {
      assembleMenu();
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   /**
    * Adds handler for showing/hiding the console panel
    *
    * @param al  the {@code ActionListener}
    */
   public void consoleItmAct(ActionListener al) {
      consoleItm.addActionListener(al);
   }
   
   /**
    * Adds handler for showing/hiding the file view panel
    *
    * @param al  the {@code ActionListener}
    */
   public void fileViewItmAct(ActionListener al) {
      fileViewItm.addActionListener(al);
   }
   
   /**
    * Adds handler for showing/hiding the function panel
    *
    * @param al  the {@code ActionListener}
    */
   public void functionItmAct(ActionListener al) {
      functionItm.addActionListener(al);
   }
   
   /**
    * Adds handler for showing/hiding the tab bar
    *
    * @param al  the {@code ActionListener}
    */
   public void tabItmAct(ActionListener al) {
      tabItm.addActionListener(al);
   }
   
   /** 
    * Adds handler for opening the window for the view seetings
    *
    * @param al  the {@code ActionListener}
    */
   public void openSettingWinItmAct(ActionListener al) {
      openSettingsItm.addActionListener(al);
   }
   
   /**
    * @return  if this checkbox menu item for showing the console
    * is selected
    */
   public boolean isConsoleItmSelected() {
      return consoleItm.isSelected();
   }

   /**
    * @return  if this checkbox menu item for showing the file explorer
    * is selected
    */
   public boolean isFileViewItmSelected() {
      return fileViewItm.isSelected();
   }

   /**
    * @return  if this checkbox menu item for showing the function panel
    * is selected
    */
   public boolean isFunctionItmSelected() {
      return functionItm.getState();
   }
   
   /**
    * @return  if this menu item for showing tabs
    * is selected
    */
   public boolean isTabItmSelected() {
      return tabItm.isSelected();
   }

   /**
    * Performs the action added to the menu item to open/close the
    * console panel
    *
    * @param select  true/false to select/unselect the menu item
    */
   public void doConsoleItmAct(boolean select) {
      if (select != consoleItm.isSelected()) {
         consoleItm.doClick();
      }
   }

   /**
    * Unselects the menu item for showing the file view and performs
    * the action added to the item
    */
   public void doUnselectFileViewAct() {
      if (fileViewItm.isSelected()) {
          fileViewItm.doClick();
       }
   }

   /**
    * Performs the action added to the menu item to open/close the
    * function panel
    *
    * @param select  true/false to select/unselect the menu item
    */
   public void doFunctionItmAct(boolean select) {
      if (select != functionItm.isSelected()) {
         functionItm.doClick();
      }
   }

   /**
    * Sets the selection state of the menu item for showing the tab bar
    * @param select  true/false to set the item selected/unselected
    */
   public void selectTabsItm(boolean select) {
      tabItm.setSelected(select);
   }
   
   /**
    * Enables the menu item for showing the fileview
    */
   public void enableFileView() {
      fileViewItm.setEnabled(true);
   }
   
   /**
    * Enables the menu item for showing/hiding the tab bar
    * @param isEnabled  true to set the menu item for showing
    * or hiding the tab bar enabled 
    */
   public void enableTabItm(boolean isEnabled) {
      tabItm.setEnabled(isEnabled);
   }
   
   private void assembleMenu() {
      menu.add(consoleItm);
      menu.add(fileViewItm);
      fileViewItm.setEnabled(false);
      menu.add(functionItm);
      menu.add(tabItm);
      menu.addSeparator();
      menu.add(openSettingsItm);
      menu.setMnemonic(KeyEvent.VK_V);
   }
}
