package eg;

import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFrame;

/**
 * The execution of an action during which a wait cursor is displayed
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
         setWaitCursor();
         r.run();
      }
      finally {
         setDefCursor();
      }
   }

   /**
    * Executes the action after other events are processed (the
    * specified <code>Runnable</code> is passeed to
    * <code>EventQueue.invokeLater</code>)
    *
    * @param r  the Runnable to execute
    */
   public void executeLater(Runnable r) {
      try {
         setWaitCursor();
         EventQueue.invokeLater(r);
      }
      finally {
         setDefCursor();
      }
   }

   //
   //--private--/
   //

   private void setWaitCursor() {
      f.getGlassPane().setVisible(true);
      f.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   }

   private void setDefCursor() {
      EventQueue.invokeLater(() -> {
         f.getGlassPane().setVisible(false);
         f.getGlassPane().setCursor(Cursor.getDefaultCursor());
      });
   }
}
