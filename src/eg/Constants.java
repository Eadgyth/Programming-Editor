package eg;

import java.awt.Font;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

//--Eadgyth--/
import eg.utils.ScreenParams;

/**
 * Holds different static values.
 * <p>
 * The note 'scaled' in the comments means that sizes may be scaled to
 * the screen resolution.
 * @see ScreenParams#scaledSize
 */
public class Constants {

   //
   // Sizes

   /**
    * The scaled height for bars */
   public final static int BAR_HEIGHT = scaledSize(17);

   //
   // Colors

   /**
    * The gray color */
   public final static Color GRAY = new Color(100, 100, 100);

   /**
    * The ligh gray color */
   public final static Color LIGHT_GRAY = new Color(200, 200, 200);

   //
   // Borders

   /**
    * The line border with gray color */
   public final static Border GRAY_LINE_BORDER = new LineBorder(Constants.GRAY, 1);

   /**
    * The empty border with thickness of 5 pt */
   public final static Border EMPTY_BORDER_5 = new EmptyBorder(5, 5, 5, 5);

   /**
    * The empty border with thickness of 0 pt */
   public final static Border EMPTY_BORDER_0 = new EmptyBorder(0, 0, 0, 0);

   /**
    * The empty border with thickness of 10 pt */
   public final static Border EMPTY_BORDER_10 = new EmptyBorder(10, 10, 10, 10);

   /**
    * The <code>MatteBorder</code> with inset of 1 pt in light gray at
    * the top
    */
   public final static Border MATTE_TOP_LIGHT_GRAY = new MatteBorder(1, 0, 0, 0,
         Constants.LIGHT_GRAY);

   /**
    * The <code>MatteBorder</code> with inset of 1 pt in gray at the top */
   public final static Border MATTE_TOP_GREY = new MatteBorder(1, 0, 0, 0,
         Constants.GRAY);

   //
   // Fonts

   /**
    * The font sans-serif, plain, size 9 pt (scaled) */
   public final static Font SANSSERIF_PLAIN_8
         = new Font("SansSerif", Font.PLAIN, scaledSize(8.0));

   /**
    * The font sans-serif, bold, size 8 pt (scaled) */
   public final static Font SANSSERIF_BOLD_8
         = new Font("SansSerif", Font.BOLD, scaledSize(8.0));

   /**
    * The font sans-serif, plain, size 9 pt (scaled) */
   public final static Font SANSSERIF_PLAIN_9
         = new Font("SansSerif", Font.PLAIN, scaledSize(9.0));

   /**
    * The font sans-serif, bold, size 9 pt (scaled) */
   public final static Font SANSSERIF_BOLD_9
         = new Font("SansSerif", Font.BOLD, scaledSize(9.0));

  /**
    * The font sans-serif, bold, size 11 pt (scaled) */
   public final static Font SANSSERIF_BOLD_11
         = new Font("SansSerif", Font.BOLD, scaledSize(11.0));

   /**
    * The font verdana, plain, size 9 pt (scaled) */
   public final static Font VERDANA_PLAIN_8
         = new Font("Verdana", Font.PLAIN, scaledSize(8.0));

   //
   // Strings

   /**
    * The system's line separator */
   public final static String LINE_SEP = System.lineSeparator();

   //
   //--private--/
   //

   private static int scaledSize(double size) {
      return ScreenParams.scaledSize(size);
   }
}
