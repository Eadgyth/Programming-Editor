package eg.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.UIManager;

import javax.swing.plaf.metal.MetalTabbedPaneUI;

/**
 * Makes tabs rectengular, selected tab white, unselected tabs "normal" gray,
 * aligns tabs at the left edge
 */
class UIForTab extends MetalTabbedPaneUI {

   @Override
   protected void paintTabBorder(Graphics g, int tabPlacement,
          int tabIndex, int x, int y, int w, int h, boolean isSelected) {

      y -= 2;
      h += 2;
      x -= 2;
      w += 2;
      g.setColor(eg.Constants.BORDER_GRAY);
      g.drawLine(x, y, x, y + h); // left vertical
      g.drawLine(x, y , x + w, y ); // bottom hor
      g.drawLine(x + w, y, x + w, y + h); // right vertical
   }
   
   @Override
   protected void paintTabBackground(Graphics g, int tabPlacement,
       int tabIndex, int x, int y, int w, int h, boolean isSelected) {
      
      y -= 2;
      h += 2;
      x -= 2;
      w += 2;
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
}
