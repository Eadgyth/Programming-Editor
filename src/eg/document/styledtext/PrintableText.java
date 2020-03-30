package eg.document.styledtext;

import java.awt.Font;

import java.awt.print.PrinterException;

import javax.swing.JTextPane;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.DefaultStyledDocument;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.utils.FileUtils;
import eg.utils.ScreenParams;

/**
 * The styled text for printing to a printer
 */
public class PrintableText extends StyledText {

   private final static BackgroundTheme THEME = BackgroundTheme.whiteTheme();
   private final static Attributes ATTR = new Attributes(THEME);
   private final static SimpleAttributeSet SET = new SimpleAttributeSet();
   
   private final JTextPane printArea;
   private final String text;

   static {
      StyleConstants.setForeground(SET, THEME.normalText());
      StyleConstants.setBold(SET, false);
      StyleConstants.setLineSpacing(SET, 0.25f);
   }

   /**
    * @param text  the text to style and to print
    * @param font  the font
    */
   public PrintableText(String text, Font font) {
      super(new DefaultStyledDocument(), SET);
      this.text = text;
      printArea = new JTextPane();
      printArea.setDocument(doc());
      int size = ScreenParams.invertedScaledSize(font.getSize());
      Font printFont = font.deriveFont((float) size);
      printArea.setFont(printFont);
      printArea.setText(text);
   }

   @Override
   public final Attributes attributes() {
      return ATTR;
   }
   
   @Override
   public final String text() {
      return text;
   }      
   
   /**
    * Prints this styled text
    */
   public final void print() {
      try {
         printArea.print();
      }
      catch(PrinterException e) {
         FileUtils.log(e);
      }
   }      
}
