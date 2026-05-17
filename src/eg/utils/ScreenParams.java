package eg.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Toolkit;

/**
 * The screen size and parameters that depend on the screen
 * resolution.
 *
 * The scaling used in this class assumes that autoscaling is
 * disabled via <code>sun.java2d.uiScale</code> in the main
 * method when run on Java 9 or higher.
 */
public class ScreenParams {

  /**
   * The screen size */
   public static final Dimension SCREEN_SIZE
        = Toolkit.getDefaultToolkit().getScreenSize();

   private static final int SCREEN_RES
         = Toolkit.getDefaultToolkit().getScreenResolution();

   private static final GraphicsEnvironment GE
           = GraphicsEnvironment.getLocalGraphicsEnvironment();

   private static final double SCREEN_RES_RATIO = SCREEN_RES / 72.0;

   /**
    * Returns a new <code>Dimension</code> which may be scaled
    * depending on the criteria in {@link #scaledSize}
    *
    * @param unscaledWidth  the width in pt
    * @param unscaledHeight  the height in pt
    * @return   the Dimension
    */
   public static Dimension scaledDimension(int unscaledWidth, int unscaledHeight) {
      int width = scaledSize(unscaledWidth);
      int height = scaledSize(unscaledHeight);
      return new Dimension(width, height);
   }

   /**
    * Returns if currently a setting with more that one monitor
    * is present
    *
    * @return true for more that one monitor, false otherwise
    */
   public static boolean isMultipleScreens() {
      try {
         GraphicsDevice[] devices = GE.getScreenDevices();
         return devices.length > 1;
      } catch (HeadlessException e) {
         FileUtils.log(e);
         return false;
      }
   }

   /**
    * Returns the specified font with a scaled size and type plain
    *
    * @param f  the font
    * @param unscaledSize  the original unscaled size
    * @return  the screen-scaled font
    */
   public static Font scaledFontToPlain(Font f, int unscaledSize) {
      float s = scaledSize(unscaledSize);
      return f.deriveFont(Font.PLAIN, s);
   }

   /**
    * Returns the specified font with a scaled size and type bold
    *
    * @param f  the font
    * @param unscaledSize  the original unscaled size
    * @return  the screen-scaled font
    */
   public static Font scaledFontToBold(Font f, int unscaledSize) {
      float s = scaledSize(unscaledSize);
      return f.deriveFont(Font.BOLD, s);
   }

   /**
    * Returns the given size scaled to the screen resolution.
    *
    * <p>Java assumes a screen resolution of 72 dpi.On screens
    * with a higher DPI, this causes UI elements to render too
    * small. This method corrects for the actual screen DPI.
    *
    * @param  unscaledSize  the original unscaled size
    * @return  the screen-scaled size
    */
   public static int scaledSize(int unscaledSize) {
      return (int) (Math.round(unscaledSize * SCREEN_RES_RATIO));
   }

   /**
    * Returns the given screen-scaled size converted back to
    * its intended size.
    *
    * @param  scaledSize  the screen-scaled size
    * @return  the indended unscaled size
    */
   public static int invertedScaledSize(int scaledSize) {
      return (int) (Math.round(scaledSize / SCREEN_RES_RATIO));
   }

   //
   //--private--/
   //

   private ScreenParams() {}
}
