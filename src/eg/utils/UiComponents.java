package eg.utils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JTabbedPane;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

//--Eadgyth--//
import eg.Constants;
import eg.ui.tabpane.ExtTabbedPane;

/**
 * Static methods to create specialized UI components
 */
public class UIComponents {

   /**
    * Creates a <code>JToolBar</code> with the last button (intended for a
    * close button) aligned at the right. Besides the toolar is set up as
    * in {@link#toolbar(JButton[],String[])}
    *
    * @param bts  the array of JButtons
    * @param tooltips  the array of tooltips
    * @return  a new <code>JToolBar</code>
    */
   public static JToolBar lastBtRightToolbar(JButton[] bts, String[] tooltips) {     
      JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
      setupToolbar(tb, bts, tooltips, true);
      return tb;
   }
   
   /**
    * Creates a <code>JToolBar</code>. The toolbar is not opaque, has
    * no border and is not floatable. Buttons are not focusable and
    * have an empty border.
    *
    * @param bts  the array of JButtons
    * @param tooltips  the array of tooltips
    * @return  a new <code>JToolBar</code>
    */
   public static JToolBar toolbar(JButton[] bts, String[] tooltips) {
      JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
      setupToolbar(tb, bts, tooltips, false);
      return tb;
   }
   
   /**
    * Creates a titled border with the specified title shown with scaled
    * font size and a line border in gray
    *
    * @param title  the title
    * @return a new titled border
    */
   public static TitledBorder titledBorder(String title) {
      TitledBorder tBorder = BorderFactory.createTitledBorder
            (new LineBorder(Constants.GRAY, 1), title);

      tBorder.setTitleFont(eg.Constants.VERDANA_PLAIN_8);
      return tBorder;
   }
   
   /**
    * Creates an <code>ExtTabbedPane</code> that is scrollable
    * and not focusable
    *
    * @return  a new {@link ExtTabbedPane}
    */
   public static ExtTabbedPane scolledUnfocusableTabPane() {
      ExtTabbedPane tabPane = new ExtTabbedPane();
      tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      tabPane.setFocusable(false);
      return tabPane;
   }
   
   //
   //--private--/
   //
   
   private UIComponents() {}
   
   private static void setupToolbar(JToolBar tb, JButton[] bts, String[] tooltips,
         boolean isLastButtonRight) {

      tb.setOpaque(false);
      tb.setBorder(null);
      tb.setFloatable(false);
      for (int i = 0; i < bts.length; i++) {
         if (isLastButtonRight && i == bts.length - 1) {
            tb.add(Box.createHorizontalGlue());
         }
         tb.add(bts[i]);
         bts[i].setBorder(new EmptyBorder(5, 7, 5, 7));
         bts[i].setToolTipText(tooltips[i]);
         bts[i].setFocusable(false);
      }
   }
}
