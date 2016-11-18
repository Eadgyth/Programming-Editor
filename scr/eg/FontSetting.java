package eg;

//--Eadgyth--//
import eg.ui.EditArea;

/**
 * The setting of the font and font size
 */
public class FontSetting {

   private final FontSettingWin fontSetWin = new FontSettingWin();
   private final EditArea[] editArea;

   public FontSetting(EditArea[] editArea) {
      this.editArea = editArea;
      fontSetWin.okAct(e -> setFont());
   }
   
   public void makeFontSetWinVisible(boolean isVisible) {
      fontSetWin.makeVisible(isVisible);
   }

   private void setFont() {
      String currentFont = fontSetWin.fontComboBxRes();
      int currentFontSize = fontSetWin.sizeComboBxRes();
       for (EditArea ea : editArea) {
           if (ea != null) {
               ea.setFont(currentFont);
               ea.setFontSize(currentFontSize);
           }
       }
      fontSetWin.makeVisible(false);
   }
}