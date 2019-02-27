package eg;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.ui.MainWin;

/**
 * The starting of tasks with certain predefined settings and behaviour
 * of the UI. All 'run...' methods update the file tree after completion
 * of a task.
 */
public class TaskRunner {

   private final MainWin mw;
   private final ProcessStarter proc;
   private final ConsolePanel consPnl;
   private final Runnable fileTreeUpdate;
   private final TaskRunner.ConsolePrinter printer;

   /**
    * @param mw  the reference to MainWin
    * @param proc  the reference to ProcessStarter
    * @param fileTreeUpdate  the updating of the file tree
    */
   public TaskRunner(MainWin mw, ProcessStarter proc,
         Runnable fileTreeUpdate) {

      this.mw = mw;
      this.proc = proc;
      this.fileTreeUpdate = fileTreeUpdate;
      consPnl = mw.consolePnl();
      printer = new TaskRunner.ConsolePrinter();
   }

   /**
    * Gets this <code>ConsolePrinter</code>. The print methods are only
    * unlocked for a task that is run by {@link #runWithConsoleOutput}
    *
    * @return  the ConsolePrinter
    */
   public ConsolePrinter consolePrinter() {
      return printer;
   }

   /**
    * Runs the specified <code>Runnable</code>, which may use
    * <code>ConsolePrinter</code> for showing output in the console,
    * in a separate thread. If the console is blocked by another task
    * a dialog is shown and the thread is not started.
    *
    * @param r  the Runnable
    * @param initialMsg  the message that is printed to the console
    * before the thread is started; may be null or the empty string
    * @param toTop  true to move to the top of the console after the
    * task is finished
    * @see ConsolePrinter
    */
   public void runWithConsoleOutput(Runnable r, String initialMsg, boolean toTop) {
      if (!consPnl.setUnlocked()) {
         return;
      }
      mw.showConsole();
      consPnl.setText("");
      if (initialMsg != null && !initialMsg.isEmpty()) {
         consPnl.appendTextBr(initialMsg);
      }
      new Thread(() -> {
         r.run();
         EventQueue.invokeLater(() -> {
            if (toTop) {
              consPnl.setCaretWhenUneditable(0);
            }
            consPnl.setLocked();
         });
         EventQueue.invokeLater(fileTreeUpdate);
      }).start();
   }

   /**
    * Runs the specified Runnable at the end of pending EDT events
    * and shows the wait cursor during processing
    *
    * @param r  the Runnable
    */
   public void runBusy(Runnable r) {
      mw.busyFunction().executeLater(r);
      EventQueue.invokeLater(fileTreeUpdate);
   }

   /**
    * Runs the specified command as a system process which uses the
    * console to show output/error and read input. If the conosle
    * is blocked by another task a dialog is shown and the process
    * is not started.
    *
    * @param cmd  the command
    * @see ProcessStarter
    */
   public void runSystemCommand(String cmd) {
      mw.showConsole();
      proc.startProcess(cmd);
   }

   /**
    * The printing of output to the console
    */
   public final class ConsolePrinter {

      /**
       * Prints the specified text without appending a line separator
       *
       * @param text  the text
       */
      public void print(String text) {
         EventQueue.invokeLater(() -> consPnl.appendText(text));
      }

      /**
       * Prints the specified text with a line separator at the end
       *
       * @param text  the text
       */
      public void printLine(String text) {
         EventQueue.invokeLater(() -> consPnl.appendText(text + "\n"));
      }

      /**
       * Prints a message which is formatted such that it begins with
       * two opening angle brackets and ends with the line separator.
       * This output is intended for predefined status/error messages.
       *
       * @param text  the text
       */
      public void printBr(String text) {
         EventQueue.invokeLater(() -> consPnl.appendTextBr(text));
      }

      private ConsolePrinter() {}
   }
}
