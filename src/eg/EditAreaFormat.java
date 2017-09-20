package eg;

import eg.ui.EditArea;
import eg.ui.ViewSettingWin;
import eg.ui.FontSettingWin;
import eg.ui.menu.FormatMenu;

/**
 * The formatting (word wrap, font, display of line numbers) for
 * <code>EditArea</code> objects.
 * <p>The font is applied to all objects, whereas wordwrap and the
 * visibility of line numbers is applied to the currently viewed editor
 * <p>The initial parameters are given by entries in the prefs file.
 */
public class EditAreaFormat {
   
   private final ViewSettingWin viewSetWin;
   private final FormatMenu fMenu;
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
    * Selects an element from this array of {@code EditArea} and
    * selects/unselects the wordwrap menu item depending on the state
    * of that element
    *
    * @param i  the index of the array element
    */
   public void setEditAreaAt(int i) {
      currEdArea = editArea[i];
      fMenu.selectWordWrapItm(currEdArea.isWordWrap());
   }
   
   /** 
    * Returns a new {@code EditArea} that is initialized with the
    * current settings
    *
    * @return  a new {@link EditArea}
    */
   public EditArea createEditArea() {
      return new EditArea(isWordWrap, isShowLineNr, font, fontSize);
   }
   
   /**
    * Changes the wordwrap state of the <code>EditArea</code>
    * selected by {@link #setEditArea(int)}
    */
   public void changeWordWrap() {
      isWordWrap = fMenu.isWordWrapItmSelected();
      if (isWordWrap) {
         currEdArea.enableWordWrap();
      }
      else {
         showLineNumbers(currEdArea);
      }
      String state = isWordWrap ? "enabled" : "disabled";
      prefs.storePrefs("wordWrap", state);
   }

   /**
    * Applies the selection in the {@link ViewSettingWin} to show or hide
    * line numbers
    */
   public void applySetWinOk() {
      boolean show = viewSetWin.isShowLineNumbers();
      if (isShowLineNr != show) {
         isShowLineNr = show;
         for (EditArea ea : editArea) {
            if (ea != null && !ea.isWordWrap()) {
               showLineNumbers(ea);
            }
         }
         String state = isShowLineNr ? "show" : "hide";
         prefs.storePrefs("lineNumbers", state);
      }
   }
   
   //
   //--private methods
   //
   
   private void showLineNumbers(EditArea ea) {
      if (isShowLineNr) {
         ea.showLineNumbers();
      } else {
         ea.hideLineNumbers();
      }
   }
   
   private void setFont() {
      font = fontSetWin.fontComboBxRes();
      fontSize = fontSetWin.sizeComboBxRes();
      for (EditArea ea : editArea) {
         if (ea != null) {
             ea.setFont(font, fontSize);
             ea.revalidateLineAreaWidth();
         }
      }
      fontSetWin.makeVisible(false);
   }
}
