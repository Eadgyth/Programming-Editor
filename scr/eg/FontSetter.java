package eg;

//--Eadgyth--//
import eg.ui.EditArea;
import eg.ui.FontSettingWin;

/**
 * The setting of the font and font size
 */
public class FontSetter {

   private final FontSettingWin fontSetWin = new FontSettingWin();
   private final EditArea[] editArea;
   
   private String font;
   private int fontSize;

   public FontSetter(EditArea[] editArea) {
      font = fontSetWin.fontComboBxRes();
      fontSize = fontSetWin.sizeComboBxRes();
      this.editArea = editArea;
      fontSetWin.okAct(e -> setFont());
   }
   
   public void makeFontSetWinVisible(boolean isVisible) {
      fontSetWin.makeVisible(isVisible);
   }
   
   public String getFont() {
      return font;
   }
   
   public int getFontSize() {
      return fontSize;
   }

   private void setFont() {
      font = fontSetWin.fontComboBxRes();
      fontSize = fontSetWin.sizeComboBxRes();
      for (EditArea ea : editArea) {
         if (ea != null) {
             ea.setFont(font);
             ea.setFontSize(fontSize);
         }
      }
      fontSetWin.makeVisible(false);
   }
}
