package eg.document;

//--Eadgyth--//
import eg.ui.EditArea;

/**
 * The line numbering<br>
 * Created in {@link TypingEdit}
 */
public class LineNumbers {

   private final EditArea editArea;

   private int nOld;
   private int nNew;

   /**
    * @param editArea  the reference to {@link EditArea}
    */
   public LineNumbers(EditArea editArea) {
      this.editArea = editArea;
      editArea.appendLineNumber(1);
   }

   /**
    * Updates the line numbers
    *
    * @param text  the text of the document
    */
   public void updateLineNumber(String text) {
      nNew = countLines(text);
      if (nNew > nOld) {
         addLineNumbers(nOld + 1);
      }
      else if (nNew < nOld) {
         replaceLineNr();
      }
      nOld = nNew;
   }

   //
   //----private methods----//
   //

   private void replaceLineNr() {
      editArea.removeAllLineNumbers();
      addLineNumbers(0);
   }

   private void addLineNumbers(int start) {
      for (int i = start; i <= nNew; i++) {
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
