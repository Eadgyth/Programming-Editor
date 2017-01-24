package eg.ui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

//--Eadgyth--//
import eg.DisplaySetter;

public class ViewMenu {
   
   private final JMenu menu = new JMenu("View");

   private final JCheckBoxMenuItem consoleItm
         = new JCheckBoxMenuItem("Console");
   private final JCheckBoxMenuItem fileViewItm
         = new JCheckBoxMenuItem("Project explorer");
   private final JCheckBoxMenuItem functionItm
         = new JCheckBoxMenuItem("Function panel");
   private final JMenuItem openSettingsItm
         = new JMenuItem("Other...");

   ViewMenu() {
      assembleMenu();
   }
   
   JMenu getMenu() {
      return menu;
   }
   
   public void registerAct(DisplaySetter displSet) {
      consoleItm.addActionListener(e ->
            displSet.showConsole(isConsoleItmSelected()));
      fileViewItm.addActionListener(e ->
            displSet.showFileView(isFileViewItmSelected()));
      functionItm.addActionListener(e ->
            displSet.showFunction(isFunctionItmSelected()));
      openSettingsItm.addActionListener(e ->
            displSet.makeViewSetWinVisible());
   }
   
   /**
    * @return  true if this checkbox menu item for showing the console
    * is selected
    */
   public boolean isConsoleItmSelected() {
      return consoleItm.getState();
   }

   /**
    * @return  true if this checkbox menu item for showing the file explorer
    * is selected
    */
   public boolean isFileViewItmSelected() {
      return fileViewItm.getState();
   }

   /**
    * @return  if this checkbox menu item for showing the function panel
    * is selected
    */
   public boolean isFunctionItmSelected() {
      return functionItm.getState();
   }
   
   public void selectConsoleItm(boolean select) {
      consoleItm.setState(select);
   }

   public void selectFileViewItm(boolean select) {
      fileViewItm.setState(select);
   }

   public void selectFunctionItm(boolean select) {
      functionItm.setState(select);
   }
   
   /**
    * Enables the file view menu item
    */
   public void enableFileView() {
      fileViewItm.setEnabled(true);
   }
   
   private void assembleMenu() {
      menu.add(consoleItm);
      menu.add(fileViewItm);
      fileViewItm.setEnabled(false);
      menu.add(functionItm);
      menu.addSeparator();
      menu.add(openSettingsItm);
   }
}
