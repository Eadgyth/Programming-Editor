package eg.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import javax.swing.border.EmptyBorder;

import javax.swing.event.ChangeListener;

import javax.swing.plaf.TabbedPaneUI;

//--Eadgyth--//
import eg.Constants;

/**
 * A JTabbedPane with a close button in the tabs, the possibility
 * to show and hide the tab bar and a somewhat changed tab appearance
 * @see ExtTabbedPaneUI
 */
public class ExtTabbedPane extends JTabbedPane {

   private final static FlowLayout FLOW_LAYOUT_LEFT
         = new FlowLayout(FlowLayout.LEFT, 0, 0);
   private final static EmptyBorder EMPTY_BORDER
         = new EmptyBorder(0, 0, 0, 0);
   private final static int FONT_SIZE = 11;
   private final static Font VERDANA_PLAIN
         = new Font("Verdana", Font.PLAIN, FONT_SIZE);

   private ExtTabbedPaneUI ui = new ExtTabbedPaneUI();
   private int iTabMouseOver = -1;
   
   public ExtTabbedPane() {
      ui.setHeight(FONT_SIZE);
      super.setUI(ui);
      addMouseMotionListener(mml);
   }
   
   /**
    * Show or hides the tab bar
    *
    * @param show  true/false to show/hide the tanbar
    * @throws IllegalStateException  if <code>show</code> is false when
    * two or more tabs are open
    */
   public void showTabbar(boolean show) {
      if (!show && getTabCount() > 1) {
         throw new IllegalStateException("Cannot hide tab bar"
               + " when two or more tabs are added");
      }
      ui.setShowTabs(show);
      super.setUI(ui);
   }

   /**
    * Adds a new tab
    *
    * @param title  the title for the tab
    * @param c  the component to be displayed when this tab is clicked
    * @param closeBt  the button displayed in the tab. An
    * <code>ActionListener</code> is expected to have been added to the
    * button
    * @param index  the index of the tab where a component is added
    */
   public void addTab(String title, Component c, JButton closeBt,
         int index) {

      addTab(null, c);
      JPanel tabPnl = new JPanel(FLOW_LAYOUT_LEFT);
      tabPnl.setOpaque(false);
      JLabel titleLb = new JLabel(title);
      titleLb.setFont(VERDANA_PLAIN);
      closeBt.setBorder(EMPTY_BORDER);
      closeBt.setBorderPainted(false);
      closeBt.setContentAreaFilled(false);
      closeBt.setFocusable(false);
      tabPnl.add(titleLb);
      tabPnl.add(closeBt);
      setTabComponentAt(index, tabPnl);
      setSelectedIndex(index);
   }
   
   /**
    * Returns the index of the tab where the mouse was moved over
    *
    * @return  the index
    */
   public int iTabMouseOver() {
      return iTabMouseOver;
   }

   /**
    * Changes the title for the tab at the specified index.
    * Used instead of <code>setTitleAt()</code> in parent class if
    * tabs are added through
    * {@link #addTab(String, Component, JButton, int)}
    *
    * @param index  the index of the tab where the title is set
    * @param title  the tiltle
    */
   public void changeTitle(int index, String title) {
      JPanel p = (JPanel) getTabComponentAt(index);
      JLabel lb = (JLabel) p.getComponent(0);
      lb.setText(title);
   }

   /**
    * No effect
    */
   @Override
   public void setTabPlacement(int tabPlacement) {
   }

   /**
    * No effect
    */
   @Override
   public void setUI(TabbedPaneUI ui) {
   }
   
   //
   //--private
   //

   private final MouseMotionListener mml = new MouseMotionAdapter() {

      @Override
      public void mouseMoved(MouseEvent e) {
         JTabbedPane sourceTb = (JTabbedPane) e.getSource();
         int x = sourceTb.indexAtLocation(e.getX(), e.getY());
         if (x != -1 & x != iTabMouseOver) {
            iTabMouseOver = x;
         }
      }
   };
}
