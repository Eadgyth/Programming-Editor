package eg;

//--Eadgyth--//
import eg.document.TextDocument;

/**
 * The setting of the font and font size
 */
public class FontSetting {

   private final FontSettingWin fontSetWin = new FontSettingWin();
   private final TextDocument[] txtDoc;

   public FontSetting(TextDocument[] txtDoc) {
      this.txtDoc = txtDoc;
      fontSetWin.okAct(e -> setFont());
   }
   
   public void makeFontSetWinVisible(boolean isVisible) {
      fontSetWin.makeVisible(isVisible);
   }

   private void setFont() {
      String currentFont = fontSetWin.fontComboBxRes();
      int currentFontSize = fontSetWin.sizeComboBxRes();
       for (TextDocument txt : txtDoc) {
           if (txt != null) {
               txt.setFont(currentFont);
               txt.setFontSize(currentFontSize);
           }
       }
      fontSetWin.makeVisible(false);
   }
}