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
import eg.ui.tabpane.ExtTabbedPane;
import eg.utils.JOptions;

/**
 * The view settings of the main window that depend on saved preferences
 */
public class ViewSetter {

   private final MainWin mw;
   private final ViewMenu vMenu;
   private final ExtTabbedPane tabPane;
   private final ViewSettingWin viewSetWin;
   private final Preferences prefs = new Preferences();

   private boolean isShowToolbar;
   private boolean isShowStatusbar;
   private int selectedIconSizeInd;
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
      selectedIconSizeInd = viewSetWin.selectedIconSize();
      selectedLafInd = viewSetWin.selectedLaf();
      
      prefs.readPrefs();
      boolean isShowTabs = "show".equals(prefs.getProperty("showTabs"));
      vMenu.selectTabsItm(isShowTabs);
      tabPane.showTabbar(isShowTabs);
      registerActions();
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
    * Applies the selections in {@link ViewSettingWin} to show or
    * hide the toolbar and the status bar and to chnage the LaF
    */
   public void applySetWinOk() {
      boolean show = false;
      int index = 0;
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
      index = viewSetWin.selectedIconSize();
      if (index != selectedIconSizeInd) {
         selectedIconSizeInd = index;
         prefs.storePrefs("iconSize", ViewSettingWin.ICON_SIZES[selectedIconSizeInd]);
      }
      index = viewSetWin.selectedLaf();
      if (index != selectedLafInd) {
         selectedLafInd = index;
         prefs.storePrefs("LaF", ViewSettingWin.LAF_OPT[selectedLafInd]);
      }
   }
   
   private void registerActions() {
      vMenu.tabItmAct(e -> showTabbar(vMenu.isTabItmSelected()));
   }      
}
