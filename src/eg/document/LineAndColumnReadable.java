package eg.document;

/**
 * The interface to read the number of the line and the column
 * where the cursor is located
 */
@FunctionalInterface
public interface LineAndColumnReadable {
   
   /**
    * Sets the numbers of the line and the column where the cursor
    * is located
    *
    * @param lineNr  the line number
    * @param columnNr  the column number
    */
   public void setCurrLineAndColumn(int lineNr, int columnNr);
}
