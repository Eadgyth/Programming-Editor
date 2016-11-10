package eg.document;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import javax.swing.JPanel;
import java.awt.Color;

import eg.utils.Finder;

/**
 * The row numbering of the scrolled text area
 */
class RowNumbers {

   private final static Color NUM_GRAY = new Color( 70, 70, 70 );

   private SimpleAttributeSet lineSet = new SimpleAttributeSet();
   private StyledDocument lineDoc;
   private JPanel scrolledArea; // to revalidate when width of lineDoc changes
   
   private int rowNumberHelper;

   RowNumbers(StyledDocument lineDoc, JPanel scrolledArea) {
      this.lineDoc = lineDoc;
      this.scrolledArea = scrolledArea;
      setStyle();
      addRowNumber(0);
   }
   
   /**
    * Updates row numbers based on the number of newlines
    */
   void updateRowNumber(String in) {
      int rowNumber = Finder.countMotive(in, "\n");
      if (rowNumber != rowNumberHelper) {
         insertAllRowNumbers(rowNumber + 1);
         revalidateArea(rowNumber + 1);
      }
      rowNumberHelper = rowNumber;
      //System.out.println("row number: " + rowNumber);
   }

   void insertAllRowNumbers(int rowNumber) {
      removeRowNumbers();
      for (int i = 0; i < rowNumber; i++) {
         addRowNumber(i);
      }
   }

   private void addRowNumber(int rowNumber) {
      try {
         lineDoc.insertString( lineDoc.getLength(),
               Integer.toString(rowNumber + 1) + "\n", lineSet );
      }
      catch( BadLocationException ble ) {
         ble.printStackTrace();
      }
      revalidateArea(rowNumber);
   }

   private void removeRowNumbers() {
      try {
         lineDoc.remove(0, lineDoc.getLength());
      }
      catch (BadLocationException ble) {
         ble.printStackTrace();
      }
   }

   /**
    * Adapts the width of lineNumber pane as #digits of line numbers change
    */
   private void revalidateArea(int rowNumber) {
      if ((rowNumber + 1) % 10 == 0 || rowNumber == 0) {
         scrolledArea.revalidate();
      }
   }

   private void setStyle() {
      StyleConstants.setForeground(lineSet, NUM_GRAY);
      StyleConstants.setAlignment(lineSet, StyleConstants.ALIGN_RIGHT);     
      StyleConstants.setLineSpacing(lineSet, 0.2f); // as for JtextPane 'input'
      Element el2 = lineDoc.getParagraphElement(0);
      lineDoc.setParagraphAttributes(0, el2.getEndOffset(), lineSet, false);
   }
}