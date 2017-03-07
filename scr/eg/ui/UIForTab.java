package eg.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;

import javax.swing.UIManager;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * Makes tabs rectengular, selected tab white, unselected tabs "normal" gray,
 * aligns tabs at the left edge
 */
class UIForTab extends BasicTabbedPaneUI {
   
   private final Insets borderInsets = new Insets(0, 0, 0, 0);
   
   @Override
   protected int calculateMaxTabHeight(int tabPlacement) {
      return 22;
   }

   @Override
   protected void paintTabBorder(Graphics g, int tabPlacement,
          int tabIndex, int x, int y, int w, int h, boolean isSelected) {

      y = 0;
      if (x < 0) {
         x = 0;
      }
      if (isSelected) {
         g.setColor(eg.Constants.BORDER_DARK_GRAY);
      }
      else {
         g.setColor(eg.Constants.BORDER_LIGHT_GRAY);
      }
      g.drawLine(x, y , x + w, y );       // top hor
      g.drawLine(x, y, x, y + h);         // left vertical
      g.drawLine(x + w, y, x + w, y + h); // right vertical
   }
   
   @Override
   protected void paintTabBackground(Graphics g, int tabPlacement,
       int tabIndex, int x, int y, int w, int h, boolean isSelected) {

      y = 0;
      if (x < 0) {
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
      int maxHeight = super.calculateMaxTabHeight(tabPlacement);
      return 21 - maxHeight;
   }
     
   @Override
   protected void paintContentBorder(Graphics g, int tabPlacement,
         int selectedIndex) {
   }

   @Override
   protected Insets getContentBorderInsets(int tabPlacement) {
      return borderInsets;
   }
   
   @Override
   protected Insets getTabAreaInsets(int tabPlacement) {
      return borderInsets;
   }
}
