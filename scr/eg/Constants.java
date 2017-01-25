package eg;

import java.awt.Font;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * Holds different static values
 */
public class Constants {

    // Borders
    //

    /**
     * The Border for borders. This is the lowered etched border */
    public final static Border BORDER = new EtchedBorder(EtchedBorder.LOWERED);  
    /**
     * The color for borders which is 160, 160, 160 (rgb) */
    public final static Color BORDER_GRAY = new Color(160, 160, 160);

    // Fonts
    //

    /**
     * The font sans-serif, plain, size 12 pt */
    public final static Font SANSSERIF_PLAIN_12
          = new Font("SansSerif", Font.PLAIN, 12);
    /**
     * The font sans-serif, bold, size 12 pt */
    public final static Font SANSSERIF_BOLD_12
          = new Font("SansSerif", Font.BOLD, 12);
    /**
     * The font verdana, plain, size 11 pt */
    public final static Font VERDANA_PLAIN_11
          = new Font("Verdana", Font.PLAIN, 11);
}
