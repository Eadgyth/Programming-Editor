package eg.document;

//--Eadgyth--//
import eg.ui.EditArea;

/**
 * The line numbering.<br>
 * Created in {@link TypingEdit}
 */
public class LineNumbers {

   private final LineNumberDocument lineDoc;
   private int nOld;

   /**
    * @param lineDoc  the reference to {@link LineNumberDocument}
    */
   public LineNumbers(LineNumberDocument lineDoc) {
      this.lineDoc = lineDoc;
      nOld = 1;
      lineDoc.appendLineNumbers(0, 1);
   }

   /**
    * Updates the line numbers
    *
    * @param text  the text of the document
    */
   public void updateLineNumber(String text) {
      int nNew = countLines(text);
      if (nNew > nOld) {
         lineDoc.appendLineNumbers(nOld, nNew);
      }
      else if (nNew < nOld) {
         lineDoc.removeLineNumbers(nOld, nNew);
      }
      nOld = nNew;
   }
   
   private int countLines(String text) {
      int count = 1;
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
