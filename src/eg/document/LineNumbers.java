package eg.document;

//--Eadgyth--//
import eg.ui.EditArea;

/**
 * The line numbering
 */
class LineNumbers {

   private final EditArea editArea;

   private int currLineNr = 0;

   LineNumbers(EditArea editArea) {
      this.editArea = editArea;
      editArea.appendLineNumber(1);
   }

   int getCurrLineNr() {
      return currLineNr;
   }

   void updateLineNumber(String text) {
      int nLines = countLines(text);
      if (nLines > currLineNr) {
         addLineNumbers(currLineNr + 1, nLines);
      }
      else if (nLines < currLineNr) {
         replaceLineNr(nLines);
      }
      currLineNr = nLines;
   }

   void addAllLineNumbers(String text) {
      int nLines = countLines(text);
      currLineNr = nLines;
      replaceLineNr(nLines);
   }

   //
   //----private methods----//
   //

   private void replaceLineNr(int nLines) {
      editArea.removeAllLineNumbers();
      addLineNumbers(0, nLines);
   }

   private void addLineNumbers(int previousLines, int nLines) {
      for (int i = previousLines; i <= nLines; i++) {
         editArea.appendLineNumber(i + 1);
      }
      editArea.revalidateLineAreaWidth();
   }
   
   private int countLines(String text) {
      int count = 0;
      int i = 0;
      while (i != -1) {
         i = text.indexOf("\n", i);
         if (i != -1) {
            count++;
            i++;
         }
      }
      return count;
   }
}
