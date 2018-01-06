package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.Color;

/**
 * Defines static attribute sets each with the attributes foreground color and
 * weight (plain or bold)
 * 
 */ 
public class Attributes {
   
   public final static SimpleAttributeSet RED_PLAIN    = new SimpleAttributeSet();
   public final static SimpleAttributeSet RED_BOLD     = new SimpleAttributeSet();
   public final static SimpleAttributeSet BLUE_PLAIN   = new SimpleAttributeSet();
   public final static SimpleAttributeSet BLUE_BOLD    = new SimpleAttributeSet();
   public final static SimpleAttributeSet GREEN_PLAIN  = new SimpleAttributeSet();
   public final static SimpleAttributeSet GRAY_BOLD    = new SimpleAttributeSet();
   public final static SimpleAttributeSet ORANGE_PLAIN = new SimpleAttributeSet();
   public final static SimpleAttributeSet PURPLE_PLAIN = new SimpleAttributeSet();

   private final static Color BLUE   = new Color(20, 30, 255);
   private final static Color RED    = new Color(250, 0, 50);
   private final static Color GREEN  = new Color(80, 190, 80);
   private final static Color GRAY   = new Color(30, 30, 30);
   private final static Color ORANGE = new Color(240, 140, 0);
   private final static Color PURPLE = new Color(150, 0, 255);
   
   static {
      StyleConstants.setForeground(RED_PLAIN, RED);
      StyleConstants.setBold(RED_PLAIN, false);
      
      StyleConstants.setForeground(RED_BOLD, RED);
      StyleConstants.setBold(RED_BOLD, true);

      StyleConstants.setForeground(BLUE_PLAIN, BLUE);
      StyleConstants.setBold(BLUE_PLAIN, false);

      StyleConstants.setForeground(BLUE_BOLD, BLUE);
      StyleConstants.setBold(BLUE_BOLD, true);

      StyleConstants.setForeground(GREEN_PLAIN, GREEN);
      StyleConstants.setBold(GREEN_PLAIN, false);

      StyleConstants.setForeground(GRAY_BOLD, GRAY);
      StyleConstants.setBold(GRAY_BOLD, true);

      StyleConstants.setForeground(ORANGE_PLAIN, ORANGE);
      StyleConstants.setBold(ORANGE_PLAIN, false);
      
      StyleConstants.setForeground(PURPLE_PLAIN, PURPLE);
      StyleConstants.setBold(PURPLE_PLAIN, false);
   }
}
