package eg;

import java.awt.Color;
import javax.swing.UIManager;

/**
 * Defines colors for the background themes.
 * <p>
 * The background theme 'dark blue' corresponds to the 'Dracula'
 * theme (https://github.com/dracula/dracula-theme) with a few
 * modifications. Text colors of 'Dracula' are also partly adopted
 * for the other dark backgrounds or modified starting from
 * there.
 */
public class BackgroundTheme {

   private static final Prefs PREFS = new Prefs();
   private boolean isDark = false;
   //
   // text
   private final Color background;
   private final Color normalText;
   private final Color accentedNormalText;
   private final Color redText;
   private final Color blueText;
   private final Color orangeText;
   private final Color purpleText;
   private final Color commentText;
   private final Color selectionBackground;
   //
   // Components
   private final Color lightBackground;
   private final Color scrollbarThumb;
   private final Color scrollbarTrack;
   private final Color lineBorder;

   /**
    * Creates a <code>BackgroundTheme</code> which is given by the
    * theme name read from <code>Prefs</code>.
    *
    * @return  the BackgroundTheme
    */
   public static BackgroundTheme givenTheme() {
      String theme = PREFS.property(Prefs.THEME_KEY);
      return new BackgroundTheme(theme);
   }

   /**
    * Creates a <code>BackgroundThme</code> that has the white
    * background
    *
    * @return  the BackgroundTheme
    */
   public static BackgroundTheme whiteTheme() {
      String theme = "White";
      return new BackgroundTheme(theme);
   }

   /**
    * Returns if colors are definded for any of the dark
    * backgrounds, i.e. dark blue, gray, dark gray, or black
    *
    * @return true for dark, false for white
    */
   public boolean isDark() {
      return isDark;
   }

   /**
    * Returns the background color for text areas
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
    * Returns the color for blue text which can be rather cyan
    * depending on the background
    *
    * @return  the color
    */
   public Color blueText() {
      return blueText;
   }

   /**
    * Returns the color for red text which is a more or less
    * pink red depending on the background.
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
    * Returns the color for 'accented' normal text. This
    * is a dark blue in the case of a white background and
    * a yellow tone in the case of a dark background
    *
    * @return  the color
    */
   public Color accentedNormalText() {
      return accentedNormalText;
   }

   /**
    * Returns the color for comments which is a green tone
    * or, in the case of the 'Dracula' dark blue background,
    * the corresponding blue-gray color
    *
    * @return the color
    */
   public Color commentText() {
      return commentText;
   }

   /**
    * Returns the color for the text selection background
    *
    * @return the color
    */
   public Color selectionBackground() {
      return selectionBackground;
   }

   /**
    * Returns a lighter version of the background color.
    * If the theme name is 'White' the 'panel' gray of the
    * current LaF is returned
    *
    * @return the color
    */
   public Color lightBackground() {
      return lightBackground;
   }

   /**
    * Returns the color for the scrollbar thumb but throws
    * an exception if this <code>BackgroundTheme</code> is not
    * 'dark'
    *
    * @return  the color
    */
   public Color scrollbarThumb() {
      if (scrollbarThumb == null) {
         throw new IllegalStateException(
               "The background theme is \"White\" and a "
               + "scrollbar thumb color not available.");
      }
      return scrollbarThumb;
   }

   /**
    * Returns the color for the scrollbar track but throws
    * an exception if this <code>BackgroundTheme</code> is not
    * 'dark'
    *
    * @return  the color
    */
   public Color scrollbarTrack() {
      if (scrollbarTrack == null) {
         throw new IllegalStateException(
               "The background theme is \"White\" and a "
               + "scrollbar track color not available.");
      }
      return scrollbarTrack;
   }

   /**
    * Returns the gray tone for line borders
    *
    * @return  the color
    */
   public Color lineBorder() {
      return lineBorder;
   }

   //
   //--private--/
   //

   private BackgroundTheme(String theme) {
      isDark = !"White".equals(theme);

      if ("Dark Blue".equals(theme)) {// Dracula (D.)
         background = new Color(40, 42, 54);
         normalText = new Color(236, 236, 230);// D. -5%
         redText = new Color(255, 121, 198);//D. pink
         blueText = new Color(125, 210, 228);//D. cyan -10%
         purpleText = new Color(189, 147, 249);
         orangeText = new Color(244, 184, 108);
         accentedNormalText = new Color(193, 200, 112);// D. yellow -20%
         commentText = new Color(108, 125, 182);// D. +10%
         selectionBackground = new Color(53, 73, 166);// not D.

         lightBackground = new Color(64, 67, 86);
         scrollbarThumb = new Color(91, 96, 124);
         scrollbarTrack = new Color(36, 38, 49);
         lineBorder = new Color(110, 110, 110);
      }
      else if ("Gray".equals(theme)) {
         background = new Color(57, 57, 57);
         normalText = new Color(250, 250, 250);
         redText = new Color(255, 128, 198); // 255, 158, 203
         blueText = new Color(143, 224, 250);
         purpleText = new Color(222, 163, 255);
         orangeText = new Color(255, 177, 87);
         accentedNormalText = new Color(201, 207, 130);
         commentText = new Color(167, 205, 167);
         selectionBackground = new Color(53, 73, 166);

         lightBackground = new Color(91, 91, 91);
         scrollbarThumb = new Color(120, 120, 120);
         scrollbarTrack = new Color(51, 51, 51);
         lineBorder = new Color(130, 130, 130);
      }
      else if ("Dark Gray".equals(theme)) {
         background = new Color(37, 37, 37);
         normalText = new Color(220, 220, 220);
         redText = new Color(255, 107, 192);
         blueText = new Color(143, 178, 250);
         purpleText = new Color(191, 112, 255);
         orangeText = new Color(244, 184, 108);
         accentedNormalText = new Color(171, 186, 69);
         commentText = new Color(102, 153, 102);
         selectionBackground = new Color(53, 73, 166);

         lightBackground = new Color(66, 66, 66);
         scrollbarThumb = new Color(95, 95, 95);
         scrollbarTrack = new Color(33, 33, 33);
         lineBorder = new Color(110, 110, 110);
      }
      else if ("Black".equals(theme)) {
         background = new Color(18, 18, 18);
         normalText = new Color(210, 210, 210);
         redText = new Color(245, 77, 150);
         blueText = new Color(107, 141, 250);
         purpleText = new Color(191, 112, 255);
         orangeText = new Color(225, 165, 25);
         accentedNormalText = new Color(157, 169, 65);
         commentText = new Color(102, 153, 102);
         selectionBackground = new Color(53, 73, 166);

         lightBackground = new Color(59, 59, 59);
         scrollbarThumb = new Color(80, 80, 80);
         scrollbarTrack = new Color(8, 8, 8);
         lineBorder = new Color(95, 95, 95);
      }
      else {
         background = Color.WHITE;
         normalText = Color.BLACK;
         redText = new Color(230, 0, 75);
         blueText = new Color(0, 20, 255);
         purpleText = new Color(158, 43, 226);
         orangeText = new Color(255, 118, 26);
         accentedNormalText = new Color(30, 30, 180);
         commentText = new Color(56, 142, 56);
         selectionBackground = new Color(173, 212, 250);

         lightBackground = UIManager.getColor("Panel.background");
         scrollbarThumb = null;
         scrollbarTrack = null;
         lineBorder = new Color(150, 150, 150);
      }
   }
}
