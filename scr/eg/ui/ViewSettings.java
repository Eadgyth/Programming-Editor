package eg.ui;

//--Eadgyth--//
import eg.Preferences;
import eg.ui.EditArea;
import eg.Constants;

/**
 * Controls the display of the main window
 */
public class ViewSettings {

   private final MainWin mw;
   private final Menu menu;
   private final Preferences prefs = new Preferences();
   private final ViewSettingsWin viewSetWin = new ViewSettingsWin();
   
   private EditArea[] editArea;
   private int editAreaIndex;
   private boolean[] isWordWrap = new boolean[20];
   
   private boolean isShowToolbar;
   private boolean isShowLineNumbers;
   private boolean isShowStatusbar;
   private int selectedLafInd;
   
   public ViewSettings(MainWin mw, Menu menu) {
      this.mw = mw;
      this.menu = menu;
      
      prefs.readPrefs();

      isShowStatusbar = viewSetWin.isShowStatusbar();
      isShowToolbar = viewSetWin.isShowToolbar();
      isShowLineNumbers = viewSetWin.isShowLineNumbers();
      selectedLafInd = viewSetWin.selectedLaf();
      isWordWrap[0] = menu.isWordWrapSelected();

      menu.showConsoleAct(e -> showHideConsole());
      menu.showFileViewAct(e -> showHideFileView());
      menu.showFunctionPnlAct(e -> showHideFunctionPnl());
      menu.wordWrapAct(e -> enableWordWrap());

      menu.openViewSettingsAct(e -> viewSetWin.makeViewSetWinVisible(true));
      viewSetWin.okAct(e -> applyChanges());
   }
   
   public void setEditAreaArr(EditArea[] editArea) {
      this.editArea = editArea;
   }
   
   public void setEditAreaIndex(int index) {
      editAreaIndex = index;
      menu.selectWordWrapItm(isWordWrap[editAreaIndex]);
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
         prefs.storePrefs("LaF", ViewSettingsWin.LAF_OPT[selectedLafInd]);
         this.selectedLafInd = selectedLafInd;
      }    
      viewSetWin.makeViewSetWinVisible(false);
   }
   
   private void showHideLineNumbers() {
      boolean isWordWrapDisabled = false;
      for (int i = 0; i < editArea.length; i++) {
         if (editArea[i] != null && !isWordWrap[i]) {
            isWordWrapDisabled = true;
            if (!isShowLineNumbers) {
               editArea[i].hideLineNumbers();
            }
            else {
               editArea[i].showLineNumbers();
            }
            isWordWrap[editAreaIndex] = false;
         }
      }
      if (isWordWrapDisabled) {
         menu.selectWordWrapItm(false);
      }
      if (!isShowLineNumbers) {
         prefs.storePrefs("lineNumbers", Constants.HIDE);
      }
      else {
         prefs.storePrefs("lineNumbers", Constants.SHOW);
      }
   }
   
   private void enableWordWrap() {
      boolean isLineNumbers =
         Constants.SHOW.equals(prefs.prop.getProperty("lineNumbers"));
    
      if (menu.isWordWrapSelected()) {
         editArea[editAreaIndex].enableWordWrap();
         isWordWrap[editAreaIndex] = true;
         prefs.storePrefs("wordWrap", "enabled");
      }
      else {
         if (isLineNumbers) {
            editArea[editAreaIndex].showLineNumbers();
         }
         else {
            editArea[editAreaIndex].hideLineNumbers();
         }
         isWordWrap[editAreaIndex] = false;
         prefs.storePrefs("wordWrap", "disabled");
      }   
   }
   
   private void showHideConsole() {
      if (menu.isConsoleSelected()) {
         mw.showConsole();
      }
      else {
         mw.hideConsole();
      }
   }

   private void showHideFileView() {
      if (menu.isFileViewSelected()) {
         mw.showFileView();
      }
      else {
         mw.hideFileView();
      }
   }

   private void showHideFunctionPnl() {
      if (menu.isFunctionPnlSelected()) {
         mw.showFunctionPnl();
      }
      else {
         mw.hideFunctionPnl();
      }
   }
}