package eg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.Box;
import javax.swing.BoxLayout;

import javax.swing.border.EmptyBorder;

//--Eadgyth--/
import eg.Constants;
import eg.utils.UiComponents;

/**
 * Defines a panel to which a component can be added
 */
public class ToolPanel {
   
   private final JPanel pnl = new JPanel(new BorderLayout());
   private final JPanel headPnl = new JPanel();
   private final JButton closeBt = new JButton(IconFiles.CLOSE_ICON);
   
   public ToolPanel() {
      pnl.setBorder(Constants.GRAY_BORDER);
   }
   
   /**
    * Adds a <code>Component</code> to this tool panel.
    * <p> The panel has a <code>BorderLayout</code> and gray border. The
    * Component is added to the center. A previously added component is
    * removed.
    *
    * @param c  the Component that is added
    */
   public void addComponent(Component c) {
      BorderLayout layout = (BorderLayout) pnl.getLayout();
      Component cCenter = layout.getLayoutComponent(BorderLayout.CENTER);
      if (cCenter != null) {
         pnl.remove(cCenter);
      }
      if (c != null) {
         pnl.add(c, BorderLayout.CENTER);
      }
      pnl.revalidate();
      pnl.repaint();
   }
   
   public JPanel panel() {
      return pnl;
   }
}
   
