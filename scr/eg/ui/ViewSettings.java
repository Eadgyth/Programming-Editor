package eg.ui;

//--Eadgyth--//
import eg.Preferences;
import eg.document.TextDocument;
import eg.Constants;

/**
 * Methods to respond to action events that change the display
 * of the main window
 */
public class ViewSettings {

   private final MainWin mw;
   private final TextDocument[] txtDoc;
   private final Preferences prefs = new Preferences();
   private final ViewSettingsWin viewSetWin = new ViewSettingsWin();
   
   private boolean isShowToolbar;
   private boolean isShowLineNumbers;
   private boolean isShowStatusbar;
   private int selectedLafInd;
   
   public ViewSettings(MainWin mw, TextDocument[] txtDoc) {
      this.mw = mw;
      this.txtDoc = txtDoc;
      isShowStatusbar = viewSetWin.isShowStatusbar();
      isShowToolbar = viewSetWin.isShowToolbar();
      isShowLineNumbers = viewSetWin.isShowLineNumbers();
      selectedLafInd = viewSetWin.selectedLaf();
      viewSetWin.okAct(e -> applyChanges());
   }
   
   public void makeSetWinVisible() {
      viewSetWin.makeViewSetWinVisible(true);
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
      for (int i = 0; i < txtDoc.length; i++) {
         if (txtDoc[i] != null) {
            if (!isShowLineNumbers) {
               txtDoc[i].hideLineNumbers();
            }
            else {
               txtDoc[i].showLineNumbers();
            }
         }
      }
      if (!isShowLineNumbers) {
         prefs.storePrefs("lineNumbers", Constants.HIDE);
      }
      else {
         prefs.storePrefs("lineNumbers", Constants.SHOW);
      }
   }
}