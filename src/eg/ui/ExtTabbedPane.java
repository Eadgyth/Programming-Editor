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

import javax.swing.plaf.TabbedPaneUI;

//--Eadgyth--//
import eg.Constants;
import eg.Preferences;

/**
 * A JTabbedPane with a close button in the tabs, the possibility
 * to show and hide the tab bar and somewhat changed tab appearance
 */
public class ExtTabbedPane extends JTabbedPane {

   private UIForTab ui = new UIForTab();
   private int iTabMouseOver = -1;
   
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
    * Sets this <code>MouseMotionListener</code> which determines
    * the index of the tab where the moue was moved over
    */
   public void setMouseMotionListener() {
      addMouseMotionListener(mml);
   }

   /**
    * Adds a new tab
    *
    * @param title  the title for the tab
    * @param toAdd  the added Component
    * @param closeBt  the button displayed in the tab. An
    * <code>ActionListener</code> is expected to have been added to the
    * button
    * @param index  the index of the tab where a component is added
    */
   public void addTab(String title, Component toAdd, JButton closeBt,
         int index) {

      add(title, toAdd);
      setSelectedIndex(index);
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
      setTabComponentAt(index, tabPnl);
   }
   
   /**
    * Returns the index of the tab where the mouse was moved over
    *
    * @return  the index
    */
   public int iTabMouseOver() {
      return iTabMouseOver;
   }

   @Override
   public void setTitleAt(int index, String title) {
      JPanel p = (JPanel) getTabComponentAt(index);
      JLabel lb = (JLabel) p.getComponent(0);
      lb.setText(title);
   }

   /**
    * Disabled
    */
   @Override
   public void setTabPlacement(int tabPlacement) {
   }

   /**
    * Disabled
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
