package eg.utils;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;

import javax.swing.border.EmptyBorder;

/**
 * Contains the file view panel that consists in a panel in which a JTree showing
 * the project's file system can be displayed and a toolbar. 
 */
public class UiComponents {

   /**
    * Creates a toolbar with the last button (intended for a close button)
    * at the right.
    */
   public static JToolBar toolbarLastBtRight(JButton[] bts, String[] tooltips) {
      JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
      tb.setOpaque(false);
      tb.setBorder(null);
      tb.setFloatable(false);
      
      for (int i = 0; i < bts.length; i++) {
         if (i == bts.length - 1) {
            tb.add(Box.createHorizontalGlue());
         }
         tb.add(bts[i]);
         bts[i].setBorder(new EmptyBorder(2, 5, 2, 5));
         bts[i].setToolTipText(tooltips[i]);
         bts[i].setFocusable(false);
      }
      return tb;
   }
}