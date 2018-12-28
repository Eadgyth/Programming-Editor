package eg;

import java.awt.Color;

/**
 * Defines colors for the dark and white background themes.<br>
 */
public class BackgroundTheme {

   private final static Prefs PREFS = new Prefs();

   private final Color background;
   private final Color normalForeground;
   private final Color accentedNormalForeground;
   private final Color blueForeground;
   private final Color selectionBackground;

   /**
    * Returns a <code>BackgroundTheme</code> in which the theme
    * is given by the theme name read from <code>Prefs</code>
    *
    * @return  the BackgroundTheme
    */
   public static BackgroundTheme givenTheme() {
      String theme = PREFS.getProperty("Background");
      return new BackgroundTheme(theme);
   }

   /**
    * Creates a <code>BackgroundThme</code> that has the white background
    *
    * @return  the BackgroundTheme
    */
   public static BackgroundTheme whiteTheme() {
      String theme = "White";
      return new BackgroundTheme(theme);
   }

   /**
    * Returns the background color
    *
    * @return  the color
    */
   public Color background() {
      return background;
   }

   /**
    * Returns the foreground for normal text
    *
    * @return  the color
    */
   public Color normalForeground() {
      return normalForeground;
   }

   /**
    * Returns the foreground for accented normal text. This is light
    * yellow and dark blue for the dark and white themes, respectively
    *
    * @return  the color
    */
   public Color accentedNormalForeground() {
      return accentedNormalForeground;
   }

   /**
    * Returns the foreground for blue colored text
    *
    * @return  the color
    */
   public Color blueForeground() {
      return blueForeground;
   }

   /**
    * Returns the selection background color
    *
    * @return the color
    */
   public Color selectionBackground() {
      return selectionBackground;
   }

   //
   //--private--/
   //

   private BackgroundTheme(String theme) {
      if ("Dark".equals(theme)) {
         background = new Color(0, 0, 20);
         normalForeground = new Color(228, 228, 228);
         accentedNormalForeground = new Color(200, 200, 100);
         blueForeground = new Color(0, 160, 255);
         selectionBackground = new Color(50, 50, 250);
      }
      else {
         background = Color.WHITE;
         normalForeground = Color.BLACK;
         accentedNormalForeground = new Color(30, 30, 180);
         blueForeground = new Color(0, 20, 255);
         selectionBackground = new Color(190, 230, 255);
      }
   }
}
