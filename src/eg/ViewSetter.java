package eg;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.ui.ViewSettingWin;

/**
 * The view settings in the main window that are set in
 * <code>ViewSettingWin</code> except for showing/hiding line numbers
 */
public class ViewSetter {

   private final MainWin mw;
   private final ViewSettingWin viewSetWin;
   private final Preferences prefs = new Preferences();

   private boolean isShowToolbar;
   private boolean isShowStatusbar;
   private int selectedIconSizeInd;
   private int selectedLafInd;

   /**
    * @param viewSetWin  the reference to <code>ViewSettingWin</code>
    * @param mw  the reference to <code>MainWin</code>
    */
   public ViewSetter(ViewSettingWin viewSetWin, MainWin mw) {
      this.viewSetWin = viewSetWin;
      this.mw = mw;
      isShowStatusbar = viewSetWin.isShowStatusbar();
      mw.showStatusbar(isShowStatusbar);
      isShowToolbar = viewSetWin.isShowToolbar();
      mw.showToolbar(isShowToolbar);
      selectedIconSizeInd = viewSetWin.selectedIconSize();
      selectedLafInd = viewSetWin.selectedLaf();
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
}
