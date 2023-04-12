package eg.ui.tabpane;

/**
 * The interface to close a tab at a given index. The
 * implementation checks if closing is possible and calls
 * JTabbedPane.removeTabAt(int) as needed
 */
@FunctionalInterface
public interface TabClosing {

   /**
    * Closes a tab at the specified index if closing is
    * possible
    *
    * @param index  the index
    */
   public void close(int index);
}
