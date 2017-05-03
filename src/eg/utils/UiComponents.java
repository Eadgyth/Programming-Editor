package eg.utils;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JTabbedPane;
import javax.swing.border.MatteBorder;
import javax.swing.border.EmptyBorder;

import eg.ui.ExtTabbedPane;

/**
 * Static methods to create customized UI components
 */
public class UiComponents {

   /**
    * Creates a toolbar with the last button (intended for a close button)
    * aligned at the right
    *
    * @param bts  the array of JButtons added to the toolbar
    * @param tooltips  the array of tooltips for the buttons
    * @return  a new {@code JToolBar}
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
   
   /**
    * Creates an <code>ExtTabbedPane</code> that is
    * scrollable, unfocusable and has no border
    *
    * @return  a new {@link ExtTabbedPane}
    */
   public static ExtTabbedPane extTabbedPane() {
      ExtTabbedPane tabPane = new ExtTabbedPane();
      tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      tabPane.setBorder(null);
      tabPane.setFocusable(false);
      return tabPane;
   }
}
