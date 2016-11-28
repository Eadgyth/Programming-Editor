package eg;

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

    // look and feel
    public final static LookAndFeel CURR_LAF = UIManager.getLookAndFeel();
    public final static String CURR_LAF_STR = CURR_LAF.getName();

    // strings
    public final static String SHOW = "show";
    public final static String HIDE = "hide";
}