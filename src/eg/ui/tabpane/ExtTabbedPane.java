package eg.ui.tabpane;

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

/**
 * A JTabbedPane with a close button in the tabs, the possibility
 * to show and hide the tab bar and a somewhat changed tab appearance.
 * The tab placement is restricted to be at the top, however.
 */
public class ExtTabbedPane extends JTabbedPane {

   private final static FlowLayout FLOW_LAYOUT_LEFT = new FlowLayout(FlowLayout.LEFT, 0, 0);
   private final static EmptyBorder EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);

   private final ExtTabbedPaneUI ui = new ExtTabbedPaneUI();

   private int iTabMouseOver = -1;
   private boolean isShowTabbar;
   
   /**
    * Creates an <code>ExtTabbedPane</code> using the parameterless
    * constructor of the superclass (tab placement at the top) and
    * sets this {@link ExtTabbedPaneUI}
    */
   public ExtTabbedPane() {
      ui.setHeight((int) (eg.Constants.SCREEN_RES_RATIO * 16));
      super.setUI(ui);
      addMouseMotionListener(mml);
   }
   
   /**
    * Show or hides the tab bar. Hiding requires that only ony tab is
    * displayed.
    *
    * @param show  true/false to show/hide the tabbar
    */
   public void showTabbar(boolean show) {
      if (!show && getTabCount() > 1) {
         throw new IllegalStateException("Hiding tabs is illegal"
               + " when the number of tabs is bigger than one");
      }
      ui.setShowTabs(show);
      updateUI();
      isShowTabbar = show;
   }
   
   public boolean isShowTabbar() {
      return isShowTabbar;
   }

   /**
    * Adds a new tab
    *
    * @param title  the title for the tab
    * @param c  the component to be displayed when the tab is selected
    * @param closeBt  the button displayed in the tab. An
    * <code>ActionListener</code> is expected to have been added to the
    * button
    */
   public void addTab(String title, Component c, JButton closeBt) {
      int index = getTabCount();
      addTab(null, c);
      JPanel tabPnl = new JPanel(FLOW_LAYOUT_LEFT);
      tabPnl.setOpaque(false);
      JLabel titleLb = new JLabel(title);
      titleLb.setFont(eg.Constants.VERDANA_PLAIN_8);
      closeBt.setBorder(EMPTY_BORDER);
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
    * Used instead of <code>setTitleAt()</code> in parent class if tabs
    * are added through {@link #addTab(String, Component, JButton)}
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
    * Sets this UI
    */
   @Override
   public void updateUI() {
       super.setUI(ui);
   }
   
   /**
    * No effect. The UI object is set in this class
    */
   @Override
   public void setUI(TabbedPaneUI ui) {
   }
   
   /**
    * No effect. The placement must remain at the top
    */
   @Override
   public void setTabPlacement(int tabPlacement) {
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
