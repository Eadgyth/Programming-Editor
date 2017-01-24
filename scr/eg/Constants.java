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
    //
    /**
     * The swing lowered etched border */
    public final static Border LOW_ETCHED = new EtchedBorder(EtchedBorder.LOWERED);  
    /**
     * The color for borders which is 160, 160, 160 (rgb) */
    public final static Color BORDER_GRAY = new Color(160, 160, 160);

    // Fonts
    //
    /**
     * The font sans-serif, plain, size 12 pt */
    public final static Font SANSSERIF_PLAIN_12 = new Font("SansSerif", Font.PLAIN, 12);
    /**
     * The font sans-serif, bold, size 12 pt */
    public final static Font SANSSERIF_BOLD_12  = new Font("SansSerif", Font.BOLD, 12);
    /**
     * The font verdana, plain, size 11 pt */
    public final static Font VERDANA_PLAIN_11 = new Font("Verdana", Font.PLAIN, 11);

    // Strings
    //
    /**
     * The file separator of the system */
    public final static String F_SEP = File.separator;
    /**
     * The line separator of the system */
    public final static String LINE_SEP = System.lineSeparator();
    /**
     * Has the value {@value #SHOW} */
    public final static String SHOW = "show";
    /**
     * Has the value {@value #HIDE} */
    public final static String HIDE = "hide";
    /**
     * Has the value {@value #LINE_NUMBERS} */
    public final static String LINE_NUMBERS = "lineNumbers";
    /**
     * Has the value {@value #ENABLED} */
    public final static String ENABLED = "enabled";
    /**
     * Has the value {@value #FONT} */
    public final static String FONT = "font";
    /**
     * Has the value {@value #FONT_SIZE} */
    public final static String FONT_SIZE = "fontSize";
    /**
     * Has the value {@value #JAVA_EXT} */
    public final static String JAVA_EXT = ".java";
    /**
     * Has the value {@value #HTML_EXT} */
    public final static String HTML_EXT = ".html";
    /**
     * Has the value {@value #PERL_PL_EXT} */
    public final static String PERL_PL_EXT = ".pl";
    /**
     * Has the value {@value #PERL_PM_EXT} */
    public final static String PERL_PM_EXT = ".pm";
}
