package eg;

import java.io.File;

import java.awt.Font;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * Holds different static values
 */
public class Constants {

    // Borders
    public final static Border LOW_ETCHED = new EtchedBorder(EtchedBorder.LOWERED);  

    // Colors
    public final static Color BORDER_GRAY = new Color(160, 160, 160);

    // Fonts
    public final static Font SANSSERIF_PLAIN_12 = new Font("SansSerif", Font.PLAIN, 12);
    public final static Font SANSSERIF_BOLD_12  = new Font("SansSerif", Font.BOLD, 12);
    public final static Font VERDANA_PLAIN_11 = new Font("Verdana", Font.PLAIN, 11);

    // strings

    /**
     * The file separator of the system */
    public final static String F_SEP = File.separator;
    /**
     * The line separator of the system */
    public final static String LINE_SEP = System.lineSeparator();

    public final static String SHOW = "show";
    public final static String HIDE = "hide";
    public final static String LINE_NUMBERS = "lineNumbers";
    public final static String ENABLED = "enabled";
    public final static String FONT = "font";
    public final static String FONT_SIZE = "fontSize";
}
