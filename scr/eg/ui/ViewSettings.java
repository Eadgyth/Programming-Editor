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
   
   public void setEditAreaArr(EditArea[] editArea) {
      this.editArea = editArea;
   }
   
   public void setEditAreaIndex(int index) {
      editAreaIndex = index;
      fMenu.selectWordWrap(editArea[index].isWordWrap());
   }
   
   public void makeViewSetWinVisible() {
      viewSetWin.makeViewSetWinVisible(true);
   }
   
   public void showHideLineNumbers() {
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
         prefs.storePrefs("lineNumbers", Constants.HIDE);
      }
      else {
         prefs.storePrefs("lineNumbers", Constants.SHOW);
      }
   }
   
   public void enableWordWrap() {
      boolean isLineNumbers =
         Constants.SHOW.equals(prefs.prop.getProperty("lineNumbers"));
    
      if (fMenu.isWordWrapSelected()) {
         editArea[editAreaIndex].enableWordWrap();
         prefs.storePrefs("wordWrap", "enabled");
      }
      else {
         if (isLineNumbers) {
            editArea[editAreaIndex].showLineNumbers();
         }
         else {
            editArea[editAreaIndex].hideLineNumbers();
         }
         prefs.storePrefs("wordWrap", "disabled");
      }   
   }
   
   public boolean isConsoleSelected() {
      return vMenu.isConsoleSelected();
   }
   
   public void setShowConsoleState(boolean show) {
      showConsole(show);
      vMenu.selectShowConsole(show);
   }
   
   public void setShowFileViewState(boolean show) {
      showFileView(show);
      vMenu.selectShowFileView(show);
   }
   
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
}