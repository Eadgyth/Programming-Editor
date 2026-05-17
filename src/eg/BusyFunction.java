package eg;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;

//--Eadgyth--/
import eg.utils.FileUtils;

/**
 * The execution of a task during which a wait cursor is displayed
 */
public class BusyFunction {

   private final JPanel glass = new JPanel(new java.awt.GridLayout(1,1));

   /**
    * @param f  the top level JFrame
    */
   public BusyFunction(JFrame f) {
      glass.setOpaque(false);
      glass.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      glass.addMouseListener(new MouseAdapter() {});
      f.setGlassPane(glass);
   }

   /**
    * Executes the specified <code>Runnable</code> at the end of
    * pending EDT events and blocks until completion.
    *
    * @param r  the Runnable
    */
    public void execute(Runnable r) {
      if (glass.isVisible()) {
         return;
      }
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
