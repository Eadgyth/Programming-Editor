package eg.ui;

import java.awt.Font;

//--Eadgyth--/
import eg.utils.ScreenParams;

/**
 * Holds static fonts whose sizes are scaled to the screen resolution.
 * The indicated font sizes refer to the unscaled size (72 dpi).
 * @see ScreenParams#scaledSize
 */
public class Fonts {

   /**
    * Sans-serif, plain, size 9 pt */
   public final static Font SANSSERIF_PLAIN_8
         = new Font("SansSerif", Font.PLAIN, scaledSize(8.0));

   /**
    * Sans-serif, bold, size 8 pt */
   public final static Font SANSSERIF_BOLD_8
         = new Font("SansSerif", Font.BOLD, scaledSize(8.0));

   /**
    * Sans-serif, plain, size 9 pt */
   public final static Font SANSSERIF_PLAIN_9
         = new Font("SansSerif", Font.PLAIN, scaledSize(9.0));

   /**
    * Sans-serif, bold, size 9 pt */
   public final static Font SANSSERIF_BOLD_9
         = new Font("SansSerif", Font.BOLD, scaledSize(9.0));

  /**
    * Sans-serif, bold, size 11 pt */
   public final static Font SANSSERIF_BOLD_11
         = new Font("SansSerif", Font.BOLD, scaledSize(11.0));

   /**
    * Verdana, plain, size 9 pt */
   public final static Font VERDANA_PLAIN_8
         = new Font("Verdana", Font.PLAIN, scaledSize(8.0));

   //
   //--private--/
   //

   private static int scaledSize(double size) {
      return ScreenParams.scaledSize(size);
   }
}
