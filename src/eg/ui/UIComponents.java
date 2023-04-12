package eg.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.Prefs;
import eg.utils.ScreenParams;
import eg.ui.tabpane.ExtTabbedPane;

/**
 * Static methods to create UI components with common layouts for
 * the editor.
 * <p>
 * Unless otherwise noted, the coloring depends on the selected
 * background given in {@link eg.BackgroundTheme}
 * <p>
 * Other properties are the 'bar height' for menu, tool, tab bars,
 * and scaled (font) sizes given in {@link eg.utils.ScreenParams}
 */
public class UIComponents {

   private static final Prefs PREFS = new Prefs();
   private static final boolean IS_SYSTEM_LAF
         = PREFS.property(Prefs.LAF_KEY).equals("System");
   private static final BackgroundTheme THEME = BackgroundTheme.givenTheme();
   private static final int BAR_HEIGHT = ScreenParams.scaledSize(17);
   private static final Color GRAY = THEME.lineBorder();
   private static final Color LIGHT_GRAY = new Color(170, 170, 170);
   private static final Border GRAY_LINE_BORDER = new LineBorder(GRAY, 1);
   private static final Border LIGHT_GRAY_LINE_BORDER = new LineBorder(LIGHT_GRAY, 1);
   private static final Border MATTE_BOTTOM_GRAY = new MatteBorder(0, 0, 1, 0, GRAY);
   private static final EmptyBorder BAR_BUTTON_BORDER = new EmptyBorder(2, 8, 2, 8);

   /**
    * Creates a gray <code>MatteBorder</code>
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
    * Creates a <code>MatteBorder</code> with the light (panel)
    * background
    *
    * @param t  the top inset
    * @param l  the left inset
    * @param b  the bottom inset
    * @param r  the right inset
    * @return  the border
    */
   public static MatteBorder lightBkgdMatteBorder(int t, int l, int b, int r) {
      return new MatteBorder(t, l, b, r, THEME.lightBackground());
   }

   /**
    * Creates a titled border with 'light gray' color and a title
    * that is displayed in Verdana with the scaled font size based on
    * 8 pt (no usage of background theme)
    *
    * @param title  the title
    * @return a new titled border
    */
   public static TitledBorder titledBorder(String title) {
      TitledBorder tb = new TitledBorder(LIGHT_GRAY_LINE_BORDER, title);
      tb.setTitleFont(ScreenParams.SANSSERIF_PLAIN_8);
      return tb;
   }

   /**
    * Creates a <code>JPanel</code> with a gray line border and
    * weight of one pixel
    *
    * @return  the JPanel
    */
   public static JPanel grayBorderedPanel() {
      JPanel pnl = new JPanel();
      pnl.setBorder(GRAY_LINE_BORDER);
      return pnl;
   }

