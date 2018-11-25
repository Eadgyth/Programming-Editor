package eg.document;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.Color;

//--Eadgyth--/
import eg.BackgroundTheme;

/**
 * Defines character attributes, each with the attributes foreground
 * color and weight. The shade of blue and the forground for brackets
 * depend on the background theme.
 */
public final class Attributes {

   public final SimpleAttributeSet redPlain     = new SimpleAttributeSet();
   public final SimpleAttributeSet bluePlain    = new SimpleAttributeSet();
   public final SimpleAttributeSet blueBold     = new SimpleAttributeSet();
   public final SimpleAttributeSet greenPlain   = new SimpleAttributeSet();
   public final SimpleAttributeSet orangePlain  = new SimpleAttributeSet();
   public final SimpleAttributeSet purplePlain  = new SimpleAttributeSet();
   public final SimpleAttributeSet bracketsBold = new SimpleAttributeSet();

   private final static Color GREEN  = new Color(80, 190, 80);
   private final static Color RED    = new Color(255, 20, 20);
   private final static Color ORANGE = new Color(255, 128, 0);
   private final static Color PURPLE = new Color(230, 0, 255);

   /**
    * @param theme  the BackgroundTheme
    */
   public Attributes(BackgroundTheme theme) {

      StyleConstants.setForeground(redPlain, RED);
      StyleConstants.setBold(redPlain, false);

      StyleConstants.setForeground(bluePlain, theme.blueForeground());
      StyleConstants.setBold(bluePlain, false);

      StyleConstants.setForeground(blueBold, theme.blueForeground());
      StyleConstants.setBold(blueBold, true);

      StyleConstants.setForeground(greenPlain, GREEN);
      StyleConstants.setBold(greenPlain, false);

      StyleConstants.setForeground(orangePlain, ORANGE);
      StyleConstants.setBold(orangePlain, false);

      StyleConstants.setForeground(purplePlain, PURPLE);
      StyleConstants.setBold(purplePlain, false);

      StyleConstants.setForeground(bracketsBold, theme.accentedNormalForeground());
      StyleConstants.setBold(bracketsBold, true);
   }
}
