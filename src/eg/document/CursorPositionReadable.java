package eg.document;

/**
 * The interface to read the numbers of the line and the column
 * where the cursor is located
 */
@FunctionalInterface
public interface CursorPositionReadable {
   
   /**
    * Sets the numbers of the line and the column within the line
    * where the cursor is located
    *
    * @param lineNr  the line number
    * @param columnNr  the column number
    */
   public void setPosition(int lineNr, int columnNr);
}
