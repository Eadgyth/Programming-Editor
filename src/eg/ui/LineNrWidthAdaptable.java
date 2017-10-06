package eg.ui;

/**
 * Interface to revalidate the edit area when the width of the
 * area that shows line numbers has to change
 */
@FunctionalInterface
public interface LineNrWidthAdaptable {
   
   /**
    * Revalidates the edit area as the number of digits of the
    * largest line number changes
    *
    * @param prevLineNr  the previous number of lines
    * @param lineNr  the new number of lines
    */
   public void adaptLineNrWidth(int prevLineNr, int lineNr);
   
}
