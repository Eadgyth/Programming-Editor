package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.BoxLayout;

import javax.swing.border.EmptyBorder;

import eg.Constants;

/**
 * Defines a panel to which a component can be added
 */
public class FunctionPanel {
   
   private final JPanel pnl = new JPanel(new BorderLayout());
   private final JPanel titlePnl = new JPanel();
   private final JLabel titleLb = new JLabel(" No function selected");
   private final JButton closeBt = new JButton(IconFiles.CLOSE_ICON);
   
   FunctionPanel() {
      initPanel();
   }
   
   /**
    * Adds a <code>Component</code> to this JPanel.
    * <p> The panel has a <code>BorderLayout</code>. The title is
    * shown in the north and the added component in the center. Any
    * previously added component is removed.
    *
    * @param c  the Component that is added
    * @param title  the title for the function
    */
   public void addComponent(Component c, String title) {
      BorderLayout layout = (BorderLayout) pnl.getLayout();
      Component cCenter = layout.getLayoutComponent(BorderLayout.CENTER);
      if (cCenter != null) {
         pnl.remove(cCenter);
      }
      titleLb.setText(" " + title);
      if (c != null) {
         pnl.add(c, BorderLayout.CENTER);
      }
      pnl.revalidate();
      pnl.repaint();
   }
   
   void closeAct(ActionListener al) {
      closeBt.addActionListener(al);
   }
   
   JPanel functionPanel() {
      return pnl;
   }
   
   private void initPanel() {
      titlePnl.setLayout(new BoxLayout(titlePnl, BoxLayout.LINE_AXIS));
      titlePnl.add(titleLb);
      titleLb.setFont(Constants.SANSSERIF_PLAIN_9);
      closeBt.setBorder(new EmptyBorder(3, 5, 3, 5));
      closeBt.setContentAreaFilled(false);
      closeBt.setToolTipText("Close function area");
      closeBt.setFocusable(false);
      titlePnl.add(Box.createHorizontalGlue());
      titlePnl.add(closeBt);
      pnl.setBorder(Constants.GRAY_BORDER);
      pnl.add(titlePnl, BorderLayout.NORTH);
   }
}
   
