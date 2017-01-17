package eg.ui;

import java.awt.Cursor;

//--Eadgyth--//
import eg.Constants;
import eg.Preferences;

import eg.ui.EditArea;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.ViewMenu;

/**
 * Controls the display of the main window
 */
public class ViewSettings {

   private static Constants c;

   private final MainWin mw;
   private final FormatMenu fMenu;
   private final ViewMenu vMenu;
   private final ViewSettingsWin viewSetWin = new ViewSettingsWin();
   private final Preferences prefs = new Preferences();

   private EditArea[] editArea;
   private int editAreaIndex;

   private boolean isShowToolbar;
   private boolean isShowLineNumbers;
   private boolean isShowStatusbar;
   private int selectedLafInd;

   /**
    * Creates a ViewSettings
    * @param mw  the reference to {@link MainWin}
    * @param vMenu  the reference to {@link ViewMenu}
    * @param fMenu  the reference to {@link FormatMenu}
    */
   public ViewSettings(MainWin mw, ViewMenu vMenu, FormatMenu fMenu) {
      this.mw = mw;
      this.vMenu = vMenu;
      this.fMenu = fMenu;

      prefs.readPrefs();

      isShowStatusbar = viewSetWin.isShowStatusbar();
      isShowToolbar = viewSetWin.isShowToolbar();
      isShowLineNumbers = viewSetWin.isShowLineNumbers();
      selectedLafInd = viewSetWin.selectedLaf();

      viewSetWin.okAct(e -> applyChanges());
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
      fMenu.selectWordWrap(editArea[index].isWordWrap());
   }

   /**
    * Makes the window in which view settings are changed visible
    */
   public void makeViewSetWinVisible() {
      viewSetWin.makeViewSetWinVisible(true);
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


   //
   // private methods
   //

   private void applyChanges() {
      boolean isShowToolbar = viewSetWin.isShowToolbar();
      if (this.isShowToolbar != isShowToolbar) {
         mw.showToolbar(isShowToolbar);
         this.isShowToolbar = isShowToolbar;
      }

      boolean isShowStatusbar = viewSetWin.isShowStatusbar();
      if (this.isShowStatusbar != isShowStatusbar) {
         mw.showStatusbar(isShowStatusbar);
         this.isShowStatusbar = isShowStatusbar;
      }

      boolean isShowLineNumbers = viewSetWin.isShowLineNumbers();
      if (this.isShowLineNumbers != isShowLineNumbers) {
         this.isShowLineNumbers = isShowLineNumbers;
         this.showHideLineNumbers();
      }

      int selectedLafInd = viewSetWin.selectedLaf();
      if (this.selectedLafInd != selectedLafInd) {
         prefs.storePrefs("LaF",
               ViewSettingsWin.LAF_OPT[selectedLafInd]);
         this.selectedLafInd = selectedLafInd;
      }    
      viewSetWin.makeViewSetWinVisible(false);
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
         fMenu.selectWordWrap(false);
      }
      if (!isShowLineNumbers) {
         prefs.storePrefs(c.LINE_NUM_PREFS, c.HIDE);
      }
      else {
         prefs.storePrefs(c.LINE_NUM_PREFS, c.SHOW);
      }
   }
}
