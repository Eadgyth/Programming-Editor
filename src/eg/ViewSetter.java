package eg;

import java.awt.Component;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.ui.Toolbar;
import eg.ui.menu.ViewMenu;
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
   private final ViewMenu vMenu;
   private final TabbedPane tabPane;
   private final ViewSettingWin viewSetWin;
   private final Preferences prefs = new Preferences();

   private boolean isShowToolbar;
   private boolean isShowStatusbar;
   private int selectedLafInd;

   public ViewSetter(ViewSettingWin viewSetWin, MainWin mw) {
      this.viewSetWin = viewSetWin;
      this.mw = mw;
      this.vMenu = mw.menu().viewMenu();
      this.tabPane = mw.tabPane();

      isShowStatusbar = viewSetWin.isShowStatusbar();
      mw.showStatusbar(isShowStatusbar);
      isShowToolbar = viewSetWin.isShowToolbar();
      mw.showToolbar(isShowToolbar);
      selectedLafInd = viewSetWin.selectedLaf();
      
      prefs.readPrefs();
      boolean isShowTabs = "show".equals(prefs.getProperty("showTabs"));
      vMenu.selectTabsItm(isShowTabs);
      tabPane.showTabbar(isShowTabs);
      registerActions();
   }
   
   /**
    * If showing the tab bar is selected in the view menu
    *
    * @return  if showing the tab bar is selected
    */
   public boolean isShowTabs() {
      return vMenu.isTabItmSelected();
   }
   
   /**
    * Enables/disables the menu item to control visiblity of the
    * tab bar
    *
    * @param isEnabled  true/false to enable/disable the menu item for
    * controlling visiblity of the tab bar
    */
   public void enableTabItm(boolean isEnabled) {
      vMenu.enableTabItm(isEnabled);
   }
   
   /**
    * Shows/hides the tab bar
    *
    * @param show  true/false to show/hide the tab bar
    */
   public void showTabbar(boolean show) {       
      tabPane.showTabbar(show);
      String state = show ? "show" : "hide";
      prefs.storePrefs("showTabs", state);
   }
   
   /**
    * Displays text in the title bar of the main window (i.e., the file)
    *
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
      if (show != isShowToolbar) {
         mw.showToolbar(show);
         isShowToolbar = show; 
         state = isShowToolbar ? "show" : "hide";
         prefs.storePrefs("toolbar", state);
      }
      show = viewSetWin.isShowStatusbar();
      if (show != isShowStatusbar) {
         mw.showStatusbar(show);
         isShowStatusbar = show;
         state = isShowStatusbar ? "show" : "hide";
         prefs.storePrefs("statusbar", state);
      }
      int index = viewSetWin.selectedLaf();
      if (index != selectedLafInd) {
         selectedLafInd = index;
         prefs.storePrefs("LaF", ViewSettingWin.LAF_OPT[selectedLafInd]);
      }
   }
   
   private void registerActions() {
      vMenu.tabItmAct(e -> showTabbar(isShowTabs()));
   }      
}
