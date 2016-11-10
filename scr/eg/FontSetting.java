package eg;

import java.awt.event.ActionListener;

//--Eadgyth--//
import eg.document.TextDocument;
import eg.ui.MainWin;

/**
 * The setting of the font and font size
 */
public class FontSetting {

   private final FontSettingWin fontSetWin = new FontSettingWin();
   private int currentFontSize = 0;
   private String currentFont = null;
   private TextDocument[] txtDoc;

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
      for (int i = 0; i < txtDoc.length; i++) {
         if (txtDoc[i] != null) {
            txtDoc[i].setFont(currentFont);
            txtDoc[i].setFontSize(currentFontSize);
         }
      }
      fontSetWin.makeVisible(false);
   }
}