package eg.utils;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JTabbedPane;

import javax.swing.border.EmptyBorder;

//--Eadgyth--//
import eg.Constants;
import eg.ui.tabpane.ExtTabbedPane;

/**
 * Static methods to create specialized UI components
 */
public class UiComponents {

   /**
    * Creates a <code>JToolBar</code> with the last button (intended for a
    * close button) aligned at the right. The toolbar is not opaque, has
    * a line border at the bottom and is not floatable
    *
    * @param bts  the array of JButtons added to the toolbar
    * @param tooltips  the array of tooltips for the buttons
    * @return  a new <code>JToolBar</code>
    */
   public static JToolBar lastBtRightToolbar(JButton[] bts, String[] tooltips) {
      JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
      Dimension dim = new Dimension(0, Constants.BAR_HEIGHT);
      tb.setPreferredSize(dim);
      tb.setOpaque(false);
      tb.setBorder(null);
      tb.setFloatable(false);     
      for (int i = 0; i < bts.length; i++) {
         if (i == bts.length - 1) {
            tb.add(Box.createHorizontalGlue());
         }
         tb.add(bts[i]);
         bts[i].setBorder(new EmptyBorder(2, 5, 0, 5));
         bts[i].setToolTipText(tooltips[i]);
         bts[i].setFocusable(false);
      }
      return tb;
   }
   
   /**
    * Creates an <code>ExtTabbedPane</code> that is scrollable
    * and not focusable
    *
    * @return  a new {@link ExtTabbedPane}
    */
   public static ExtTabbedPane extTabbedPane() {
      ExtTabbedPane tabPane = new ExtTabbedPane();
      tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      tabPane.setFocusable(false);
      return tabPane;
   }
   
   /**
    * Returns the Dimension with the specified width and height scaled
    * to the ratio between screen resolution and graphic resolution
    *
    * @param width  the width in pt
    * @param height  the height in pt
    * @return   a new scaled Dimension
    */
   public static Dimension scaledDimension(int width, int height) {
      width = (int) (Math.round(width * eg.Constants.SCREEN_RES_RATIO));
      height = (int) (Math.round(height * eg.Constants.SCREEN_RES_RATIO));
      return new Dimension(width, height);
   }
}
