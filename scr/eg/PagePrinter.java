package eg;

import java.awt.*;
import java.awt.Font;
import java.awt.print.*;

/**
 * Implements the Printable to output text to a Printer.
 * It has a fixed Font at the moment which is Consolas, plain, 8
 */
class PagePrinter implements Printable {
   
   private final static Font TO_PRINT = new Font("Consolas", Font.PLAIN, 8);
   String[] output;
   int[] pageBreaks;
   
   public PagePrinter(String text) {
      output = text.split("\n");
   }
   
   @Override
   public int print(Graphics g, PageFormat pf, int page)
         throws PrinterException {
 
      FontMetrics metrics = g.getFontMetrics(TO_PRINT);
      int lineHeight = metrics.getHeight();
      if (pageBreaks == null) {
         int linesPerPage = (int) (pf.getImageableHeight() / lineHeight);
         int numBreaks = (output.length - 1) / linesPerPage;
         pageBreaks = new int[numBreaks];
         for (int b = 0; b < numBreaks; b++) {
             pageBreaks[b] = (b + 1) * linesPerPage; 
         }
      } 
      if (page > pageBreaks.length) {
          return NO_SUCH_PAGE;
      }
      Graphics2D g2d = (Graphics2D)g;
      g2d.translate(pf.getImageableX(), pf.getImageableY());
      g.setFont(TO_PRINT);
      int y = 0; 
      int start = (page == 0) ? 0 : pageBreaks[page - 1];
      int end   = (page == pageBreaks.length)
                       ? output.length : pageBreaks[page];
      for (int line=start; line<end; line++) {
          y += lineHeight;
          g.drawString(output[line], 0, y);
      }
      return PAGE_EXISTS;
   }
}
