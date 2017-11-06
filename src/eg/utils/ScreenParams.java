package eg.utils;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Static methods to obtain quantities that depend on the screen resolution
 */
public class ScreenParams {
 
   private final static int SCREEN_RES
        = Toolkit.getDefaultToolkit().getScreenResolution();

   private final static double SCREEN_RES_RATIO = SCREEN_RES / 72.0;
   
  /**
   * The screen size */
   public final static Dimension SCREEN_SIZE
        = Toolkit.getDefaultToolkit().getScreenSize();
   
   /**
    * Returns the Dimension with the specified width and height scaled
    * to the ratio between screen resolution and graphic resolution
    *
    * @param width  the width in pt
    * @param height  the height in pt
    * @return   a new scaled Dimension
    */
   public static Dimension scaledDimension(int width, int height) {
      width = scaledSize(width);
      height = scaledSize(height);
      return new Dimension(width, height);
   }
  
   /**
    * Returns an integer that is the rounded product of the specified
    * size and the ratio between the screen resolution and the resolution
    * assumed by Java's Graphics2D
    *
    * @param size  the size
    * @return  the rounded rescaled size
    */
   public static int scaledSize(double size) {
      return (int) (Math.round(size * SCREEN_RES_RATIO));
   }
}
