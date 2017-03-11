package eg;

import eg.ui.menu.FormatMenu;
import eg.ui.EditArea;
import eg.ui.ViewSettingWin;

/**
 * The setting of the view of the {@code EditArea}
 */
public class EditAreaView {
   
   private final FormatMenu fMenu;
   private final ViewSettingWin viewSetWin;
   private final Preferences prefs = new Preferences();
   
   private EditArea[] editArea;
   private EditArea currEdArea;
   
   private boolean isWordWrap;
   private boolean isShowLineNumbers;
   
   public EditAreaView(ViewSettingWin viewSetWin, FormatMenu fMenu) {
      this.viewSetWin = viewSetWin;
      this.fMenu = fMenu;
      isShowLineNumbers = viewSetWin.isShowLineNumbers();
      prefs.readPrefs();
      isWordWrap = "enabled".equals(prefs.getProperty("wordWrap"));
      fMenu.changeWordWrapAct(e -> changeWordWrap());
   }
   
   /**
    * Sets the array of type {@code EditArea}
    * @param editArea  the array of {@link EditArea}
    */
   public void setEditAreaArr(EditArea[] editArea) {
      this.editArea = editArea;
   }

  /**
    * Sets the {@code EditArea} at the specified index.
    * <p>
    * The method also selects/unselects the wordwrap menu
    * item depending on te state of the {@code EditArea} at the given
    * index
    * @param index  the index of the {@link EditArea} element
    */
   public void setEditAreaIndex(int index) {
      currEdArea = editArea[index];
      fMenu.selectWordWrapItm(currEdArea.isWordWrap());
   }
   
   /**
    * If enabling word wrap is currently selected.
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
    * Changes the wordwrap state in the {@code EditArea} of the
    * selected document
    */
   public void changeWordWrap() {
      this.isWordWrap = fMenu.isWordWrapItmSelected();
      if (isWordWrap) {
         currEdArea.enableWordWrap();
      }
      else {
         if (isShowLineNumbers) {
            currEdArea.showLineNumbers();
         }
         else {
            currEdArea.hideLineNumbers();
         }
      }
      String state = isWordWrap ? "enabled" : "disabled";
      prefs.storePrefs("wordWrap", state);  
   }

   /**
    * Applies the selection in the {@link ViewSettingWin} to show/hide
    * line numbers
    */
   public void applySetWinOk() {
      boolean show = false;
      String state = null;
   
      show = viewSetWin.isShowLineNumbers();
      if (isShowLineNumbers != show) {
         isShowLineNumbers = show;
         showHideLineNumbers();
         state = isShowLineNumbers ? "show" : "hide";
         prefs.storePrefs("lineNumbers", state);
      }
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
