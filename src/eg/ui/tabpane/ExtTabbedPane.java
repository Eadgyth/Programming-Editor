package eg.ui.tabpane;

import java.awt.Component;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.FunctionalAction;
import eg.utils.ScreenParams;
import eg.ui.IconFiles;

/**
 * The tabbed pane for the editor.
 * <p>
 * Uses {@link ExtTabbedPaneUI} for the tabbar appearance. The
 * UI requires that the tab placement property (top) and the
 * tab layout property (scroll layout) are not changed.
 */
@SuppressWarnings("serial")
public final class ExtTabbedPane extends JTabbedPane {

   private final transient ExtTabbedPaneUI etpUI;
   private final transient BackgroundTheme theme;

   /**
    * The <code>FunctionalAction</code> that may remove a tab
    * on a button click at a given index */
   private FunctionalAction closeAct;
   /**
    * The index of the tab where the mouse is moved over */
   private int iTabMouseOver = -1;
   /**
    * The boolean that indicates if the tabbar is currently
    * shown */
   private boolean isShowTabbar;

   /**
    * Creates an <code>ExtTabbedPane</code>
    *
    * @param theme  the BackgroundTheme
    * @param tabHeight  the height of the tabbar
    */
   public ExtTabbedPane(BackgroundTheme theme, int tabHeight) {
      this.theme = theme;
      etpUI = new ExtTabbedPaneUI(theme, tabHeight);
      setUI(etpUI);
      setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      setFocusable(false);
      addMouseMotionListener(mml);
   }

   /**
    * Sets the <code>TabClosing</code>
    *
    * @param tabClose  the TabClosing
    */
   public void setTabClosing(TabClosing tabClose) {
      ActionListener close = e -> tabClose.close(iTabMouseOver);
      closeAct = new FunctionalAction("", IconFiles.CLOSE_ICON, close);
   }

   /**
    * Adds a new closable tab
    *
    * @param title  the title for the tab
    * @param c  the component to be displayed when the tab is selected
    */
   public void addClosableTab(String title, Component c) {
      int index = getTabCount();
      addTab(title, c);
      setTabComponentAt(index, new ETPTabComponent(title));
      setSelectedIndex(index);
   }

   /**
    * Shows or hides the tabbar
    *
    * @param b  true to show the tabbar, false to hide
    * @throws IllegalStateException  if b is false
    * while more than one tab is open
    */
   public void showTabbar(boolean b) {
      if (!b && getTabCount() > 1) {
         throw new IllegalStateException(
               "Hiding tabs is not allowed since more than "
               + " one tab is open");
      }
      etpUI.setShowTabs(b);
      revalidate();
      repaint();
      isShowTabbar = b;
   }

   /**
    * Returns if this tabbar is currently set visible
    *
    * @return  true if visible, false otherwise
    */
   public boolean isShowTabbar() {
      return isShowTabbar;
   }

   /**
    * Sets the title for the tab at the specified index
    *
    * @param index  the index
    * @param title  the title
    */
   @Override
   public void setTitleAt(int index, String title) {
      super.setTitleAt(index, title);
      ETPTabComponent c = (ETPTabComponent) getTabComponentAt(index);
      if (c != null) {
         c.setTitle(title);
      }
   }

   //
   //--private--/
   //

   private class ETPTabComponent extends JPanel {

      private final JLabel lb = new JLabel();

      private ETPTabComponent(String title) {
         setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
         setOpaque(false);
         Font f = lb.getFont();
         lb.setFont(ScreenParams.scaledFontToPlain(f, 8));
         lb.setText(title);
         lb.setForeground(theme.normalText());
         JButton bt = undecoratedButton();
         bt.setAction(closeAct);
         add(lb);
         add(Box.createRigidArea(ScreenParams.scaledDimension(5, 0)));
         add(bt);
      }

      private void setTitle(String title) {
         lb.setText(title);
      }

      private JButton undecoratedButton() {
         JButton bt = new JButton();
         bt.setBorder(null);
         bt.setFocusable(false);
         bt.setFocusPainted(false);
         bt.setContentAreaFilled(false);
         return bt;
      }
   }

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
