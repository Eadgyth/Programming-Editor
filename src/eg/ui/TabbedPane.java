package eg.ui;

import java.awt.Component;
import java.awt.FlowLayout;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.border.MatteBorder;

import javax.swing.border.EmptyBorder;

import javax.swing.event.ChangeListener;

//--Eadgyth--//
import eg.Constants;
import eg.Preferences;

/**
 * Defines a JTabbedPane with a (close) button in the tabs.
 * <p>
 * A button indended for closing tabs is passed in the method 
 * {@link #addNewTab(String, Component, JButton, int)}. The button
 * is expected to have an ActionListener added to it. To detect which
 * tab is selected for closing the method {@link #iTabMouseOver()}
 * is called.
 */
public class TabbedPane { 

   private JTabbedPane tabbedPane;
   private final UIForTab ui = new UIForTab();
   
   /* The index of the tab the mouse moved over */
   private int iTabMouseOver = -1;
   
   public TabbedPane() {
      initTab();
   }
   
   public JTabbedPane tabbedPane() {
      return tabbedPane;
   }

   /**
    * @param title  the title for the tab
    * @param toAdd  the added Component
    * @param closeBt  the button displayed in the tab
    * @param index  the tab index at which a component is added
    */
   public void addNewTab(String title, Component toAdd, JButton closeBt,
         int index) {
      tabbedPane.add(title, toAdd);
      selectTab(index);
      JPanel tabPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      tabPnl.setOpaque(false);
      JLabel titleLb = new JLabel(title);
      titleLb.setFont(Constants.VERDANA_PLAIN_11);
      closeBt.setIcon(IconFiles.CLOSE_ICON);
      closeBt.setBorder(new EmptyBorder(0, 0, 0, 0));
      closeBt.setBorderPainted(false);
      closeBt.setContentAreaFilled(false);
      closeBt.setFocusable(false);
      tabPnl.add(titleLb);
      tabPnl.add(closeBt); 
      tabbedPane.setTabComponentAt(index, tabPnl);
   }
   
   public void showTabbar(boolean show) {
      if (!show && nTabs() > 1) {
         throw new IllegalStateException("More than one tab was added."
               + "Cannot hide tab bar");
      }  
      ui.setShowTabs(show);
      tabbedPane.setUI(ui);
   }
         
   
   public void changeTabTitle(int index, String filename) {
      JPanel p = (JPanel) tabbedPane.getTabComponentAt(index);
      JLabel lb = (JLabel) p.getComponent(0);
      lb.setText(filename);
   }
   
   /**
    * Returns the number of open tabs
    * @return  the number of open tabs
    */
   public int nTabs() {
      return tabbedPane.getTabCount();
   }
   
   /**
    * Returns the index of the selected tab
    * @return  the index of the selected tab
    */
   public int selectedIndex() {
      return tabbedPane.getSelectedIndex();
   }
   
   /**
    * selects the tab at the specified index
    * @param index  the index of the tab that is selected
    */
   public void selectTab(int index) {
      tabbedPane.setSelectedIndex(index);
   }
   
   /**
    * Returns the index of the tab where the mouse was moved over
    * @return  the index of the tab where the mouse was moved over
    */
   public int iTabMouseOver() {
      return iTabMouseOver;
   }
   
   public void removeTab(int index) {
      tabbedPane.remove(index);
   }
   
   public void changeListen(ChangeListener cl) {
      tabbedPane.addChangeListener(cl);
   }

   private JTabbedPane initTab() {
      tabbedPane = new JTabbedPane();
      tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      tabbedPane.setUI(ui);
      tabbedPane.setBorder(null);
      tabbedPane.setFocusable(false);
      tabbedPane.addMouseMotionListener(mml);
      return tabbedPane;
   }
   
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
