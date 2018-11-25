package eg.document;

import java.awt.Font;

import javax.swing.JTextPane;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.utils.ScreenParams;

/**
 * The styled text for printing to a printer
 */
public class PrintableText extends StyledText {
   
   private final static BackgroundTheme THEME = BackgroundTheme.whiteTheme();
   private final static Attributes ATTR = new Attributes(THEME);
   private final static SimpleAttributeSet SET = new SimpleAttributeSet();
   
   private final String text;
   
   static {
      StyleConstants.setForeground(SET, THEME.normalForeground());
      StyleConstants.setBold(SET, false);
      StyleConstants.setLineSpacing(SET, 0.25f);
   }
   
   /**
    * @param text  the text to print
    * @param f  the font
    */
   public PrintableText(String text, Font f) {
      super(new JTextPane(), SET);
      int size = ScreenParams.invertedScaledSize(f.getSize());
      Font font = f.deriveFont((float) size);
      textArea().setFont(font);
      insert(0, text);
      this.text = text;
   }
   
   @Override
   public String text() {
      return text;
   }
   
   @Override
   public Attributes attributes() {
      return ATTR;
   }
}
