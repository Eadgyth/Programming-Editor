package eg;

import java.lang.Runnable;

import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFrame;

/**
 * The interface to run an action during which a wait cursor may
 * be displayed
 */
@FunctionalInterface
public interface BusyFunction extends Runnable {

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
    * other processes started on the EDT before <code>run</code> are
    * finished
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
