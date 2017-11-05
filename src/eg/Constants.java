package eg;

import java.awt.Font;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import eg.utils.ScreenParams;

/**
 * Holds different static values
 */
public class Constants {

   //
   // Sizes

   /**
    * The integer used for bars (namely toolbars, tabbar) */
   public final static int BAR_HEIGHT = scaledSize(16);

   //
   // Colors

   /**
    * The gray color */
   public final static Color GRAY = new Color(100, 100, 100);

   /**
    * The ligher gray color */
   public final static Color LIGHT_GRAY = new Color(200, 200, 200);

   //
   // Borders

   /**
    * The line border with dark gray color */
   public final static Border GRAY_BORDER = new LineBorder(GRAY, 1);

   /**
    * The empty border with thickness of 5 pt */
   public final static Border EMPTY_BORDER = new EmptyBorder(5, 5, 5, 5);

   /**
    * The <code>MatteBorder</code> with inset of 1 pt in light gray at the top */
   public final static Border MATTE_TOP = new MatteBorder(1, 0, 0, 0,
         Constants.LIGHT_GRAY);

   //
   // Fonts

   /**
    * The font sans-serif, plain, size 9 pt (scaled) */
   public final static Font SANSSERIF_PLAIN_9
         = new Font("SansSerif", Font.PLAIN, scaledSize(9.0));
   /**
    * The font sans-serif, bold, size 9 pt (scaled) */
   public final static Font SANSSERIF_BOLD_9
         = new Font("SansSerif", Font.BOLD, scaledSize(9.0));
   /**
    * The font verdana, plain, size 9 pt (scaled) */
   public final static Font VERDANA_PLAIN_8
         = new Font("Verdana", Font.PLAIN, scaledSize(8.0));
   /**
    * The font consolas, plain, size 9 pt (scaled) */
   public final static Font CONSOLAS_PLAIN_9
         = new Font("Consolas", Font.PLAIN, scaledSize(9.0));

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
