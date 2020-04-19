package eg;

import java.lang.reflect.InvocationTargetException;

import java.awt.Cursor;
import java.awt.EventQueue;

import java.awt.event.MouseAdapter;

import javax.swing.JPanel;
import javax.swing.JFrame;

//--Eadgyth--/
import eg.utils.FileUtils;

/**
 * The execution of a task during which a wait cursor is displayed
 */
public class BusyFunction {

   private final JPanel glass = new JPanel();

   /**
    * @param f  the top level JFrame
    */
   public BusyFunction(JFrame f) {
      glass.setOpaque(false);
      glass.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      glass.addMouseListener(new MouseAdapter() {} );
      f.setGlassPane(glass);
   }

   /**
    * Executes the specified <code>Runnable</code> at the end of
    * pending EDT events and blocks until completion.
    * The task is run by java.awt.EventQueue.invokeAndWait method.
    *
    * @param r  the Runnable
    */
   public void execute(Runnable r) {
      glass.setVisible(true);
      new Thread(() -> {
         try {
            EventQueue.invokeAndWait(r);
         }
         catch (InterruptedException | InvocationTargetException e) {
            FileUtils.log(e);
            Thread.currentThread().interrupt();
         }
         finally {
            EventQueue.invokeLater(() -> glass.setVisible(false));
         }
      }).start();
   }
}
