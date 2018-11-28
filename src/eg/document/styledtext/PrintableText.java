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
 * The styled text for printing to a printer with the white
 * background theme and a font size that is the inverted scaled size
 */
public class PrintableText extends StyledText {

   private final static BackgroundTheme THEME = BackgroundTheme.whiteTheme();
   private final static Attributes ATTR = new Attributes(THEME);
   private final static SimpleAttributeSet SET = new SimpleAttributeSet();
   
   private final JTextPane printArea;
   private final String text;

   static {
      StyleConstants.setForeground(SET, THEME.normalForeground());
      StyleConstants.setBold(SET, false);
      StyleConstants.setLineSpacing(SET, 0.25f);
   }

   /**
    * @param text  the text to style and to print
    */
   public PrintableText(String text) {
      super(new DefaultStyledDocument(), SET);
      this.text = text;
      printArea = new JTextPane();
      printArea.setDocument(doc());
      printArea.setText(text);
   }

   @Override
   public Attributes attributes() {
      return ATTR;
   }
   
   @Override
   public String text() {
      return text;
   }      
   
   /**
    * Prints the styled content of this <code>JTextPane</code> to a
    * printer
    *
    * @param font  the font
    */
   public void print(Font font) {
      int size = ScreenParams.invertedScaledSize(font.getSize());
      Font printFont = font.deriveFont((float) size);
      printArea.setFont(printFont);
      try {
         printArea.print();
      }
      catch(PrinterException e) {
         FileUtils.log(e);
      }
   }      
}
