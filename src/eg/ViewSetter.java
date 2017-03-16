package eg;

import java.awt.Component;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.ui.Toolbar;
import eg.ui.menu.Menu;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.ViewMenu;
import eg.ui.menu.ProjectMenu;
import eg.ui.ViewSettingWin;
import eg.ui.TabbedPane;
import eg.utils.JOptions;

/**
 * The view settings of the main window that depend on saved preferences
 */
public class ViewSetter {

   private final MainWin mw;
   private final Menu menu;
   private final TabbedPane tabPane;
   private final ViewSettingWin viewSetWin;
   private final Preferences prefs = new Preferences();

   private boolean isShowToolbar;
   private boolean isShowStatusbar;
   private boolean isShowTabs;
   private int selectedLafInd;

   /**
    * @param viewSetWin  the reference to {@link ViewSettingWin}
    * @param mw  the reference to {@link MainWin}
    * @param menu  the reference to {@link Menu}
    * @param tabPane  the reference to {@link TabbedPane}
    */
   public ViewSetter(ViewSettingWin viewSetWin, MainWin mw,
         TabbedPane tabPane) {

      this.viewSetWin = viewSetWin;
      this.mw = mw;
      this.menu = mw.getMenu();
      this.tabPane = tabPane;

      isShowStatusbar = viewSetWin.isShowStatusbar();
      isShowToolbar = viewSetWin.isShowToolbar();
      selectedLafInd = viewSetWin.selectedLaf();
      
      prefs.readPrefs();
      isShowTabs = "show".equals(prefs.getProperty("showTabs"));
      menu.getViewMenu().selectTabsItm(isShowTabs);
      tabPane.showTabbar(isShowTabs);
      
      registerActions();
   }
   
   /**
    * If showing tabs (and thus opening multiple files) is
    * is selected
    * @return if showing tabs is selected
    */
   public boolean isShowTabs() {
      return isShowTabs;
   }
   
   /**
    * Enables/disables the menu item to control visiblity of the
    * tab bar
    * @param isEnabled  true/false to enable/disable the menu item for
    * controlling visiblity of the tab bar
    */
   public void enableTabItm(boolean isEnabled) {
      menu.getViewMenu().enableTabItm(isEnabled);
   }
   
   /**
    * Shows/hides the tab bar
    * @param show  true/false to show/hide the tab bar
    */
   public void showTabbar(boolean show) {       
      tabPane.showTabbar(show);
      String state = show ? "show" : "hide";
      prefs.storePrefs("showTabs", state);
      this.isShowTabs = show;
   }
   
   /**
    * Displays text in the title bar of the main window (i.e., the file)
    * @param title  the text that is displayed in the title bar of the
    * main window
    */
   public void displayFrameTitle(String title) {
      mw.displayFrameTitle(title);
   }

   /**
    * the selections in the {@link ViewSettingWin} to show the toolbar
    * and the status bar and to change the LaF
    */
   public void applySetWinOk() {
      boolean show = false;
      String state = null;

      show = viewSetWin.isShowToolbar();
      if (isShowToolbar != show) {
         mw.showToolbar(show);
         isShowToolbar = show; 
         state = isShowToolbar ? "show" : "hide";
         prefs.storePrefs("toolbar", state);
      }

      show = viewSetWin.isShowStatusbar();
      if (isShowStatusbar != show) {
         mw.showStatusbar(show);
         isShowStatusbar = show;
         state = isShowStatusbar ? "show" : "hide";
         prefs.storePrefs("statusbar", state);
      }

      int index = viewSetWin.selectedLaf();
      if (selectedLafInd != index) {
         selectedLafInd = index;
         prefs.storePrefs("LaF", ViewSettingWin.LAF_OPT[selectedLafInd]);
      }
   }
   
   private void registerActions() {
      ViewMenu vm = menu.getViewMenu();
      vm.tabItmAct(e -> showTabbar(vm.isTabItmSelected()));
   }      
}
