package eg;

import eg.ui.menu.FormatMenu;
import eg.ui.EditArea;
import eg.ui.ViewSettingWin;
import eg.ui.FontSettingWin;

/**
 * Represents the formatting of the {@code EditArea}.
 * <p>
 * The initial parameters are given by entries in the prefs file.
 */
public class EditAreaFormat {
   
   private final FormatMenu fMenu;
   private final ViewSettingWin viewSetWin;
   private final FontSettingWin fontSetWin = new FontSettingWin();
   private final Preferences prefs = new Preferences();

   private EditArea[] editArea;  
   private EditArea currEdArea;
   
   private boolean isWordWrap;
   private boolean isShowLineNr;
   private String font;
   private int fontSize;
   
   public EditAreaFormat(ViewSettingWin viewSetWin, FormatMenu fMenu) {
      this.viewSetWin = viewSetWin;
      this.fMenu = fMenu;

      isShowLineNr = viewSetWin.isShowLineNumbers();
      prefs.readPrefs();
      isWordWrap = "enabled".equals(prefs.getProperty("wordWrap"));
      font = fontSetWin.fontComboBxRes();
      fontSize = fontSetWin.sizeComboBxRes();

      fMenu.changeWordWrapAct(e -> changeWordWrap());
      fMenu.fontAct(e -> fontSetWin.makeVisible(true));
      fontSetWin.okAct(e -> setFont());
   }
   
   /**
    * Sets the array of type {@code EditArea}
    *
    * @param editArea  the array of type {@link EditArea}
    */
   public void setEditAreaArr(EditArea[] editArea) {
      this.editArea = editArea;
   }
   
   /** 
    * Returns a new {@code EditArea} that is initialized with the
    * current settings
    *
    * @return  a new {@link EditArea} that is initialized with the
    * current settings
    */
   public EditArea createEditArea() {
      return new EditArea(isWordWrap, isShowLineNr, font, fontSize);
   }

  /**
    * Selects an element from this array of {@code EditArea}.
    * <p>
    * The method also selects/unselects the wordwrap menu item
    * depending on the state of the {@code EditArea} at the specified
    * index
    *
    * @param index  the index of the selected {@link EditArea} element
    */
   public void setCurrEditArea(int index) {
      currEdArea = editArea[index];
      fMenu.selectWordWrapItm(currEdArea.isWordWrap());
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
         if (isShowLineNr) {
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
      if (isShowLineNr != show) {
         isShowLineNr = show;
         showHideLineNumbers();
         state = isShowLineNr ? "show" : "hide";
         prefs.storePrefs("lineNumbers", state);
      }
   }
   
   //
   //--private methods
   //

   private void showHideLineNumbers() {
      for (EditArea ea : editArea) {
         if (ea != null && !ea.isWordWrap()) {
            if (!isShowLineNr) {
               ea.hideLineNumbers();
            } else {
               ea.showLineNumbers();
            }
         }
      }
   }
   
   private void setFont() {
      font = fontSetWin.fontComboBxRes();
      fontSize = fontSetWin.sizeComboBxRes();
      for (EditArea ea : editArea) {
         if (ea != null) {
             ea.setFont(font);
             ea.setFontSize(fontSize);
             ea.revalidateLineAreaWidth();
         }
      }
      fontSetWin.makeVisible(false);
   }
}
