package eg;

import java.awt.EventQueue;

//--Eadgyth--/
import eg.console.*;
import eg.ui.MainWin;

/**
 * The starting of tasks with certain predefined settings and behaviour
 * of the UI
 */
public class TaskRunner {

   private final MainWin mw;
   private final ProcessStarter proc;
   private final Console cons;
   private final Runnable fileTreeUpdate;
   private final TaskRunner.ConsolePrinter printer;

   /**
    * @param mw  the MainWin
    * @param cons  the Console
    * @param proc  the ProcessStarter
    * @param fileTreeUpdate  the updating of the file tree
    */
   public TaskRunner(MainWin mw, Console cons, ProcessStarter proc,
         Runnable fileTreeUpdate) {

      this.mw = mw;
      this.proc = proc;
      this.fileTreeUpdate = fileTreeUpdate;
      this.cons = cons;
      printer = new TaskRunner.ConsolePrinter(cons);
   }

   /**
    * Gets this <code>ConsolePrinter</code>
    *
    * @return  the ConsolePrinter
    */
   public ConsolePrinter consolePrinter() {
      return printer;
   }

   /**
    * Runs the specified <code>Runnable</code> in a separate thread.
    * The Runnable is supposed to use {@link ConsolePrinter} for
    * output to the console. If the console is blocked by another task
    * a warning dialog is shown and the thread is not started.
    *
    * @param r  the Runnable
    * @param initialMsg  the message that is printed to the console
    * before the thread is started; maybe null or the empty string
    * @param toTop  true to move to the top of the console after the
    * task is finished
    * @see ConsolePrinter
    */
   public void runWithConsoleOutput(Runnable r, String initialMsg, boolean toTop) {
      if (!cons.setUnlocked()) {
         return;
      }
      mw.showConsole();
      cons.setText("");
      if (initialMsg != null && !initialMsg.isEmpty()) {
         cons.appendTextBr(initialMsg);
      }
      new Thread(() -> {
         r.run();
         EventQueue.invokeLater(() -> {
            if (toTop) {
               cons.setCaret(0);
            }
            cons.setLocked();
            fileTreeUpdate.run();
         });
      }).start();
   }

   /**
    * Runs the specified Runnable at the end of pending EDT events
    * and shows the wait cursor during processing
    *
    * @param r  the Runnable
    */
   public void runBusy(Runnable r) {
      mw.busyFunction().execute(r);
      EventQueue.invokeLater(fileTreeUpdate);
   }

   /**
    * Runs the specified system command with the current project
    * directory.
    * Method invokes {@link ProcessStarter#startProcess(String)}
    *
    * @param cmd  the system command
    */
   public void runSystemCommand(String cmd) {
      mw.showConsole();
      proc.startProcess(cmd);
   }
   
   /**
    * Runs the specified system command with the current project
    * directory.
    * Method invokes {@link ProcessStarter#startProcess(String, String)}
    *
    * @param cmd  the system command
    * @param startMsg  an initial message that describes the command
    * in the console
    */
   public void runSystemCommand(String cmd, String startMsg) {
      mw.showConsole();
      proc.startProcess(cmd, startMsg);
   }

   /**
    * The printing of output to the console
    */
   public static class ConsolePrinter {

       private final Console cons;

      /**
       * Prints the specified text without appending a line separator
       *
       * @param text  the text
       */
      public void print(String text) {
         EventQueue.invokeLater(() -> cons.appendText(text));
      }

      /**
       * Prints the specified text with a line separator at the end
       *
       * @param text  the text
       */
      public void printLine(String text) {
         EventQueue.invokeLater(() -> cons.appendText(text + "\n"));
      }

      /**
       * Prints a message which is formatted such that it begins with
       * two closing angle brackets and ends with the line separator.
       * This output is intended for predefined status/error messages.
       *
       * @param text  the text
       */
      public void printBr(String text) {
         EventQueue.invokeLater(() -> cons.appendTextBr(text));
      }

      //
      //--private--/
      //

      private ConsolePrinter(Console cons) {
         this.cons = cons;
      }
   }
}
