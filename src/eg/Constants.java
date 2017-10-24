package eg;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

/**
 * Holds different static values
 */
public class Constants {

  //
  // Screen parameters
  //

  /**
   * The screen size */
  public final static Dimension SCREEN_SIZE
        = Toolkit.getDefaultToolkit().getScreenSize();

  /**
   * The screen resolution */
  public final static int SCREEN_RES
        = Toolkit.getDefaultToolkit().getScreenResolution();

  /**
   * The ratio between the screen resolution and the resolution in Graphics */
  public final static double SCREEN_RES_RATIO = SCREEN_RES / 72.0;

   //
   // Colors
   //

   /**
    * The gray color */
   public final static Color GRAY = new Color(100, 100, 100);
   
   public final static Color WHITE = new Color(255, 255, 255);

   //
   // Borders
   //

   /**
    * The line border with dark gray color */
   public final static Border GRAY_BORDER = new LineBorder(GRAY, 1);
   
   /**
    * The empty border with thickness of 3 */
   public final static Border EMPTY_BORDER_3 = new EmptyBorder(3, 3, 3, 3); 

   /**
    * The empty border with thickness of 5 pt */
   public final static Border EMPTY_BORDER = new EmptyBorder(5, 5, 5, 5);

   //
   // Fonts
   //

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

    //
    // private
    //

    private static int scaledSize(double size) {
       return (int) (Math.round(size * SCREEN_RES_RATIO));
    }
}
