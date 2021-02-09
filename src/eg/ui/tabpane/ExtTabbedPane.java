package eg.ui.tabpane;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.TabbedPaneUI;

//--Eadgyth--/
import eg.FunctionalAction;
import eg.ui.UIComponents;
import eg.utils.ScreenParams;

/**
 * The <code>JTabbedPane</code> for the editor.
 * <p>
 * Uses {@link ExtTabbedPaneUI} for the tabbar appearance
 */
@SuppressWarnings("serial")
public final class ExtTabbedPane extends JTabbedPane {

   private final transient ExtTabbedPaneUI tui = new ExtTabbedPaneUI();

   private int iTabMouseOver = -1;
   private boolean isShowTabbar;

   /**
    * Creates an <code>ExtTabbedPane</code>
    *
    * @param barHeight  the height of the tab bar
    */
   public ExtTabbedPane(int barHeight) {
      tui.setHeight(barHeight);
      super.setUI(tui);
      addMouseMotionListener(mml);
   }

   /**
    * Shows or hides the tabbar
    *
    * @param b  true to show the tabbar, false to hide
    * @throws IllegalStateException  if <code>b</code> is false while more
    * than one tab is open
    */
   public void showTabbar(boolean b) {
      if (!b && getTabCount() > 1) {
         throw new IllegalStateException(
               "Hiding tabs is not allowed since more than "
               + " one tab is open");
      }
      tui.setShowTabs(b);
      updateUI();
      isShowTabbar = b;
   }

   /**
    * Adds a new tab
    *
    * @param title  the title for the tab
    * @param c  the component to be displayed when the tab is selected
    * @param closeAct  the closing action
    */
   public void addTab(String title, Component c, FunctionalAction closeAct) {
      int index = getTabCount();
      addTab(null, c);
      JLabel titleLb = new JLabel(title);
      Font f = titleLb.getFont();
      titleLb.setFont(ScreenParams.scaledFontToPlain(f, 8));
      JButton closeBt = UIComponents.undecoratedButton();
      closeBt.setAction(closeAct);
      JPanel pnl = UIComponents.labeledPanel(titleLb, closeBt);
      setTabComponentAt(index, pnl);
      setSelectedIndex(index);
   }

   /**
    * Sets the title at the index in tabs that were added by
    * {@link #addTab(String,Component,FunctionalAction)}
    *
    * @param index  the index
    * @param title  the title
    */
   public void setTitle(int index, String title) {
      JPanel p = (JPanel) getTabComponentAt(index);
      JLabel lb = (JLabel) p.getComponent(0);
      lb.setText(title);
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
    * Returns the boolean that indicates if this tabbar is set visible
    *
    * @return  the boolean value
    */
   public boolean isShowTabbar() {
      return isShowTabbar;
   }

   /**
    * Sets this UI
    */
   @Override
   public void updateUI() {
       super.setUI(tui);
   }

   @Override
   public void setUI(TabbedPaneUI ui) {
	  // not used
   }

   @Override
   public void setTabPlacement(int tabPlacement) {
	  // not used
   }

   //
   //--private--/
   //

   private final transient MouseMotionListener mml = new MouseMotionAdapter() {

      @Override
      public void mouseMoved(MouseEvent e) {
         JTabbedPane sourceTb = (JTabbedPane) e.getSource();
         int x = sourceTb.indexAtLocation(e.getX(), e.getY());
         if (x != -1 && x != iTabMouseOver) {
            iTabMouseOver = x;
         }
      }
   };
}
