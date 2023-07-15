package eg.ui.tabpane;

import javax.swing.JComponent;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

//--Eadgyth--/
import eg.BackgroundTheme;

/**
 * The UI for the <code>ExtTabbedPane</code>
 */
public class ExtTabbedPaneUI extends BasicTabbedPaneUI {

   private static final Insets ZERO_INSETS = new Insets(0, 0, 0, 0);

   private final BackgroundTheme theme;
   private final int tabHeight;

   private boolean isShowTabs = true;

   /**
    * Creates an <code>ExtendedTabbedPaneUI</code>
    *
    * @param theme  the BackgroundTheme
    * @param tabHeight  the height of the tabbar
    */
   public ExtTabbedPaneUI(BackgroundTheme theme, int tabHeight) {
      this.theme = theme;
      this.tabHeight = tabHeight;
   }

   /**
    * Sets the boolean that indicates if the tabbar is visible
    *
    * @param b  true to show, false to hide the tabbar
    */
   public void setShowTabs(boolean b) {
      isShowTabs = b;
   }

   @Override
   public void paint(Graphics g, JComponent c) {
      Rectangle bounds = tabPane.getBounds();
      g.setColor(theme.lightBackground());
      g.fillRect(0, 0, bounds.width, bounds.height);
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
      int yy = 0;
      if (tabIndex == 0 && x != 0) {
         w = w + x;
         x = 0;
      }
      g.setColor(theme.lineBorder());
      g.drawLine(x, yy, x, yy + h);
      g.drawLine(x + w, yy, x + w, yy + h);
      g.drawLine(x, yy , x + w, yy);
   }

   @Override
   protected void paintTabBackground(Graphics g, int tabPlacement,
         int tabIndex, int x, int y, int w, int h, boolean isSelected) {

      if (!isShowTabs) {
         return;
      }
      int yy = 0;
      if (tabIndex == 0 && x != 0) {
         w = w + x;
         x = 0;
      }
      Polygon shape = new Polygon();
      shape.addPoint(x, yy);
      shape.addPoint(x, yy + h);
      shape.addPoint(x + w, yy + h);
      shape.addPoint(x + w, yy);
      if (isSelected) {
         g.setColor(theme.background());
      }
      else {
         g.setColor(theme.lightBackground());
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

	  // should not paint
   }

   @Override
   protected Insets getContentBorderInsets(int tabPlacement) {
      return ZERO_INSETS;
   }

   @Override
   protected Insets getTabAreaInsets(int tabPlacement) {
       return ZERO_INSETS;
   }
}
