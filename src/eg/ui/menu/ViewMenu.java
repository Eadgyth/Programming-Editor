package eg.ui.menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

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
         = new JMenuItem("Preferences");

   public ViewMenu() {
      assembleMenu();
   }
   
   public JMenu getMenu() {
      return menu;
   }
   
   public void setConsoleItmAction(ActionListener al) {
      consoleItm.addActionListener(al);
   }
   
   public void setFileViewItmAction(ActionListener al) {
      fileViewItm.addActionListener(al);
   }
   
   public void setTabItmAction(ActionListener al) {
      tabItm.addActionListener(al);
   }
   
   public void openSettingWinItmAction(ActionListener al) {
      openSettingsItm.addActionListener(al);
   }
   
   public boolean isConsoleItmSelected() {
      return consoleItm.isSelected();
   }

   public boolean isFileViewItmSelected() {
      return fileViewItm.isSelected();
   }
   
   public boolean isTabItmSelected() {
      return tabItm.isSelected();
   }

   public void doConsoleItmAct(boolean select) {
      if (select != consoleItm.isSelected()) {
         consoleItm.doClick();
      }
   }

   public void doUnselectFileViewAct() {
      if (fileViewItm.isSelected()) {
          fileViewItm.doClick();
       }
   }

   public void selectTabsItm(boolean select) {
      tabItm.setSelected(select);
   }
   
   public void enableFileViewItm() {
      fileViewItm.setEnabled(true);
   }
   
   public void enableTabItm(boolean isEnabled) {
      tabItm.setEnabled(isEnabled);
   }
   
   private void assembleMenu() {
      menu.add(consoleItm);
      menu.add(fileViewItm);
      menu.add(tabItm);
      menu.addSeparator();
      menu.add(openSettingsItm);
      menu.setMnemonic(KeyEvent.VK_V);
      fileViewItm.setEnabled(false);
   }
}
