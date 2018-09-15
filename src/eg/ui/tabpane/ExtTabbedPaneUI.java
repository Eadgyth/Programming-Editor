package eg.ui.tabpane;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;

import javax.swing.UIManager;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * A modified appearance for the <code>JTabbedPane</code>.
 * <p>The background of a selected tab is white; other tabs are in 'normal'
 * gray.<br>
 * The tab height can be defined in {@link #setHeight(int)} and the selected
 * tab is not elevated.<br>
 * Tabs are rectangular and aligned at the left edge.<br>
 * The content area insets are set to zero and a content border is not
 * painted. The tab area insets are zero except for the lower end where the
 * inset value is one point when the tab bar is visible.<br>
 * The tab bar is visible or hidden depending on the value passed in
 * {@link #setShowTabs(boolean)}.
 */
public class ExtTabbedPaneUI extends BasicTabbedPaneUI {

   private final static Color BORDER_GRAY = new Color(100, 100, 100);
   private final static Color LIGHT_GRAY = new Color(150, 150, 200);
   private final static Insets TAB_INSETS_BOTTOM = new Insets(0, 0, 1, 0);
   private final static Insets TAB_INSETS_ZERO = new Insets(0, 0, 0, 0);
   private final static Insets CONTENT_INSETS = new Insets(0, 0, 0, 0);

   private boolean isShowTabs = true;
   private int tabHeight;
   
   /**
    * Sets the height of the tabs
    *
    * @param height  the height
    */
   public void setHeight(int height) {
      tabHeight = height;      
   }

   /**
    * Controls if the tab bar is visible when this ui is updated.
    *
    * @param show  true/false to show/hide the tab bar
    */
   public void setShowTabs(boolean show) {
      isShowTabs = show;
   }

   @Override
   protected int calculateMaxTabHeight(int tabPlacement) {
      if (isShowTabs) {
         return tabHeight;
      }
      else {
         return 0;
      }
   }

   @Override
   protected void paintTabBorder(Graphics g, int tabPlacement,
          int tabIndex, int x, int y, int w, int h, boolean isSelected) {

      if (!isShowTabs) {
         return;
      }

      y = 0;
      if (tabIndex == 0 && x != 0) {
        w = w + x;
        x = 0;
      }
      g.setColor(BORDER_GRAY);
      g.drawLine(x, y , x + w, y);
      g.drawLine(x, y, x, y + h);
      g.drawLine(x + w, y, x + w, y + h);
      g.drawLine(x, y + tabHeight, x + w, y + tabHeight);
   }

   @Override
   protected void paintTabBackground(Graphics g, int tabPlacement,
         int tabIndex, int x, int y, int w, int h, boolean isSelected) {

      if (!isShowTabs) {
         return;
      }

      y = 0;
      if (tabIndex == 0 && x != 0) {
        w = w + x;
        x = 0;
      }
      Polygon shape = new Polygon(); 
      shape.addPoint(x, y);
      shape.addPoint(x, y + h);
      shape.addPoint(x + w, y + h);
      shape.addPoint(x + w, y);
      if (isSelected) {
         g.setColor(Color.WHITE);
      }
      else {
         g.setColor(UIManager.getColor("Panel.background"));
      }
      g.fillPolygon(shape);
   }

   @Override
   protected int getTabLabelShiftY(int tabPlacement, int tabIndex,
         boolean isSelected) {

      return 0;
   }

   @Override
   protected void paintContentBorder(Graphics g, int tabPlacement,
         int selectedIndex) {
   }

   @Override
   protected Insets getContentBorderInsets(int tabPlacement) {
      return CONTENT_INSETS;
   }

   @Override
   protected Insets getTabAreaInsets(int tabPlacement) {
      if (isShowTabs) {
         return TAB_INSETS_BOTTOM;
      }
      else {
         return TAB_INSETS_ZERO;
      }
   }
}
