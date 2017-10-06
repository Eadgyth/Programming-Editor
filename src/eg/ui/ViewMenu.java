package eg.ui;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

class ViewMenu implements ConsoleOpenable{
   
   private final JMenu menu = new JMenu("View");

   private final JCheckBoxMenuItem consoleItm
         = new JCheckBoxMenuItem("Console");
   private final JCheckBoxMenuItem fileViewItm
         = new JCheckBoxMenuItem("Project explorer");
   private final JCheckBoxMenuItem functionItm
         = new JCheckBoxMenuItem("Function panel");
   private final JCheckBoxMenuItem tabItm
         = new JCheckBoxMenuItem("Files in tabs");
   private final JMenuItem openSettingsItm
         = new JMenuItem("Other settings...");

   ViewMenu() {
      assembleMenu();
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   @Override
   public boolean isConsoleOpen() {
      return isConsoleItmSelected();
   }
   
   @Override
   public void openConsole() {
      doConsoleItmAct(true);
   }
   
   void consoleItmAct(ActionListener al) {
      consoleItm.addActionListener(al);
   }
   
   void fileViewItmAct(ActionListener al) {
      fileViewItm.addActionListener(al);
   }
   
   void functionItmAct(ActionListener al) {
      functionItm.addActionListener(al);
   }
   
   void tabItmAct(ActionListener al) {
      tabItm.addActionListener(al);
   }
   
   void openSettingWinItmAct(ActionListener al) {
      openSettingsItm.addActionListener(al);
   }
   
   boolean isConsoleItmSelected() {
      return consoleItm.isSelected();
   }

   boolean isFileViewItmSelected() {
      return fileViewItm.isSelected();
   }

   boolean isFunctionItmSelected() {
      return functionItm.isSelected();
   }
   
   boolean isTabItmSelected() {
      return tabItm.isSelected();
   }

   void doConsoleItmAct(boolean select) {
      if (select != consoleItm.isSelected()) {
         consoleItm.doClick();
      }
   }

   void doUnselectFileViewAct() {
      if (fileViewItm.isSelected()) {
          fileViewItm.doClick();
       }
   }

   void doFunctionItmAct(boolean select) {
      if (select != functionItm.isSelected()) {
         functionItm.doClick();
      }
   }

   void selectTabsItm(boolean select) {
      tabItm.setSelected(select);
   }
   
   void enableFileViewItm() {
      fileViewItm.setEnabled(true);
   }
   
   void enableTabItm(boolean isEnabled) {
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
