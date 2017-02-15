package eg.document;

import javax.swing.JTextPane;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import javax.swing.JPanel;
import java.awt.Color;

//--Eadgyth--//
import eg.utils.Finder;
import eg.utils.FileUtils;

/**
 * The row numbering of the scrolled text area
 */
class RowNumbers {

   private final static Color NUM_GRAY = new Color(160, 160, 160);

   private final SimpleAttributeSet lineSet = new SimpleAttributeSet();
   private final StyledDocument lineDoc;
   private final JPanel scrolledArea;

   private int nRowsCurr = 0;

   RowNumbers(JTextPane lineArea, JPanel scrolledArea) {
      this.lineDoc = lineArea.getStyledDocument();
      this.scrolledArea = scrolledArea;
      setStyle();
   }
   
   /**
    *
    */
   int getCurrLineNr() {
      return nRowsCurr;
   }

   /**
    * Updates row numbers based on the number of newlines
    */
   void updateRowNumber(String in) {
      int nRows = Finder.countMotif(in, "\n");
      if (nRows != nRowsCurr) {
         if (nRows > nRowsCurr) {
            addRowNumbers(nRowsCurr + 1, nRows);
         }
         else if (nRows < nRowsCurr) {
            removeRowNumbers();
            addAllRowNumbers(in);
         }
         revalidateArea(nRows);
      }
      nRowsCurr = nRows;
   }

   /**
    * Adds all row numbers for a text at once
    */
   void addAllRowNumbers(String in) {
      int nRows = Finder.countMotif(in, "\n");
      nRowsCurr = nRows;
      removeRowNumbers();
      addRowNumbers(0, nRows);
   }

   //
   //----private methods----//
   //
   
   private void addRowNumbers(int previousRows, int nRows) {
      for (int i = previousRows; i <= nRows; i++) {
         appendRowNumber(i + 1);
      }
   }

   private void appendRowNumber(int nRows) {
      try {
         lineDoc.insertString(lineDoc.getLength(),
               Integer.toString(nRows) + "\n", lineSet);
      }
      catch(BadLocationException e) {
         FileUtils.logStack(e);
      }
      revalidateArea(nRows);
   }

   private void removeRowNumbers() {
      try {
         lineDoc.remove(0, lineDoc.getLength());
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }

   /**
    * Adapts the width of lineNumber pane as #digits of line numbers change
    */
   private void revalidateArea(int nRows) {
      if ((nRows + 1) % 10 == 0 || nRows == 0) {
         scrolledArea.revalidate();
      }
   }

   private void setStyle() {
      StyleConstants.setForeground(lineSet, NUM_GRAY);
      StyleConstants.setAlignment(lineSet, StyleConstants.ALIGN_RIGHT);     
      StyleConstants.setLineSpacing(lineSet, 0.2f);
      Element el = lineDoc.getParagraphElement(0);
      lineDoc.setParagraphAttributes(0, el.getEndOffset(), lineSet, false);
   }
}
