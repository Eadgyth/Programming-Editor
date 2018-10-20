package eg.ui;

/**
 * Interface to ask if the console is visible and to open the console
 * in <code>MainWin</code>
 */
public interface ConsoleOpenable {
   
   /**
    * Returns if the console panel is open
    *
    * @return  true if open, false otherwise
    */
   public boolean isConsoleOpen();
   
   /**
    * Opens the console
    */
   public void openConsole(); 
}