   /**
    * Creates a <code>JPanel</code> with a left aligned
    * <code>JLabel</code> that displays the specified string in
    * bold and scaled font size based on 8 pt (no usage of
    * background theme)
    *
    * @param label  the label
    * @return  the JPanel
    */
   public static JPanel labelPanel(String label) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JLabel lb = new JLabel(label);
      lb.setFont(ScreenParams.scaledFontToBold(lb.getFont(), 8));
      pnl.add(lb);
      return pnl;
   }

  /**
   * Creates a <code>JMenuBar</code>
   *
   * @param menus  the JMenus to add to the
   * @return  the JMenuBar
   */
   public static JMenuBar menuBar(JMenu[] menus) {
      JMenuBar mb = new JMenuBar();
      if (!IS_SYSTEM_LAF && THEME != null && THEME.isDark()) {
         mb.setBackground(THEME.background());
      }
      addMenus(menus, mb);
      return mb;
   }

   /**
    * Creates a <code>JMenuBar</code> with a right button, a gray
    * line border at the bottom and the 'bar height'
    *
    * @param menus  the objects of JMenu to add to the menubar
    * @param rightBt  the button aligned to the right. Can be null
    * @return  the JMenuBar
    */
   public static JMenuBar menuBar(JMenu[] menus, JButton rightBt) {
      JMenuBar mb = menuBar(menus);
      mb.setPreferredSize(new Dimension(0, BAR_HEIGHT));
      mb.setBorder(MATTE_BOTTOM_GRAY);
      if (rightBt != null) {
         mb.add(Box.createHorizontalGlue());
         undecorateButton(rightBt);
         mb.add(rightBt);
         mb.add(Box.createRigidArea(ScreenParams.scaledDimension(5, 0)));
      }
      return mb;
   }

   /**
    * Creates a <code>JToolBar</code>
    *
    * @param bts  the objects of JButton to add. Can be null
    * @param tooltips  the objects of tooltips for bts in the
    * corresponding order. Can be null
    * @return  the JToolbar
    */
   public static JToolBar toolbar(JButton[] bts, String[] tooltips) {
      JToolBar tb = new JToolBar(SwingConstants.HORIZONTAL);
      if (bts != null) {
         addButtons(bts, tooltips, tb);
      }
      if (THEME != null && THEME.isDark()) {
         tb.setBackground(THEME.lightBackground());
      }
      else {
         tb.setOpaque(false);
      }
      return tb;
   }

   /**
    * Creates a <code>JToolBar</code> with a right button, a gray
    * line border at the bottom and the 'bar height'
    *
    * @param bts  the objects of JButton to add. Can be null
    * @param tooltips  the objects of tooltips for bts in the
    * corresponding order. Can be null
    * @param rightBt  the button aligned to the right. Can be null
    * @return  the JToolbar
    */
   public static JToolBar toolbar(JButton[] bts, String[] tooltips,
         JButton rightBt) {

      JToolBar tb = toolbar(bts, tooltips);
      tb.setPreferredSize(new Dimension(0, UIComponents.BAR_HEIGHT));
      tb.setBorder(UIComponents.MATTE_BOTTOM_GRAY);
      tb.setFloatable(false);
      if (rightBt != null) {
         tb.add(Box.createHorizontalGlue());
         undecorateButton(rightBt);
         tb.add(rightBt);
         tb.add(Box.createRigidArea(ScreenParams.scaledDimension(5, 0)));
      }
      return tb;
   }

   /**
    * Creates an <code>ExtTabbedPane</code> with the
    * <code>BackgroundTheme</code> and the 'bar height'
    *
    * @return  the ExtTabbedPane
    * @see eg.ui.tabpane.ExtTabbedPane
    */
   public static ExtTabbedPane tabPane() {
      return new ExtTabbedPane(THEME, BAR_HEIGHT);
   }

   /**
    * Creates a <code>JScrollPane</code> with scrollbars shown
    * 'as needed' and no border. The value for 'unitIncrement'
    * is set to 15
    *
    * @return  the JScollPane
    */
   public static JScrollPane scrollPane() {
      JScrollPane sp = new JScrollPane();
      if (THEME.isDark()) {
         sp.getVerticalScrollBar().setUI(new ExtScrollBarUI());
         sp.getHorizontalScrollBar().setUI(new ExtScrollBarUI());
         sp.setBackground(THEME.background());
      }
      sp.setBorder(null);
      sp.getVerticalScrollBar().setUnitIncrement(15);
      return sp;
   }

   //
   //--private--/
   //

   private static void addMenus(JMenu[] menus, JMenuBar mb) {
      int strutSize = 0;
      if ("Windows".equals(UIManager.getLookAndFeel().getName())) {
         strutSize = 5;
      }
      for (int i = 0; i < menus.length; i ++) {
         mb.add(menus[i]);
         mb.add(Box.createHorizontalStrut(strutSize));
         if (!IS_SYSTEM_LAF && THEME != null && THEME.isDark()) {
             menus[i].setForeground(THEME.normalText());
         }
      }
   }

   private static void addButtons(JButton[] bts, String[] tooltips, JToolBar tb) {
      for (int i = 0; i < bts.length; i++) {
         tb.add(bts[i]);
         bts[i].setOpaque(false);
         bts[i].setBorder(BAR_BUTTON_BORDER);
         bts[i].setFocusable(false);
         if (tooltips != null) {
            bts[i].setToolTipText(tooltips[i]);
         }
      }
   }

   private static void undecorateButton(JButton bt) {
      bt.setBorder(null);
      bt.setFocusable(false);
      bt.setFocusPainted(false);
      bt.setContentAreaFilled(false);
   }

   private static final class ExtScrollBarUI extends BasicScrollBarUI {

      @Override
      protected void configureScrollBarColors() {
         //
         // highlight colors seem to be effectless when
         // extending BasicScrollBarUI
         trackColor = THEME.scrollbarTrack();
         trackHighlightColor = THEME.scrollbarTrack();
         thumbColor = THEME.scrollbarThumb();
         thumbHighlightColor = THEME.scrollbarThumb();
         thumbDarkShadowColor = THEME.background();
         thumbLightShadowColor = THEME.scrollbarThumb();
      }

      @Override
      protected JButton createDecreaseButton(int orientation) {
         JButton btn = super.createDecreaseButton(orientation);
         btn.setBorder(new LineBorder(THEME.scrollbarThumb(), 1));
         btn.setBackground(THEME.background());
         return btn;
      }

      @Override
      protected JButton createIncreaseButton(int orientation) {
         JButton btn = super.createIncreaseButton(orientation);
         btn.setBorder(new LineBorder(THEME.scrollbarThumb(), 1));
         btn.setBackground(THEME.background());
         return btn;
      }
   }

   private UIComponents() {}
}
