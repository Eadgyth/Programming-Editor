package eg.document.styledtext;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.Color;

//--Eadgyth--/
import eg.BackgroundTheme;

/**
 * Holds character attributes, each with a foreground color and a
 * weight. The shade of colors depend on the background theme.
 */
public final class Attributes {

   public final SimpleAttributeSet redPlain     = new SimpleAttributeSet();
   public final SimpleAttributeSet bluePlain    = new SimpleAttributeSet();
   public final SimpleAttributeSet greenPlain   = new SimpleAttributeSet();
   public final SimpleAttributeSet orangePlain  = new SimpleAttributeSet();
   public final SimpleAttributeSet purplePlain  = new SimpleAttributeSet();
   public final SimpleAttributeSet bracketsBold = new SimpleAttributeSet();

   private static final Color GREEN  = new Color(80, 190, 80);

   /**
    * @param theme  the BackgroundTheme
    */
   public Attributes(BackgroundTheme theme) {

      StyleConstants.setForeground(redPlain, theme.redText());
      StyleConstants.setBold(redPlain, false);

      StyleConstants.setForeground(bluePlain, theme.blueText());
      StyleConstants.setBold(bluePlain, false);

      StyleConstants.setForeground(greenPlain, GREEN);
      StyleConstants.setBold(greenPlain, false);

      StyleConstants.setForeground(orangePlain, theme.orangeText());
      StyleConstants.setBold(orangePlain, false);

      StyleConstants.setForeground(purplePlain, theme.purpleText());
      StyleConstants.setBold(purplePlain, false);

      StyleConstants.setForeground(bracketsBold, theme.accentedNormalText());
      StyleConstants.setBold(bracketsBold, true);
   }
}
