package eg;

import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFrame;

/**
 * The interface to run an action during which a wait cursor is displayed
 */
@FunctionalInterface
public interface BusyFunction {

   /**
    * Runs the action
    */
   public void run();

   /**
    * Makes the glass pane visible and sets the wait cursor
    *
    * @param f  the top level JFrame
    */
   public default void setBusyCursor(JFrame f) {
      f.getGlassPane().setVisible(true);
      f.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   }

   /**
    * Makes the glass pane invisible and sets the default cursor after
    * other processes started before on the EDT are finished
    *
    * @param f  the top level JFrame
    */
   public default void setDefaultCursor(JFrame f) {
      EventQueue.invokeLater(() -> {
         f.getGlassPane().setVisible(false);
         f.getGlassPane().setCursor(Cursor.getDefaultCursor());
      });
   }
}
