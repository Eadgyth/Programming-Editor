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
import javax.swing.UIManager;

import javax.swing.border.EmptyBorder;

import javax.swing.event.ChangeListener;

//--Eadgyth--//
import eg.Constants;

/**
 * Defines a JTabbedPane with a button in the tabs.
 * <p>
 * A button indended for closing tabs is passed in the method 
 * {@link #addNewTab(String, Component, JButton, int)}. The button
 * is expected to have an ActionListener added ti it. To detect
 * which tab is selected for closing the method {@link #iTabMouseOver()}
 * is called.
 */
public class TabbedPane { 

   private JTabbedPane tabbedPane;
   
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
      closeBt.setIcon(IconFiles.closeIcon);
      closeBt.setBorder(new EmptyBorder(0, 0, 0, 0));
      closeBt.setBorderPainted(false);
      closeBt.setContentAreaFilled(false);
      closeBt.setFocusable(false);
      tabPnl.add(titleLb);
      tabPnl.add(closeBt); 
      tabbedPane.setTabComponentAt(index, tabPnl);
   }
   
   public void changeTabTitle(int index, String filename) {
      JPanel p = (JPanel) tabbedPane.getTabComponentAt(index);
      JLabel lb = (JLabel) p.getComponent(0);
      lb.setText(filename);
   }
   
   public int tabCount() {
      return tabbedPane.getTabCount();
   }
   
   public int selectedIndex() {
      return tabbedPane.getSelectedIndex();
   }
   
   public void selectTab(int index) {
      tabbedPane.setSelectedIndex(index);
   }
   
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
     // tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
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
         if (x == -1) {
            return;
         }
         else if (x == iTabMouseOver) {
            return;
         }
         else {
            iTabMouseOver = x;
         }
      }
   };
}