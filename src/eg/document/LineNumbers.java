package eg.document;

//--Eadgyth--//
import eg.ui.EditArea;
import eg.utils.Finder;

/**
 * The line numbering of the scrolled text area
 */
class LineNumbers {

   private final EditArea editArea;

   private int currLineNr = 0;

   LineNumbers(EditArea editArea) {
      this.editArea = editArea;
   }

   int getCurrLineNr() {
      return currLineNr;
   }

   void updateLineNumber(String in) {
      int nLines = Finder.countLines(in);
      if (nLines > currLineNr) {
         addRowNumbers(currLineNr + 1, nLines);
      }
      else if (nLines < currLineNr) {
         replaceLineNr(nLines);
      }
      currLineNr = nLines;
   }

   void addAllLineNumbers(String in) {
      int nLines = Finder.countLines(in);
      currLineNr = nLines;
      replaceLineNr(nLines);
   }

   //
   //----private methods----//
   //
   
   private void replaceLineNr(int nLines) {
      editArea.removeAllLineNumbers();
      addRowNumbers(0, nLines);
   }
   
   private void addRowNumbers(int previousLines, int nLines) {
      for (int i = previousLines; i <= nLines; i++) {
         editArea.appendRowNumber(i + 1);
      }
      editArea.revalidateLineAreaWidth();
   }
}
