package eg.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import javax.swing.JComponent;

import javax.swing.UIManager;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * Makes tabs rectengular, selected tab white, unselected tabs "normal" gray,
 * aligns tabs at the left edge
 */
class EmptyUIForTab extends BasicTabbedPaneUI {
   
   private final Insets borderInsets = new Insets(0, 0, 0, 0);

    @Override  
    protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {  
       return 0;  
    }
    
    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
    }

    @Override
    protected Insets getContentBorderInsets(int tabPlacement) {
       return borderInsets;
    } 
}
