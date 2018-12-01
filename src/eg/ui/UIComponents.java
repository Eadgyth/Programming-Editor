package eg.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTabbedPane;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

//--Eadgyth--/
import eg.utils.ScreenParams;
import eg.ui.tabpane.ExtTabbedPane;

/**
 * Static methods to create UI components with common behaviours, layouts
 * or colors
 */
public class UIComponents {

   private final static int BAR_HEIGHT = ScreenParams.scaledSize(17);
   private final static Color GRAY = new Color(150, 150, 150);
   private final static Color LIGHT_GRAY = new Color(170, 170, 170);
   private final static Border GRAY_LINE_BORDER = new LineBorder(GRAY, 1);

   private final static Border LIGHT_GRAY_LINE_BORDER
         = new LineBorder(LIGHT_GRAY, 1);

   private final static EmptyBorder BAR_BUTTON_BORDER
         = new EmptyBorder(BAR_HEIGHT/2, 8, BAR_HEIGHT/2, 8);

   private final static Border MATTE_BOTTOM_GRAY
         = new MatteBorder(0, 0, 1, 0, GRAY);

   /**
    * Creates a <code>MatteBorder</code> with the gray color
    *
    * @param t  the top inset
    * @param l  the left inset
    * @param b  the bottom inset
    * @param r  the right inset
    * @return  the border
    */
  public static MatteBorder grayMatteBorder(int t, int l, int b, int r) {
      return new MatteBorder(t, l, b, r, GRAY);
   }

   /**
    * Creates a titled border with the light gray color and a title that
    * is displayed in Verdana with the scaled font size based on 8 pt
    *
    * @param title  the title
    * @return a new titled border
    */
   public static TitledBorder titledBorder(String title) {
      TitledBorder tb = new TitledBorder(LIGHT_GRAY_LINE_BORDER, title);
      tb.setTitleFont(Fonts.VERDANA_PLAIN_8);
      return tb;
   }

   /**
    * Creates a <code>JPanel</code> with a line border that has the gray
    * color and weight of one pixel
    *
    * @return  the JPanel
    */
   public static JPanel grayBorderedPanel() {
      JPanel pnl = new JPanel();
      pnl.setBorder(GRAY_LINE_BORDER);
      return pnl;
   }

   /**
    * Creates a <code>JPanel</code> that contains the specified
    * <code>JLabel</code> and <code>JButton</code> with a scaled gap
    * in between
    *
    * @param lb  the JLabel
    * @param bt  the JButton
    * @return  the JPanel
    */
   public static JPanel labeledPanel(JLabel lb, JButton bt) {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
      pnl.setOpaque(false);
      pnl.add(lb);
      pnl.add(Box.createRigidArea(ScreenParams.scaledDimension(5, 0)));
      pnl.add(bt);
      return pnl;
   }

   /**
    * Creates a <code>JToolBar</code> that has the bar height and a
    * border in the gray color with a weight of one pixel at the bottom.
    * <P>
    * The toolbar is not opaque and not floatable. The buttons in
    * <code>bts</code> are not focusable.
    *
    * @param bts  the array of JButtons. Can be null
    * @param tooltips  the array of tooltips for bts. Can be null
    * @param rightBt  the button aligned to the right
    * @return  a new <code>JToolBar</code>
    */
   public static JToolBar toolBar(JButton[] bts, String[] tooltips,
         JButton rightBt) {

      JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
      tb.setOpaque(false);
      tb.setPreferredSize(new Dimension(0, BAR_HEIGHT));
      tb.setBorder(MATTE_BOTTOM_GRAY);
      tb.setFloatable(false);
      if (bts != null) {
         for (int i = 0; i < bts.length; i++) {
            tb.add(bts[i]);
            bts[i].setBorder(BAR_BUTTON_BORDER);
            bts[i].setFocusable(false);
            bts[i].setFocusPainted(false);
            if (tooltips != null) {
               bts[i].setToolTipText(tooltips[i]);
            }
         }
      }
      tb.add(Box.createHorizontalGlue());
      tb.add(rightBt);
      tb.add(Box.createRigidArea(ScreenParams.scaledDimension(5, 0)));
      return tb;
   }

   /**
    * Creates a <code>JMenuBar</code> that has the bar height and a
    * border with a weight of one pixel in the gray color at the bottom
    *
    * @return  the JMenuBar
    */
   public static JMenuBar menuBar() {
      JMenuBar mb = new JMenuBar();
      mb.setOpaque(false);
      mb.setPreferredSize(new Dimension(0, BAR_HEIGHT));
      mb.setBorder(MATTE_BOTTOM_GRAY);
      return mb;
   }

   /**
    * Creates a <code>JButton</code> that has no border, is not focusable
    * and has an unfilled content area
    *
    * @return  the JButton
    */
   public static JButton undecoratedButton() {
      JButton bt = new JButton();
      bt.setBorder(null);
      bt.setFocusable(false);
      bt.setFocusPainted(false);
      bt.setContentAreaFilled(false);
      return bt;
   }

   /**
    * Creates an <code>ExtTabbedPane</code> that has the bar height, is
    * scrollable and not focusable
    *
    * @return  a new ExtTabbedPane
    */
   public static ExtTabbedPane tabPane() {
      ExtTabbedPane tabPane = new ExtTabbedPane(BAR_HEIGHT);
      tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      tabPane.setFocusable(false);
      return tabPane;
   }

   /**
    * Creates a <code>JScrollPane</code> that has no border and a unit
    * increment set to the value of 15. The scrollbars are displayed as
    * as needed
    *
    * @return  the JScollPane
    */
   public static JScrollPane scrollPane() {
      JScrollPane sp = new JScrollPane(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      sp.setBorder(null);
      sp.getVerticalScrollBar().setUnitIncrement(15);
      return sp;
   }

   //
   //--private--/
   //
   
   private UIComponents() {}
}
