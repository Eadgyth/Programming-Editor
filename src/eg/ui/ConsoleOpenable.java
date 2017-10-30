package eg.ui;

/**
 * Interface to open the console panel in <code>MainWin</code>
 */
public interface ConsoleOpenable {
   
   /**
    * Returns if the console panel is open
    *
    * @return  if the console is open
    */
   public boolean isConsoleOpen();
   
   /**
    * Opens the console panel
    */
   public void openConsole(); 
}
