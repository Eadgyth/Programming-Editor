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
 * Holds different static values. Sizes are scaled to the screen resolution
 * as defined in <code>ScreenParams</code>
 * @see ScreenParams
 */
public class Constants {

   //
   // Sizes

   /**
    * The scaled height for bars (toolbars, tabbar) */
   public final static int BAR_HEIGHT = scaledSize(15);

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
    * The line border with dark gray color */
   public final static Border GRAY_BORDER = new LineBorder(GRAY, 1);

   /**
    * The empty border with thickness of 5 pt */
   public final static Border EMPTY_BORDER_5 = new EmptyBorder(5, 5, 5, 5);
   
   /**
    * The empty border with thickness of 10 pt */
   public final static Border EMPTY_BORDER_10 = new EmptyBorder(10, 10, 10, 10);

   /**
    * The <code>MatteBorder</code> with inset of 1 pt in light gray at the top */
   public final static Border MATTE_TOP = new MatteBorder(1, 0, 0, 0,
         Constants.LIGHT_GRAY);
         
   /**
    * The <code>MatteBorder</code> with inset of 1 pt in light gray at the top
    * and bottom */
   public final static Border MATTE_TOP_BOTTOM = new MatteBorder(1, 0, 1, 0,
         Constants.LIGHT_GRAY);

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
