package eg.console;

import eg.ui.ConsolePanel;
import eg.utils.Dialogs;

/**
 * The console for showing messages and for running system processes.
 * System commands are run by this {@link ProcessStarter}.
 */
public class Console {
   
   private final ConsolePanel consPnl;
   private final ProcessStarter proc;
   
   /**
    * @param consPnl  the reference to {@link ConsolePanel}
    */
   public Console(ConsolePanel consPnl) {
      this.consPnl = consPnl;
      proc = new ProcessStarter(consPnl);
   }
   
   /**
    * Gets this <code>ProcessStarter</code>
    *
    * @return  this ProcessStarter
    */
   public ProcessStarter processStarter() {
      return proc;
   }
   
   /**
    * Returns the boolean that, if true, indicates that the console
    * is writable. False indicates that a process started in this
    * <code>ProcessStarter</code> currently uses the console. A
    * warning dialog is shown in this case.
    *
    * @return  the boolean value
    */
   public boolean canPrint() {
      boolean b = !consPnl.isActive();
      if (!b) {
         Dialogs.warnMessage(
               "A currently running process must be terminated first.");
      }
      return b;
   }
   
   /**
    * Clears the console
    *
    * @throws  IllegalStateException if the console is currently used
    * by a process run in this {@link ProcessStarter}
    */
   public void clear() {
      writableException();
      consPnl.setText("");
   }
   
   /**
    * Prints the specified text without appending a line separator
    *
    * @param text  the text
    * @throws  IllegalStateException if the console is currently used
    * by a process run in this {@link ProcessStarter}
    */
   public void print(String text) {
      writableException();
      consPnl.appendText(text);
   }
   
   /**
    * Prints a message which will be formatted such that it is bordered
    * by double angle brackets and a the line separator is appended. This
    * output is intended for predefined status messages.
    *
    * @param text  the message
    * @throws  IllegalStateException if the console is currently used
    * by a process run in this {@link ProcessStarter}
    */
   public void printBr(String text) {
      writableException();
      consPnl.appendTextBr(text);
   }
   
   /**
    * Jumps to the beginning of the console
    *
    * @throws  IllegalStateException if the console is currently used
    * by a process run in this {@link ProcessStarter}
    */
   public void toTop() {
      writableException();
      consPnl.setCaretWhenUneditable(0);
   }
   
   //--private--/
   
   private void writableException() {
      if (consPnl.isActive()) {
         throw new IllegalStateException(
               "The console is used by a process");
      }
   }      
}  
