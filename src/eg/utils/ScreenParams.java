package eg.utils;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Static constants and methods to obtain quantities that depend on the screen size and
 * resolution
 */
public class ScreenParams {
   
   public final static boolean IS_WINDOWS
         = System.getProperty("os.name").toLowerCase().contains("win");
   private final static String version = System.getProperty("java.version");
   private final static boolean IS_JAVA_9_OR_10 = version.startsWith("9")
         || version.startsWith("10"); 
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
    * Returns an integer that is the rounded product of the specified size and
    * the resolution ratio.<br>
    * This ratio is the screen resolution divided by the resolution assumed by Java's
    * Graphics2D (72 dpi). However, when the program is run using Java 9 the resolution
    * ratio is constantly 96/72 when the operating system is Windows and 1 otherwise.
    *
    * @param size  the size
    * @return  the rounded rescaled size
    */
   public static int scaledSize(double size) {
      if (IS_JAVA_9_OR_10) {
         if (IS_WINDOWS) {
            return (int) (Math.round(size * 96/72));
         }
         else {
            return (int) size;
         }
      }
      else {
         return (int) (Math.round(size * SCREEN_RES_RATIO));
      }
   }
}
