package eg.document.styledtext;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

//--Eadgyth--/
import eg.BackgroundTheme;

/**
 * Holds character attributes with colors depending on
 * the background theme.
 */
public final class Attributes {

   /**
    * The red colored, plain style which is a more or less
    * pink red depending on the background.
    */
   public final SimpleAttributeSet redPlain = new SimpleAttributeSet();
   /**
    * The blue colored, plain style which can be rather cyan
    * depending on the background
    */
   public final SimpleAttributeSet bluePlain = new SimpleAttributeSet();
   /**
    * The orange colored, plain style
    */
   public final SimpleAttributeSet orangePlain = new SimpleAttributeSet();
   /**
    * The purple colored, plain style
    */
   public final SimpleAttributeSet purplePlain = new SimpleAttributeSet();
   /**
    * The purple colored, bold style
    */
   public final SimpleAttributeSet purpleBold = new SimpleAttributeSet();
   /**
    * The bold style for brackets. The color is dark blue with
    * a white background and a yellow tone with a dark
    * background.
    * {@see eg.BackgroundTheme#accentedNormalText}
    */
   public final SimpleAttributeSet bracketsBold = new SimpleAttributeSet();
   /**
    * The color for comments
    * {@see eg.BackgroundTheme#commentText}
    */
   public final SimpleAttributeSet comment = new SimpleAttributeSet();

   /**
    * @param theme  the BackgroundTheme
    */
   public Attributes(BackgroundTheme theme) {

      StyleConstants.setForeground(redPlain, theme.redText());
      StyleConstants.setBold(redPlain, false);

      StyleConstants.setForeground(bluePlain, theme.blueText());
      StyleConstants.setBold(bluePlain, false);

      StyleConstants.setForeground(orangePlain, theme.orangeText());
      StyleConstants.setBold(orangePlain, false);

      StyleConstants.setForeground(purplePlain, theme.purpleText());
      StyleConstants.setBold(purplePlain, false);

      StyleConstants.setForeground(purpleBold, theme.purpleText());
      StyleConstants.setBold(purpleBold, true);

      StyleConstants.setForeground(comment, theme.commentText());
      StyleConstants.setBold(comment, false);

      StyleConstants.setForeground(bracketsBold, theme.accentedNormalText());
      StyleConstants.setBold(bracketsBold, true);
   }
}
