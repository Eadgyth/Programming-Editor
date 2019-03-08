package eg.utils;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Static constants and methods to obtain quantities that depend on the
 * screen size and resolution
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
    * Returns a new <code>Dimension</code> which may be scaled
    * depending on the criteria in {@link #scaledSize}
    *
    * @param width  the width in pt
    * @param height  the height in pt
    * @return   the Dimension
    */
   public static Dimension scaledDimension(int width, int height) {
      width = scaledSize(width);
      height = scaledSize(height);
      return new Dimension(width, height);
   }

   /**
    * Returns a size which may be scaled depending on the Java version,
    * the OS or the screen resolution.
    * <p>
    * If the Java version is 8 the scaled size is the product of the
    * specified size and the resolution ratio. This ratio is the
    * screen resolution divided by the graphics resolution assumed
    * by Java graphics (72 dpi).
    * <p>
    * If the program is run using a higher Java version the specified
    * size is returned unchanged or, if the OS is Windows, multiplied
    * with the ratio 96/72.
    *
    * @param size  the size to scale
    * @return  the rounded scaled size
    */
   public static int scaledSize(int size) {
      if (SystemParams.IS_JAVA_9_OR_HIGHER) {
         if (SystemParams.IS_WINDOWS) {
            return Math.round(size * 96 / 72);
         }
         else {
            return size;
         }
      }
      else {
         return (int) (Math.round(size * SCREEN_RES_RATIO));
      }
   }

   /**
    * Returns a size that is the inversion of the scaling calculated
    * by {@link #scaledSize}
    *
    * @param size  the size to scale
    * @return  the rounded inverted scaled size
    */
   public static int invertedScaledSize(int size) {
      if (SystemParams.IS_JAVA_9_OR_HIGHER) {
         if (SystemParams.IS_WINDOWS) {
            return Math.round(size / (96 / 72));
         }
         else {
            return size;
         }
      }
      else {
         return (int) (Math.round(size / SCREEN_RES_RATIO));
      }
   }

   //
   //--private--/
   //

   private ScreenParams() {}
}
