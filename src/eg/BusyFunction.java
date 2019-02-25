package eg;

import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFrame;

/**
 * The execution of a task during which a wait cursor is displayed
 */
public class BusyFunction {

   private final JFrame f;

   /**
    * @param f  the top level JFrame
    */
   public BusyFunction(JFrame f) {
      this.f = f;
   }

   /**
    * Executes the action
    *
    * @param r  the action to execute
    */
   public void execute(Runnable r) {
      try {
         start();
         r.run();
      }
      finally {
         end();
      }
   }

   /**
    * Executes the action at the end of pending EDT events
    *
    * @param r  the action to execute
    */
   public void executeLater(Runnable r) {
      try {
         start();
         EventQueue.invokeLater(() -> {
            r.run();
         });
      }
      finally {
         end();
      }
   }

   //
   //--private--/
   //

   private void start() {
      f.getGlassPane().setVisible(true);
      f.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   }

   private void end() {
      EventQueue.invokeLater(() -> {
         f.getGlassPane().setVisible(false);
         f.getGlassPane().setCursor(Cursor.getDefaultCursor());
      });
   }
}
