package eg;

import java.awt.Color;

/**
 * Defines colors for the background themes
 */
public class BackgroundTheme {

   private static final Prefs PREFS = new Prefs();

   private final Color background;
   private final Color normalText;
   private final Color accentedNormalText;
   private final Color redText;
   private final Color blueText;
   private final Color orangeText;
   private final Color purpleText;
   private final Color selectionBackground;

   /**
    * Returns a <code>BackgroundTheme</code> which is given by the
    * theme name read from <code>Prefs</code>
    *
    * @return  the BackgroundTheme
    */
   public static BackgroundTheme givenTheme() {
      String theme = PREFS.property(Prefs.THEME_KEY);
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
    * Returns the color for normal text
    *
    * @return  the color
    */
   public Color normalText() {
      return normalText;
   }

   /**
    * Returns the color for accented normal text
    *
    * @return  the color
    */
   public Color accentedNormalText() {
      return accentedNormalText;
   }

   /**
    * Returns the color for blue text
    *
    * @return  the color
    */
   public Color blueText() {
      return blueText;
   }

   /**
    * Returns the color for red text
    *
    * @return  the color
    */
   public Color redText() {
      return redText;
   }

   /**
    * Returns the color for orange text
    *
    * @return  the color
    */
   public Color orangeText() {
      return orangeText;
   }

   /**
    * Returns the color for purple text
    *
    * @return  the color
    */
   public Color purpleText() {
      return purpleText;
   }

   /**
    * Returns the color for the selection background
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
      boolean isDark = true;
      if ("Black".equals(theme)) {
         background = new Color(0, 0, 20);
         normalText = new Color(210, 210, 210);
      }
      else if ("Blue".equals(theme)) {
         background = new Color(20, 20, 70);
         normalText = new Color(228, 228, 228);
      }
      else if ("Gray".equals(theme)) {
         background = new Color(46, 46, 60);
         normalText = new Color(228, 228, 228);
      }
      else {
         background = Color.WHITE;
         normalText = Color.BLACK;
         isDark = false;
      }
      if (isDark) {
         accentedNormalText = new Color(230, 230, 100);
         blueText = new Color(0, 150, 255);
         redText = new Color(255, 80, 0);
         orangeText = new Color(244, 164, 0);
         purpleText = new Color(250, 0, 255);
         selectionBackground = new Color(50, 50, 250);
      }
      else {
         accentedNormalText = new Color(30, 30, 180);
         blueText = new Color(0, 20, 255);
         redText = new Color(255, 20, 20);
         orangeText = new Color(255, 130, 0);
         purpleText = new Color(180, 0, 210);
         selectionBackground = new Color(190, 230, 255);
      }
   }
}
