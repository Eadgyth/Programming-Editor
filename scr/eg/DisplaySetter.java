package eg;

import java.awt.Cursor;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.ui.EditArea;
import eg.ui.Toolbar;
import eg.ui.menu.Menu;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.ViewMenu;
import eg.ui.menu.ProjectMenu;
import eg.ui.DisplaySettingWin;

/**
 * Controls the display of the main window
 */
public class DisplaySetter {

   private static Constants c;

   private final MainWin mw;
   private final FormatMenu fMenu;
   private final ViewMenu vMenu;
   private final ProjectMenu prMenu;
   private final Toolbar tBar;
   private final DisplaySettingWin displSetWin = new DisplaySettingWin();
   private final Preferences prefs = new Preferences();

   private EditArea[] editArea;
   private int editAreaIndex;

   private boolean isShowToolbar;
   private boolean isShowLineNumbers;
   private boolean isShowStatusbar;
   private int selectedLafInd;

   public DisplaySetter(MainWin mw, Menu menu, Toolbar tBar) {
      this.mw = mw;
      this.vMenu = menu.getViewMenu();
      this.fMenu = menu.getFormatMenu();
      this.prMenu = menu.getProjectMenu();
      this.tBar = tBar;
      prefs.readPrefs();
      isShowStatusbar = displSetWin.isShowStatusbar();
      isShowToolbar = displSetWin.isShowToolbar();
      isShowLineNumbers = displSetWin.isShowLineNumbers();
      selectedLafInd = displSetWin.selectedLaf();
      displSetWin.okAct(e -> applyChanges());
   }

   /**
    * Sets the array of type {@code EditArea}
    * @param editArea  the array of {@link EditArea}
    */
   public void setEditAreaArr(EditArea[] editArea) {
      this.editArea = editArea;
   }

   /**
    * Sets the index of the {@code EditArea} array whose display is
    * to be changed and also selects/unselects the wordwrap menu
    * item depending on te state of the {@code EditArea}
    * @param index  the index of the array of {@link EditArea} objects
    */
   public void setEditAreaIndex(int index) {
      editAreaIndex = index;
      fMenu.selectWordWrapItm(editArea[index].isWordWrap());
   }

   /**
    * Makes the window in which view settings are changed visible
    */
   public void makeViewSetWinVisible() {
      displSetWin.makeViewSetWinVisible(true);
   }

   /**
    * Enables/disables wordwrap in the {@code EditArea} whose
    * index is currently set
    * <p>
    * @param isWordWrap  true to enable wordwrap, if false line
    * numbers are shown depending on the entry in prefs
    */
   public void changeWordWrap(boolean isWordWrap) {
      prefs.readPrefs();
      if (isWordWrap) {
         editArea[editAreaIndex].enableWordWrap();
         prefs.storePrefs("wordWrap", "enabled");
      }
      else {
         boolean isLineNumbers
               = c.SHOW.equals(prefs.getProperty(c.LINE_NUM_PREFS));
         if (isLineNumbers) {
            editArea[editAreaIndex].showLineNumbers();
         }
         else {
            editArea[editAreaIndex].hideLineNumbers();
         }
         prefs.storePrefs("wordWrap", "disabled");
      }   
   }

   /**
    * If the console panel is shown
    * @return  true if the console panel is shown
    */
   public boolean isConsoleSelected() {
      return vMenu.isConsoleSelected();
   }

   /**
    * Shows/hides the console panel and selects/deselects
    * the console menu item
    * @param show  true to show the console panel
    */
   public void setShowConsoleState(boolean show) {
      showConsole(show);
      vMenu.selectShowConsole(show);
   }

   /**
    * Shows/hides the file view panel and selects/deselects
    * the file view menu item
    * @param show  true to show the file view panel
    */
   public void setShowFileViewState(boolean show) {
      showFileView(show);
      vMenu.selectShowFileView(show);
   }

   /**
    * Shows/hides the function panel and selects/deselects
    * the function menu item
    * @param show  true to show the function panel
    */
   public void setShowFunctionState(boolean show) {
      showFunction(show);
      vMenu.selectShowFunction(show);
   }

   public void showConsole(boolean show) {
      if (show) {
         mw.showConsole();
      }
      else {
         mw.hideConsole();
      }
   }

   public void showFileView(boolean show) {
      if (show) {
         mw.showFileView();
      }
      else {
         mw.hideFileView();
      }
   }

   public void showFunction(boolean show) {
      if (show) {
         mw.showFunctionPnl();
      }
      else {
         mw.hideFunctionPnl();
      }
   }
   
   public void setBuildMenuItmText(String buildKind) {
      prMenu.setBuildKind(buildKind);
   }
   
   public void enableProjActions(boolean isCompile, boolean isRun,
         boolean isBuild, int projCount) {
      if (projCount == 1) {
         vMenu.enableFileView();
      }
      if (projCount == 2) {
         enableChangeProjItm();
      }
      prMenu.enableProjItms(isCompile, isRun, isBuild);
      tBar.enableProjBts(isCompile, isRun);
   }
   
   public void enableChangeProjItm() {
      prMenu.enableChangeProjItm();
   }
   
   public void setBusyCursor(boolean isBusy) {
      mw.setBusyCursor(isBusy);
   }
   
   public void showProjectInfo(String name) {
      mw.showProjectInfo(name);
   }

   //
   // private methods
   //

   private void applyChanges() {
      boolean isShowToolbar = displSetWin.isShowToolbar();
      if (this.isShowToolbar != isShowToolbar) {
         mw.showToolbar(isShowToolbar);
         this.isShowToolbar = isShowToolbar;
      }

      boolean isShowStatusbar = displSetWin.isShowStatusbar();
      if (this.isShowStatusbar != isShowStatusbar) {
         mw.showStatusbar(isShowStatusbar);
         this.isShowStatusbar = isShowStatusbar;
      }

      boolean isShowLineNumbers = displSetWin.isShowLineNumbers();
      if (this.isShowLineNumbers != isShowLineNumbers) {
         this.isShowLineNumbers = isShowLineNumbers;
         this.showHideLineNumbers();
      }

      int selectedLafInd = displSetWin.selectedLaf();
      if (this.selectedLafInd != selectedLafInd) {
         prefs.storePrefs("LaF",
               DisplaySettingWin.LAF_OPT[selectedLafInd]);
         this.selectedLafInd = selectedLafInd;
      }    
      displSetWin.makeViewSetWinVisible(false);
   }

   private void showHideLineNumbers() {
      boolean isWordWrapDisabled = false;
      for (int i = 0; i < editArea.length; i++) {
         if (editArea[i] != null && !editArea[i].isWordWrap()) {
            isWordWrapDisabled = true;
            if (!isShowLineNumbers) {
               editArea[i].hideLineNumbers();
            }
            else {
               editArea[i].showLineNumbers();
            }
         }
      }
      if (isWordWrapDisabled) {
         fMenu.selectWordWrapItm(false);
      }
      if (!isShowLineNumbers) {
         prefs.storePrefs(c.LINE_NUM_PREFS, c.HIDE);
      }
      else {
         prefs.storePrefs(c.LINE_NUM_PREFS, c.SHOW);
      }
   }
}
