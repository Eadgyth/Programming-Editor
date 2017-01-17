package eg;

import java.io.File;

import java.awt.Font;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;

public class Constants {
   
    // Borders
    public final static Border LOW_ETCHED = new EtchedBorder(EtchedBorder.LOWERED);  

    // colors
    public final static Color BORDER_GRAY = new Color(160, 160, 160);

    // fonts
    public final static Font SANSSERIF_PLAIN_12 = new Font("SansSerif", Font.PLAIN, 12);
    public final static Font SANSSERIF_BOLD_12  = new Font("SansSerif", Font.BOLD, 12);
    public final static Font VERDANA_PLAIN_11 = new Font("Verdana", Font.PLAIN, 11);

    // strings
    public final static String F_SEP = File.separator;
    public final static String SYS_LINE_SEP = System.lineSeparator();
    public final static String SHOW = "show";
    public final static String HIDE = "hide";
    public final static String LINE_NUM_PREFS = "lineNumbers";
    public final static String FONT_PREFS = "font";
    public final static String FONT_SIZE_PREFS = "fontSize";
}
