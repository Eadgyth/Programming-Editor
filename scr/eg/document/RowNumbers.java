package eg.document;

//--Eadgyth--//
import eg.ui.EditArea;
import eg.utils.Finder;
import eg.utils.FileUtils;

/**
 * The row numbering of the scrolled text area
 */
class RowNumbers {

   private final EditArea editArea;

   private int nRowsCurr = 0;

   RowNumbers(EditArea editArea) {
      this.editArea = editArea;
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
            editArea.removeAllLineNumbers();
            addAllRowNumbers(in);
         }
         editArea.revalidateLineAreaWidth(nRows);
      }
      nRowsCurr = nRows;
   }

   /**
    * Adds all row numbers for a text at once
    */
   void addAllRowNumbers(String in) {
      int nRows = Finder.countMotif(in, "\n");
      nRowsCurr = nRows;
      editArea.removeAllLineNumbers();
      addRowNumbers(0, nRows);
   }

   //
   //----private methods----//
   //
   
   private void addRowNumbers(int previousRows, int nRows) {
      for (int i = previousRows; i <= nRows; i++) {
         editArea.appendRowNumber(i + 1);
      }
   }
}
