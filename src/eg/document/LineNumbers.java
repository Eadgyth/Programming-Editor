package eg.document;

//--Eadgyth--//
import eg.ui.EditArea;

/**
 * The line numbering.<br>
 * Created in {@link TypingEdit}
 */
public class LineNumbers {

   private final LineNumberDocument lineNrDoc;
   private int nOld;

   /**
    * @param lineNrDoc  the reference to {@link LineNumberDocument}
    */
   public LineNumbers(LineNumberDocument lineNrDoc) {
      this.lineNrDoc = lineNrDoc;
      nOld = 1;
      lineNrDoc.appendLineNumbers(0, 1);
   }

   /**
    * Updates the line numbers
    *
    * @param text  the text of the document
    */
   public void updateLineNumber(String text) {
      int nNew = countLines(text);
      if (nNew > nOld) {
         lineNrDoc.appendLineNumbers(nOld, nNew);
      }
      else if (nNew < nOld) {
         lineNrDoc.removeLineNumbers(nOld, nNew);
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
