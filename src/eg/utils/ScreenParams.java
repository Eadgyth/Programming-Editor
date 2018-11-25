package eg.utils;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Static constants and methods to obtain quantities that depend on the
 * screen size and resolution
 */
public class ScreenParams {

   private final static boolean IS_WINDOWS
         = System.getProperty("os.name").toLowerCase().contains("win");
         
   private final static String VERSION = System.getProperty("java.version");
   private final static boolean IS_JAVA_9_OR_HIGHER = !VERSION.startsWith("1.8");
   private final static int SCREEN_RES
         = Toolkit.getDefaultToolkit().getScreenResolution();
         
   private final static double SCREEN_RES_RATIO = SCREEN_RES / 72.0;

  /**
   * The screen size */
   public final static Dimension SCREEN_SIZE
        = Toolkit.getDefaultToolkit().getScreenSize();

   /**
    * Returns a new <code>Dimension</code> that is scaled to the
    * screen resolution ratio
    * @see #scaledSize(double)
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
    * Returns a scaled size that depends on the Java version, the OS or
    * the screen resolution.
    * <p>
    * If the program is run using Java 8 the scaled size is the product
    * of the specified size and the resolution ratio. This ratio is the
    * screen resolution divided by the graphics resolution assumed by
    * Java (72 dpi).
    * <p>
    * If the program is run using a higher Java version the specified size
    * is returned unchanged or, if the OS is Windows, multiplied with the
    * ratio 96/72.
    *
    * @param size  the size to scale
    * @return  the rounded scaled size
    */
   public static int scaledSize(double size) {
      if (IS_JAVA_9_OR_HIGHER) {
         if (IS_WINDOWS) {
            return (int) (Math.round(size * 96 / 72));
         }
         else {
            return (int) size;
         }
      }
      else {
         return (int) (Math.round(size * SCREEN_RES_RATIO));
      }
   }
   
   /**
    * Returns a size that reverts the scaling calculated by {@link #scaledSize}
    *
    * @param size  the size to scale
    * @return  the rounded scaled size
    */
   public static int invertedScaledSize(int size) {
      if (IS_JAVA_9_OR_HIGHER) {
         if (IS_WINDOWS) {
            return (int) (Math.round(size / (96 / 72)));
         }
         else {
            return (int) size;
         }
      }
      else {
         return (int) (Math.round(size / SCREEN_RES_RATIO));
      }
   }
}
