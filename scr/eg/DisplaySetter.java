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
         prefs.storePrefs("wordWrap", Constants.ENABLED);
      }
      else {
         boolean isLineNumbers
               = Constants.SHOW.equals(prefs.getProperty(Constants.LINE_NUMBERS));
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
      return vMenu.isConsoleItmSelected();
   }

   /**
    * Shows/hides the console panel and selects/deselects
    * the console menu item
    * @param show  true to show the console panel
    */
   public void setShowConsoleState(boolean show) {
      showConsole(show);
      vMenu.selectConsoleItm(show);
   }

   /**
    * Shows/hides the file view panel and selects/deselects
    * the file view menu item
    * @param show  true to show the file view panel
    */
   public void setShowFileViewState(boolean show) {
      showFileView(show);
      vMenu.selectFileViewItm(show);
   }

   /**
    * Shows/hides the function panel and selects/deselects
    * the function menu item
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
    * Enables/disables menu items and toolbar buttons for to project
    * actions
    * @param isCompile  true/false to enable/disable the compilation
    * action
    * @param isRun  true/false to enable/disable the run action
    * @param isBuild  true/false to enable/disable the build action
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
    * Enabled the menu item to change between projects
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
    * Displays the project name in the status bar
    * @param name  the name of the project
    */
   public void showProjectInfo(String name) {
      mw.showProjectInfo(name);
   }

   //
   // private methods
   //

   private void applyChanges() {
      boolean isToolbar = displSetWin.isShowToolbar();
      if (this.isShowToolbar != isToolbar) {
         mw.showToolbar(isToolbar);
         this.isShowToolbar = isToolbar;
      }

      boolean isStatusbar = displSetWin.isShowStatusbar();
      System.out.println("statusbar");
      if (this.isShowStatusbar != isStatusbar) {
         mw.showStatusbar(isStatusbar);
         this.isShowStatusbar = isStatusbar;
      }

      boolean isLineNumbers = displSetWin.isShowLineNumbers();
      if (this.isShowLineNumbers != isLineNumbers) {
         this.isShowLineNumbers = isLineNumbers;
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
       for (EditArea editArea1 : editArea) {
           if (editArea1 != null && !editArea1.isWordWrap()) {
               isWordWrapDisabled = true;
               if (!isShowLineNumbers) {
                   editArea1.hideLineNumbers();
               } else {
                   editArea1.showLineNumbers();
               }
           }
       }
      if (isWordWrapDisabled) {
         fMenu.selectWordWrapItm(false);
      }
      if (!isShowLineNumbers) {
         prefs.storePrefs("lineNumbers", c.HIDE);
      }
      else {
         prefs.storePrefs("lineNumbers", c.SHOW);
      }
   }
}
