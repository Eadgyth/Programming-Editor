package eg;

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
 * The setting of the display and behavior of the main window
 */
public class DisplaySetter {

   private final MainWin mw;
   private final FormatMenu fMenu;
   private final ViewMenu vMenu;
   private final ProjectMenu prMenu;
   private final Toolbar tBar;
   private final DisplaySettingWin displSetWin = new DisplaySettingWin();
   private final Preferences prefs = new Preferences();

   private EditArea[] editArea;
   private int editAreaIndex;

   private boolean isWordWrap;
   private boolean isShowLineNumbers;
   private boolean isShowToolbar;
   private boolean isShowStatusbar;
   private int selectedLafInd;

   /**
    * @param mw  the reference to {@link MainWin}
    * @param menu  the reference to {@link Menu}
    * @param tBar  the reference to {@link Toolbar}
    */
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
      isWordWrap = "enabled".equals(prefs.getProperty("wordWrap"));
      selectedLafInd = displSetWin.selectedLaf();
      displSetWin.okAct(e -> applySetWinOk());
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
    * to be changed.
    * <p> The method also selects/unselects the wordwrap menu
    * item depending on te state of the {@code EditArea} at the given
    * index
    * @param index  the index of the array of {@link EditArea} objects
    */
   public void setEditAreaIndex(int index) {
      editAreaIndex = index;
      fMenu.selectWordWrapItm(editArea[index].isWordWrap());
   }

   /**
    * Makes the frame of this {@link DisplaySettingWin} visible
    */
   public void makeViewSetWinVisible() {
      displSetWin.makeViewSetWinVisible(true);
   }
   
   /**
    * If enabling word wrap is currently set.
    * @return  if wordwrap is currently set
    */
   public boolean isWordWrap() {
      return isWordWrap;
   }
   
   /**
    * If showing line numbering is currently set
    * @return If showing line numbering is currenty set
    */
   public boolean isLineNumbers() {
      return isShowLineNumbers;
   }
   
   /**
    * Stores view settings to prefs
    */
   public void storeToPrefs() {
      String state = null;
      state = isWordWrap ? "enabled" : "disabled";
      prefs.storePrefs("wordWrap", state);
      state = isShowLineNumbers ? "show" : "hide";
      prefs.storePrefs("lineNumbers", state);
      state = isShowToolbar ? "show" : "hide";
      prefs.storePrefs("toolbar", state);
      state = isShowStatusbar ? "show" : "hide";
      prefs.storePrefs("statusbar", state);
      prefs.storePrefs("LaF", displSetWin.LAF_OPT[selectedLafInd]);
   } 

   /**
    * Enables/disables wordwrap in the {@code EditArea} whose
    * index is currently set.
    * <p>
    * @param isWordWrap  true to enable wordwrap, if false line
    * numbers are shown depending on whether showing line numbers
    * is selected in the view settings win
    */
   public void changeWordWrap(boolean isWordWrap) {
      this.isWordWrap = isWordWrap;
      if (isWordWrap) {
         editArea[editAreaIndex].enableWordWrap();
      }
      else {
         if (isShowLineNumbers) {
            editArea[editAreaIndex].showLineNumbers();
         }
         else {
            editArea[editAreaIndex].hideLineNumbers();
         }
      }   
   }

   /**
    * If the console panel is shown
    * @return  true if the console panel is shown
    */
   public boolean isConsoleSelected() {
      return vMenu.isConsoleItmSelected();
   }

   /**
    * Shows/hides the console panel and selects/deselects
    * the checked menu item for the consel panel
    * @param show  true to show the console panel
    */
   public void setShowConsoleState(boolean show) {
      showConsole(show);
      vMenu.selectConsoleItm(show);
   }

   /**
    * Shows/hides the file view panel and selects/deselects
    * the checked menu item for showing the file view
    * @param show  true to show the file view panel
    */
   public void setShowFileViewState(boolean show) {
      showFileView(show);
      vMenu.selectFileViewItm(show);
   }

   /**
    * Shows/hides the function panel and selects/deselects
    * the checked menu item for showing the function panel
    * @param show  true to show the function panel
    */
   public void setShowFunctionState(boolean show) {
      showFunction(show);
      vMenu.selectFunctionItm(show);
   }

   /**
    * Shows/hides the console panel
    * @param show  true to show the console panel
    */
   public void showConsole(boolean show) {
      if (show) {
         mw.showConsole();
      }
      else {
         mw.hideConsole();
      }
   }

   /**
    * Shows/hides the fileview panel
    * @param show  true to show the fileview panel
    */
   public void showFileView(boolean show) {
      if (show) {
         mw.showFileView();
      }
      else {
         mw.hideFileView();
      }
   }

   /**
    * Shows/hides the function panel
    * @param show  true to show the function panel
    */
   public void showFunction(boolean show) {
      if (show) {
         mw.showFunctionPnl();
      }
      else {
         mw.hideFunctionPnl();
      }
   }
   
   /**
    * Sets the text displayed by the menu item for creating a build
    * @param buildKind  the name that descibes the kind of build
    */
   public void setBuildMenuItmText(String buildKind) {
      prMenu.setBuildKind(buildKind);
   }
   
   /**
    * Enables/disables menu items and toolbar buttons for project
    * actions
    * @param isCompile  true to enable the compilation action
    * @param isRun  true to enable the run action
    * @param isBuild  true to enable the build action
    * @param projCount  the number of loaded projects. If 1 the action
    * to show the fileview is enabled, if 2 the action to change between
    * projects is enabled
    */
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
   
   /**
    * Enables the menu item to change between projects
    */
   public void enableChangeProjItm() {
      prMenu.enableChangeProjItm();
   }
   
   /**
    * Sets busy or default curser
    * @param isBusy  true to set a busy curor, false to set the default
    * cursor
    */
   public void setBusyCursor(boolean isBusy) {
      mw.setBusyCursor(isBusy);
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
    * Displays the project name in the status bar
    * @param name  the name of the project
    */
   public void showProjectInfo(String name) {
      mw.showProjectInfo(name);
   }

   //
   // private methods
   //

   private void applySetWinOk() {
      boolean show = false;

      show = displSetWin.isShowToolbar();
      if (isShowToolbar != show) {
         mw.showToolbar(show);
         isShowToolbar = show;
      }

      show = displSetWin.isShowStatusbar();
      if (isShowStatusbar != show) {
         mw.showStatusbar(show);
         isShowStatusbar = show;
      }

      show = displSetWin.isShowLineNumbers();
      if (isShowLineNumbers != show) {
         isShowLineNumbers = show;
         showHideLineNumbers();
      }

      int index = displSetWin.selectedLaf();
      if (selectedLafInd != index) {
         selectedLafInd = index;
      }    
      displSetWin.makeViewSetWinVisible(false);
   }

   private void showHideLineNumbers() {
      for (EditArea ea : editArea) {
         if (ea != null && !ea.isWordWrap()) {
            if (!isShowLineNumbers) {
               ea.hideLineNumbers();
            } else {
               ea.showLineNumbers();
            }
         }
      }
   }
}
