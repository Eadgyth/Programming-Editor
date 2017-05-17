package eg.utils;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

//--Eadgyth--//
import eg.ui.tabpane.ExtTabbedPane;

/**
 * Static methods to create customized UI components
 */
public class UiComponents {

   /**
    * Creates a <code>JToolBar</code> with the last button (intended for a
    * close button) aligned at the right. The toolbar is not opaque, has
    * no border and is not floatable
    *
    * @param bts  the array of JButtons added to the toolbar
    * @param tooltips  the array of tooltips for the buttons
    * @return  a new <code>JToolBar</code>
    */
   public static JToolBar lastBtRightToolbar(JButton[] bts, String[] tooltips) {
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
