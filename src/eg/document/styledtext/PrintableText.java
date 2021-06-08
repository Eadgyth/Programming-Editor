package eg.document.styledtext;

import java.awt.Font;

import java.awt.print.PrinterException;

import javax.swing.JTextPane;

import javax.swing.text.DefaultStyledDocument;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.utils.FileUtils;
import eg.utils.ScreenParams;

/**
 * The printable styled text
 */
public class PrintableText extends StyledText {

   private static final BackgroundTheme THEME = BackgroundTheme.whiteTheme();
   private static final Attributes ATTR = new Attributes(THEME);

   private final JTextPane printArea;
   private final String text;

   /**
    * @param text  the text to style and to print
    * @param font  the font
    */
   public PrintableText(String text, Font font) {
      super(new DefaultStyledDocument(), THEME.normalText());
      this.text = text;
      printArea = new JTextPane(doc);
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
