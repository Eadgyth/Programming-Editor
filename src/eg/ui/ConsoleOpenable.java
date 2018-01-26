package eg.ui;

/**
 * Interface to open the console panel in <code>MainWin</code>
 */
public interface ConsoleOpenable {
   
   /**
    * Returns if the console panel is open
    *
    * @return  the boolan value, true if open
    */
   public boolean isConsoleOpen();
   
   /**
    * Opens the console panel
    */
   public void openConsole(); 
}
