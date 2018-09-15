package eg.console;

import eg.ui.ConsolePanel;
import eg.utils.Dialogs;

/**
 * The console for showing messages and for running system processes.
 * System commands are run by this {@link ProcessStarter}.
 * <p>
 * Created by {@link eg.Projects}
 */
public class Console {
   
   private final ConsolePanel consPnl;
   private final ProcessStarter proc;
   
   /**
    * @param consPnl  the refence to {@link ConsolePanel} contained in
    * {@link eg.ui.MainWin}
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
   public ProcessStarter getProcessStarter() {
      return proc;
   }
   
   /**
    * Returns the boolean that, if true, indicates that text can
    * be printed. False indicates that a process started in this
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
      if (consPnl.isActive()) {
         throw new IllegalStateException(
               "Cannot set Text. The console is used by a process");
      }
      consPnl.setText("");
   }
   
   /**
    * Prints the specified text without adding a line separator
    *
    * @param text  the text
    * @throws  IllegalStateException if the console is currently used
    * by a process run in this {@link ProcessStarter}
    */
   public void print(String text) {
      if (consPnl.isActive()) {
         throw new IllegalStateException(
               "Cannot append Text. The console is used by a process");
      }
      consPnl.appendText(text);
   }
   
   /**
    * Prints a message which will be formatted such that it is bordered
    * by double angle brackets and a the line separator is added. This
    * output is intended for hardcoded status messages.
    *
    * @param text  the message
    * @throws  IllegalStateException if the console is currently used
    * by a process run in this {@link ProcessStarter}
    */
   public void printStatus(String text) {
      if (consPnl.isActive()) {
         throw new IllegalStateException(
               "Cannot append Text. The console is used by a process");
      }
      consPnl.appendTextFormatted(text);
   }
   
   /**
    * Jumps to the beginning of the console
    *
    * @throws  IllegalStateException if the console is currently used
    * by a process run in this {@link ProcessStarter}
    */
   public void toTop() {
      if (consPnl.isActive()) {
         throw new IllegalStateException(
               "Cannot jump to the top. The console is used by a process");
      }
      consPnl.setCaretWhenUneditable(0);
   }
}  
